package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Consumer SQS
        from("aws2-sqs://orders-queue")
                .routeId("sqs-consumer")
                .log("📥 Mensagem recebida da SQS: ${body}")

                // Simples transformação e envio para SNS
                .setHeader("CamelAwsSnsTopicArn", constant("arn:aws:sns:us-east-1:000000000000:orders-topic"))
                .to("aws2-sns://orders-topic");
    }
}