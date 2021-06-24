# Transactional JPA

*generar el project comun*
```dos
set ARTIF_PROJECT_NAME_FUSE=transactional-jpa
set GROUP_PROJECT_NAME_FUSE=cl.ccastillo.app
set VERSI_PROJECT_NAME_FUSE=0.1
set VERSI_CAMEL_KARAF_BLUEPRINT=2.21.0.fuse-000077-redhat-1

mvn archetype:generate -DarchetypeGroupId=org.apache.camel.archetypes -DarchetypeArtifactId=camel-archetype-blueprint -DarchetypeVersion=%VERSI_CAMEL_KARAF_BLUEPRINT% -DgroupId=%GROUP_PROJECT_NAME_FUSE% -DartifactId=%ARTIF_PROJECT_NAME_FUSE% -Dversion=%VERSI_PROJECT_NAME_FUSE%
```

*agregar las dependencias*
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jpa</artifactId>
    <version>3.10.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>4.0.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.openjpa</groupId>
    <artifactId>openjpa-persistence-jdbc</artifactId>
    <version>3.2.0</version>
</dependency>
```
*TransactionalJpaApplication.java*
```java
@SpringBootApplication
@ImportResource(locations = {"classpath:META-INF/spring/context-jpa.xml"})
public class TransactionalJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionalJpaApplication.class, args);
	}

}
```
*agregar un processor*
```java
public class ProcessorEntidad implements Processor {
	@Override
	public void process(Exchange exchange) throws Exception {
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setAmount(120002d);
		purchaseOrder.setCustomer("Cesar Castillo");
		purchaseOrder.setName(UUID.randomUUID().toString());
		exchange.getOut().setBody(purchaseOrder);
	}
}
```
*agregar un obejcto entidad`*
```java
@Data
@Entity
public class PurchaseOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private double amount;
	private String customer;
}
```
*persistence.xml*
```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
	<persistence-unit name="camel" transaction-type="RESOURCE_LOCAL">
		<class>cl.ccastillo.app.PurchaseOrder</class>
		<properties>
			<property name="openjpa.ConnectionDriverName" value="org.apache.derby.jdbc.EmbeddedDriver" />
			<property name="openjpa.ConnectionURL" value="jdbc:derby:memory:order;create=true" />
			<property name="openjpa.ConnectionUserName" value="sa" />
			<property name="openjpa.ConnectionPassword" value="" />
			<property name="openjpa.jdbc.SynchronizeMappings" value="buildSchema" />
		</properties>
	</persistence-unit>
</persistence>
```
*contexnt-jpa.xml*
```xml
<beans 
	xmlns="http://www.springframework.org/schema/beans" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:camel="http://camel.apache.org/schema/spring" 
	xsi:schemaLocation="
	http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jpa" class="org.apache.camel.component.jpa.JpaComponent">
		<property name="entityManagerFactory" ref="entityManagerFactory" />
	</bean>
	<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalEntityManagerFactoryBean">
		<property name="persistenceUnitName" value="camel" />
		<property name="jpaVendorAdapter" ref="jpaAdapter" />
	</bean>
	<bean id="jpaAdapter" class="org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter">
		<property name="databasePlatform" value="org.apache.openjpa.jdbc.sql.DerbyDictionary" />
		<property name="database" value="DERBY" />
	</bean>
	<bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
		<property name="transactionManager">
			<bean class="org.springframework.orm.jpa.JpaTransactionManager">
				<property name="entityManagerFactory" ref="entityManagerFactory" />
			</bean>
		</property>
	</bean>
	<bean id="processorEntidad" class="cl.ccastillo.app.ProcessorEntidad"/>
    <camelContext id="camel-898" xmlns="http://camel.apache.org/schema/spring">
    	<route>
	    	<from uri="timer://foo?period=5s"/>
	    	<process ref="processorEntidad"/>
      		<to uri="jpa://cl.ccastillo.app.PurchaseOrder"/>
      		<log message="agregando un dato ${body.name}"/>
	    	<to uri="mock:out"/>
    	</route>
    	 <route>
	    	<from uri="timer://foo?period=10s"/>
      		<to uri="jpa://cl.ccastillo.app.PurchaseOrder?query=select o from cl.ccastillo.app.PurchaseOrder o"/>
			<log message="${body}"/>      		
	    	<to uri="mock:out"/>
    	</route>
    </camelContext>
</beans>
```
---
# Transactional JDBC

