package com.all.homedesire.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "app_notification")
public class AppNotification extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3481811848354984889L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private User user;

	@Column(name = "NOTIFICATION_TITLE")
	@JsonProperty("notification_title")
	private String notificationTitle;

	@Column(name = "NOTIFICATION_TEXT", columnDefinition = "LONGTEXT")
	@JsonProperty("notification_text")
	private String notificationText;

	@Column(name = "NOTIFICATION_FOR", length = 512)
	@JsonProperty("notification_for")
	private String notificationFor;

	@Column(name = "IS_READ", columnDefinition = "boolean default false")
	@JsonProperty("is_read")
	Boolean isRead;

	@Column(name = "CREATED_BY")
	@JsonProperty("created_by")
	private long createdBy;

	@Column(name = "UPDATED_BY")
	@JsonProperty("updated_by")
	private long updatedBy;

	@Transient
	@JsonProperty("user_id")
	private long userId;

	@Transient
	@JsonProperty("user_name")
	private String userName;

	@Transient
	@JsonProperty("user_type")
	private String userType;
	
	@JsonProperty("created_at")
	    @Override
	    public Date getCreatedOn() {
	        return super.getCreatedOn();
	    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public String getNotificationFor() {
		return notificationFor;
	}

	public void setNotificationFor(String notificationFor) {
		this.notificationFor = notificationFor;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public long getUserId() {
		return (userId > 0) ? userId : user.getId();
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return (userName != null && !userName.equals("")) ? userName : user.getFirstName() + " " + user.getLastName();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		return (userType != null && !userType.equals("")) ? userType : user.getUserType().getName().toString();
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
