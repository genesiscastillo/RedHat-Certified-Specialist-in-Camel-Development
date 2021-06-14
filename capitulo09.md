# Integracion con FTP (camel-ftp)

## Despliegue un servidor ftp con docker-compose

*docker-compose.yml*
```yml
version: '3'
services:
  ftpd_server:
    image: stilliard/pure-ftpd
    container_name: pure-ftpd
    ports:
      - "21:21"
      - "30000-30009:30000-30009"
    volumes:
      - "/folder_on_disk/data:/home/username/"
      - "/folder_on_disk/passwd:/etc/pure-ftpd/passwd"
    environment:
      PUBLICHOST: "localhost"
      FTP_USER_NAME: username
      FTP_USER_PASS: mypass
      FTP_USER_HOME: /home/username
    restart: always
```
ejecutar lo siguiente
```bash
docker-compose up -d
```
agregar la siguiente dependencia
```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-ftp</artifactId>
</dependency>
```
o para proyectos con springboot
```xml
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-ftp-starter</artifactId>
</dependency>
```
Desarrolle Java DSL
```java
public class TestingFtpMain {
	public static void main(String[] args) throws Exception {
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:start-receive")
				.to("ftp://username:mypass@localhost:21/home/username?passiveMode=true")
				.to("mock:out");
			}
		});
		camelContext.start();
		File fichero = Paths.get("C:", "workspace", "bq", "schemausuario.json").toFile();
		ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);
		producerTemplate.start();
		producerTemplate.sendBodyAndHeader("direct:start-receive", fichero, "CamelFileName", "schemausuario.json");
		TimeUnit.SECONDS.sleep(20);
		camelContext.stop();
	}
}
```
Desarrolle en XML DSL
```xml
<bean id="convertToFileProcessor" class="cl.ccastillo.app.springboot.camel.capitulo3.ConvertToFileProcessor"/>
...
<route>
    <from uri="direct:sendCsvFtp"/>
    <process ref="convertToFileProcessor"></process>
    <to uri="ftp://username:mypass@localhost:21/home/username?passiveMode=true"/>
    <to uri="mock:out"/>
</route>
```
siendo ConvertToFileProcessor.java un Processor
```java
public class ConvertToFileProcessor implements Processor	{
	@Override
	public void process(Exchange exchange) throws Exception {
		String body = exchange.getIn().getBody(String.class);
		String nameFile = String.format("%s.csv", UUID.randomUUID().toString());
		exchange.getOut().setHeader("CamelFileName", nameFile);
		exchange.getOut().setBody(body);
	}
}
```
DEMO

![Ftp](img/ftp1.png)

---
# RECURSOS

* [Tu server FTP con DOCKER](https://www.youtube.com/watch?v=y9sPUF7vRC0)
* [FTP Camel](https://camel.apache.org/components/latest/ftp-component.html)
