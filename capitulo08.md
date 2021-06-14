# Transforming data with Camel

> Yes Spring Boot is not an application server to host N+ applications in the same JVM.

> Camel on Spring Boot is optimized and adhered to run one CamelContext only.

### Using the Message Translator EIP

Camel provides three ways of using this pattern:
* Using Processor
* Using Java beans
* Using \<transform\>

## Transforming using Processor

The Camel Processor is an interface defined in **org.apache.camel.Processor** with a single method:

> *public void process(Exchange exchange) throws Exception;*

Example:
```java
public class OrderToCsvProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {
        String custom = exchange.getIn().getBody(String.class);
        String id = custom.substring(0, 10);
        String customerId = custom.substring(10, 20);
        String date = custom.substring(20, 30);
        String items = custom.substring(30);
        String[] itemIds = items.split("@");
        StringBuilder csv = new StringBuilder();
        csv.append(id.trim());
        csv.append(",").append(date.trim());
        csv.append(",").append(customerId.trim());
        for (String item : itemIds) {
            csv.append(",").append(item.trim());
        }
        exchange.getIn().setBody(csv.toString());
}
}
```

In java DSL

```java
from("quartz2://report?cron=0+0+6+*+*+?")
.to("http://riders.com/orders/cmd=received&date=yesterday")
.process(new OrderToCsvProcessor())
.to("file://riders/orders?fileName=report-${header.Date}.csv");
```

In XML DSL
```xml
<bean id="csvProcessor" class="camelinaction.OrderToCsvProcessor"/>

<camelContext xmlns="http://camel.apache.org/schema/spring">
    <route>
        <from uri="quartz2://report?cron=0+0+6+*+*+?"/>
        <to uri="http://riders.com/orders/cmd=received&amp;date=yesterday"/>
        <process ref="csvProcessor"/>
        <to uri="file://riders/orders?fileName=report-${header.Date}.csv"/>
    </route>
</camelContext>
```
## Transforming using beans

```java
public class OrderToCsvBean {
    public static String map(String custom) {
        String id = custom.substring(0, 10);
        String customerId = custom.substring(10, 20);
        String date = custom.substring(20, 30);
        String items = custom.substring(30);
        String[] itemIds = items.split("@");
        StringBuilder csv = new StringBuilder();
        csv.append(id.trim());
        csv.append(",").append(date.trim());
        csv.append(",").append(customerId.trim());
        for (String item : itemIds) {
            csv.append(",").append(item.trim());
        }
        return csv.toString();
    }
}
```
## Transforming using the transform
### Transforming using the transform method from the Java DSL

```java
from("direct:start")
.transform(body().regexReplaceAll("\n", "<br/>"))
.to("mock:result");
```
### Transforming using the transform method from the XML DSL
```xml
<bean id="htmlBean" class="camelinaction.HtmlBean"/>
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <route>
        <from uri="direct:start"/>
        <transform>
            <method bean="htmlBean" method="toHtml"/>
        </transform>   
        <to uri="mock:result"/>
    </route>
</camelContext>
```
```java
public class HtmlBean {
    public static String toHtml(String body) {
        body = body.replaceAll("\n", "<br/>");
        body = "<body>" + body + "</body>";
        return body;
    }
}
```
---
## Using the Content Enricher EIP

* **pollEnrich**.—This method merges data retrieved from another source by using a consumer.
* **enrich**.—This method merges data retrieved from another source by using a producer.

### The difference between pollEnrich and enrich
The difference between pollEnrich and enrich is that the former uses a consumer, and the latter uses a producer, to retrieve data from the source. Knowing the difference is important: the file component can be used with both, but using enrich will write the message content as a file; using pollEnrich will read the file as the source, which is most likely the scenario you’ll be facing when enriching with files. **The HTTP component works only with enrich;** it allows you to invoke an external HTTP service and use its reply as the source.

Camel uses the **org.apache.camel.processor.aggregate.AggregationStrategy** interface to merge the result from the source with the original message, as follows: 
```java 
Exchange aggregate(Exchange oldExchange, Exchange newExchange);
```

* Using pollEnrich to merge additional data with an existing message

