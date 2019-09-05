#This is the base definition of the versions used by my projects.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies)
[![](https://jitpack.io/v/rapidpm/rapidpm-dependencies.svg)](https://jitpack.io/#rapidpm/rapidpm-dependencies)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c1133e6bd62d49d39c79c5b58d31c661)](https://app.codacy.com/app/sven-ruppert/rapidpm-dependencies?utm_source=github.com&utm_medium=referral&utm_content=RapidPM/rapidpm-dependencies&utm_campaign=badger)
![Libraries.io dependency status for GitHub repo](https://img.shields.io/librariesio/github/RapidPM/rapidpm-dependencies?style=plastic)
![Snyk Vulnerabilities for GitHub Repo](https://img.shields.io/snyk/vulnerabilities/github/RapidPM/rapidpm-dependencies)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


## Info
**Project is build with svenruppert/maven-3.6.1-adopt:1.8.212-04 docker image !!**


## Versioninfos

### NEXT
* TBD

### 04.01.11-RPM
* pitest 1.4.10
* added junit-platform-testkit to scope test
* vaadinXX switched from V14 to V15  - 15.0.0.alpha1
* cache2k update to v1.2.4.Final

### 04.01.10-RPM
* added java-faker to scope test
* vaadin v14.0.2

### 04.01.09-RPM
* Update to V14 Final
* Update to stagemonitor 0.89.0

### 04.01.08-RPM
* junit-platform-maven-plugin.timeout - default 300 [s]

### 04.01.07-RPM
* version updates
* added Google Truth as default dependency into scope test
* excluded generated NodeJS/ NPM things from lic check

### 04.01.06-RPM
* version updates
* config changes for vaadin dependencies

### 04.01.05-RPM
* version updates
* config changes for vaadin dependencies

### 04.01.04-RPM
* version updates
* added rapidpm-vaadin-dependencies for V08/V10/V14/Vxx

### 04.01.02-RPM
* replaced reflections with reflections8
* deleted module - reflections
* changed compile cycle for AnnotationProcessing
* replaced dom4j to org.dom4j
* kotlin version update

### 04.01.01-RPM
* version updates
* externalizing version definitions
* version updates
* smaller BugFixings
* reduced maven minimum version to 3.3.9 for compatibility reasons

### 04.01.00-RPM
* added development profile for NodeJS/NPM - for Vaadin 14 projects
* externalizing required maven version

To define your maven version you could override the property **maven-enforcer-plugin.version**.
The default is mostly the latest maven version.


## Properties
* **minimum-maven.version** - setting required maven version, default is 3.3.9
* **activateJavaOnly** - true = JDK only / false JDK plus Kotlin activated
* **vaadin-productionMode** - activated inside the vaadin-dependencies the production mode
* **vaadin-install-nodejs** - true or false (default)
    * frontend-maven-plugin.nodeVersion
    * frontend-maven-plugin.npmVersion
    * frontend-maven-plugin.installDirectory - default value - target
* **kotlin.compiler.jvmTarget** - default 1.8
* **kotlin.version** - default mostly latest version
* **kotlin.compiler.incremental** - default true


## Profiles

### _nodejs_npm - For NodeJS && NMP installation
Some Webprojects need NodeJS and NPM installed. Vaadin 14 Flow is one example.
To configure this you can use the following properties.

```xml
    <!--Vaadin-->
    <vaadin-productionMode>false</vaadin-productionMode>
    <vaadin-install-nodejs>true</vaadin-install-nodejs>
    <frontend-maven-plugin.nodeVersion>v4.6.0</frontend-maven-plugin.nodeVersion>
    <frontend-maven-plugin.npmVersion>2.15.9</frontend-maven-plugin.npmVersion>
    <frontend-maven-plugin.installDirectory>target</frontend-maven-plugin.installDirectory>
```



## switched to new version string format
To make search/replace easier, I started with a new version format.
04.00.05-RPM ( -SNAPSHOT). The x.y.z is used in the same way, as before, but added RPM
and leading zeros to make this format different from others.
With this it is less possible to mix/change version numbers from 
other dependencies. A **4.0.3** could be used from different 
dependencies. ;-) 

## Changes between 3.6.x and 3.7
There a re a few modules deactivated and removed.
* reflection : moved as module into dynamic-dependency-injection project
* jdbc* : all deactivated and removed, code is available in the history

## Version 3.* 
This will be the version based on **Java8**
I will create a LTD branch for this
The  version 4.* will be based on the JDK 10/11 (as soon as 11 is released)
JDK 11 will be a LTS Version again.

## Version 4.*
With the beginning of this version
the project will be compiled with the actual JDK but on source level Java 8.




