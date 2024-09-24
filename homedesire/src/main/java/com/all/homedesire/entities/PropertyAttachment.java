package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "property_attachments")
public class PropertyAttachment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 479314846184879845L;

	@JsonBackReference
	@ManyToOne(targetEntity = Property.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
			CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
	private Property property;

	@Column(name = "NAME")
	@JsonProperty("name")
	private String name;

	@Column(name = "DESCRIPTION")
	@JsonProperty("description")
	private String description;

	@Column(name = "ATTACHMENT_PATH")
	private String attachmentPath;

	@Column(name = "ATTACHMENT_URL")
	@JsonProperty("attachment_url")
	private String attachmentUrl;

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
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

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

}
