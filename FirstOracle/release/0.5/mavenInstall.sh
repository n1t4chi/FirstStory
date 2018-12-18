#!/usr/bin/env bash
mvn install:install-file \
-DgroupId=com.FirstStory \
-DartifactId=FirstStory \
-Dversion=0.1 \
-Dpackaging=pom \
-Dfile=.\deploy\linux\FirstStory.pom.xml

mvn install:install-file \
-DgroupId=com.FirstStory.FirstOracle \
-DartifactId=FirstOracle \
-Dversion=0.5 \
-Dpackaging=pom \
-Dfile=.\deploy\linux\FirstOracle.pom.xml

mvn install:install-file \
-Dfile=.\deploy\linux\FirstOracle-Engine-0.5.jar \
-Djavadoc=.\javadoc\FirstOracle-Engine-0.5-javadoc.jar \
-Dsources=.\sources\FirstOracle-Engine-0.5-sources.jar \
-DgroupId=com.FirstStory.FirstOracle -DartifactId=FirstOracle-Engine \
-Dversion=0.5 \
-Dpackaging=jar \
-DpomFile=.\deploy\linux\FirstOracle-Engine-0.5.pom.xml

mvn install:install-file \
-Dfile=.\deploy\linux\FirstOracle-Impl-0.5.jar \
-Djavadoc=.\javadoc\FirstOracle-Impl-0.5-javadoc.jar \
-Dsources=.\sources\FirstOracle-Impl-0.5-sources.jar \
-DgroupId=com.FirstStory.FirstOracle -DartifactId=FirstOracle-Impl \
-Dversion=0.5 -Dpackaging=jar \
-DpomFile=.\deploy\linux\FirstOracle-Impl-0.5.pom.xml

mvn install:install-file \
-Dfile=.\deploy\linux\FirstOracle-Interfaces-0.5.jar \
-Djavadoc=.\javadoc\FirstOracle-Interfaces-0.5-javadoc.jar \
-Dsources=.\sources\FirstOracle-Interfaces-0.5-sources.jar \
-DgroupId=com.FirstStory.FirstOracle \
-DartifactId=FirstOracle-Interfaces \
-Dversion=0.5 \
-Dpackaging=jar \
-DpomFile=.\deploy\linux\FirstOracle-Interfaces-0.5.pom.xml
