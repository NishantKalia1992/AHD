package com.all.homedesire.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "leads")
public class Lead extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -507344988984343143L;

	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = true)
	@JsonIgnore
	private User customer;

	@JoinColumn(name = "PARTNER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = true)
	@JsonIgnore
	private User partner;

	@JoinColumn(name = "PURPOSE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private PropertyPurpose propertyPurpose;

	@JoinColumn(name = "SUB_TYPE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private PropertySubType propertySubType;

	@JoinColumn(name = "AREA_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private Area area;

	@Column(name = "TITLE")
	@JsonProperty("title")
	private String title;

	@Column(name = "DESCRIPTION", columnDefinition = "LONGTEXT")
	@JsonProperty("description")
	private String description;

	@Column(name = "PRICE", columnDefinition = "DOUBLE DEFAULT 0")
	@JsonProperty("price")
	private Double price;

	// (Yes/No)
	@Column(name = "FEATURED_PROPERTY", columnDefinition = "BIT(1) DEFAULT 0")
	@JsonProperty("featured_property")
	private boolean featuredProperty;

	@Basic(optional = true)
	@Column(name = "IS_PUBLISH", columnDefinition = "BOOLEAN DEFAULT false")
	private boolean isPublished;

	@Transient
	@JsonProperty("created_on")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "GMT+5:30")
	private Date createdDate;

	@OneToMany(mappedBy = "lead", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Comment> comments;

	@Basic(optional = true)
	@OneToOne(mappedBy = "lead", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Property property;
	
	@Transient
	@JsonIgnore
	private State state;
	
	@Transient
	@JsonIgnore
	private City city;

	@Transient
	@JsonProperty("property_id")
	private long propertyId;

	@Transient
	@JsonProperty("customer_id")
	private long customerId;

	@Transient
	@JsonProperty("customer_name")
	private String customerName;

	@Transient
	@JsonProperty("partner_id")
	private long partnerId;

	@Transient
	@JsonProperty("partner_name")
	private String partnerName;

	@Transient
	@JsonProperty("purpose_id")
	private long purposeId;

	@Transient
	@JsonProperty("purpose_name")
	private String purposeName;

	@Transient
	@JsonProperty("type_id")
	private long typeId;

	@Transient
	@JsonProperty("type_name")
	private String typeName;

	@Transient
	@JsonProperty("sub_type_id")
	private long subTypeId;

	@Transient
	@JsonProperty("sub_type_name")
	private String subTypeName;

	@Transient
	@JsonProperty("comment_count")
	private int commentCount;

	@Transient
	@JsonProperty("area_name")
	private String areaName;

	@Transient
	@JsonProperty("city_name")
	private String cityName;

	@Transient
	@JsonProperty("state_name")
	private String stateName;

	@Transient
	@JsonProperty("country_name")
	private String countryName;

	@Transient
	@JsonProperty("area_id")
	private long areaId;

	@Transient
	@JsonProperty("city_id")
	private long cityId;

	@Transient
	@JsonProperty("state_id")
	private long stateId;

	@Transient
	@JsonProperty("country_id")
	private long countryId;

	@Transient
	@JsonProperty("images")
	private List<PropertyImage> images;

	@OneToMany(mappedBy = "lead", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<PropertyImage> propertyImages;

	@OneToMany(mappedBy = "lead", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Favorite> favorites;

	public PropertyPurpose getPropertyPurpose() {
		return propertyPurpose;
	}

	public void setPropertyPurpose(PropertyPurpose propertyPurpose) {
		this.propertyPurpose = propertyPurpose;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public User getPartner() {
		return partner;
	}

	public void setPartner(User partner) {
		this.partner = partner;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public long getPropertyId() {
		long propId = 0;
		if (propertyId > 0) {
			propId = propertyId;
		} else {
			propId = (property != null) ? property.getId() : 0;
		}
		return propId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public PropertySubType getPropertySubType() {
		return propertySubType;
	}

	public void setPropertySubType(PropertySubType propertySubType) {
		this.propertySubType = propertySubType;
	}

	public long getPurposeId() {
		long pId = 0;
		if (purposeId > 0) {
			pId = purposeId;
		} else {
			pId = (propertyPurpose != null) ? propertyPurpose.getId() : 0;
		}
		return pId;
	}

	public void setPurposeId(long purposeId) {
		this.purposeId = purposeId;
	}

	public String getPurposeName() {
		return (propertyPurpose != null) ? propertyPurpose.getName() : "";
	}

	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}

	public long getTypeId() {
		long tId = 0;
		if (typeId > 0) {
			tId = typeId;
		} else {
			tId = (propertySubType != null) ? propertySubType.getPropertyType().getId() : 0;
		}
		return tId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return (propertySubType != null) ? propertySubType.getPropertyType().getName() : "";
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public int getCommentCount() {
		return (comments != null && !comments.isEmpty()) ? comments.size() : 0;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public long getSubTypeId() {
		long tId = 0;
		if (subTypeId > 0) {
			tId = subTypeId;
		} else {
			tId = (propertySubType != null) ? propertySubType.getId() : 0;
		}
		return tId;
	}

	public void setSubTypeId(long subTypeId) {
		this.subTypeId = subTypeId;
	}

	public String getSubTypeName() {

		return (propertySubType != null) ? propertySubType.getName() : "";
	}

	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}

	public boolean isFeaturedProperty() {
		return featuredProperty;
	}

	public void setFeaturedProperty(boolean featuredProperty) {
		this.featuredProperty = featuredProperty;
	}

	public String getAreaName() {
		if (areaName == null) {
			areaName = (area != null) ? area.getName() : "";
		}
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getCityName() {
		if (cityName == null) {
			cityName = (area != null) ? area.getCity().getName() : "";
		}
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		if (stateName == null) {
			stateName = (area != null) ? area.getCity().getState().getName() : "";
		}
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountryName() {
		if (countryName == null) {
			countryName = (area != null) ? area.getCity().getState().getCountry().getName() : "";
		}
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public long getAreaId() {
		if (areaId <= 0) {
			areaId = (area != null) ? area.getId() : 0;
		}
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public long getCityId() {
		if (cityId <= 0) {
			cityId = (area != null) ? area.getCity().getId() : 0;
		}
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public long getStateId() {
		if (stateId <= 0) {
			stateId = (area != null) ? area.getCity().getState().getId() : 0;
		}
		return stateId;
	}

	public void setStateId(long stateId) {
		this.stateId = stateId;
	}

	public long getCountryId() {
		if (countryId <= 0) {
			countryId = (area != null) ? area.getCity().getState().getCountry().getId() : 0;
		}
		return countryId;
	}

	public void setCountryId(long countryId) {
		this.countryId = countryId;
	}

	public long getCustomerId() {
		if (customerId <= 0) {
			customerId = (customer != null) ? customer.getId() : 0;
		}
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		if (customerName == null) {
			customerName = (customer != null) ? customer.getFirstName() + " " + customer.getLastName() : "";
		}
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getPartnerId() {
		if (partnerId <= 0) {
			partnerId = (partner != null) ? partner.getId() : 0;
		}
		return partnerId;
	}

	public void setPartnerId(long partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerName() {
		if (partnerName == null) {
			partnerName = (partner != null) ? partner.getFirstName() + " " + partner.getLastName() : "";
		}
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
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

	public List<PropertyImage> getImages() {
		if (images == null || images.size() <= 0) {
			images = (propertyImages != null) ? propertyImages : new ArrayList<>();
		}
		return images;
	}

	public void setImages(List<PropertyImage> images) {
		this.images = images;
	}

	public List<PropertyImage> getPropertyImages() {

		return propertyImages;
	}

	public void setPropertyImages(List<PropertyImage> propertyImages) {
		this.propertyImages = propertyImages;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

}
