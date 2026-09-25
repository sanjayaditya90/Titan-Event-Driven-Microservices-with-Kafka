package com.titan.notification.processor;

import com.titan.notification.event.OrderCreatedEvent;

public interface NotificationProcessor {
	void sendNotification(OrderCreatedEvent event);
}
