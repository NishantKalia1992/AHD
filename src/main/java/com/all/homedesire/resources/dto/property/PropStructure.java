package com.all.homedesire.resources.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PropStructure {
	@JsonProperty("id")
	private long id;

	@JsonProperty("bed_rooms")
	private String bedRooms;

	@JsonProperty("bath_rooms")
	private String bathRooms;

	@JsonProperty("rooms")
	private String rooms;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

}
