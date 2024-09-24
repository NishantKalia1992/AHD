package com.all.homedesire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
	private static final long serialVersionUID = -6092349191833904372L;

	@JoinColumn(name = "USER_TYPE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = false)
	@JsonProperty("userType")
	private UserType userType;

	@JoinColumn(name = "SERVICE_ID", referencedColumnName = "ID")
	@ManyToOne(optional = true)
	@JsonIgnore
	private HomeService homeService;

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH })
	@JoinTable(name = "user_cities", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "CITY_ID"))
	private Set<City> cities = new HashSet<>();

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH })
	@JoinTable(name = "user_areas", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "AREA_ID"))
	private Set<Area> areas = new HashSet<>();

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH })
	@JoinTable(name = "user_states", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "STATE_ID"))
	private Set<State> states = new HashSet<>();

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH })
	@JoinTable(name = "user_purposes", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "PURPOSE_ID"))
	private Set<PropertyPurpose> propertyPurposes = new HashSet<>();

	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	private Set<Role> roles = new HashSet<>();

	@Column(name = "PASSWORD")
	// @JsonIgnore
	@JsonProperty("password")
	private String password;

	@Column(name = "FIRST_NAME")
	@JsonProperty("first_name")
	private String firstName;

	@Column(name = "LAST_NAME")
	@JsonProperty("last_name")
	private String lastName;

	@Column(name = "MOBILE_NUMBER")
	@JsonProperty("mobile_number")
	private String mobileNo;

	@Column(name = "EMAIL_ID")
	@JsonProperty("email_address")
	private String emailId;

	@Column(name = "PHOTO")
	@JsonProperty("photo")
	private String photo;

	@Column(name = "RESET_PASSWORD_TOKEN")
	@JsonProperty("reset_password_token")
	private String resetPasswordToken;

	@Column(name = "STATUS")
	@JsonProperty("status")
	private String status;

	@Column(name = "PARENT_USER", columnDefinition = "BIGINT DEFAULT '0'")
	@JsonIgnore
	private long parentUser;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Customer> customers;

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Lead> customerLeads;

	@OneToMany(mappedBy = "partner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Lead> partnerLeads;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Partner> partners;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Comment> comments;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<UserContact> userContacts;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty("documents")
	private List<UserDocument> userDocuments;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty("payments")
	private List<UserPayment> userPayments;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<UserLog> userLogs;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty("favorites")
	private List<Favorite> favorites;

	@Transient
	@JsonProperty("user_purposes")
	private Set<PropertyPurpose> userPurposes;

	@Transient
	@JsonProperty("user_states")
	private Set<State> userStates;

	@Transient
	@JsonProperty("user_cities")
	private Set<City> userCities;

	@Transient
	@JsonProperty("user_areas")
	private Set<Area> userAreas;

	@Transient
	@JsonProperty("role")
	private Set<String> role;

	@Transient
	@JsonProperty("service_id")
	private long serviceId;

	@Transient
	@JsonProperty("service_name")
	private String serviceName;

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public Set<City> getCities() {
		return cities;
	}

	public void setCities(Set<City> cities) {
		this.cities = cities;
	}

	public Set<Area> getAreas() {
		return areas;
	}

	public void setAreas(Set<Area> areas) {
		this.areas = areas;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Set<City> getUserCities() {
		return userCities;
	}

	public void setUserCities(Set<City> userCities) {
		this.userCities = userCities;
	}

	public Set<Area> getUserAreas() {
		return userAreas;
	}

	public void setUserAreas(Set<Area> userAreas) {
		this.userAreas = userAreas;
	}

	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}

	public long getParentUser() {
		return parentUser;
	}

	public void setParentUser(long parentUser) {
		this.parentUser = parentUser;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<Lead> getCustomerLeads() {
		return customerLeads;
	}

	public void setCustomerLeads(List<Lead> customerLeads) {
		this.customerLeads = customerLeads;
	}

	public List<Lead> getPartnerLeads() {
		return partnerLeads;
	}

	public void setPartnerLeads(List<Lead> partnerLeads) {
		this.partnerLeads = partnerLeads;
	}

	public List<Partner> getPartners() {
		return partners;
	}

	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<UserContact> getUserContacts() {
		return userContacts;
	}

	public void setUserContacts(List<UserContact> userContacts) {
		this.userContacts = userContacts;
	}

	public List<UserLog> getUserLogs() {
		return userLogs;
	}

	public void setUserLogs(List<UserLog> userLogs) {
		this.userLogs = userLogs;
	}

	public Set<State> getStates() {
		return states;
	}

	public void setStates(Set<State> states) {
		this.states = states;
	}

	public Set<PropertyPurpose> getPropertyPurposes() {
		return propertyPurposes;
	}

	public void setPropertyPurposes(Set<PropertyPurpose> propertyPurposes) {
		this.propertyPurposes = propertyPurposes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<PropertyPurpose> getUserPurposes() {
		return userPurposes;
	}

	public void setUserPurposes(Set<PropertyPurpose> userPurposes) {
		this.userPurposes = userPurposes;
	}

	public Set<State> getUserStates() {
		return userStates;
	}

	public void setUserStates(Set<State> userStates) {
		this.userStates = userStates;
	}

	public HomeService getHomeService() {
		return homeService;
	}

	public void setHomeService(HomeService homeService) {
		this.homeService = homeService;
	}

	public long getServiceId() {
		long sId = 0;
		if (serviceId > 0) {
			sId = serviceId;
		} else {
			sId = (homeService != null) ? homeService.getId() : 0;
		}
		return sId;
	}

	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return (homeService != null) ? homeService.getName() : "";
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}

	public List<UserDocument> getUserDocuments() {
		return userDocuments;
	}

	public void setUserDocuments(List<UserDocument> userDocuments) {
		this.userDocuments = userDocuments;
	}

	public List<UserPayment> getUserPayments() {
		return userPayments;
	}

	public void setUserPayments(List<UserPayment> userPayments) {
		this.userPayments = userPayments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
