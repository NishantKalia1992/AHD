package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user_log")
public class UserLog extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5113651260033173968L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private User user;

	@Column(name = "ACTION")
	@JsonProperty("action")
	private String action;

	@Column(name = "SUB_ACTION")
	@JsonProperty("sub_action")
	private String subAction;

	@Transient
	@JsonProperty("user_name")
	private String userName;

	@Transient
	@JsonProperty("user_type")
	private String type;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSubAction() {
		return subAction;
	}

	public void setSubAction(String subAction) {
		this.subAction = subAction;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
