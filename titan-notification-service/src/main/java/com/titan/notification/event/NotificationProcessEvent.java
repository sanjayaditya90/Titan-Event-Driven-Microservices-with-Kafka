package com.titan.notification.event;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_processed_events")
public class NotificationProcessEvent {
	@Id
	private String eventId;

	private LocalDateTime processedAt;

	public NotificationProcessEvent() {
	}

	public NotificationProcessEvent(String eventId) {
		this.eventId = eventId;
		this.processedAt = LocalDateTime.now();
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public LocalDateTime getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}

}
