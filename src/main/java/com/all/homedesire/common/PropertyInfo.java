package com.all.homedesire.common;

import java.util.List;

import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyFeature;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertyStructure;
import com.all.homedesire.entities.PropertyVideo;
import com.all.homedesire.entities.User;
import com.all.homedesire.resources.dto.property.Purpose;
import com.all.homedesire.resources.dto.property.SubType;
import com.all.homedesire.resources.dto.property.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.State;

public class PropertyInfo {
	@JsonProperty("id")
	private Long id;
	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("city_name")
	private String cityName;
	@JsonProperty("state_name")
	private String stateName;
	@JsonProperty("country_name")
	private String countryName;
	@JsonProperty("area_name")
	private String areaName;
	@JsonProperty("property_type_id")
	private Long propertyTypeId;
	@JsonProperty("sub_type_id")
	private Long propertySubTypeId;
	@JsonProperty("purpose_id")
	private Long propertyPurposeId;
	@JsonProperty("property_status_id")
	private Long propertyStatusId;
	@JsonProperty("city")
	private City city;
	@JsonProperty("state")
	private State state;
	@JsonProperty("structure")
	private PropertyStructure propertyStructure;
	@JsonProperty("feature")
	private PropertyFeature propertyFeature;
	@JsonProperty("images")
	private List<PropertyImage> images;
	@JsonProperty("videos")
	private List<PropertyVideo> videos;
	@JsonProperty("attachments")
	private List<PropertyAttachment> attachments;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("address")
	private String address;
	@JsonProperty("land_mark")
	private String landMark;
	@JsonProperty("pin_code")
	private String pinCode;
	@JsonProperty("price")
	private Double price;
	@JsonProperty("tags")
	private String tags;
	@JsonProperty("featured_property")
	private boolean featuredProperty;
	@JsonProperty("lead_id")
	private long leadId;
	@JsonProperty("is_favorite")
	private boolean isFavorite;
	@JsonProperty("favorite_id")
	private long favoriteId;

	@JsonProperty("user")
	private User user;

	@JsonProperty("purpose")
	private Purpose purpose;

	@JsonProperty("type")
	private Type type;

	@JsonProperty("sub_type")
	private SubType subType;

	@JsonProperty("status")
	private PropertyStatus propertyStatus;

	@JsonProperty("is_published")
	private boolean isPublished;
	
	@JsonProperty("is_shared")
	private boolean isShared;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Long getPropertyTypeId() {
		return propertyTypeId;
	}

	public void setPropertyTypeId(Long propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}

	public Long getPropertySubTypeId() {
		return propertySubTypeId;
	}

	public void setPropertySubTypeId(Long propertySubTypeId) {
		this.propertySubTypeId = propertySubTypeId;
	}

	public Long getPropertyPurposeId() {
		return propertyPurposeId;
	}

	public void setPropertyPurposeId(Long propertyPurposeId) {
		this.propertyPurposeId = propertyPurposeId;
	}

	public Long getPropertyStatusId() {
		return propertyStatusId;
	}

	public void setPropertyStatusId(Long propertyStatusId) {
		this.propertyStatusId = propertyStatusId;
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

	public List<PropertyImage> getImages() {
		return images;
	}

	public void setImages(List<PropertyImage> images) {
		this.images = images;
	}

	public List<PropertyVideo> getVideos() {
		return videos;
	}

	public void setVideos(List<PropertyVideo> videos) {
		this.videos = videos;
	}

	public List<PropertyAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<PropertyAttachment> attachments) {
		this.attachments = attachments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public long getLeadId() {
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PropertyStatus getPropertyStatus() {
		return propertyStatus;
	}

	public void setPropertyStatus(PropertyStatus propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public Purpose getPurpose() {
		return purpose;
	}

	public void setPurpose(Purpose purpose) {
		this.purpose = purpose;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public SubType getSubType() {
		return subType;
	}

	public void setSubType(SubType subType) {
		this.subType = subType;
	}

	public long getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(long favoriteId) {
		this.favoriteId = favoriteId;
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

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}
	

}
