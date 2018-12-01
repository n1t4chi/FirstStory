#!/usr/bin/env bash
java \
--enable-preview \
-DApplicationClassName="com.firststory.firstoracle.templates.GlfwApplication2D" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner