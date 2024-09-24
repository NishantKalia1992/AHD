package com.all.homedesire.resources.dto.property;

import com.all.homedesire.entities.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class Partner {

	@JsonProperty("id")
	private long id;

	@JsonProperty("userType")
	private UserType userType;

	@Column(name = "FIRST_NAME")
	@JsonProperty("first_name")
	private String firstName;

	@Column(name = "LAST_NAME")
	@JsonProperty("last_name")
	private String lastName;

	@Column(name = "MOBILE_NUMBER")
	@JsonProperty("mobile_number")
	private String mobileNo;

	@Column(name = "EMAIL_ID")
	@JsonProperty("email_address")
	private String emailId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

}
