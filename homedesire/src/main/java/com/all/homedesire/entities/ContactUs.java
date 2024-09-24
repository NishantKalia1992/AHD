package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact_us")
public class ContactUs extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2024649523769673638L;

	@Column(name = "NAME")
	@JsonProperty("name")
	private String name;

	@Column(name = "MOBILE_NUMBER")
	@JsonProperty("mobile_number")
	private String mobileNumber;

	@Column(name = "EMAIL_ADDRESS")
	@JsonProperty("email_address")
	private String emailAddress;

	@Column(name = "MESSAGE")
	@JsonProperty("message")
	private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
