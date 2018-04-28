/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanGraphicPipelineException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanPipelineLayoutException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanRenderPass;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.List;

/**
 * @author n1t4chi
 */
public class VulkanGraphicPipeline {
    
    private final VulkanPhysicalDevice device;
    private final VkPipelineDynamicStateCreateInfo dynamicStateCreateInfo;
    private long pipelineLayoutAddress = VK10.VK_NULL_HANDLE;
    private long renderPassAddress = VK10.VK_NULL_HANDLE;
    private long graphicsPipelineAddress = VK10.VK_NULL_HANDLE;
    
    VulkanGraphicPipeline(  VulkanPhysicalDevice device ) {
        this.device = device;
        dynamicStateCreateInfo = createDynamicStateCreateInfo();
    
    }
    
    void dispose() {
        if( graphicsPipelineAddress != VK10.VK_NULL_HANDLE ) {
            VK10.vkDestroyPipeline( device.getLogicalDevice(), graphicsPipelineAddress, null );
            graphicsPipelineAddress = VK10.VK_NULL_HANDLE;
        }
        if( pipelineLayoutAddress != VK10.VK_NULL_HANDLE ) {
            VK10.vkDestroyPipelineLayout( device.getLogicalDevice(), pipelineLayoutAddress, null );
            pipelineLayoutAddress = VK10.VK_NULL_HANDLE;
        }
        if( renderPassAddress != VK10.VK_NULL_HANDLE ) {
            VK10.vkDestroyRenderPass( device.getLogicalDevice(), renderPassAddress, null );
            renderPassAddress = VK10.VK_NULL_HANDLE;
        }
    }
    
    void update( VulkanSwapChain swapChain, List<VkPipelineShaderStageCreateInfo> shaderStages ) {
        dispose();
        pipelineLayoutAddress = createVulkanPipelineLayout();
        renderPassAddress = createRenderPass( swapChain );
        graphicsPipelineAddress = createGraphicPipeline( swapChain, shaderStages );
    }
    
    long getGraphicPipeline() {
        return graphicsPipelineAddress;
    }
    
    long getRenderPass() {
        return renderPassAddress;
    }
    
    private long createGraphicPipeline(
        VulkanSwapChain swapChain, List< VkPipelineShaderStageCreateInfo > shaderStages
    ) {
        VkGraphicsPipelineCreateInfo createInfo = createGraphicPipelineCreateInfo( swapChain, shaderStages );
        long[] graphicsPipelineAddress = new long[1];
        if ( VK10.vkCreateGraphicsPipelines( device.getLogicalDevice(),
            VK10.VK_NULL_HANDLE,
            VkGraphicsPipelineCreateInfo.calloc( 1 ).put( createInfo ).flip(),
            null,
            graphicsPipelineAddress
            ) != VK10.VK_SUCCESS
        ) {
            throw new CannotCreateVulkanGraphicPipelineException( device );
        }
        return graphicsPipelineAddress[0];
    }
    
    private VkGraphicsPipelineCreateInfo createGraphicPipelineCreateInfo(
        VulkanSwapChain swapChain, List< VkPipelineShaderStageCreateInfo > shaderStages
    ) {
        return VkGraphicsPipelineCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO )
            .pStages( createShaderStageCreateInfoBuffer(shaderStages) )
            .pVertexInputState( createVertexInputStateCreateInfo() )
            .pInputAssemblyState( createInputAssemblyStateCreateInfo() )
            .pViewportState( createViewportStateCreateInfo( swapChain ) )
            .pRasterizationState( createRasterizationStateCreateInfo() )
            .pMultisampleState( createMultisampleStateCreateInfo() )
            .pDepthStencilState( null )
            .pColorBlendState( createColourBlendStateCreateInfo() )
            .pDynamicState( null )
            .layout( pipelineLayoutAddress )
            .renderPass( renderPassAddress )
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
    
    private long createRenderPass( VulkanSwapChain swapChain ) {
        long[] address = new long[1];
        if ( VK10.vkCreateRenderPass(
                device.getLogicalDevice(), createRenderPassCreateInfo( swapChain ), null, address )
            != VK10.VK_SUCCESS
        ) {
            throw new CannotCreateVulkanRenderPass( device );
        }
        return address[0];
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
            .frontFace( VK10.VK_FRONT_FACE_CLOCKWISE )
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
            .pVertexBindingDescriptions( null )
            .pVertexAttributeDescriptions( null );
    }
    
    private long createVulkanPipelineLayout() {
        VkPipelineLayoutCreateInfo createInfo = VkPipelineLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO )
            .pSetLayouts( null )
            .pPushConstantRanges( null );
        
        long[] address = new long[1];
        if ( VK10.vkCreatePipelineLayout( device.getLogicalDevice(), createInfo, null, address )
            != VK10.VK_SUCCESS
        ) {
            throw new CannotCreateVulkanPipelineLayoutException( device );
        }
        return address[0];
    }
}
