package splitter;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class WtapExample {

	public static void main(String[] args) throws Exception {

		CamelContext con = new DefaultCamelContext();
		con.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
				from("direct:start")
				.wireTap("direct:foo")
				.split(body()
						.tokenize(","))
				.streaming().to("seda:end");

			}
		});
		con.start();
		
		ProducerTemplate pt = con.createProducerTemplate();
		pt.sendBody("direct:start", "hello,welcome");
//		System.out.println("\n\nsent message as >> helll00123" );
		
		ConsumerTemplate ct = con.createConsumerTemplate();
		String recvdString = ct.receiveBody("direct:foo",  String.class);
		System.out.println("wire tap messages>> "+ recvdString);
		
		ConsumerTemplate ct2 = con.createConsumerTemplate();
		String recvdString2 = ct.receiveBody("seda:end",  String.class);
		System.out.println("After splitting>> "+ recvdString2);
	}

}
