package com.all.homedesire.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "areas")
public class Area extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6935163607757698516L;

	@JoinColumn(name = "CITY_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonIgnore
	private City city;

	@Column(name = "NAME")
	@JsonProperty("name")
	private String name;

	@Column(name = "DESCRIPTION")
	@JsonProperty("description")
	private String description;

	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Lead> leads;

	@Transient
	@JsonProperty("city_id")
	private long cityId;

	@Transient
	@JsonProperty("city_name")
	private String cityName;

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
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

	public List<Lead> getLeads() {
		return leads;
	}

	public void setLeads(List<Lead> leads) {
		this.leads = leads;
	}

	public long getCityId() {
		if (cityId <= 0) {
			cityId = (city != null) ? city.getId() : 0;
		}
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		if (cityName == null) {
			cityName = (city != null) ? city.getName() : "";
		}
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
