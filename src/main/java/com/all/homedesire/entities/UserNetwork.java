package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user_networks")
public class UserNetwork extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7675076046709647134L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private User user;
	
	@JoinColumn(name = "PARTNER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = true)
	@JsonIgnore
	private User partnerId;

	@JoinColumn(name = "AREA_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	private Area area;

	@Column(name = "FIRST_NAME", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("first_name")
	private String firstName;

	@Column(name = "LAST_NAME", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("last_name")
	private String lastName;

	@Column(name = "MOBILE_NUMBER", columnDefinition = "VARCHAR(13) DEFAULT ''")
	@JsonProperty("mobile_number")
	private String mobileNo;

	@Column(name = "EMAIL_ID", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("email_address")
	private String emailId;

	@Column(name = "ADDRESS", columnDefinition = "LONGTEXT")
	@JsonProperty("address")
	private String address;

	@Column(name = "ADDRESS1", columnDefinition = "LONGTEXT")
	@JsonProperty("address1")
	private String addressOne;

	@Basic(optional = true)
	@Column(name = "STREET", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("street")
	private String street;

	@Basic(optional = true)
	@Column(name = "APARTMENT", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("apartment")
	private String apartment;

	@Transient
	@JsonProperty("country_name")
	private String countryName;

	@Transient
	@JsonProperty("state_name")
	private String stateName;

	@Transient
	@JsonProperty("city_name")
	private String cityName;

	@Transient
	@JsonProperty("area_name")
	private String areaName;

	@Column(name = "ZIP_CODE")
	@JsonProperty("zip_code")
	private String zipCode;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFirstName() {
		return (firstName != null) ? firstName : "";
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return (lastName != null) ? lastName : "";
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNo() {
		return (mobileNo != null) ? mobileNo : "";
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return (emailId != null) ? emailId : "";
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAddress() {
		return (address != null) ? address : "";
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressOne() {
		return (address != null) ? addressOne : "";
	}

	public void setAddressOne(String addressOne) {
		this.addressOne = addressOne;
	}

	public String getStreet() {
		return (street != null) ? street : "";
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getApartment() {
		return (apartment != null) ? apartment : "";
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}

	public String getCountryName() {
		return (countryName != null) ? countryName : area.getCity().getState().getCountry().getName();
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getStateName() {
		return (stateName != null) ? stateName : area.getCity().getState().getName();
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCityName() {
		return (cityName != null) ? cityName : area.getCity().getName();
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getZipCode() {
		return (zipCode != null) ? zipCode : "";
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAreaName() {
		return (areaName != null) ? areaName : area.getName();
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public User getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(User partnerId) {
		this.partnerId = partnerId;
	}
	

}
