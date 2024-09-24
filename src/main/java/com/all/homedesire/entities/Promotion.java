package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotions")
public class Promotion extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -802713623715802089L;

	@Column(name = "TITLE", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("title")
	private String title;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255) DEFAULT ''")
	@JsonProperty("description")
	private String description;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
