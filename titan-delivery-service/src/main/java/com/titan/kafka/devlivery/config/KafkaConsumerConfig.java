package com.titan.kafka.devlivery.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

	@Bean
	public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {

		System.out.println("========================================");
		System.out.println("Creating Kafka DefaultErrorHandler in Delivery Service");
		System.out.println("========================================");

		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
				(record, exception) -> {

					String dltTopic = record.topic() + ".DLT";
					int partition = record.partition();

					System.out.println("========================================");
					System.out.println("DLT RECOVERER CALLED");
					System.out.println("Original Topic : " + record.topic());
					System.out.println("Partition      : " + partition);
					System.out.println("Offset         : " + record.offset());
					System.out.println("DLT Topic      : " + dltTopic);
					System.out.println("Exception      : " + exception.getClass().getName());
					System.out.println("Error Message  : " + exception.getMessage());
					System.out.println("========================================");

					return new TopicPartition(dltTopic, partition);
				});

		FixedBackOff backOff = new FixedBackOff(2000L, 2);

		System.out.println("========================================");
		System.out.println("Kafka Retry Configuration");
		System.out.println("Retry Delay    : 2000 ms");
		System.out.println("Max Retries    : 2");
		System.out.println("Total Attempts : 3");
		System.out.println("========================================");

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

		errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> {

			System.out.println("========================================");
			System.out.println("KAFKA RETRY");
			System.out.println("Attempt        : " + deliveryAttempt);
			System.out.println("Topic          : " + record.topic());
			System.out.println("Partition      : " + record.partition());
			System.out.println("Offset         : " + record.offset());
			System.out.println("Exception      : " + exception.getClass().getName());
			System.out.println("Error Message  : " + exception.getMessage());

			Throwable cause = exception;

			while (cause.getCause() != null) {
				cause = cause.getCause();
			}

			System.out.println("ROOT CAUSE     : " + cause.getClass().getName());
			System.out.println("ROOT MESSAGE   : " + cause.getMessage());

			System.out.println("========================================");

			cause.printStackTrace();
		});

		System.out.println("DefaultErrorHandler created successfully");

		return errorHandler;
	}
}
