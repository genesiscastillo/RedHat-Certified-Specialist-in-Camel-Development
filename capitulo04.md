# Work with databases and the Camel JPA component

[TUTORIAL](http://liquid-reality.de/Karaf-Tutorial/07/)


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

[JPA ](https://examples.javacodegeeks.com/enterprise-java/jpa/java-persistence-xml-example/)

https://camel.apache.org/components/3.4.x/jpa-component.html

```sql
DROP SCHEMA IF EXISTS `mydb`;

CREATE SCHEMA IF NOT EXISTS `mydb` 

USE `mydb`;

CREATE TABLE IF NOT EXISTS `mydb`.`pet` (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100),
    owner VARCHAR(100),
    species VARCHAR(20),
    sex VARCHAR(20),
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

[add plugin HOT deploy](https://github.com/genesiscastillo/RedHat-Certified-Specialist-in-Camel-Development/blob/master/INICIO.md#generating-a-bundle-project)


```xml
<plugin>
				<artifactId>maven-antrun-plugin</artifactId>
				<version>1.7</version>
				<executions>
					<execution>
						<phase>install</phase>
						<configuration>
							<tasks>
								<delete
									file="j:\devtools\fuse-karaf-7.0.0.fuse-000191-redhat-1\deploy\proyecto02-1.0.0.jar" />
								<copy file="./target/proyecto02-1.0.0.jar"
									tofile="j:\devtools\fuse-karaf-7.0.0.fuse-000191-redhat-1\deploy\proyecto02-1.0.0.jar" />
							</tasks>
						</configuration>
						<goals>
							<goal>run</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```

#### Add components for working jpa

[Hibernate ORM](https://hibernate.org/orm/)

```xml
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
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
```
[Project Lombok](https://projectlombok.org/)

```java
@Entity
@AllArgsConstructor
public class Pet {

	@Id
	private Long id;

	@Column(name="name")
	private String name;
	
	@Column(name = "owner")
	private String owner;
	
	@Column(name = "sex")
	private String sex;
	
	@Column(name="species")
	private String spcecies;
	
	@Column(name="birth")
	private LocalDate birth;
	
	@Column(name="death")
	private LocalDate death;
	
}
```
#### add persistence.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.0"
	xmlns="http://java.sun.com/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd">
	<persistence-unit name="persistence-unit-app" transaction-type="RESOURCE_LOCAL">
		<class>cl.ccastillo.dao.entities.Pet</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/mydb" />
            <property name="javax.persistence.jdbc.user" value="cesar" />
            <property name="javax.persistence.jdbc.password" value="cesar" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
        </properties>
	</persistence-unit>	
</persistence>
```
#### testing jpa
```java
	public static void main(String[] args) throws Exception {
		EntityManagerFactory  emf = Persistence.createEntityManagerFactory("persistence-unit-app");
		
		System.out.println("About to store dog");
		EntityManager em = emf.createEntityManager();

		em.getTransaction().begin();
		
		Pet perro = new Pet();
		perro.setName("pelusa");
		perro.setOwner("Cesar Castillo");
		perro.setSex("H");
		perro.setSpecies("Perro");
		perro.setBirth(LocalDate.now());
		
		em.persist(perro);
		em.getTransaction().commit();
		
		Long perroId = perro.getId();
		System.out.println(String.format( "Save dog wirth id %s",perroId));
		em.close();

		em = emf.createEntityManager();
		perro = em.find(Pet.class, perroId);
		System.out.println(String.format ("Found dog %s of breed %s", perro.getName() , perro.getOwner()));
		em.close();

		emf.close();
	}
```
## **JPA Component**

[APACHE CAMEL COMPONENT REFERENCE](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.0/html/apache_camel_component_reference/index)


**SENDING TO THE ENDPOINT**

You can store a Java entity bean in a database by sending it to a JPA producer endpoint. The body of the In message is assumed to be an entity bean (that is, a POJO with an **@Entity** annotation on it) or a collection or array of entity beans.

If the body is a List of entities, make sure to use entityType=java.util.ArrayList as a configuration passed to the producer endpoint.

If the body does not contain one of the previous listed types, put a Message Translator in front of the endpoint to perform the necessary conversion first.

From Camel 2.19 onwards you can use **query, namedQuery or nativeQuery** for the producer as well. Also in the value of the parameters, you can use Simple expression which allows you to retrieve parameter values from Message body, header and etc. Those query can be used for retrieving a set of data with using **SELECT** JPQL/SQL statement as well as executing bulk update/delete with using **UPDATE/DELETE** JPQL/SQL statement. Please note that you need to specify useExecuteUpdate to true if you execute **UPDATE/DELETE with namedQuery** as camel don’t look into the named query unlike query and nativeQuery.

**CONSUMING FROM THE ENDPOINT**

Consuming messages from a JPA consumer endpoint removes (or updates) entity beans in the database. This allows you to use a database table as a logical queue: consumers take messages from the queue and then delete/update them to logically remove them from the queue.

If you do not wish to delete the entity bean when it has been processed (and when routing is done), you can specify *consumeDelete=false* on the URI. This will result in the entity being processed each poll.

If you would rather perform some update on the entity to mark it as processed (such as to exclude it from a future query) then you can annotate a method with **@Consumed** which will be invoked on your entity bean when the entity bean when it has been processed (and when routing is done).

From Camel 2.13 onwards you can use **@PreConsumed** which will be invoked on your entity bean before it has been processed (before routing).

**URI format**

```bash
jpa:entityClassName[?options]
```


```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jpa</artifactId>
    <version>x.x.x</version>
</dependency>
```

## Practica

* eliminir los datos de la tabala Pet
```sql
truncate pet;
```
* add dependencies camel jpa component
```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jpa</artifactId>
    <version>x.x.x</version>
</dependency>
```

* crear dos enums
```java
public enum SPECIES {
	BIRD,
	CAT,
	DOG
}
public enum SEX {
	MALE,
	FEMALE;
}
```
[Mapeo de enumeraciones con @Enumerated](https://www.oscarblancarteblog.com/2016/11/14/mapeo-enumeraciones-enumerated/)

* cambiar los entities con @Enumerated
```java
	@Column(name = "sex")
	@Enumerated(EnumType.STRING)
	private SEX sex;
	
	@Column(name="species")
	@Enumerated(EnumType.STRING)
	private SPECIES species;

```

* Cambiar la logica para insercion de datos pets
```java
	public static void main(String[] args) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("camel");
		System.out.println("About to store dog");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (int i = 0; i < 100; i++) {
			Pet perro = new Pet();
			perro.setName("pet " + UUID.randomUUID());
			perro.setOwner("Cliente # " + new Random().nextInt(5000));
			perro.setSex(i % 2 == 0 ? SEX.MALE : SEX.FEMALE);
			perro.setSpecies(new Random().nextInt() % 2 == 0 ? SPECIES.BIRD
					: new Random().nextInt() % 2 == 0 ? SPECIES.DOG : SPECIES.CAT);
			LocalDate fecha = obtenerFechaAleatoria(); 
			perro.setBirth(fecha);
			if(new Random().nextBoolean()) {
				perro.setDeath(fecha.plusYears(20));
			}
			em.persist(perro);
		}
		em.getTransaction().commit();
		em.close();
		emf.close();
	}

	public static LocalDate obtenerFechaAleatoria() {
	        Random aleatorio = new Random();
	        Calendar unaFecha = Calendar.getInstance();
	        unaFecha.set (aleatorio.nextInt(10)+1990, aleatorio.nextInt(12)+1, aleatorio.nextInt(30)+1);
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMMM/yyyy");
	        System.out.println("La fecha vale " + sdf.format(unaFecha.getTime()));
	        return unaFecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

```xml
<blueprint
    xmlns:jpa="http://aries.apache.org/xmlns/jpa/v2.0.0"
    xmlns:tx="http://aries.apache.org/xmlns/transactions/v2.0.0"
    xsi:schemaLocation="http://www.osgi.org/xmlns/blueprint/v1.0.0 https://osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd
           http://aries.apache.org/xmlns/jpa/v2.0.0 http://aries.apache.org/schemas/jpa/jpa_200.xsd">


    <jpa:enable/>
    <tx:enable/>

</blueprint>
```
* configurar el ambiente Fuse Karaf

    - [Installation instructions for jpa-examples](https://github.com/apache/aries-jpa/tree/master/examples)
    - [OPS4J Pax JDBC - MySQL Driver Adapter](https://ops4j1.jira.com/wiki/spaces/PAXJDBC/pages/23953834/MySQL+Driver+Adapter)

**Required Maven Artifacts**
```properties
- An OSGi 4.2 framework
- org.ops4j.pax.jdbc:pax-jdbc-mysql
- An OSGi-ready version of mysql:mysql-connector-java
- org.osgi:org.osgi.enterprise:4.2.0
```
[Apache Karaf Features for OSGi Deployment](https://dzone.com/articles/apache-karaf-features-for-osgi-deployment)
```bash
karaf@root()> version
4.2.0.fuse-000237-redhat-1
karaf@root()> features:list | grep pax-jdbc
```
![repo-add1](/img/repo-add1.png)

```bash
karaf@root()> feature:repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/1.3.0/xml/features-gpl
karaf@root()> features:list | grep pax-jdbc
```
![repo-add1](/img/repo-add2.png)
```bash
karaf@root()> features:install pax-jdbc-mysql
```
![repo-add1](/img/repo-add3.png)
```bash
karaf@root()> features:install transaction
karaf@root()> features:install jpa
karaf@root()> features:install hibernate
karaf@root()> features:install camel-jpa
karaf@root()> features:install jndi
karaf@root()> features:install aries-blueprint
```


<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.25</version>
</dependency>



```properties
osgi.jdbc.driver.class=com.mysql.jdbc.Driver
osgi.jdbc.driver.name=mysql
xa=false
databaseName = tasklist;create=true
dataSourceName = tasklist
```


* Crear contexto camel para probar jpa component

```java
public static void main(String[] args) throws Exception {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("camel");		

    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    
    TransactionTemplate transactionTemplate = new TransactionTemplate();
    transactionTemplate.setTransactionManager( jpaTransactionManager );

    JpaComponent jpaComponent = new JpaComponent();
    jpaComponent.setEntityManagerFactory(entityManagerFactory);
    jpaComponent.setTransactionManager(  jpaTransactionManager );
    
    CamelContext context = new DefaultCamelContext();
    camelContext.addComponent("jpa", jpaComponent);
    
    context.start();
    TimeUnit.SECONDS.sleep(5);
    context.stop();
}
```

* vamos a ejecutar un select, agregar el route con jpa

