/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.vulkan.allocators.*;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * @author n1t4chi
 */
public class VulkanRenderingContext implements RenderingContext {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanRenderingContext.class );
    private static final String OVERLAY = "overlay";
    private static final String SCENE_3D = "scene3D";
    private static final String SCENE_2D = "scene2D";
    private static final String BACKGROUND = "background";
    
    private final VulkanFrameworkAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final boolean shouldDrawBorder;
    private final boolean shouldDrawTextures;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    
    private VulkanStages stages;
    private final ExecutorService executorService;
    private final ExecutorService executorService2;
    
    private final Deque< VulkanDataBuffer > availableDataBuffers = new LinkedList<>();
    
    private Colour backgroundColour;
    
    public VulkanRenderingContext(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this( allocator, device,
            PropertiesUtil.isPropertyTrue( PropertiesUtil.DRAW_BORDER_PROPERTY ),
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY )
        );
    }
    
    private VulkanRenderingContext(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device,
        boolean shouldDrawBorder,
        boolean shouldDrawTextures
    ) {
        this.allocator = allocator;
        this.device = device;
        this.shouldDrawBorder = shouldDrawBorder;
        this.shouldDrawTextures = shouldDrawTextures;
        executorService = Executors.newFixedThreadPool( 4 );
        executorService2 = Executors.newFixedThreadPool( PropertiesUtil.getIntegerProperty( "vulkan.threads", 8 ) );
    }
    
    public void dispose() {
        allocator.deregisterRenderingContext( this );
    }
    
    public void disposeUnsafe() {
        executorService.shutdownNow();
        executorService2.shutdownNow();
    }
    
    public void setUpSingleRender() {
        stages = new VulkanStages();
    }
    
    @Override
    public void renderOverlay( Camera2D camera, List< RenderData > renderDatas ) {
        var overlay = stages.getOverlay();
        overlay.addRenderDatas( renderDatas );
        overlay.setCamera( camera );
    }
    
    @Override
    public void renderBackground( Camera2D camera, Colour backgroundColour, List< RenderData > renderDatas ) {
        this.backgroundColour = backgroundColour;
        var background = stages.getBackground();
        background.addRenderDatas( renderDatas );
        background.setCamera( camera );
    }
    
    @Override
    public void renderScene3D( Camera3D camera, List< RenderData > renderDatas ) {
        var scene3d = stages.getScene3D();
        scene3d.addRenderDatas( renderDatas );
        scene3d.setCamera( camera );
    }
    
    @Override
    public void renderScene2D( Camera2D camera, List< RenderData > renderDatas ) {
        var scene2D = stages.getScene2D();
        scene2D.addRenderDatas( renderDatas );
        scene2D.setCamera( camera );
    }
    
    public void tearDownSingleRender(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        VulkanImageIndex imageIndex,
        VulkanSwapChain swapChain,
        List< VulkanTransferCommandPool > transferCommandPools
    ) {
        var stages = this.stages;
        var scene3D = stages.getScene3D();
        var scene2D = stages.getScene2D();
        var overlay = stages.getOverlay();
        var background = stages.getBackground();
        
        var deviceAllocator = device.getAllocator();
        availableDataBuffers.clear();
        availableDataBuffers.addAll( dataBuffers );
        var frameBuffer = imageIndex.getFrameBuffer();
    
        var backgroundFuture = executorService.submit( createWorker(
            trianglePipelines,
            linePipelines,
            swapChain,
            frameBuffer,
            background,
            executorService2
        ) );
        var scene2DFuture = executorService.submit( createWorker(
            trianglePipelines,
            linePipelines,
            swapChain,
            frameBuffer,
            scene2D,
            executorService2
        ) );
        var scene3DFuture = executorService.submit( createWorker(
            trianglePipelines,
            linePipelines,
            swapChain,
            frameBuffer,
            scene3D,
            executorService2
        ) );
        var overlayFuture = executorService.submit( createWorker(
            trianglePipelines,
            linePipelines,
            swapChain,
            frameBuffer,
            overlay,
            executorService2
        ) );
        
        var backgroundBatchDatas = waitAndGet( backgroundFuture, BACKGROUND );
        var scene2dBatchDatas = waitAndGet( scene2DFuture, SCENE_2D );
        var scene3dBatchDatas = waitAndGet( scene3DFuture, SCENE_3D );
        var overlayBatchDatas = waitAndGet( overlayFuture, OVERLAY );
    
        var imageAvailableSemaphore = imageIndex.getImageAvailableSemaphore();
        var renderFinishedSemaphore = imageIndex.getRenderFinishedSemaphore();
        
        List< VulkanSemaphore > initialWaitSemaphores = new ArrayList<>(  );
        for ( var transferCommandPool : transferCommandPools ) {
            initialWaitSemaphores.addAll( transferCommandPool.executeTransfers() );
        }
        initialWaitSemaphores.add( imageAvailableSemaphore );
        
        List< VulkanRenderBatchData > batchDatas = new ArrayList<>();
        batchDatas.add( backgroundBatchDatas );
        batchDatas.add( scene2dBatchDatas );
        batchDatas.add( scene3dBatchDatas );
        batchDatas.add( overlayBatchDatas );
        batchDatas.removeIf( Objects::isNull );
        
        var processedInfo = processBatchDatas( deviceAllocator, batchDatas, initialWaitSemaphores, renderFinishedSemaphore );
        
        var fence = deviceAllocator.createFence();
        
        device.getGraphicQueueFamily().submit( fence, processedInfo.getSubmitInfos() );
        
        swapChain.presentQueue( imageIndex );
        
        fence.executeWhenFinishedThenDispose( () -> {
            batchDatas.forEach( VulkanRenderBatchData::dispose );
            initialWaitSemaphores.forEach( VulkanSemaphore::finishedWait );
            processedInfo.getSemaphores().forEach( semaphore -> {
                semaphore.finishedSignal();
                semaphore.finishedWait();
            } );
            renderFinishedSemaphore.finishedSignal();
        } );
    }
    
    private VulkanRenderStageWorker createWorker(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        VulkanSwapChain swapChain,
        VulkanFrameBuffer frameBuffer,
        VulkanStage stage,
        ExecutorService executorService
    ) {
        return new VulkanRenderStageWorker(
            device,
            dataBuffers,
            availableDataBuffers,
            shouldDrawTextures,
            shouldDrawBorder,
            executorService,
            stage,
            swapChain,
            frameBuffer,
            backgroundColour,
            linePipelines,
            trianglePipelines
        );
    }
    
    private VulkanRenderBatchDatasProcessedInfo processBatchDatas(
        VulkanDeviceAllocator deviceAllocator,
        List< VulkanRenderBatchData > batchDatas,
        List< VulkanSemaphore > initialWaitSemaphores,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        List< VkSubmitInfo > submitInfos = new ArrayList<>(  );
        List< VulkanSemaphore > semaphores = new ArrayList<>();
        
        if( batchDatas.isEmpty() ) {
            submitInfos.add( VulkanSubmitInfo.createSubmitInfo(
                initialWaitSemaphores,
                renderFinishedSemaphore
            ) );
        } else {
            VulkanSemaphore waitSemapore = null;
            var firstSubmit = true;
            for ( int i = 0, batchDatasSize = batchDatas.size(); i < batchDatasSize; i++ ) {
                var batchData = batchDatas.get( i );
                VkSubmitInfo submitInfo;
                var primaryBuffers = batchData.getPrimaryBuffers();
                for( int j = 0, primaryBuffersSize = primaryBuffers.size(); j< primaryBuffersSize; j++ ) {
                    VulkanSemaphore signalSemaphore;
                    if( i == batchDatasSize - 1 && j == primaryBuffersSize - 1 ) {
                        signalSemaphore = renderFinishedSemaphore;
                    } else {
                        signalSemaphore = deviceAllocator.createSemaphore();
                        semaphores.add( signalSemaphore );
                    }
                    if ( firstSubmit ) {
                        submitInfo = VulkanSubmitInfo.createSubmitInfo(
                            batchData.getPrimaryBuffers(),
                            initialWaitSemaphores,
                            signalSemaphore
                        );
                        firstSubmit = false;
                    } else {
                        submitInfo = VulkanSubmitInfo.createSubmitInfo(
                            batchData.getPrimaryBuffers(),
                            waitSemapore,
                            signalSemaphore
                        );
                    }
                    waitSemapore = signalSemaphore;
                    submitInfos.add( submitInfo );
                }
            }
        }
        return new VulkanRenderBatchDatasProcessedInfo( submitInfos, semaphores );
    }
    
    private VulkanRenderBatchData waitAndGet(
        Future< VulkanRenderBatchData > stageFuture,
        String stageName
    ) {
        try {
            return stageFuture.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            logger.log( Level.WARNING, "Exception while rendering  " + stageName + ".", ex );
        }
        return null;
    }
}
