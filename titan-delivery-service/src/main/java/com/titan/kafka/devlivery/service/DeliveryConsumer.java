package com.titan.kafka.devlivery.service;

import java.lang.runtime.ObjectMethods;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.titan.devlivery.event.PaymentCreatedEvent;
import com.titan.devlivery.service.DeliveryService;

import tools.jackson.databind.ObjectMapper;

@Service
public class DeliveryConsumer {

	private final ObjectMapper objectMapper;
	private final DeliveryService deliveryService;

	public DeliveryConsumer(ObjectMapper objectMapper, DeliveryService deliveryService) {
		super();
		this.objectMapper = objectMapper;
		this.deliveryService = deliveryService;
	}

	@KafkaListener(topics = "payment-success", groupId = "PS2")
	public void consumePaymentMessage(String message) {
		long start = System.currentTimeMillis();

		PaymentCreatedEvent event = objToOrderEvent(message);

		System.out.println("-----------------------------------------");
		System.out.println("Reading message from Delivery Service Kafka");
		System.out.println("Event Id : " + event.getEventId());
		System.out.println("Order No : " + event.getOrderNo());
		System.out.println("Payment Mode : " + event.getPaymentMethod());
		System.out.println("Amount : " + event.getAmount());
		System.out.println("Delivery Address : " + event.getDeliveryAddress());
		System.out.println("Payment Status : " + event.getPaymentStatus());
		System.out.println("Message Read from Delivery Service Kafka");
		System.out.println("-----------------------------------------");

		deliveryService.process(event);

		long end = System.currentTimeMillis();

		System.out.println("Delivery Service processing time: " + (end - start) + " ms");
	}

	private PaymentCreatedEvent objToOrderEvent(String message) {
		return objectMapper.readValue(message, PaymentCreatedEvent.class);
	}

}
