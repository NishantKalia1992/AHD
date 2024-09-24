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
@Table(name = "user_documents")
public class UserDocument extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2154048864177781509L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	private User user;

	@Column(name = "NAME",columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("name")
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

}
