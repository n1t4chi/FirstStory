#!/usr/bin/env bash
java \
-Doptimised=false \
-DRenderingFrameworkClassName="com.firststory.firstoracle.vulkan.VulkanFrameworkProvider" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.templates.optimisation.App2D