package com.all.homedesire.entities;

import java.util.List;

import com.all.homedesire.enums.EType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user_type")
public class UserType extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4706082113120968794L;

	@Enumerated(EnumType.STRING)
	@Column(name = "NAME", length = 20)
	@JsonIgnore
	private EType eType;

	@Column(name = "DESCRIPTION")
	@JsonProperty("description")
	private String description;

	@Transient
	@JsonProperty("name")
	private String name;

	@OneToMany(mappedBy = "userType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<User> users;

	public EType geteType() {
		return eType;
	}

	public void seteType(EType eType) {
		this.eType = eType;
	}

	public String getName() {
		return (name!=null) ? name : eType.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
