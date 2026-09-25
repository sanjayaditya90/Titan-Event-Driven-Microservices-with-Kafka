package com.titan.kafka.devlivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeliveryProducer {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	public void writeDeliveryMessage(String _topic, String _key, String _data) {
		System.out.println("-----------------------------------------");
		System.out.println("Writing message to Delivery Service Kafka");
		kafkaTemplate.send(_topic, _key, _data);
		System.out.println("Message Written to Delivery Service Kafka");
		System.out.println("-----------------------------------------");
	}
}
