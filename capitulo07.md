# Servicios Rest con camel-servlet

[Servlet](https://camel.apache.org/components/3.4.x/servlet-component.html)
[Rest DSL](https://camel.apache.org/components/latest/rest-component.html)
[Index camel bom 2.21.0.fuse-000077-redhat-1](https://maven.repository.redhat.com/ga/org/apache/camel/camel-parent/2.21.0.fuse-000077-redhat-1/camel-parent-2.21.0.fuse-000077-redhat-1.pom)
add dependencies

## Generar el proyecto base de bundle de camel

```bash
set ARTIF_PROJECT_NAME_FUSE=rest-servlet-example
set GROUP_PROJECT_NAME_FUSE=cl.ccastillo.app
set VERSI_PROJECT_NAME_FUSE=1.0.0
set VERSI_CAMEL_KARAF_BLUEPRINT=2.21.0.fuse-000077-redhat-1

mvn archetype:generate -DarchetypeGroupId=org.apache.camel.archetypes -DarchetypeArtifactId=camel-archetype-blueprint -DarchetypeVersion=%VERSI_CAMEL_KARAF_BLUEPRINT% -DgroupId=%GROUP_PROJECT_NAME_FUSE% -DartifactId=%ARTIF_PROJECT_NAME_FUSE% -Dversion=%VERSI_PROJECT_NAME_FUSE%
```

## modificar el pom.xml con los siguientes:

Cambiar el tipo de bundle

```xml
<project ... >
...
<packaging>bundle</packaging>
...
</project
```

agregar el plugin de bundle 
```xml
<project ... >
...
    <build>
        <defaultGoal>install</defaultGoal>
        <plugins>
        ...
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>3.3.0</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Bundle-SymbolicName>${project.groupId}.${project.artifactId}
                        </Bundle-SymbolicName>
                        <Import-Package>*</Import-Package>
                    </instructions>
                </configuration>
            </plugin>
        </plugins>
    </build>
...
</project>
```

agregar las siguientes dependencias


```xml
<properties>
    <restlet-version>2.3.12</restlet-version>
<properties>

<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-rest</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-servlet</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-http</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-restlet</artifactId>
</dependency>
<dependency>
    <groupId>org.restlet.jee</groupId>
    <artifactId>org.restlet</artifactId>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/resources/lib/org.restlet-2.3.12.jar</systemPath>
</dependency>
<dependency>
    <groupId>org.restlet.jee</groupId>
    <artifactId>org.restlet.ext.httpclient</artifactId>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/resources/lib/org.restlet.ext.httpclient-2.3.12.jar</systemPath>
</dependency>
<dependency>
    <groupId>org.restlet.jse</groupId>
    <artifactId>org.restlet.ext.jackson</artifactId>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/resources/lib/org.restlet.ext.jackson-2.3.12.jar</systemPath>
</dependency>
<dependency>
    <groupId>org.restlet.jse</groupId>
    <artifactId>org.restlet.ext.gson</artifactId>
    <scope>system</scope>
    <systemPath>${basedir}/src/main/resources/lib/org.restlet.ext.gson-2.3.12.jar</systemPath>
</dependency>
```
ESTE NO SIRVE - INVESTIGAR
```bash
admin@root()> feature:repo-add file:J:/workspace-fuse-camel/rest-servlet-example/src/main/resources/features.xml
```
```bash
admin@root()> feature:install camel-servlet
admin@root()> feature:install camel-jackson
admin@root()> feature:install camel-swagger-java
```



