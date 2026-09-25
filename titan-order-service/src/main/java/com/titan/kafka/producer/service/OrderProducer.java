package com.titan.kafka.producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.titan.order.response.OrderResponse;

@Service
public class OrderProducer {
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	public void writeOrderMessage(String _topic, String _key, String _data) {
		System.out.println("-----------------------------------------");
		System.out.println("Writing message to Order Service Kafka");
		kafkaTemplate.send(_topic, _key, _data);
		System.out.println("Message Written to Order Service Kafka");
		System.out.println("-----------------------------------------");
	}

}
