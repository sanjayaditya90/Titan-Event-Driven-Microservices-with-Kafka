package com.titan.payment.processor;

import org.springframework.stereotype.Service;

import com.titan.payment.event.OrderCreatedEvent;
import com.titan.payment.response.PaymentResponse;

@Service
public class UpiPaymentProcessor implements PaymentProcessor {

	@Override
	public PaymentResponse processPayment(OrderCreatedEvent event) {
		if (event.getAmount() <= 0) {
			System.out.println("Amount is " + event.getAmount());
			new PaymentResponse(event.getOrderNo(), "FAILED");
			throw new RuntimeException("Invalid payment amount");
		}
		System.out.println("Processing UPI payment for Order No : " + event.getOrderNo() + " Amount : " + event.getAmount());
//		need to implement the payment gateway part and based on that response should be constructed
		return new PaymentResponse(event.getOrderNo(), "SUCCESS");
	}

	@Override
	public boolean supports(String paymentMethod) {
		return "UPI".equalsIgnoreCase(paymentMethod);
	}

}
