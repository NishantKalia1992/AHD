package com.all.homedesire.entities;



import com.all.homedesire.enums.DService;
import com.all.homedesire.enums.DServiceType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "desired_service")
public class DesiredService extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5528652621132048164L;

	@Enumerated(EnumType.STRING)
	@Column(name = "NAME", length = 20)
	@JsonProperty("name")
	private DService name;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", length = 20)
	@JsonProperty("type")
	private DServiceType type;

	public DService getName() {
		return name;
	}

	public void setName(DService name) {
		this.name = name;
	}

	public DServiceType getType() {
		return type;
	}

	public void setType(DServiceType type) {
		this.type = type;
	}

}
