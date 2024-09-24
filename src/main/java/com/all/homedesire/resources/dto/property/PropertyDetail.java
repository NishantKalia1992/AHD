package com.all.homedesire.resources.dto.property;

import java.util.List;

import com.all.homedesire.entities.Comment;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyVideo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyDetail {

	@JsonProperty("id")
	private long id;

	@JsonProperty("lead_id")
	private long leadId;

	@JsonProperty("customer")
	private Customer customer;

	@JsonProperty("Partner")
	private Partner partner;

	@JsonProperty("purpose")
	private Purpose purpose;

	@JsonProperty("type")
	private Type type;

	@JsonProperty("sub_type")
	private SubType subType;

	@JsonProperty("area")
	private PropArea propArea;

	@JsonProperty("status")
	private PropStatus propStatus;

	@JsonProperty("structure")
	private PropStructure propStructure;

	@JsonProperty("feature")
	private PropFeature propFeature;

	// Start
	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("budget")
	private Double price;

	@JsonProperty("address")
	private String address;

	@JsonProperty("land_mark")
	private String landMark;

	@JsonProperty("pin_code")
	private String pinCode;

	@JsonProperty("tags")
	private String tags;

	@JsonProperty("featured_property")
	private boolean featuredProperty;

	@JsonProperty("comment_count")
	private int commentCount;

	@JsonProperty("comments")
	private List<Comment> comments;

	@JsonProperty("is_published")
	private boolean isPublished;

	@JsonProperty("is_favorite")
	private boolean isFavorite;

	@JsonProperty("favorite_id")
	private long favoriteId;

	@JsonProperty("city")
	private PropCity propCity;

	@JsonProperty("state")
	private PropState propState;

	@JsonProperty("country")
	private PropCountry propCountry;

	@JsonProperty("images")
	private List<PropertyImage> images;
	@JsonProperty("videos")
	private List<PropertyVideo> videos;
	@JsonProperty("attachments")
	private List<PropertyAttachment> attachments;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
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

	public PropArea getPropArea() {
		return propArea;
	}

	public void setPropArea(PropArea propArea) {
		this.propArea = propArea;
	}

	public PropStatus getPropStatus() {
		return propStatus;
	}

	public void setPropStatus(PropStatus propStatus) {
		this.propStatus = propStatus;
	}

	public PropStructure getPropStructure() {
		return propStructure;
	}

	public void setPropStructure(PropStructure propStructure) {
		this.propStructure = propStructure;
	}

	public PropFeature getPropFeature() {
		return propFeature;
	}

	public void setPropFeature(PropFeature propFeature) {
		this.propFeature = propFeature;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public long getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(long favoriteId) {
		this.favoriteId = favoriteId;
	}

	public PropCity getPropCity() {
		return propCity;
	}

	public void setPropCity(PropCity propCity) {
		this.propCity = propCity;
	}

	public PropState getPropState() {
		return propState;
	}

	public void setPropState(PropState propState) {
		this.propState = propState;
	}

	public PropCountry getPropCountry() {
		return propCountry;
	}

	public void setPropCountry(PropCountry propCountry) {
		this.propCountry = propCountry;
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

	public long getLeadId() {
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public boolean isPublished() {
		return isPublished;
	}

	public void setPublished(boolean isPublished) {
		this.isPublished = isPublished;
	}

}
