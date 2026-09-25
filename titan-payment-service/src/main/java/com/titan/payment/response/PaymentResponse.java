package com.titan.payment.response;

public class PaymentResponse {

	private String orderNo;
	private String status;

	public PaymentResponse(String orderNo, String status) {
		super();
		this.orderNo = orderNo;
		this.status = status;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
