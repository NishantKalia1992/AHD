package com.all.homedesire.entities;

import java.util.HashSet;
import java.util.Set;

import com.all.homedesire.enums.ERole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 861270090655246403L;

	@Enumerated(EnumType.STRING)
	@Column(name = "NAME", length = 20)
	@JsonProperty("name")
	private ERole name;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_service", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "SERVICE_ID"))
	private Set<DesiredService> service = new HashSet<>();

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

}
