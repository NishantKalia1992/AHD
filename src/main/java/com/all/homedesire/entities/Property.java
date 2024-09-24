package com.all.homedesire.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "property")
public class Property extends BaseEntity {

	private static final long serialVersionUID = 2009457482304226012L;

	@Basic(optional = true)
	@OneToOne(targetEntity = Lead.class, fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.PERSIST,
			CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private Lead lead;

	@JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonProperty("status")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PropertyStatus propertyStatus;

	@JoinColumn(name = "STRUCTURE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonProperty("structure")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PropertyStructure propertyStructure;

	@JoinColumn(name = "FEATURE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonProperty("feature")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PropertyFeature propertyFeature;

	@Column(name = "ADDRESSS")
	@JsonProperty("address")
	private String address;

	@Column(name = "LAND_MARK")
	@JsonProperty("land_mark")
	private String landMark;

	@Column(name = "PIN_CODE")
	@JsonProperty("pin_code")
	private String pinCode;

	@Column(name = "TAGS")
	@JsonProperty("tags")
	private String tags;

	// (Yes/No)
	@Column(name = "FEATURED_PROPERTY", columnDefinition = "BIT(1) DEFAULT 0")
	@JsonProperty("featured_property")
	private boolean featuredProperty;

	@Basic(optional = true)
	@Column(name = "IS_PUBLISH", columnDefinition = "BOOLEAN DEFAULT false")
	@JsonIgnore
	private boolean isPublished;
	
	@Basic(optional = true)
	@Column(name = "IS_SHARED", columnDefinition = "BOOLEAN DEFAULT false")
	@JsonIgnore
	@JsonProperty("is_Shared")
	private boolean isShared;

	@Transient
	@JsonProperty("created_on")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "GMT+5:30")
	private Date createdDate;

	@OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty("videos")
	private List<PropertyVideo> propertyVideos;

	@OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty("attachements")
	private List<PropertyAttachment> propertyAttachments;

	

	@Transient
	@JsonProperty("lead_id")
	private long leadId;

	public PropertyStatus getPropertyStatus() {
		return propertyStatus;
	}

	public void setPropertyStatus(PropertyStatus propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	public PropertyStructure getPropertyStructure() {
		return propertyStructure;
	}

	public void setPropertyStructure(PropertyStructure propertyStructure) {
		this.propertyStructure = propertyStructure;
	}

	public PropertyFeature getPropertyFeature() {
		return propertyFeature;
	}

	public void setPropertyFeature(PropertyFeature propertyFeature) {
		this.propertyFeature = propertyFeature;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLandMark() {
		return landMark;
	}

	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean isFeaturedProperty() {
		return featuredProperty;
	}

	public void setFeaturedProperty(boolean featuredProperty) {
		this.featuredProperty = featuredProperty;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public List<PropertyVideo> getPropertyVideos() {
		return propertyVideos;
	}

	public void setPropertyVideos(List<PropertyVideo> propertyVideos) {
		this.propertyVideos = propertyVideos;
	}

	public List<PropertyAttachment> getPropertyAttachments() {
		return propertyAttachments;
	}

	public void setPropertyAttachments(List<PropertyAttachment> propertyAttachments) {
		this.propertyAttachments = propertyAttachments;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	public long getLeadId() {
		long tempId = 0;
		if (leadId > 0) {
			tempId = leadId;
		} else {
			tempId = (lead != null) ? lead.getId() : 0;
		}
		return tempId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	
	public Date getCreatedDate() {
		if (createdDate == null) {
			createdDate = (getCreatedOn() != null) ? getCreatedOn() : null;
		}
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}
	
}
