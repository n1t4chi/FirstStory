#!/usr/bin/env bash
java \
--enable-preview \
-Doptimised=false \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.templates.optimisation.App2D