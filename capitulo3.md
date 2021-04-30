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

