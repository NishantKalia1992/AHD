package com.all.homedesire.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "property_sub_type")
public class PropertySubType extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4860659224416134551L;

	@JoinColumn(name = "TYPE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JsonIgnore
	private PropertyType propertyType;

	@Column(name = "NAME")
	@JsonProperty("name")
	private String name;

	@Column(name = "DESCRIPTION")
	@JsonProperty("description")
	private String description;

	@Transient
	@JsonProperty("property_type_id")
	private long propertyTypeId;

	@Transient
	@JsonProperty("property_type_name")
	private String propertyTypeName;

	@OneToMany(mappedBy = "propertySubType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Lead> leads;

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
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

	public long getPropertyTypeId() {
		long typeId = 0;
		if (propertyTypeId > 0) {
			typeId = propertyTypeId;
		} else {
			typeId = (propertyType != null) ? propertyType.getId() : 0;
		}
		return typeId;
	}

	public void setPropertyTypeId(long propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}

	public String getPropertyTypeName() {
		String typeName = "";
		if (propertyTypeId > 0) {
			typeName = propertyTypeName;
		} else {
			typeName = (propertyType != null) ? propertyType.getName() : "";
		}
		return typeName;
	}

	public void setPropertyTypeName(String propertyTypeName) {
		this.propertyTypeName = propertyTypeName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Lead> getLeads() {
		return leads;
	}

	public void setLeads(List<Lead> leads) {
		this.leads = leads;
	}

}
