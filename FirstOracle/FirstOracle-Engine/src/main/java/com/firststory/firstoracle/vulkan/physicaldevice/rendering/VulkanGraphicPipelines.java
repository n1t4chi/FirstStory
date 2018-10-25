/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDepthResources;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotCreateVulkanGraphicPipelineException;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotCreateVulkanPipelineLayoutException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.*;

/**
 * @author n1t4chi
 */
public class VulkanGraphicPipelines {
    
    private static final int ATTRIBUTES_POSITION = 3;
    private static final int ATTRIBUTES_UV = 2;
    private static final int ATTRIBUTES_COLOUR = 4;
    private static final int VERTEX_POSITION_DATA_SIZE = ATTRIBUTES_POSITION * BYTE_SIZE_FLOAT;
    private static final int VERTEX_UVMAP_DATA_SIZE = ATTRIBUTES_UV * BYTE_SIZE_FLOAT;
    private static final int VERTEX_COLOUR_DATA_SIZE = ATTRIBUTES_COLOUR * BYTE_SIZE_FLOAT;
    private static final int UNIFORM_COUNT_VEC4 = 5;
    private static final int UNIFORM_COUNT_INT = 1;
    private static final int UNIFORM_DATA_SIZE = BYTE_SIZE_FLOAT * FLOAT_SIZE_VEC_4F * UNIFORM_COUNT_VEC4 + BYTE_SIZE_INT * UNIFORM_COUNT_INT ;
    private static final int ATTRIBUTE_UNIFORM_SIZE = UNIFORM_COUNT_VEC4 + UNIFORM_COUNT_INT;
    private static final int[] DYNAMIC_STATE_FLAGS = new int[]{ VK10.VK_DYNAMIC_STATE_LINE_WIDTH };
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    
    private final VulkanAddress pipelineLayout = VulkanAddress.createNull();
    
    private final Pipeline backgroundPipeline;
    private final Pipeline overlayPipeline;
    private final Pipeline scene2DPipeline;
    private final Pipeline scene3DPipeline;
    private final List< Pipeline > pipelines;
    
    private final int topologyType;
    
    public VulkanGraphicPipelines( VulkanDeviceAllocator allocator, VulkanPhysicalDevice device, int topologyType ) {
        this.allocator = allocator;
        this.device = device;
        backgroundPipeline = new Pipeline( true );
        overlayPipeline = new Pipeline( false );
        scene2DPipeline = new Pipeline( false );
        scene3DPipeline = new Pipeline( false );
        pipelines = Arrays.asList(
            backgroundPipeline,
            overlayPipeline,
            scene2DPipeline,
            scene3DPipeline
        );
        this.topologyType = topologyType;
    }
    
    public void dispose() {
        allocator.deregisterGraphicPipelines( this );
    }
    
    public void disposeUnsafe() {
        pipelines.forEach( Pipeline::dispose );
        
        if( pipelineLayout.isNotNull() ) {
            VK10.vkDestroyPipelineLayout( device.getLogicalDevice(), pipelineLayout.getValue(), null );
            pipelineLayout.setNull();
        }
    }
    
    public void update(
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages,
        VulkanDepthResources depthResources,
        VulkanAddress descriptorSet
    ) {
        disposeUnsafe();
        updateVulkanPipelineLayout( descriptorSet );
        pipelines.forEach( pipeline ->  pipeline.update( swapChain, shaderStages, depthResources ) );
    }
    
    public Pipeline getBackgroundPipeline() {
        return backgroundPipeline;
    }
    
    public Pipeline getScene2DPipeline() {
        return scene2DPipeline;
    }
    
    public Pipeline getScene3DPipeline() {
        return scene2DPipeline;
    }
    
    public Pipeline getOverlayPipeline() {
        return overlayPipeline;
    }
    
    VulkanAddress getPipelineLayout() {
        return pipelineLayout;
    }
    
    private void createGraphicPipeline(
        VulkanAddress graphicsPipeline,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages
    ) {
    
        VulkanHelper.updateAddress( graphicsPipeline,
            ( address ) -> VK10.vkCreateGraphicsPipelines( device.getLogicalDevice(),
                VK10.VK_NULL_HANDLE,
                VkGraphicsPipelineCreateInfo.calloc( 1 )
                    .put( 0, createGraphicPipelineCreateInfo( swapChain, shaderStages, renderPass ) ),
                null,
                address
            ),
            resultCode -> new CannotCreateVulkanGraphicPipelineException( device, resultCode )
        );
    }
    