```bash
set ARTIF_PROJECT_NAME_FUSE=transactional-jdbc
set GROUP_PROJECT_NAME_FUSE=cl.ccastillo.app
set VERSI_PROJECT_NAME_FUSE=0.1
set VERSI_CAMEL_KARAF_BLUEPRINT=2.21.0.fuse-000077-redhat-1

mvn archetype:generate -DarchetypeGroupId=org.apache.camel.archetypes -DarchetypeArtifactId=camel-archetype-blueprint -DarchetypeVersion=%VERSI_CAMEL_KARAF_BLUEPRINT% -DgroupId=%GROUP_PROJECT_NAME_FUSE% -DartifactId=%ARTIF_PROJECT_NAME_FUSE% -Dversion=%VERSI_PROJECT_NAME_FUSE%
```
*agregar dependencia*
```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jdbc</artifactId>
    <version>3.10.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
</dependency>
```
*agregar un schema para creacion de tabla y asignacion de datos a la tabla*
```xml
CREATE TABLE Student (
   Id INT NOT NULL GENERATED ALWAYS AS IDENTITY,
   Age INT NOT NULL,
   First_Name VARCHAR(255),
   last_name VARCHAR(255),
   PRIMARY KEY (Id)
);
INSERT INTO Student(Age, First_Name, Last_Name) VALUES (21, 'Sucharitha' , 'Tyagi');
INSERT INTO Student(Age, First_Name, Last_Name) VALUES (20, 'Amit', 'Bhattacharya'), (22, 'Rahul', 'Desai');
```

*TransactionalJdbcJmsJtaApplication.java*
```java
@SpringBootApplication
@ImportResource(locations = {"classpath:META-INF/spring/context-jdbc.xml"})
public class TransactionalJdbcJmsJtaApplication {
	public static void main(String[] args)     {
		SpringApplication.run(TransactionalJdbcJmsJtaApplication.class, args);
	}
}
```
*context-jdbc.xml*
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:camel="http://camel.apache.org/schema/spring"
       xmlns:jdbc="http://www.springframework.org/schema/jdbc"       
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd
       http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.0.xsd">

    <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="myDataSource"/>
    </bean>

    <bean id="myDataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="org.apache.derby.jdbc.EmbeddedXADataSource"/>
        <property name="url" value="jdbc:derby:memory:mydb;create=true"/>
    </bean>
	<jdbc:initialize-database data-source="myDataSource" enabled="true">
    	<jdbc:script location="classpath:datastore-schema.sql" />
  	</jdbc:initialize-database>
  	
    <camelContext id="camel-898" xmlns="http://camel.apache.org/schema/spring">
    	<route>
	    	<from uri="timer://foo?period=3s"/>
 			<setBody>
        		<simple>select * from Student</simple>
      		</setBody>
      		<to uri="jdbc:myDataSource"/>
			<log message="${body}"/>      		
	    	<to uri="mock:out"/>
    	</route>

    	<route>
	    	<from uri="timer://foo?period=5s"/>
	    	<setHeader name="edad">
	    		<constant>22</constant>
	    	</setHeader>
	    	<setBody>
	    		<simple>select * from Student where AGE = ${header['edad']}</simple>
	    	</setBody>
      		<to uri="jdbc:myDataSource"/>
			<log message="${body}"/>      		
	    	<to uri="mock:out"/>
    	</route>
    </camelContext>
</beans>
```
