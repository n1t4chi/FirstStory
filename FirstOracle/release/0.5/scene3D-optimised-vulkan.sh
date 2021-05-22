#!/usr/bin/env bash
java \
-Doptimised=true \
-DRenderingFrameworkClassName="com.firststory.firstoracle.vulkan.VulkanFrameworkProvider" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.templates.optimisation.App3D