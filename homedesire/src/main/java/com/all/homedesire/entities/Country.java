package com.all.homedesire.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6684613836101812517L;

	@Column(name = "NAME", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("name")
	private String name;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("description")
	private String description;

	@Column(name = "SHORT_CODE", columnDefinition = "VARCHAR(5) DEFAULT ''")
	@JsonProperty("short_code")
	private String shortCode;

	@Column(name = "COUNTRY_CODE", columnDefinition = "VARCHAR(5) DEFAULT ''")
	@JsonProperty("country_code")
	private String countryCode;

	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<State> states;

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

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

}
