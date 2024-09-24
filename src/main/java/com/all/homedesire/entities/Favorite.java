package com.all.homedesire.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "favorites")
public class Favorite extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3830340029326437068L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private User user;

	@JoinColumn(name = "LEAD_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	private Lead lead;

	@Transient
	@JsonProperty("user_id")
	private long userId;

	@Transient
	@JsonProperty("user_name")
	private String userName;

	@Transient
	@JsonProperty("user_type")
	private String type;

	@Transient
	@JsonProperty("property_id")
	private long propertyId;
	
	@Transient
	@JsonProperty("lead_id")
	private long leadId;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getType() {
		return (type != null && !type.equals("")) ? type : user.getUserType().getName().toString();
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public long getPropertyId() {
		if(propertyId<=0) {
			propertyId = (lead.getProperty()!=null) ? lead.getProperty().getId() : 0;
		}
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}

	public long getLeadId() {
		if(leadId<=0) {
			leadId = (lead!=null) ? lead.getId() : 0;
		}
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
