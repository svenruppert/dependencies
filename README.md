# This is the base definition of the versions used by my projects.

[//]: # ()
[//]: # ([![Maven Central]&#40;https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies/badge.svg&#41;]&#40;https://maven-badges.herokuapp.com/maven-central/org.rapidpm/rapidpm-dependencies&#41;)
[![](https://jitpack.io/v/svenruppert/dependencies.svg)](https://jitpack.io/#svenruppert/dependencies)

[//]: # ()
[//]: # ([![Codacy Badge]&#40;https://api.codacy.com/project/badge/Grade/c1133e6bd62d49d39c79c5b58d31c661&#41;]&#40;https://app.codacy.com/app/sven-ruppert/rapidpm-dependencies?utm_source=github.com&utm_medium=referral&utm_content=RapidPM/rapidpm-dependencies&utm_campaign=badger&#41;)

[//]: # (![Libraries.io dependency status for GitHub repo]&#40;https://img.shields.io/librariesio/github/RapidPM/rapidpm-dependencies?style=plastic&#41;)

[//]: # (![Snyk Vulnerabilities for GitHub Repo]&#40;https://img.shields.io/snyk/vulnerabilities/github/RapidPM/rapidpm-dependencies&#41;)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Info
Project is build via JitPack

## Version's
### 05.00.04-SRU
updated plugins and minimum maven version to 3.9.6

### 05.00.03-SRU
Switched to the groupd ID com.github.svenruppert
because custom domain names (com.svenruppert) are not working properly with Jitpack.
Java Packages are still under com.svenruppert

Added the Modules:
- Logger Adapter
- Core 
- Core Properties

I archived the original git repos and merged everything into this one.
With this, building new versions is way easier or better - less work. :-)

### 05.00.02-SRU
updated versions

### 05.00.01-SRU
Fixed the pitest pattern
<pitest-prod-classes>com.svenruppert.*</pitest-prod-classes>
<pitest-test-classes>junit.com.svenruppert.*</pitest-test-classes>

removed the distribution repo definitions, because jitpack is doing it.


### 05.00.00-SRU - BREAKING CHANGE
With version 5.x I will switch the namespace from org.rapidpm to com.svenruppert
This has to do with organisational requirements on my side.
I will move this repo to the Github organisation **svenruppert**.
The license will be still the same. The change on your side, 
should be only the declaration of the parent pom.

* version updates
* maven plugin updates
* compile JDK will be 21 LTS
* switch from docker to podman - _tools/docker/develop

### 04.08.00-RPM
* version updates
* maven plugins updates


### 04.07.03-RPM
* version updates
* maven plugins updates

### 04.06.00-RPM
* version updates

### 04.05.03-RPM
* deploy target - via property on command line
    * deploy-repo-url
    * deploy-repo-snapshots-url
    
### 04.05.02-RPM
* V14.1 activated
* V15 alpha version update
* version updates
* Kotlin version update

### 04.05.00-RPM
* deactivate V08 dependencies
* deactivate V10 dependencies
* version update nodeJS/NPM (12.13.0/6.12.0)
* V14 update - V14.0.10

### 04.04.00-RPM
* added jCenter to repositories
* removed jitpack from repositories
* vaadin update V14 - V14.0.9
* cleared **_release** profiles

### 04.03.01-RPM

### 04.03.01-RPM
* update V14.0.7 to V14.0.8

### 04.03.00-RPM
* splitted the **_release** profile 
    + _release
    + _release_prepare
* version updates

### 04.02.00-RPM
* version updates
* removed lic checker (flow) mix from transitive deps
* removed all indirect vaadin flow dependencies / components
* added flow minimal needed dependencies

### 04.01.15-RPM
* revert BUGFIX from 04.01.14-RPM

### 04.01.14-RPM
* BUGFIX - combobox dependency

### 04.01.13-RPM
* junit update 1.5.2
* switched VaadinXX to manual latest version compositions
* VaadinXX excludes all transitive webjars

### 04.01.12-RPM
* vaadin v14.0.3
* V10/V14/Vxx - removed the dependency to vaadin - way to heavy
  To get the dev cycle up to usable, add only the flow dependencies you need into your pom.
  example : [Nano-Vaadin-Demos](https://github.com/orgs/Nano-Vaadin-Demos/dashboard)
* added minimal Flow dependencies, no themes, no components

### 04.01.11-RPM
* logger-adapter
* functional-reactive-lib
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
* **minimum-maven.version** - setting required maven version, default is latest version
* **activateJavaOnly** - true = JDK only / false JDK plus Kotlin activated
* **kotlin.compiler.jvmTarget** - default latest release
* **kotlin.version** - default mostly latest version
* **kotlin.compiler.incremental** - default true

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




