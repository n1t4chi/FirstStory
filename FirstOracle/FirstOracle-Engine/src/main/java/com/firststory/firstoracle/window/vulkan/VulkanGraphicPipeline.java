/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanGraphicPipelineException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanPipelineLayoutException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanRenderPassException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.List;

/**
 * @author n1t4chi
 */
class VulkanGraphicPipeline {
    
    private static final int ATTRIBUTES_POSITION = 3;
    private static final int ATTRIBUTES_UV = 2;
    private static final int ATTRIBUTES_COLOUR = 4;
    private static final int FLOAT_SIZE = 4;
    private static final int ATTRIBUTES = ATTRIBUTES_POSITION + ATTRIBUTES_COLOUR;
    private static final int VERTEX_DATA_SIZE = ATTRIBUTES * FLOAT_SIZE;
    private static final int VERTEX_POSITION_DATA_SIZE = ATTRIBUTES_POSITION * FLOAT_SIZE;
    private static final int VERTEX_UVMAP_DATA_SIZE = ATTRIBUTES_UV * FLOAT_SIZE;
    private static final int VERTEX_COLOUR_DATA_SIZE = ATTRIBUTES_COLOUR * FLOAT_SIZE;
    
    private final VulkanPhysicalDevice device;
    private final VkPipelineDynamicStateCreateInfo dynamicStateCreateInfo;
    private VulkanAddress pipelineLayout = VulkanAddress.createNull();
    private VulkanAddress renderPass = VulkanAddress.createNull();
    private VulkanAddress graphicsPipeline = VulkanAddress.createNull();
    
    VulkanGraphicPipeline(  VulkanPhysicalDevice device ) {
        this.device = device;
        dynamicStateCreateInfo = createDynamicStateCreateInfo();
    }
    
    void dispose() {
        if( !graphicsPipeline.isNull() ) {
            VK10.vkDestroyPipeline( device.getLogicalDevice(), graphicsPipeline.getValue(), null );
            graphicsPipeline.setNull();
        }
        if( !pipelineLayout.isNull() ) {
            VK10.vkDestroyPipelineLayout( device.getLogicalDevice(), pipelineLayout.getValue(), null );
            pipelineLayout.setNull();
        }
        if( !renderPass.isNull() ) {
            VK10.vkDestroyRenderPass( device.getLogicalDevice(), renderPass.getValue(), null );
            renderPass.setNull();
        }
    }
    
    void update(
        VulkanSwapChain swapChain, List<VkPipelineShaderStageCreateInfo> shaderStages
    ) {
        dispose();
        updateVulkanPipelineLayout();
        updateRenderPass( swapChain );
        createGraphicPipeline( swapChain, shaderStages );
    }
    
    VulkanAddress getPipelineLayout() {
        return pipelineLayout;
    }
    
    VulkanAddress getGraphicPipeline() {
        return graphicsPipeline;
    }
    
    VulkanAddress getRenderPass() {
        return renderPass;
    }
    
    private void createGraphicPipeline(
        VulkanSwapChain swapChain, List< VkPipelineShaderStageCreateInfo > shaderStages
    ) {
        VkGraphicsPipelineCreateInfo createInfo =
            createGraphicPipelineCreateInfo( swapChain, shaderStages );

        VulkanHelper.updateAddress( graphicsPipeline,
            ( address ) -> VK10.vkCreateGraphicsPipelines( device.getLogicalDevice(),
                VK10.VK_NULL_HANDLE,
                VkGraphicsPipelineCreateInfo.calloc( 1 ).put( createInfo ).flip(),
                null,
                address
            ),
            resultCode -> new CannotCreateVulkanGraphicPipelineException( device, resultCode )
        );
    }
    
