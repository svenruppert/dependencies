#This is the bes definition of the versions used by my projects.


[![Build Status - travis  ](https://travis-ci.org/RapidPM/rapidpm-dependencies.svg?branch=develop)](https://travis-ci.org/RapidPM/rapidpm-dependencies)
[![Build Status - drone.io](https://drone.io/github.com/RapidPM/rapidpm-dependencies/status.png)](https://drone.io/github.com/RapidPM/rapidpm-dependencies/latest)

branch:
+ master:
[![Dependency Status](https://www.versioneye.com/user/projects/55a3a5fb323939001700053b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55a3a5fb323939001700053b)

+ develop:
[![Dependency Status](https://www.versioneye.com/user/projects/55ccca799a2f09001600001e/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55ccca799a2f09001600001e)

## SNAPSHOTS
If you are using maven you could add the following to your settings.xml to get the snapshots.

```
   <profile>
      <id>allow-snapshots</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>snapshots-repo</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
          </snapshots>
        </repository>
      </repositories>
    </profile>
```
