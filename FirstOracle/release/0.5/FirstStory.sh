#!/usr/bin/env bash
java \
--enable-preview \
-DApplicationClassName="com.firststory.firstslave.Main" \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.Runner