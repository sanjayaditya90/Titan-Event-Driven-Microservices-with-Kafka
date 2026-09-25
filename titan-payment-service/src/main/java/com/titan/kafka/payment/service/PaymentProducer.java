package com.titan.kafka.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	public void writePaymentMessage(String _topic, String _key, String _data) {
		System.out.println("-----------------------------------------");
		System.out.println("Writing message to Payment Service Kafka");
		kafkaTemplate.send(_topic, _key, _data);
		System.out.println("Message Written to Payment Service Kafka");
		System.out.println("-----------------------------------------");
	}
}
