package splitter;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Splitter {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
				from("direct:start").split(body()
						.tokenize("&"))
				.streaming().to("seda:end");
				
				from("seda:end").
				multicast().parallelProcessing()
				.to("direct:end1", "direct:end2");
				
				from("direct:end2").process(new Processor() {
					
					@Override
					public void process(Exchange exc) throws Exception {
						// TODO Auto-generated method stub
						if(exc.getIn().getBody()!=null)
						{
							String messageBody = (String) exc.getIn().getBody();
							//Just appending som test to process original message in exchange 
							exc.getIn().setBody(exc.getIn().getBody() + " Welcome123");
						}
						
					}
				}).to("seda:final");
			}
		});
		context.start();
		

		ProducerTemplate pt = context.createProducerTemplate();
		pt.sendBody("direct:start", "hello&welcome");
//		System.out.println("\n\nsent message as >> helll00123" );
		
		ConsumerTemplate ct = context.createConsumerTemplate();
		String recvdString = ct.receiveBody("seda:end",  String.class);
		
		System.out.println("\nrecieved message as >> "+ recvdString);
		
		ConsumerTemplate ct1 = context.createConsumerTemplate();
		String recvdString1 = ct1.receiveBody("direct:end1",  String.class);
		
		System.out.println("\nrecieved message at end1 >> "+ recvdString);
		
		ConsumerTemplate ct2 = context.createConsumerTemplate();
		String recvdString2 = ct2.receiveBody("seda:final",  String.class);
		
		System.out.println("\nrecieved message at final  >> "+ recvdString);
	}

}
