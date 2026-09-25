package com.titan.notification.processor;

import org.springframework.stereotype.Service;

import com.titan.notification.event.OrderCreatedEvent;

@Service
public class EmailNotificationProcessor implements NotificationProcessor{

	@Override
	public void sendNotification(OrderCreatedEvent event) {
		System.out.println(event.getEventName() + " Confirmation for Order : "+ event.getOrderNo() + " Sent to Email : "+ event.getEmail());
	}

}
