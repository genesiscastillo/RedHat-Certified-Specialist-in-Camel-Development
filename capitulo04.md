# Work with databases and the Camel JPA component

[Objetivo 4](https://www.redhat.com/en/services/training/ex421-red-hat-certified-specialist-in-camel-development-exam?section=Objectives)


[](https://www.apress.com/gp/book/9781430219569)

## MySql

[Install MySql WorkBench](https://dev.mysql.com/downloads/workbench/)

[Install MySql Docker Windows](http://www.devgi.com/2018/11/install-mysql-docker-windows.html)

* Pull mysql container

```bash
docker pull mysql/mysql-server:5.7

docker images
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
mysql/mysql-server         5.7                 76ac6291d3cf        2 weeks ago         234MB

docker run --name mysql1 -e MYSQL_USER=root -e MYSQL_PASSWORD=root -e MYSQL_DATABASE=homedb -p 3306:3306 -d mysql/mysql-server:5.7 
```

## Hibernate OGM

(Getting started with Hibernate ORM)[https://hibernate.org/orm/documentation/getting-started/]

https://examples.javacodegeeks.com/enterprise-java/jpa/java-persistence-xml-example/

https://camel.apache.org/components/3.4.x/jpa-component.html

```sql
DROP SCHEMA IF EXISTS `mydb`;

CREATE SCHEMA IF NOT EXISTS `mydb` 

USE `mydb`;

CREATE TABLE IF NOT EXISTS `mydb`.`pet` (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20),
    owner VARCHAR(20),
    species VARCHAR(20),
    sex CHAR(1),
    birth DATE,
    death DATE,
    PRIMARY KEY(Id)
);
```

[Generating a Bundle Project](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.0/html/deploying_into_apache_karaf/buildbundle#Build-ModifyMaven)


```bash
mvn archetype:generate \
  -DarchetypeGroupId=org.apache.camel.archetypes \
  -DarchetypeArtifactId=camel-archetype-blueprint \
  -DarchetypeVersion=3.9-0 \
  -DgroupId=cl.ccastillo.fuse \
  -DartifactId=pets-services-data \
  -Dversion=0.0.1
```

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
<!-- https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.0.0.Alpha7</version>
</dependency>
<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.25</version>
</dependency>
```


       