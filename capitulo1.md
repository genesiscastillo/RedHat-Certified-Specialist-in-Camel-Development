# Create and maintain Camel routes

- Why use Camel?

Camel introduces a few novel ideas into the integration space, which is why its authors
decided to create Camel in the first place. We’ll explore the rich set of Camel features
throughout the book, but these are the main ideas behind Camel:

   * Routing and mediation engine
        - The core feature of Camel is its routing and mediation engine.
   * Extensive component library
        - Camel provides an extensive library of more than 280 components
   * Enterprise integration patterns (EIPs)
        - Camel is heavily based on EIPs. Although EIPs describe
integration problems and solutions and provide a common vocabulary, the vocabulary
isn’t formalized

   * Domain-specific language (DSL)
        - Java DSL
        ```java
        from("file:data/inbox").to("jms:queue:order");`
        ```
        - XML DSL
        ```xml
        <route>
        <from uri="file:data/inbox"/>
        <to uri="jms:queue:order"/>
        </route>
        ```
    * Payload-agnostic router
    * Modular and pluggable architecture
    * Plain Old Java Object (POJO) model
    * Easy configuration
    * Automatic type converters
    * Lightweight core ideal for microservices
    * Cloud ready
    * Test kit
    * Vibrant community

## Use the Java™ language

## Use the CamelContext XML