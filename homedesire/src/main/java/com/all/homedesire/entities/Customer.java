package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1401285664174332607L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("user")
	private User user;

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
	@JsonProperty("email_id")
	private String emailId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