    private VkGraphicsPipelineCreateInfo createGraphicPipelineCreateInfo(
        VulkanSwapChain swapChain,
        List< VkPipelineShaderStageCreateInfo > shaderStages,
        VulkanRenderPass renderPass
    ) {
        return VkGraphicsPipelineCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO )
            .pStages( createShaderStageCreateInfoBuffer( shaderStages ) )
            .pVertexInputState( createVertexInputStateCreateInfo() )
            .pInputAssemblyState( createInputAssemblyStateCreateInfo() )
            .pViewportState( createViewportStateCreateInfo( swapChain ) )
            .pRasterizationState( createRasterizationStateCreateInfo() )
            .pMultisampleState( createMultisampleStateCreateInfo() )
            .pDepthStencilState( createDepthStencilState() )
            .pColorBlendState( createColourBlendStateCreateInfo() )
            .pDynamicState( createDynamicStateCreateInfo() )
            .layout( pipelineLayout.getValue() )
            .renderPass( renderPass.getAddress().getValue() )
            .subpass( 0 )
            .basePipelineHandle( VK10.VK_NULL_HANDLE )
            .basePipelineIndex( -1 )
        ;
    }
    
    private VkPipelineDepthStencilStateCreateInfo createDepthStencilState() {
        return VkPipelineDepthStencilStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO )
            .depthTestEnable( true )
            .depthWriteEnable( true )
            .depthCompareOp( VK10.VK_COMPARE_OP_LESS_OR_EQUAL )
            .depthBoundsTestEnable( false )
            .minDepthBounds( -1f )
            .maxDepthBounds( 1f )
            .stencilTestEnable( false )
            .front( VkStencilOpState.create() )
            .back( VkStencilOpState.create() )
        ;
    }
    
    private VkPipelineShaderStageCreateInfo.Buffer createShaderStageCreateInfoBuffer(
        List<VkPipelineShaderStageCreateInfo> shaderStages
    ) {
        var shaderStagesBuffer =
            VkPipelineShaderStageCreateInfo.calloc( shaderStages.size() );
        shaderStages.forEach( shaderStagesBuffer::put );
        shaderStagesBuffer.flip();
        return shaderStagesBuffer;
    }
    
    private VkPipelineDynamicStateCreateInfo createDynamicStateCreateInfo() {
        var flagsBuffer = MemoryUtil.memAllocInt( DYNAMIC_STATE_FLAGS.length );
        flagsBuffer.put( DYNAMIC_STATE_FLAGS ).flip();
        return VkPipelineDynamicStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO )
            .pDynamicStates( flagsBuffer);
    }
    
    private VkPipelineColorBlendStateCreateInfo createColourBlendStateCreateInfo() {
        return VkPipelineColorBlendStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO )
            .logicOpEnable( false )
            .logicOp( VK10.VK_LOGIC_OP_COPY )
            .pAttachments( VkPipelineColorBlendAttachmentState.calloc( 1 )
                .put( 0, createColourBlendAttachmentState() )
            )
            .blendConstants( 0, 0f )
            .blendConstants( 1, 0f )
            .blendConstants( 2, 0f )
            .blendConstants( 3, 0f );
    }
    
    private VkPipelineColorBlendAttachmentState createColourBlendAttachmentState() {
        return VkPipelineColorBlendAttachmentState.create()
            .colorWriteMask(
                VK10.VK_COLOR_COMPONENT_A_BIT | VK10.VK_COLOR_COMPONENT_R_BIT | VK10.VK_COLOR_COMPONENT_G_BIT |
                    VK10.VK_COLOR_COMPONENT_B_BIT )
            .blendEnable( true )
            .srcColorBlendFactor( VK10.VK_BLEND_FACTOR_SRC_ALPHA )
            .dstColorBlendFactor( VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA )
            .colorBlendOp( VK10.VK_BLEND_OP_ADD )
            .srcAlphaBlendFactor( VK10.VK_BLEND_FACTOR_ONE )
            .dstAlphaBlendFactor( VK10.VK_BLEND_FACTOR_ZERO )
            .alphaBlendOp( VK10.VK_BLEND_OP_ADD );
    }
    
    private VkPipelineMultisampleStateCreateInfo createMultisampleStateCreateInfo() {
        return VkPipelineMultisampleStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO )
            .sampleShadingEnable( false )
            .rasterizationSamples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .minSampleShading( 1f )
            .pSampleMask( null )
            .alphaToCoverageEnable( false )
            .alphaToOneEnable( false );
    }
    
    private VkPipelineRasterizationStateCreateInfo createRasterizationStateCreateInfo() {
        return VkPipelineRasterizationStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO )
            .depthClampEnable( false )
            .rasterizerDiscardEnable( false )
            .polygonMode( VK10.VK_POLYGON_MODE_FILL )
            .lineWidth( 1f )
            .cullMode( VK10.VK_CULL_MODE_BACK_BIT )
            .frontFace( VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE )
            .depthBiasEnable( false )
            .depthBiasConstantFactor( 0f )
            .depthBiasClamp( 0f )
            .depthBiasSlopeFactor( 0f );
    }
    
    private VkPipelineViewportStateCreateInfo createViewportStateCreateInfo( VulkanSwapChain swapChain ) {
        return VkPipelineViewportStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO )
            .viewportCount( 1 )
            .pViewports( VkViewport.create( 1 ).put( createViewport( swapChain ) ).flip() )
            .scissorCount( 1 )
            .pScissors( VkRect2D.create( 1 ).put( createScissor( swapChain ) ).flip() );
    }
    
    private VkRect2D createScissor( VulkanSwapChain swapChain ) {
        return VkRect2D.create().offset( VkOffset2D.create().set( 0, 0 ) ).extent( swapChain.getExtent() );
    }
    
    private VkViewport createViewport( VulkanSwapChain swapChain ) {
        return VkViewport.create()
            .x( 0f )
            .y( 0f )
            .width( swapChain.getWidth() )
            .height( swapChain.getHeight() )
            .minDepth( 0f )
            .maxDepth( 1f );
    }
    
    private VkPipelineInputAssemblyStateCreateInfo createInputAssemblyStateCreateInfo() {
        return VkPipelineInputAssemblyStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO )
            .topology( topologyType )
            .primitiveRestartEnable( false );
    }
    
    private VkPipelineVertexInputStateCreateInfo createVertexInputStateCreateInfo() {
        var attributeDescriptions = VkVertexInputAttributeDescription
            .create( 3 + ATTRIBUTE_UNIFORM_SIZE )
            .put( 0, createPositionAttributeDescription() )
            .put( 1, createUvMapAttributeDescription() )
            .put( 2, createColourAttributeDescription() )
        ;
        for( var i=0; i< UNIFORM_COUNT_VEC4; i ++ ) {
            attributeDescriptions.put( 3 + i, createVec4UniformDataAttributeDescription( i ) );
        }
        for( var i=0; i< UNIFORM_COUNT_INT; i ++ ) {
            attributeDescriptions.put( 3 + UNIFORM_COUNT_VEC4 + i, createIntUniformDataAttributeDescription( i ) );
        }
        
        
        return VkPipelineVertexInputStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pVertexBindingDescriptions(
                VkVertexInputBindingDescription.create( 4 )
                    .put( 0, createVertexBindingDescription( 0, VERTEX_POSITION_DATA_SIZE ) )
                    .put( 1, createVertexBindingDescription( 1, VERTEX_UVMAP_DATA_SIZE ) )
                    .put( 2, createVertexBindingDescription( 2, VERTEX_COLOUR_DATA_SIZE ) )
                    .put( 3, createInstanceBindingDescription( 3, UNIFORM_DATA_SIZE ) )
            )
            .pVertexAttributeDescriptions( attributeDescriptions );
    }
    
    private VkVertexInputBindingDescription createInstanceBindingDescription( int binding, int dataSize ) {
        return VkVertexInputBindingDescription.create()
            .binding( binding )
            .stride( dataSize ) //todo
            .inputRate( VK10.VK_VERTEX_INPUT_RATE_INSTANCE );
    }
    
    private VkVertexInputBindingDescription createVertexBindingDescription( int binding, int dataSize ) {
        return VkVertexInputBindingDescription.create()
            .binding( binding )
            .stride( dataSize ) //todo
            .inputRate( VK10.VK_VERTEX_INPUT_RATE_VERTEX );
    }
    
    /**
     * todo: for shaders
     * Description for colour shader input
     * @return colour description
     */
    private VkVertexInputAttributeDescription createPositionAttributeDescription() {
        return VkVertexInputAttributeDescription.create()
            .binding( 0 )
            .location( 0 )
            .format( VK10.VK_FORMAT_R32G32B32_SFLOAT )
            .offset( 0 ); //todo
    }
    
    /**
     * todo: for shaders
     * Description for uv map shader input
     * @return position description
     */
    private VkVertexInputAttributeDescription createUvMapAttributeDescription() {
        return VkVertexInputAttributeDescription.create()
            .binding( 1 )
            .location( 1 )
            .format( VK10.VK_FORMAT_R32G32_SFLOAT )
            .offset( 0 ); //todo
    }
    
    /**
     * todo: for shaders
     * Description for position shader input
     * @return position description
     */
    private VkVertexInputAttributeDescription createColourAttributeDescription() {
        return VkVertexInputAttributeDescription.create()
            .binding( 2 )
            .location( 2 )
            .format( VK10.VK_FORMAT_R32G32B32A32_SFLOAT )
            .offset( 0 ); //todo
    }
    
    /**
     * todo: for shaders
     * Description for uv map shader input
     * @return position description
     */
    private VkVertexInputAttributeDescription createVec4UniformDataAttributeDescription( int location ) {
        return VkVertexInputAttributeDescription.create()
            .binding( 3 )
            .location( 4 + location )
            .format( VK10.VK_FORMAT_R32G32B32A32_SFLOAT )
            .offset( FLOAT_SIZE_VEC_4F * BYTE_SIZE_FLOAT * location );
    }
    
    /**
     * todo: for shaders
     * Description for uv map shader input
     * @return position description
     */
    private VkVertexInputAttributeDescription createIntUniformDataAttributeDescription( int location ) {
        return VkVertexInputAttributeDescription.create()
            .binding( 3 )
            .location( 4 + UNIFORM_COUNT_VEC4 + location )
            .format( VK10.VK_FORMAT_R32_SINT )
            .offset( FLOAT_SIZE_VEC_4F * BYTE_SIZE_FLOAT * ( UNIFORM_COUNT_VEC4 + location ) );
    }
    
    private void updateVulkanPipelineLayout( VulkanAddress descriptorSet ) {
        var createInfo = VkPipelineLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO )
            .pSetLayouts( MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSet.getValue() ) )
            .pPushConstantRanges( null );
        
        VulkanHelper.updateAddress( pipelineLayout,
            (address) -> VK10.vkCreatePipelineLayout( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanPipelineLayoutException( device, resultCode )
        );
    }
    
    public class Pipeline {
        private final VulkanAddress graphicsPipeline = VulkanAddress.createNull();
        private final VulkanRenderPass renderPass;
        private final boolean isBackground;
    
        private Pipeline( boolean isBackground ) {
            this.isBackground = isBackground;
            this.renderPass = new VulkanRenderPass( device );
        }
        
        private void update(
            VulkanSwapChain swapChain,
            List< VkPipelineShaderStageCreateInfo > shaderStages,
            VulkanDepthResources depthResources
        ) {
            renderPass.updateRenderPass( swapChain, depthResources, isBackground );
            createGraphicPipeline( graphicsPipeline, renderPass, swapChain, shaderStages );;
        }
    
        public VulkanAddress getGraphicPipeline() {
            return graphicsPipeline;
        }
    
        public VulkanRenderPass getRenderPass() {
            return renderPass;
        }
        
        public void dispose() {
            renderPass.dispose();
            if( graphicsPipeline.isNotNull() ) {
                VK10.vkDestroyPipeline( device.getLogicalDevice(), graphicsPipeline.getValue(), null );
                graphicsPipeline.setNull();
            }
        }
    }
}
