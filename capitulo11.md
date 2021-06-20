# ADVANCED CAMEL PROGRAMMING

## UNDERSTANDING MESSAGE FORMATS

### Exchanges

Interface Exchange:
```java
// Access the In message
Message getIn();
void setIn(Message in);
// Access the Out message (if any)
Message getOut();
void setOut(Message out);
boolean hasOut();
// Access the exchange ID
String getExchangeId();
void setExchangeId(String id);
```

Lazy creation of messages:

**org.apache.camel.impl.DefaultExchange**

```java
Exchange exchange = new DefaultExchange();
```

### Messages

* Message body
* Message headers
* Message attachments

```java
// Access the message body
Object getBody();
<T> T getBody(Class<T> type);
void setBody(Object body);
<T> void setBody(Object body, Class<T> type);
// Access message headers
Object getHeader(String name);
<T> T getHeader(String name, Class<T> type);
void setHeader(String name, Object value);
Object removeHeader(String name);
Map<String, Object> getHeaders();
void setHeaders(Map<String, Object> headers);
// Access message attachments
javax.activation.DataHandler getAttachment(String id);
java.util.Map<String, javax.activation.DataHandler> getAttachments();
java.util.Set<String> getAttachmentNames();
void addAttachment(String id, javax.activation.DataHandler content)
// Access the message ID
String getMessageId();
void setMessageId(String messageId);
```

#### EXAMPLE DE EXCHANGE AND MESSAGE

* Example

```java
public static void main(String[] args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("direct:start")
            .filter( header("tipoContrato").isEqualTo("ABC1"))
            .to("direct:abc");
            
            from("direct:abc")
            .log("recibiendo ${header['tipoContrato']} - ${body}")
            .to("mock:out");
        }
    });
    camelContext.start();
    ProducerTemplate producerTemplate = new DefaultProducerTemplate(camelContext);		
    producerTemplate.start();
    for(int i = 0 ; i < 100 ; i++) {
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setProperty("tipoContrato", i % 5 == 0 ? "ABC1" : "ABC2");
        exchange.getIn().setBody( UUID.randomUUID().toString());
        
        producerTemplate.send("direct:start", exchange);
    }
    TimeUnit.SECONDS.sleep(5);
    camelContext.stop();
}
```

#### BUILT-IN TYPE CONVERTERS

Usually, the type converter is called through convenience functions, such as **Message.getBody(Class<T> type)** or **Message.getHeader(String name, Class<T> type)**. 
It is also possible to invoke the master type converter directly. For example, if you have an exchange object, exchange, you could convert a given value to a String as shown:

```java
org.apache.camel.TypeConverter tc = exchange.getContext().getTypeConverter();
String str_value = tc.convertTo(String.class, value);
```

*Basic type converters*
Apache Camel provides built-in type converters that perform conversions to and from the  following basic types:
* java.io.File
* String
* byte[] and java.nio.ByteBuffer
* java.io.InputStream and java.io.OutputStream
* java.io.Reader and java.io.Writer
* java.io.BufferedReader and java.io.BufferedWriter
* java.io.StringReader

*Collection type converters*
Apache Camel provides built-in type converters that perform conversions to and from the following collection types:
* Object[]
* java.util.Set
* java.util.List

*Map type converters*
Apache Camel provides built-in type converters that perform conversions to and from the following map types:
* java.util.Map
* java.util.HashMap
* java.util.Hashtable
* java.util.Properties

*DOM type converters*
You can perform type conversions to the following Document Object Model (DOM) types:
* org.w3c.dom.Document — convertible from byte[], String, java.io.File, and java.io.InputStream.
* org.w3c.dom.Node
* javax.xml.transform.dom.DOMSource — convertible from String.
* javax.xml.transform.Source — convertible from byte[] and String.

*SAX type converters*

* String
* InputStream
* Source
* StreamSource
* DOMSource

#### BUILT-IN UUID GENERATORS

*Provided UUID generators*
* org.apache.camel.impl.ActiveMQUuidGenerator — (Default)
* org.apache.camel.impl.SimpleUuidGenerator
* org.apache.camel.impl.JavaUuidGenerator

*Custom UUID generator*
```java
// Java
package org.apache.camel.spi;
/**
* Generator to generate UUID strings.
*/
public interface UuidGenerator {
    String generateUuid();
}
```

*Specifying the UUID generator using Java*
```java
getContext().setUuidGenerator(new org.apache.camel.impl.SimpleUuidGenerator());
```

*Specifying the UUID generator using Spring*
```xml
<beans ...>
    <bean id="simpleUuidGenerator" class="org.apache.camel.impl.SimpleUuidGenerator" />
    <camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
        ...
    </camelContext>
...
</beans>
```

#### EXAMPLE

```java
public static void main(String[] args) throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.setUuidGenerator(new SimpleUuidGenerator());
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("timer:foo//period=1s")
            .log("HOLA MUNDO ${header['generateUuid']}")
            .to("direct:bye");
            
            from("direct:bye")
            .log("mi ID es ${body.messageId.generateUuid}")
            .to("mock:out");
        }
    });
    camelContext.start();
    TimeUnit.SECONDS.sleep(5);
    camelContext.stop();
}
```