    private VkGraphicsPipelineCreateInfo createGraphicPipelineCreateInfo(
        VulkanSwapChain swapChain, List< VkPipelineShaderStageCreateInfo > shaderStages
    ) {
        return VkGraphicsPipelineCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO )
            .pStages( createShaderStageCreateInfoBuffer( shaderStages ) )
            .pVertexInputState( createVertexInputStateCreateInfo() )
            .pInputAssemblyState( createInputAssemblyStateCreateInfo() )
            .pViewportState( createViewportStateCreateInfo( swapChain ) )
            .pRasterizationState( createRasterizationStateCreateInfo() )
            .pMultisampleState( createMultisampleStateCreateInfo() )
            .pDepthStencilState( null )
            .pColorBlendState( createColourBlendStateCreateInfo() )
            .pDynamicState( null )
            .layout( pipelineLayout.getValue() )
            .renderPass( renderPass.getValue() )
            .subpass( 0 )
            .basePipelineHandle( VK10.VK_NULL_HANDLE )
            .basePipelineIndex( -1 )
        ;
    }
    
    private VkPipelineShaderStageCreateInfo.Buffer createShaderStageCreateInfoBuffer(
        List<VkPipelineShaderStageCreateInfo> shaderStages
    ) {
        VkPipelineShaderStageCreateInfo.Buffer shaderStagesBuffer = VkPipelineShaderStageCreateInfo.calloc( shaderStages
            .size() );
        shaderStages.forEach( shaderStagesBuffer::put );
        shaderStagesBuffer.flip();
        return shaderStagesBuffer;
    }
    
    private void updateRenderPass( VulkanSwapChain swapChain ) {
        VulkanHelper.updateAddress( renderPass,
            (address) -> VK10.vkCreateRenderPass(
                device.getLogicalDevice(), createRenderPassCreateInfo( swapChain ), null, address ),
            resultCode -> new CannotCreateVulkanRenderPassException( device, resultCode )
        );
    }
    
    private VkRenderPassCreateInfo createRenderPassCreateInfo( VulkanSwapChain swapChain ) {
        return VkRenderPassCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO )
            .pAttachments( VkAttachmentDescription.calloc( 1 ).put( createColourAttachmentDescription( swapChain ) ).flip() )
            .pSubpasses( VkSubpassDescription.create( 1 ).put( createSubpassDescription() ).flip() )
            .pDependencies( VkSubpassDependency.calloc( 1 ).put( createSubpassDependency() ).flip() );
    }
    
    private VkSubpassDependency createSubpassDependency() {
        return VkSubpassDependency.create()
            .srcSubpass( VK10.VK_SUBPASS_EXTERNAL )
            .dstSubpass( 0 )
            .srcStageMask( VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT )
            .dstStageMask( VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT )
            .srcAccessMask( 0 )
            .dstAccessMask( VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT );
    }
    
    private VkSubpassDescription createSubpassDescription() {
        return VkSubpassDescription.create()
            .pipelineBindPoint( VK10.VK_PIPELINE_BIND_POINT_GRAPHICS )
            .colorAttachmentCount( 1 )
            .pColorAttachments( VkAttachmentReference.create( 1 ).put( createColourAttachmentReference() ).flip() );
    }
    
    private VkAttachmentReference createColourAttachmentReference() {
        return VkAttachmentReference.create().attachment( 0 ).layout( VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL );
    }
    
    private VkAttachmentDescription createColourAttachmentDescription( VulkanSwapChain swapChain ) {
        return VkAttachmentDescription.create()
            .format( swapChain.getImageFormat() )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .loadOp( VK10.VK_ATTACHMENT_LOAD_OP_CLEAR )
            .storeOp( VK10.VK_ATTACHMENT_STORE_OP_STORE )
            .stencilLoadOp( VK10.VK_ATTACHMENT_LOAD_OP_CLEAR )
            .stencilStoreOp( VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE )
            .initialLayout( VK10.VK_IMAGE_LAYOUT_UNDEFINED )
            .finalLayout( KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR );
    }
    
    private VkPipelineDynamicStateCreateInfo createDynamicStateCreateInfo() {
        return VkPipelineDynamicStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO )
            .pDynamicStates( IntBuffer.wrap( new int[]{
                VK10.VK_DYNAMIC_STATE_VIEWPORT, VK10.VK_DYNAMIC_STATE_LINE_WIDTH
            } ) );
    }
    
    private VkPipelineColorBlendStateCreateInfo createColourBlendStateCreateInfo() {
        return VkPipelineColorBlendStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO )
            .logicOpEnable( false )
            .logicOp( VK10.VK_LOGIC_OP_COPY )
            .pAttachments( VkPipelineColorBlendAttachmentState.calloc( 1 ).put( createColourBlendAttachmentState() ).flip() )
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
            .topology( VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST )
            .primitiveRestartEnable( false );
    }
    
    private VkPipelineVertexInputStateCreateInfo createVertexInputStateCreateInfo() {
        
        return VkPipelineVertexInputStateCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pVertexBindingDescriptions(
                VkVertexInputBindingDescription.create( 3 )
                    .put( 0, createVertexBindingDescription( 0, VERTEX_POSITION_DATA_SIZE ) )
                    .put( 1, createVertexBindingDescription( 1, VERTEX_UVMAP_DATA_SIZE ) )
                    .put( 2, createVertexBindingDescription( 2, VERTEX_COLOUR_DATA_SIZE ) )
            )
            .pVertexAttributeDescriptions(
                VkVertexInputAttributeDescription.create( 3 )
                    .put( createPositionAttributeDescription() )
                    .put( createColourAttributeDescription() )
                    .put( createUvMapAttributeDescription() )
                    .flip()
            );
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
    private VkVertexInputAttributeDescription createUvMapAttributeDescription() {
        return VkVertexInputAttributeDescription.create()
            .binding( 1 )
            .location( 1 )
            .format( VK10.VK_FORMAT_R32G32_SFLOAT )
            .offset( 0 ); //todo
    }
    
    private void updateVulkanPipelineLayout() {
        VkPipelineLayoutCreateInfo createInfo = VkPipelineLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO )
            .pSetLayouts( MemoryUtil.memAllocLong( 1 ).put( 0, device.getDescriptorSetLayout().getValue() ) )
            .pPushConstantRanges( null );
        
        VulkanHelper.updateAddress( pipelineLayout,
            (address) -> VK10.vkCreatePipelineLayout( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanPipelineLayoutException( device, resultCode )
        );
    }
}
