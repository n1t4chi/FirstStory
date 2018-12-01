#!/usr/bin/env bash
java \
--enable-preview \
-DApplicationClassName="com.firststory.firstoracle.engine.templates.DualWindow" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner