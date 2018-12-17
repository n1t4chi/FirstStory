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
    
    private final ExecutorService executorService;
    private final ExecutorService executorService2;
    
    private final Deque< VulkanDataBuffer > availableDataBuffers = new LinkedList<>();
    
    private RenderParameters renderParameters;
    
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
    
    public void setUpSingleRender(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        VulkanImageIndex imageIndex,
        VulkanSwapChain swapChain,
        List< VulkanTransferCommandPool > transferCommandPools
    ) {
        
        availableDataBuffers.clear();
        availableDataBuffers.addAll( dataBuffers );
        
        renderParameters = new RenderParameters(
            trianglePipelines,
            linePipelines,
            swapChain,
            imageIndex,
            imageIndex.getFrameBuffer(),
            transferCommandPools,
            device.getAllocator()
        );
    }
    
    @Override
    public void renderBackground( Camera2D camera, Colour backgroundColour, List< RenderData > renderDatas ) {
        renderParameters.setBackgroundColour( backgroundColour );
        var background = renderParameters.getVulkanStages().getBackground();
        background.addRenderDatas( renderDatas );
        background.setCamera( camera );
    
        renderParameters.setBackgroundFuture( executorService.submit( createWorker(
            renderParameters,
            background,
            executorService2
        ) ) );
    }
    
    @Override
    public void renderScene2D( Camera2D camera, List< RenderData > renderDatas ) {
        var scene2D = renderParameters.getVulkanStages().getScene2D();
        scene2D.addRenderDatas( renderDatas );
        scene2D.setCamera( camera );
        renderParameters.setScene2DFuture( executorService.submit( createWorker(
            renderParameters,
            scene2D,
            executorService2
        ) ) );
    }
    
    @Override
    public void renderScene3D( Camera3D camera, List< RenderData > renderDatas ) {
        var scene3D = renderParameters.getVulkanStages().getScene3D();
        scene3D.addRenderDatas( renderDatas );
        scene3D.setCamera( camera );
        renderParameters.setScene3DFuture( executorService.submit( createWorker(
            renderParameters,
            scene3D,
            executorService2
        ) ) );
    }
    
    @Override
    public void renderOverlay( Camera2D camera, List< RenderData > renderDatas ) {
        var overlay = renderParameters.getVulkanStages().getOverlay();
        overlay.addRenderDatas( renderDatas );
        overlay.setCamera( camera );
        renderParameters.setOverlayFuture( executorService.submit( createWorker(
            renderParameters,
            overlay,
            executorService2
        ) ) );
    }
    Future< ? > submit = null;
    
    public void tearDownSingleRender() {
        if( submit != null )
        {
            try {
                submit.get();
            } catch ( InterruptedException e ) {
                throw new RuntimeException( e );
            } catch ( Exception e ) {
                logger.log( Level.WARNING, "exception during execution of rendering", e );
            }
            submit = null;
        }
        var parameters = renderParameters;
        var backgroundBatchDatas = waitAndGet( parameters.getBackgroundFuture(), BACKGROUND );
        var scene2dBatchDatas = waitAndGet( parameters.getScene2DFuture(), SCENE_2D );
        var scene3dBatchDatas = waitAndGet( parameters.getScene3DFuture(), SCENE_3D );
        var overlayBatchDatas = waitAndGet( parameters.getOverlayFuture(), OVERLAY );
        var imageAvailableSemaphore = parameters.getImageIndex().getImageAvailableSemaphore();
        var renderFinishedSemaphore = parameters.getImageIndex().getRenderFinishedSemaphore();
        List< VulkanSemaphore > initialWaitSemaphores = new ArrayList<>(  );
        for ( var transferCommandPool : parameters.getTransferCommandPools() ) {
            initialWaitSemaphores.addAll( transferCommandPool.executeTransfers() );
        }
        initialWaitSemaphores.add( imageAvailableSemaphore );
    
        submit = executorService.submit( () -> {
            List< VulkanRenderBatchData > batchDatas = new ArrayList<>();
            batchDatas.add( backgroundBatchDatas );
            batchDatas.add( scene2dBatchDatas );
            batchDatas.add( scene3dBatchDatas );
            batchDatas.add( overlayBatchDatas );
            batchDatas.removeIf( Objects::isNull );
            
            var processedInfo = processBatchDatas( parameters.getAllocator(),
                batchDatas,
                initialWaitSemaphores,
                renderFinishedSemaphore
            );
            
            var fence = parameters.getAllocator().createFence();
            
            device.getGraphicQueueFamily().submit(
                fence,
                processedInfo.getSubmitInfos()
            );
            
            parameters.getSwapChain().presentQueue( parameters.getImageIndex() );
            
            fence.executeWhenFinishedThenDispose( () -> {
                batchDatas.forEach( VulkanRenderBatchData::dispose );
                initialWaitSemaphores.forEach( VulkanSemaphore::finishedWait );
                processedInfo.getSemaphores().forEach( semaphore -> {
                    semaphore.finishedSignal();
                    semaphore.finishedWait();
                } );
                renderFinishedSemaphore.finishedSignal();
            } );
        } );
    }
    
    private VulkanRenderStageWorker createWorker(
        RenderParameters renderParameters,
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
            renderParameters.getSwapChain(),
            renderParameters.getFrameBuffer(),
            renderParameters.getBackgroundColour(),
            renderParameters.getLinePipelines(),
            renderParameters.getTrianglePipelines()
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
