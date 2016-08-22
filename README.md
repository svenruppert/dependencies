#This is the bes definition of the versions used by my projects.

[![](https://build.rapidpm.org/app/rest/builds/buildType:id:RapidPM_Develop_Dependencies_Snapshot/statusIcon)](https://build.rapidpm.org/viewType.html?buildTypeId=RapidPM_Develop_Dependencies_Snapshot&guest=1)


branch:

master:
[![Dependency Status](https://www.versioneye.com/user/projects/55a3a5fb323939001700053b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55a3a5fb323939001700053b)

develop:
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
