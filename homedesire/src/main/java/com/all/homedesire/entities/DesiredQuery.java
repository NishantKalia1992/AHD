package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "desired_query")
public class DesiredQuery extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1142069988592761384L;

	@JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("service")
	private HomeService service;

	@JoinColumn(name = "CITY_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("city")
	private City city;

	@JoinColumn(name = "AREA_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("area")
	private Area area;

	@Column(name = "NAME")
	@JsonProperty("name")
	private String name;

	@Column(name = "MOBILE_NUMBER")
	@JsonProperty("mobile_number")
	private String mobileNumber;

	@Column(name = "EMAIL_ADDRESS")
	@JsonProperty("email_address")
	private String emailAddress;

	@Column(name = "QUERY")
	@JsonProperty("query")
	private String query;

	public HomeService getService() {
		return service;
	}

	public void setService(HomeService service) {
		this.service = service;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