```java
    from("quartz2://report?cron=0+0+6+*+*+?")
    .to("http://riders.com/orders/cmd=received&date=yesterday")
    .process(new OrderToCsvProcessor())
    .pollEnrich("ftp://riders.com/orders/?username=rider&password=secret",
        new AggregationStrategy() {
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                if (newExchange == null) {
                    return oldExchange;
                }
                String http = oldExchange.getIn().getBody(String.class);
                String ftp = newExchange.getIn().getBody(String.class);
                String body = http + "\n" + ftp;
                oldExchange.getIn().setBody(body);
                return oldExchange;
            }
        })
    .to("file://riders/orders");
```
---
## Transforming XML

### Transforming XSLT

en la  raiz del proyecto:

*planes.dtd*
```xml
<?xml version = "1.0" encoding = "utf-8"?>
<!-- planes.dtd - a document type definition for
                  the planes.xml document, which specifies
                  a list of used airplanes for sale  -->
<!ELEMENT planes_for_sale (ad+)>
<!ELEMENT ad (year, make, model, color, description, 
              price?, seller, location)>
<!ELEMENT year (#PCDATA)>
<!ELEMENT make (#PCDATA)>
<!ELEMENT model (#PCDATA)>
<!ELEMENT color (#PCDATA)>
<!ELEMENT description (#PCDATA)>
<!ELEMENT price (#PCDATA)>
<!ELEMENT seller (#PCDATA)>
<!ELEMENT location (city, state)>
<!ELEMENT city (#PCDATA)>
<!ELEMENT state (#PCDATA)>

<!ATTLIST seller phone CDATA #REQUIRED>
<!ATTLIST seller email CDATA #IMPLIED>

<!ENTITY c "Cessna">
<!ENTITY p "Piper">
<!ENTITY b "Beechcraft">
```
*planes.xml*
```xml
<!DOCTYPE planes_for_sale SYSTEM "planes.dtd">
<planes_for_sale>
   <ad>
      <year> 1977 </year>
      <make> &c; </make>
      <model> Skyhawk </model>
      <color> Light blue and white </color>
      <description> New paint, nearly new interior,
            685 hours SMOH, full IFR King avionics </description>
      <price> 23,495 </price>
      <seller phone = "555-222-3333"> Skyway Aircraft </seller>
      <location>
         <city> Rapid City, </city>
         <state> South Dakota </state>
      </location>
   </ad>
   <ad>
      <year> 1965 </year>
      <make> &p; </make>
      <model> Cherokee </model>
      <color> Gold </color>
      <description> 240 hours SMOH, dual NAVCOMs, DME, 
                new Cleveland brakes, great shape </description>
      <seller phone = "555-333-2222"  
              email = "jseller@www.axl.com">
              John Seller </seller>
      <location>
         <city> St. Joseph, </city>
         <state> Missouri </state>
      </location>
   </ad>
</planes_for_sale>
```
*planes.xslt*
```xml
<?xml version = "1.0" encoding = "utf-8"?>
<!-- xslplanes.1.xsl.1 
     An XSLT stylesheet for xslplane.xml using child templates
     -->
<xsl:stylesheet version = "1.0"
                xmlns:xsl = "http://www.w3.org/1999/XSL/Transform"
                xmlns = "http://www.w3.org/1999/xhtml">

<!-- The template for the whole document (the plane element) -->

   <xsl:template match = "plane">
     <html><head><title> Style sheet for xslplane.xml </title>
     </head><body>
     <h2> Airplane Description </h2>

<!-- Apply the matching templates to the elements in plane -->

     <xsl:apply-templates />
     </body></html>
   </xsl:template>

<!-- The templates to be applied (by apply-templates) to the
     elements in the plane element -->

   <xsl:template match = "year">
     <span style = "font-style: italic; color: blue;"> Year: 
     </span>
     <xsl:value-of select = "." /> <br />
   </xsl:template>
   <xsl:template match = "make">
     <span style = "font-style: italic; color: blue;"> Make: 
     </span>
     <xsl:value-of select = "." /> <br />
   </xsl:template>
   <xsl:template match = "model">
     <span style = "font-style: italic; color: blue;"> Model: 
     </span>
     <xsl:value-of select = "." /> <br />
   </xsl:template>
   <xsl:template match = "color">
     <span style = "font-style: italic; color: blue;"> Color: 
     </span>
     <xsl:value-of select = "." /> <br />
   </xsl:template>
</xsl:stylesheet>
```

