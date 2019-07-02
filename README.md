#This is the base definition of the versions used by my projects.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies)
[![](https://jitpack.io/v/rapidpm/rapidpm-dependencies.svg)](https://jitpack.io/#rapidpm/rapidpm-dependencies)


[![](https://jitci.com/gh/RapidPM/rapidpm-dependencies/svg)](https://jitci.com/gh/RapidPM/rapidpm-dependencies)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c1133e6bd62d49d39c79c5b58d31c661)](https://app.codacy.com/app/sven-ruppert/rapidpm-dependencies?utm_source=github.com&utm_medium=referral&utm_content=RapidPM/rapidpm-dependencies&utm_campaign=badger)


## Info
**Project is build with svenruppert/maven-3.6.1-adopt:1.8.212-04 docker image !!**


## Versioninfos

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
* **activateNodeJS_NPM** - true or false (default)
    * frontend-maven-plugin.nodeVersion
    * frontend-maven-plugin.npmVersion
* **kotlin.compiler.jvmTarget** - default 8
* **kotlin.version** - default mostly latest version
* **kotlin.compiler.incremental** - default true


## Profiles

### _nodejs_npm - For NodeJS && NMP installation
Some Webprojects need NodeJS and NPM installed. Vaadin 14 Flow is one example.
To configure this you can use the following properties.

```xml
    <activateNodeJS_NPM>false</activateNodeJS_NPM>
    <frontend-maven-plugin.nodeVersion>v4.6.0</frontend-maven-plugin.nodeVersion>
    <frontend-maven-plugin.npmVersion>2.15.9</frontend-maven-plugin.npmVersion>
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




