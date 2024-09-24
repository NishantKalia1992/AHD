package com.all.homedesire.common;

import java.util.List;

import org.springframework.core.io.UrlResource;

import com.all.homedesire.entities.AppNotification;
import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.ContactUs;
import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.Customer;
import com.all.homedesire.entities.DesireEmail;
import com.all.homedesire.entities.DesiredQuery;
import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.Partner;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyFeature;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertyStructure;
import com.all.homedesire.entities.PropertySubType;
import com.all.homedesire.entities.PropertyType;
import com.all.homedesire.entities.PropertyVideo;
import com.all.homedesire.entities.Comment;
import com.all.homedesire.entities.Role;
import com.all.homedesire.entities.HomeService;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.entities.UserDocument;
import com.all.homedesire.entities.UserLog;
import com.all.homedesire.entities.UserNetwork;
import com.all.homedesire.entities.UserPayment;
import com.all.homedesire.entities.UserType;
import com.all.homedesire.resources.dto.property.PropertyDetail;
import com.all.homedesire.security.service.UserDetailsImpl;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("DesireStatusFilter")
public class DesireStatus {
	private String statusType;
	private String text;
	private String token;
	private String type = "Bearer";
	private String fileName;
	private long totalRecord;
	private long totalPage;
	private int maxRecord;
	private int pageNumber;
	private String assetUrl;
	private UrlResource resource;
	
	private String firstName;
    private String lastName;


	private UserDetailsImpl userDetails;
	private User user;
	private List<User> users;

	private UserContact contact;
	private List<UserContact> contacts;

	private UserDocument document;
	private List<UserDocument> documents;

	private UserPayment payment;
	private List<UserPayment> payments;

	private UserNetwork network;
	private List<UserNetwork> networks;

	private UserType userType;
	private List<UserType> userTypes;

	private UserLog userLog;
	private List<UserLog> userLogs;

	private Role role;
	private List<Role> roles;

	private Country country;
	private List<Country> countries;

	private State state;
	private List<State> states;

	private City city;
	private List<City> cities;

	private Area area;
	private List<Area> areas;

	private HomeService service;
	private List<HomeService> services;

	private ContactUs contactUs;
	private List<ContactUs> contactUss;

	private DesiredQuery desiredQuery;
	private List<DesiredQuery> desiredQueries;

	private Lead lead;
	private List<Lead> leads;

	private Comment comment;
	private List<Comment> comments;

	private Customer customer;
	private List<Customer> customers;

	private Partner partner;
	private List<Partner> partners;

	private PropertyInfo propertyInfo;
	private List<PropertyInfo> propertyInfos;

	private PropertyDetail property;
	private List<PropertyDetail> properties;

	private PropertyType propertyType;
	private List<PropertyType> propertyTypes;

	private PropertySubType propertySubType;
	private List<PropertySubType> propertySubTypes;

	private PropertyPurpose propertyPurpose;
	private List<PropertyPurpose> propertyPurposes;

	private PropertyStatus propertyStatus;
	private List<PropertyStatus> propertyStatuss;

	private PropertyStructure propertyStructure;
	private List<PropertyStructure> propertyStructures;

	private PropertyFeature propertyFeature;
	private List<PropertyFeature> propertyFeatures;

	private PropertyImage propertyImage;
	private List<PropertyImage> propertyImages;

	private PropertyAttachment propertyAttachment;
	private List<PropertyAttachment> propertyAttachments;

	private PropertyVideo propertyVideo;
	private List<PropertyVideo> propertyVideos;

	private DesireEmail desireEmail;
	private List<DesireEmail> desireEmails;

	private AppNotification notification;
	private List<AppNotification> notifications;

	private Favorite favorite;
	private List<Favorite> favorites;

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}

	public int getMaxRecord() {
		return maxRecord;
	}

	public void setMaxRecord(int maxRecord) {
		this.maxRecord = maxRecord;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getAssetUrl() {
		return assetUrl;
	}

	public void setAssetUrl(String assetUrl) {
		this.assetUrl = assetUrl;
	}

	public UserDetailsImpl getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserDetailsImpl userDetails) {
		this.userDetails = userDetails;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public List<UserType> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<UserType> userTypes) {
		this.userTypes = userTypes;
	}

	public UserLog getUserLog() {
		return userLog;
	}

	public void setUserLog(UserLog userLog) {
		this.userLog = userLog;
	}

	public List<UserLog> getUserLogs() {
		return userLogs;
	}

	public void setUserLogs(List<UserLog> userLogs) {
		this.userLogs = userLogs;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}

	public HomeService getService() {
		return service;
	}

	public void setService(HomeService service) {
		this.service = service;
	}

	public List<HomeService> getServices() {
		return services;
	}

	public void setServices(List<HomeService> services) {
		this.services = services;
	}

	public ContactUs getContactUs() {
		return contactUs;
	}

	public void setContactUs(ContactUs contactUs) {
		this.contactUs = contactUs;
	}

	public List<ContactUs> getContactUss() {
		return contactUss;
	}

	public void setContactUss(List<ContactUs> contactUss) {
		this.contactUss = contactUss;
	}

	public DesiredQuery getDesiredQuery() {
		return desiredQuery;
	}

	public void setDesiredQuery(DesiredQuery desiredQuery) {
		this.desiredQuery = desiredQuery;
	}

	public List<DesiredQuery> getDesiredQueries() {
		return desiredQueries;
	}

	public void setDesiredQueries(List<DesiredQuery> desiredQueries) {
		this.desiredQueries = desiredQueries;
	}

	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public List<Lead> getLeads() {
		return leads;
	}

	public void setLeads(List<Lead> leads) {
		this.leads = leads;
	}

	public Comment getRequestComment() {
		return comment;
	}

	public void setRequestComment(Comment comment) {
		this.comment = comment;
	}

	public List<Comment> getRequestComments() {
		return comments;
	}

	public void setRequestComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public List<Partner> getPartners() {
		return partners;
	}

	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public List<PropertyInfo> getPropertyInfos() {
		return propertyInfos;
	}

	public void setPropertyInfos(List<PropertyInfo> propertyInfos) {
		this.propertyInfos = propertyInfos;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public PropertyDetail getProperty() {
		return property;
	}

	public void setProperty(PropertyDetail property) {
		this.property = property;
	}

	public List<PropertyDetail> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyDetail> properties) {
		this.properties = properties;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public List<PropertyType> getPropertyTypes() {
		return propertyTypes;
	}

	public void setPropertyTypes(List<PropertyType> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	public PropertyPurpose getPropertyPurpose() {
		return propertyPurpose;
	}

	public void setPropertyPurpose(PropertyPurpose propertyPurpose) {
		this.propertyPurpose = propertyPurpose;
	}

	public List<PropertyPurpose> getPropertyPurposes() {
		return propertyPurposes;
	}

	public void setPropertyPurposes(List<PropertyPurpose> propertyPurposes) {
		this.propertyPurposes = propertyPurposes;
	}

	public PropertyStatus getPropertyStatus() {
		return propertyStatus;
	}

	public void setPropertyStatus(PropertyStatus propertyStatus) {
		this.propertyStatus = propertyStatus;
	}

	public List<PropertyStatus> getPropertyStatuss() {
		return propertyStatuss;
	}

	public void setPropertyStatuss(List<PropertyStatus> propertyStatuss) {
		this.propertyStatuss = propertyStatuss;
	}

	public PropertyStructure getPropertyStructure() {
		return propertyStructure;
	}

	public void setPropertyStructure(PropertyStructure propertyStructure) {
		this.propertyStructure = propertyStructure;
	}

	public List<PropertyStructure> getPropertyStructures() {
		return propertyStructures;
	}

	public void setPropertyStructures(List<PropertyStructure> propertyStructures) {
		this.propertyStructures = propertyStructures;
	}

	public PropertyFeature getPropertyFeature() {
		return propertyFeature;
	}

	public void setPropertyFeature(PropertyFeature propertyFeature) {
		this.propertyFeature = propertyFeature;
	}

	public List<PropertyFeature> getPropertyFeatures() {
		return propertyFeatures;
	}

	public void setPropertyFeatures(List<PropertyFeature> propertyFeatures) {
		this.propertyFeatures = propertyFeatures;
	}

	public PropertyImage getPropertyImage() {
		return propertyImage;
	}

	public void setPropertyImage(PropertyImage propertyImage) {
		this.propertyImage = propertyImage;
	}

	public List<PropertyImage> getPropertyImages() {
		return propertyImages;
	}

	public void setPropertyImages(List<PropertyImage> propertyImages) {
		this.propertyImages = propertyImages;
	}

	public PropertyAttachment getPropertyAttachment() {
		return propertyAttachment;
	}

	public void setPropertyAttachment(PropertyAttachment propertyAttachment) {
		this.propertyAttachment = propertyAttachment;
	}

	public List<PropertyAttachment> getPropertyAttachments() {
		return propertyAttachments;
	}

	public void setPropertyAttachments(List<PropertyAttachment> propertyAttachments) {
		this.propertyAttachments = propertyAttachments;
	}

	public PropertyVideo getPropertyVideo() {
		return propertyVideo;
	}

	public void setPropertyVideo(PropertyVideo propertyVideo) {
		this.propertyVideo = propertyVideo;
	}

	public List<PropertyVideo> getPropertyVideos() {
		return propertyVideos;
	}

	public void setPropertyVideos(List<PropertyVideo> propertyVideos) {
		this.propertyVideos = propertyVideos;
	}

	public DesireEmail getDesireEmail() {
		return desireEmail;
	}

	public void setDesireEmail(DesireEmail desireEmail) {
		this.desireEmail = desireEmail;
	}

	public List<DesireEmail> getDesireEmails() {
		return desireEmails;
	}

	public void setDesireEmails(List<DesireEmail> desireEmails) {
		this.desireEmails = desireEmails;
	}

	public UserContact getContact() {
		return contact;
	}

	public void setContact(UserContact contact) {
		this.contact = contact;
	}

	public List<UserContact> getContacts() {
		return contacts;
	}

	public void setContacts(List<UserContact> contacts) {
		this.contacts = contacts;
	}

	public AppNotification getNotification() {
		return notification;
	}

	public void setNotification(AppNotification notification) {
		this.notification = notification;
	}

	public List<AppNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<AppNotification> notifications) {
		this.notifications = notifications;
	}

	public UserNetwork getNetwork() {
		return network;
	}

	public void setNetwork(UserNetwork network) {
		this.network = network;
	}

	public List<UserNetwork> getNetworks() {
		return networks;
	}

	public void setNetworks(List<UserNetwork> networks) {
		this.networks = networks;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public UrlResource getResource() {
		return resource;
	}

	public void setResource(UrlResource resource) {
		this.resource = resource;
	}

	public PropertySubType getPropertySubType() {
		return propertySubType;
	}

	public void setPropertySubType(PropertySubType propertySubType) {
		this.propertySubType = propertySubType;
	}

	public List<PropertySubType> getPropertySubTypes() {
		return propertySubTypes;
	}

	public void setPropertySubTypes(List<PropertySubType> propertySubTypes) {
		this.propertySubTypes = propertySubTypes;
	}

	public Favorite getFavorite() {
		return favorite;
	}

	public void setFavorite(Favorite favorite) {
		this.favorite = favorite;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorite> favorites) {
		this.favorites = favorites;
	}

	public UserDocument getDocument() {
		return document;
	}

	public void setDocument(UserDocument document) {
		this.document = document;
	}

	public List<UserDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<UserDocument> documents) {
		this.documents = documents;
	}

	public UserPayment getPayment() {
		return payment;
	}

	public void setPayment(UserPayment payment) {
		this.payment = payment;
	}

	public List<UserPayment> getPayments() {
		return payments;
	}

	public void setPayments(List<UserPayment> payments) {
		this.payments = payments;
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

}