```java
	public static void main(String[] args) throws Exception{
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
					from("direct:inicio")
					.log("mensaje ${body}")
					.to("xslt:file:planes.xslt")
					.log("trata ${body}")
					.to("mock:out");
					
			}
		});
		camelContext.start();
		File file = Paths.get("planes.xml").toFile();
		ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
		producerTemplate.start();
		producerTemplate.sendBody("direct:inicio", file);
		TimeUnit.SECONDS.sleep(5);
		camelContext.stop();
	}
```
### Transforming using XStream

crear objeto java
```java
public class Texto{
    
    private String id;
    private String nombre;
    
    public Texto(String id, String texto) {
        this.id = id;
        this.nombre = texto;
    }
}
```
add dependency xstream pom.xml
```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-xstream</artifactId>
</dependency>
```

add 
```java
public static void main(String[] args) throws Exception{
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.setStreamCaching(true);
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("direct:inicioxstream")
            .setHeader("CamelFileName", () -> "cesar2.xml")
            .marshal().xstream("UTF-8")
            .to("file://data/out")
            .to("mock:out");

        }
    });
    camelContext.start();
    
    Texto texto = new CamelXStream().new Texto("100","hola mundooo");
    
    ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
    producerTemplate.start();
    producerTemplate.sendBody("direct:inicioxstream", texto);
    
    TimeUnit.SECONDS.sleep(5);
    camelContext.stop();
}
```
### Camel testing with Spring XML

add test dependency
```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test-spring</artifactId>
    <version>2.20.1</version>
    <scope>test</scope>
</dependency>
```
add camelcontext test *camelaction/firststep.xml*
```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <dataFormats>
        <xstream id="myXstream" />
    </dataFormats>
    <route>
        <from uri="direct:to-xstream" />
        <marshal ref="myXstream" />
        <log message="${body}"/>
        <to uri="mock:out" />
    </route>
</camelContext>
```
add class spring test
```java
public class CamelXmlSpringTest extends CamelSpringTestSupport {
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("camelaction/firststep.xml");
	}
	@Test
	public void testmovie() throws Exception {
		Department department = new Department();
		Employee employee = new Employee();
		employee.setDept(department);
		employee.setId(22233);
		employee.setName("Cesar Castillo");
		template.sendBody("direct:to-xstream" , employee );
	}
```
testing with maven test
```bash
mvn test -Dtest=cl.ccastillo.app.CamelXmlSpringTest -DfailIfNoTests=false  
```

* How to define custom namespace and tag alias using camel-xstream?

define dataformat
```java
XStreamDataFormat xstreamAlias = new XStreamDataFormat();
xstreamAlias.setAliases(Collections.singletonMap("Employee", Employee.class.getCanonicalName()));
```
and replace code
```java
from("direct:inicioxstream")
.setHeader("CamelFileName", () -> "cesar.xml")
.marshal( xstreamAlias )
.to("file://data/out")
.to("mock:out");
```
---
### Transforming XML with object marshaling/unmarshaling

* add xsd in root project (src/main/xsd)
*schema1.xsd*
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" version="1.0">
  <xs:complexType name="department">
    <xs:sequence>
      <xs:element name="id" type="xs:int" />
      <xs:element name="name" type="xs:string" minOccurs="0" />
    </xs:sequence>
  </xs:complexType>
  <xs:complexType name="employee">
    <xs:sequence>
      <xs:element name="id" type="xs:int" />
      <xs:element name="name" type="xs:string" minOccurs="0" />
      <xs:element name="dept" type="department" minOccurs="0" />
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```
* add plugin 
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>2.5.0</version>
    <executions>
        <execution>
            <id>xsd-to-java</id>
            <goals>
                <goal>xjc</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <packageName>cl.ccastillo.app.bo</packageName>
    </configuration>
</plugin>
```
* execute plugin
```bash
mvn clean generate-sources
```
* view the generated files

![schema1](/img/schema1.png)

