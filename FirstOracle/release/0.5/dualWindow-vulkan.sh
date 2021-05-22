#!/usr/bin/env bash
java \
-DApplicationClassName="com.firststory.firstoracle.templates.DualWindow" \
-DRenderingFrameworkClassName="com.firststory.firstoracle.vulkan.VulkanFrameworkProvider" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner