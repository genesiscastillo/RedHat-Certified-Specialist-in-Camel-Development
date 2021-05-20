## Creating a datasource OSGi service with blueprint

[Using data sources with the Java™ persistence API](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.0/html/transaction_guide/using-jdbc-data-sources)

```xml
<blueprint xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0">
    <bean class="com.mysql.jdbc.jdbc2.optional.MysqlDataSource" id="mysqlDatasource">
        <property name="serverName" value="localhost"></property>
        <property name="databaseName" value="mydb"></property>
        <property name="port" value="3306"></property>
        <property name="user" value="cesar"></property>
        <property name="password" value="cesar"></property>
   </bean>

    <service id="mysqlDS" interface="javax.sql.DataSource" ref="mysqlDatasource">
        <service-properties>
            <entry key="osgi.jndi.service.name" value="jdbc/mydb"/>
        </service-properties>
    </service>
</blueprint>
```
### Install jndi
```bash
karaf@root()> jndi:names
Command not found: jdni:names
karaf@root()> features:list | grep jndi
jndi                                     | 4.2.0.fuse-000237-redhat-1  |          | Uninstalled |
karaf@root()> features:install jndi

karaf@root()> jndi:names
JNDI Name         | Class Name
------------------+-----------------------------------------------
osgi:service/jndi | org.apache.karaf.jndi.internal.JndiServiceImpl
```

### CONFIGURACION DE SERVICIO DATASOURCE en forma MANUAL

```bash
karaf@root()> features:list | grep pax-jdbc
```
![repo-add1](/img/repo-add1.png)

```bash
karaf@root()> feature:repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/1.3.0/xml/features-gpl
karaf@root()> features:list | grep pax-jdbc
# Instalar los siguientes bundle:
karaf@root()> install -s mvn:mysql/mysql-connector-java/5.1.46
Bundle ID: 223
karaf@root()> install -s mvn:org.osgi/org.osgi.service.jdbc/1.0.0
Bundle ID: 224
karaf@root()> install -s mvn:org.ops4j.pax.jdbc/pax-jdbc-mysql/1.3.0
Bundle ID: 225
karaf@root()> install -s mvn:org.ops4j.pax.jdbc/pax-jdbc/1.3.0
Bundle ID: 226
karaf@root()> install -s mvn:org.ops4j.pax.jdbc/pax-jdbc-pool-common/1.3.0
Bundle ID: 227
karaf@root()> install -s mvn:org.ops4j.pax.jdbc/pax-jdbc-config/1.3.0
Bundle ID: 228
# Configurar el datasource desde fichero blueprints
karaf@root()> install -s blueprint:file://https://github.com/genesiscastillo/RedHat-Certified-Specialist-in-Camel-Development/blob/master/blueprints/pax-jdbc-mysql-mydb.xml
# Configurar el datasource manualmente
karaf@root()> config:edit --factory --alias mysql org.ops4j.datasource
karaf@root()> config:property-set osgi.jdbc.driver.name mysql
karaf@root()> config:property-set dataSourceName mysqlds
karaf@root()> config:property-set osgi.jndi.service.name jdbc/mysqlds
karaf@root()> config:property-set dataSourceType DataSource
karaf@root()> config:property-set jdbc.url jdbc:mysql://localhost:3306/mydb
karaf@root()> config:property-set jdbc.user fuse
karaf@root()> config:property-set jdbc.password fuse
karaf@root()> config:property-set jdbc.useSSL false
karaf@root()> config:update
karaf@root()> jndi:names
JNDI Name                 | Class Name
--------------------------+-----------------------------------------------
osgi:service/jndi         | org.apache.karaf.jndi.internal.JndiServiceImpl
osgi:service/jdbc/mysqlds | com.mysql.jdbc.jdbc2.optional.MysqlDataSource

karaf@root()> feature:install -s eclipselink

karaf@root()> feature:install -s transaction
karaf@root()> feature:install -s hibernate

## checkear
karaf@root()> feature:info camel-jpa
karaf@root()> feature:install camel-jpa
karaf@root()> feature:start camel-jpa
karaf@root()> ls PersistenceProvider
karaf@root()> ls EntityManagerFactory
feature:install hibernate
feature:start hibernate
feature:start jpa
feature:start transaction
feature:start hibernate-orm
####### checkeo 2
feature:start camel-jpa



```

