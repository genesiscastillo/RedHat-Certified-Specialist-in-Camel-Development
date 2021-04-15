package cl.cedsarcastillo.capitulo1;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class TestExample {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.start();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("timer:foo?period=5s")
				.log("HOLA MUNDO")
				.to("mock:out");
			}
		});
		TimeUnit.SECONDS.sleep(15);
		context.stop();
	}
}
