#!/usr/bin/env bash
java \
--enable-preview \
-DApplicationClassName="com.firststory.firstoracle.templates.DualWindow" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner