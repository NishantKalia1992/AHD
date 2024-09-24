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
@Table(name = "property_structure")
public class PropertyStructure extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1363794086756896748L;

	@Column(name = "BEDROOMS")
	@JsonProperty("bed_rooms")
	private String bedRooms;

	@Column(name = "BATHROOMS")
	@JsonProperty("bath_rooms")
	private String bathRooms;

	@Column(name = "ROOMS")
	@JsonProperty("rooms")
	private String rooms;

	@OneToMany(mappedBy = "propertyStructure", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Property> properties;

	public String getBedRooms() {
		return bedRooms;
	}

	public void setBedRooms(String bedRooms) {
		this.bedRooms = bedRooms;
	}

	public String getBathRooms() {
		return bathRooms;
	}

	public void setBathRooms(String bathRooms) {
		this.bathRooms = bathRooms;
	}

	public String getRooms() {
		return rooms;
	}

	public void setRooms(String rooms) {
		this.rooms = rooms;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