* add @XmlRootElement(name="department") for class Department y for class Employee
```java
@XmlRootElement(name="department") // for class Employee


@XmlRootElement(name="employee") // for class Employee
```

* add dependency in poml.xml

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jaxb</artifactId>
</dependency>
```

* creating a camelcontext *camelaction/secondstep.xml*
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="
	http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://camel.apache.org/schema/spring 
	http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="addEmployee" class="cl.ccastillo.app.services.AddEmployeeService"/>

	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">

		<dataFormats>
			<jaxb id="jaxb" contextPath="cl.ccastillo.app.bo"/>
		</dataFormats>
		
		<route>
			<from uri="direct:string-to-jaxb" />
			<marshal ref="jaxb" />
			<log message="${body}" />
			<process ref="addEmployee"/>
			<to uri="direct:jaxb-to-string" />
		</route>
		<route>
			<from uri="direct:jaxb-to-string" />
			<unmarshal ref="jaxb" />
			<log message="${body}" />
			<to uri="mock:out" />
		</route>
	</camelContext>
</beans>
```
* test class
```java
package cl.ccastillo.app;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class CamelJaxbSpringTest extends CamelSpringTestSupport	{
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("camelaction/secondstep.xml");
	}
	@Test
	public void test1() {
		StringBuilder body = new StringBuilder();
		body.append("<department>")
		.append("<id>963258</id>")
		.append("<name>Recursos Humanos</name>")
		.append("</department>");
		template.sendBody("direct:string-to-jaxb", body);
	}
}
```
* testing the test for CamelSpringTestSupport with mvn

```bash
mvn test -Dtest=cl.ccastillo.app.CamelJaxbSpringTest -DfailIfNoTests=false
```

* result

![jaxb](/img/jaxbtest.png)
---
## Transforming with data formats

### Using Camel’s CSV data format

* [camel-csv](https://camel.apache.org/components/3.4.x/dataformats/csv-dataformat.html)

* add dependency
```xml
<dependency>
  <groupId>org.apache.camel</groupId>
  <artifactId>camel-csv</artifactId>
  <version>x.x.x</version>
</dependency>
```

### marshalling csv
```java
	public static void main(String[] args) throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start")
				.marshal().csv()
				.log("${body}")
				.to("mock:out");
			}
		});
		camelContext.start();
		Map<String, String> mapa = new HashMap<String, String>();
		mapa.put("numero","854698");
		mapa.put("nombre","cesar castillo");
		ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
		producerTemplate.start();
		producerTemplate.sendBody("direct:start", mapa);
		TimeUnit.SECONDS.sleep(10);
		camelContext.stop();
	}
``` 

### unmarshalling csv

* fichero addresses.csv

[addresses.csv](./img/address.csv)

* *UnmarshalProcessor.java*
```java
public class UnmarshalProcessor implements Processor {
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
			List<List<String>> lista = exchange.getIn().getBody(List.class);
			lista.forEach( lista2 -> {
				lista2.forEach(System.out::println);
			});
	}
}
```
* *Unmarshaling Test main java*
```java
public static void main(String[] args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
                from("direct:start2")
                .unmarshal().csv()
                .process(new UnmarshalProcessor())
                .to("mock:out");
        }
    });
    camelContext.start();
    File file = Paths.get("addresses.csv").toFile();
    ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
    producerTemplate.start();
    producerTemplate.sendBody("direct:start2" , file);
    TimeUnit.SECONDS.sleep(10);
    camelContext.stop();
}
```
* *other test unmarshalling main java*
```java
public static void main(String[] args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
                from("direct:start2")
                .unmarshal().csv()
                .split( simple("DATA -> ${body}"))
                .log("${body}")
                .to("mock:out");
        }
    });
    camelContext.start();
    File file = Paths.get("addresses.csv").toFile();
    ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
    producerTemplate.start();
    producerTemplate.sendBody("direct:start2" , file);
    TimeUnit.SECONDS.sleep(10);
    camelContext.stop();
}
```


---
# OTROS RECURSOS

https://www.freecodecamp.org/news/configure-multiple-camel-context-in-spring-boot-application-d3a16396266/

https://tomd.xyz/multiple-camel-contexts/





