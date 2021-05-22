#!/usr/bin/env bash
java \
-Doptimised=true \
-cp "./deploy/linux/*:./deploy/linux/lib/*" \
com.firststory.firstoracle.templates.optimisation.App3D