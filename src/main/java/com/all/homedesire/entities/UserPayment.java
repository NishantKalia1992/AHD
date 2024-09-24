package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user_payments")
public class UserPayment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9127025908332910009L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	private User user;

	@Column(name = "MODE", columnDefinition = "VARCHAR(255) DEFAULT '' COMMENT  'ONLINE/OFFLINE'")
	@JsonProperty("mode")
	private String mode;

	@Column(name = "GATEWAY", columnDefinition = "VARCHAR(255) DEFAULT '' COMMENT  'UPI, ONLINE BANKING, NEFT'")
	@JsonProperty("gateway")
	private String gateway;

	@Column(name = "PROVIDER", columnDefinition = "VARCHAR(255) DEFAULT '' COMMENT  'PAYTM/PHONEPE/GPAY'")
	@JsonProperty("provider")
	private String provider;

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE DEFAULT 0 COMMENT  'AMOUNT PAID'")
	@JsonProperty("amount")
	private double amount;

	@Column(name = "STATUS", columnDefinition = "VARCHAR(255) DEFAULT '' COMMENT  'SUCCESS/FAILURE'")
	@JsonProperty("status")
	private String status;

	@Column(name = "DOCUMENT_URL", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("document_url")
	private String documentUrl;

	@Transient
	@JsonProperty("user_id")
	private long userId;

	@Transient
	@JsonProperty("user_name")
	private String userName;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public long getUserId() {
		if (userId <= 0) {
			userId = (user != null) ? user.getId() : 0;
		}
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		if(userName==null || userName.equals("")) {
			userName = (user!=null) ? user.getFirstName()+ " "+user.getLastName() : "";
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
