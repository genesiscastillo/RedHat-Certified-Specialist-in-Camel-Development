# Consume and produce files

## Create a simple route in CamelContext

```java
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.start();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("timer://foo?period=5s")
				.log("HOLA MUNDO ${date:now:ss}")
				.to("mock:out");
			}
		});
		TimeUnit.SECONDS.sleep(15);
		context.stop();
	}

```

## working with routes, transform and class ProducerTemplate

```java
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// work with more routes
				from("direct:inicio1")
				.log("work with object in")
				.log("${body}")
				.to("mock:out");

				from("direct:inicio2")
				.log("work with object y header")
				.transform(simple("Hello ${body}! What a beautiful ${headers['dayOrNight']}"))
				.log("resultado ${body}")
				.to("mock:out");
			}
		});
		context.start();
		ProducerTemplate producerTemplate = new DefaultProducerTemplate(context);
		producerTemplate.start();
		producerTemplate.sendBody("direct:inicio1", "Hola Mundo");
		producerTemplate.sendBodyAndHeader("direct:inicio2", "World", "dayOrNight", "day");
		context.stop();
	}
```
## working with routes, re-direct and class ConsumerTemplate

```java
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("timer://foo?period=now")
				.log("Hola Mundo - Nro ID= ${id}  ${date:now:dd/mm/yyyy HH:mm:ss}")
				.transform(simple("ADIOS Hola Mundo!!!! ${date:now:HH:mm:ss}"))
				.delay(5000)
				.to("direct:target");

				from("direct:target")
				.log("recibiendo desde log ${body} ${date:now:HH:mm:ss}")
				.transform(body().prepend("concatenando mensaje : "))
				.to("direct:log");
			}
		});
		context.start();

		ConsumerTemplate consumerTemplate = new DefaultConsumerTemplate(context);
		consumerTemplate.start();
		Exchange exchange =	consumerTemplate.receive("direct:log");
		System.out.println(exchange.getIn().getBody(String.class));
		
		consumerTemplate.stop();
		context.stop();
	}
```

## working with jndiBinding 

```java
public class UpperCase {
    public String toUpper(String s) {
        return s.toUpperCase();
    }
}

public class ExampleJndi {
	public static void main(String[] args) throws Exception {
		JndiContext jndiContext = new JndiContext();
		jndiContext.bind("uppercase", new UpperCase());

		JndiBeanRepository beanRepository = new JndiBeanRepository(jndiContext);
		
		CamelContext context = new DefaultCamelContext( beanRepository );
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start")
				.log("${body}")
				.transform(method("uppercase"))
				.log("${body}")
				.to("mock:out");
			}
		});
		context.start();

		ProducerTemplate template = new DefaultProducerTemplate(context);
		template.start();
		template.sendBody("direct:start", "cesar castillo");

		context.stop();
	}
}
```

## working with files - part 1






```xml
    <route id="loadFile">
    	<from uri="file://data/inbox"/>
    	<log message="hola cargando el archivo ${header['CamelFileName']}"/>
    	<log message="el texto es ${body}"/>
    	<log message="----------------------------------------------"/>
    	<to uri="mock:out"/>
    </route>
```

## Using Camel’s CSV data format
```xml
<dependency>
	<groupId>org.apache.camel</groupId>
	<artifactId>camel-csv</artifactId>
</dependency>
```
in java route
```java
from("file://rider/csvfiles")
.unmarshal().csv()
.split(body()).to("jms:queue:csv.record");
```

in spring xml route
```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <route>
        <from uri="file://rider/csvfiles"/>
        <unmarshal>
            <csv/>
        </unmarshal>
        <split>
            <simple>body</simple>
            <to uri="jms:queue:csv.record"/>
        </split>
    </route>
</camelContext>
```
