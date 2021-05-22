#!/usr/bin/env bash
java \
-DApplicationClassName="com.firststory.firstoracle.templates.GlfwApplication2D" \
-DRenderingFrameworkClassName="com.firststory.firstoracle.vulkan.VulkanFrameworkProvider" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner