package com.all.homedesire.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "comments")
public class Comment extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2488355818460738805L;

	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("user")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private User user;

	@JoinColumn(name = "REQUEST_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("desired_request")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Lead lead;

	@Column(name = "COMMENT")
	@JsonProperty("comment")
	private String comment;

	@Transient
	@JsonProperty("created_on")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "GMT+5:30")
	private Date createdDate;
	
	@Transient
	@JsonProperty("parent_id")
	private long parentId;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	private Comment parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Comment> children = new HashSet<Comment>();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Comment getParent() {
		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public Set<Comment> getChildren() {
		return children;
	}

	public void setChildren(Set<Comment> children) {
		this.children = children;
	}

	public long getParentId() {
		if(parentId<=0) {
			parentId = (parent!=null) ? parent.getId() : 0;
		}
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public Date getCreatedDate() {
		if(createdDate==null) {
			createdDate = (getCreatedOn()!=null) ? getCreatedOn() : null;
		}
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
