package com.all.homedesire.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.Property;
import com.all.homedesire.entities.User;
import com.all.homedesire.resources.dto.property.Customer;
import com.all.homedesire.resources.dto.property.Partner;
import com.all.homedesire.resources.dto.property.PropArea;
import com.all.homedesire.resources.dto.property.PropCity;
import com.all.homedesire.resources.dto.property.PropCountry;
import com.all.homedesire.resources.dto.property.PropFeature;
import com.all.homedesire.resources.dto.property.PropState;
import com.all.homedesire.resources.dto.property.PropStatus;
import com.all.homedesire.resources.dto.property.PropStructure;
import com.all.homedesire.resources.dto.property.PropertyDetail;
import com.all.homedesire.resources.dto.property.Purpose;
import com.all.homedesire.resources.dto.property.SubType;
import com.all.homedesire.resources.dto.property.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PropertyMapper {
	static Logger LOGGER = LoggerFactory.getLogger(PropertyMapper.class);

	public static List<PropertyDetail> getMappedProperties(List<Lead> leads, User user) {
		List<PropertyDetail> propertyDetails = new ArrayList<>();
		for (Lead lead : leads) {
			propertyDetails.add(getMappedProperty(lead, user));
		}
		return propertyDetails;
	}

	public static PropertyDetail getMappedProperty(Lead lead, User user) {
		PropertyDetail propertyDetail = new PropertyDetail();
		propertyDetail.setLeadId(lead.getId());
		// Customer
		if (lead.getCustomer() != null) {
			Customer customer = (Customer) getObjectMapped(lead.getCustomer(), Customer.class);
			propertyDetail.setCustomer(customer);
		}
		// Partner
		if (lead.getPartner() != null) {
			Partner partner = (Partner) getObjectMapped(lead.getPartner(), Partner.class);
			propertyDetail.setPartner(partner);
		}
		// Purpose
		Purpose purpose = (Purpose) getObjectMapped(lead.getPropertyPurpose(), Purpose.class);
		propertyDetail.setPurpose(purpose);
		// Type
		Type type = (Type) getObjectMapped(lead.getPropertySubType().getPropertyType(), Type.class);
		propertyDetail.setType(type);
		// SubType
		SubType subType = (SubType) getObjectMapped(lead.getPropertySubType(), SubType.class);
		propertyDetail.setSubType(subType);
		// Area
		LOGGER.info("PropertyMapper >> lead.getArea() >> " + lead.getArea());
		PropArea propArea = (PropArea) getObjectMapped(lead.getArea(), PropArea.class);
		propertyDetail.setPropArea(propArea);
		// City
		LOGGER.info("PropertyMapper >> lead.getArea().getCity() >> " + lead.getArea().getCity());
		PropCity propCity = (PropCity) getObjectMapped(lead.getArea().getCity(), PropCity.class);
		propertyDetail.setPropCity(propCity);
		// State
		LOGGER.info("PropertyMapper >> lead.getArea().getCity().getState() >> " + lead.getArea().getCity().getState());
		PropState propState = (PropState) getObjectMapped(lead.getArea().getCity().getState(), PropState.class);
		propertyDetail.setPropState(propState);
		// Country
		LOGGER.info("PropertyMapper >> lead.getArea().getCity().getState().getCountry() >> "
				+ lead.getArea().getCity().getState().getCountry());
		PropCountry propCountry = (PropCountry) getObjectMapped(lead.getArea().getCity().getState().getCountry(),
				PropCountry.class);
		propertyDetail.setPropCountry(propCountry);

		// title
		propertyDetail.setTitle(lead.getTitle());
		// description
		propertyDetail.setDescription(lead.getDescription());
		// budget
		propertyDetail.setPrice(lead.getPrice());

		// featured_property
		propertyDetail.setFeaturedProperty(lead.isFeaturedProperty());
		// comment_count
		propertyDetail.setCommentCount(lead.getCommentCount());
		// comments
		propertyDetail.setComments(lead.getComments());

		propertyDetail.setPublished(lead.isPublished());
		// isFavorite
		boolean isFavorite = false;
		long favoriteId = 0;
		if (user != null) {
			List<Favorite> favorites = user.getFavorites();
			for (Favorite favorite : favorites) {
				if (favorite.getLead().getId() == lead.getId()) {
					isFavorite = true;
					favoriteId = favorite.getId();
				}
			}
		}
		propertyDetail.setFavorite(isFavorite);
		propertyDetail.setFavoriteId(favoriteId);

		propertyDetail.setImages(lead.getPropertyImages());
		// Property details ------Started-------------
		if (lead.getProperty() != null) {
			propertyDetail.setId(lead.getProperty().getId());
			// status
			PropStatus propStatus = (PropStatus) getObjectMapped(lead.getProperty().getPropertyStatus(),
					PropStatus.class);
			propertyDetail.setPropStatus(propStatus);
			// structure
			PropStructure propStructure = (PropStructure) getObjectMapped(lead.getProperty().getPropertyStructure(),
					PropStructure.class);
			propertyDetail.setPropStructure(propStructure);
			// feature
			PropFeature propFeature = (PropFeature) getObjectMapped(lead.getProperty().getPropertyFeature(),
					PropFeature.class);
			propertyDetail.setPropFeature(propFeature);

			// address
			propertyDetail.setAddress(lead.getProperty().getAddress());
			// land_mark
			propertyDetail.setLandMark(lead.getProperty().getLandMark());
			// pin_code
			propertyDetail.setPinCode(lead.getProperty().getPinCode());
			// tags
			propertyDetail.setTags(lead.getProperty().getTags());

			propertyDetail.setAttachments(lead.getProperty().getPropertyAttachments());
			propertyDetail.setVideos(lead.getProperty().getPropertyVideos());
		}
		return propertyDetail;
	}

	public static PropertyDetail getMappedProperty(Lead lead, Property property, User user) {
		PropertyDetail propertyDetail = new PropertyDetail();
		propertyDetail.setId(property.getId());
		// Customer
		if (lead.getCustomer() != null) {
			Customer customer = (Customer) getObjectMapped(lead.getCustomer(), Customer.class);
			propertyDetail.setCustomer(customer);
		}
		// Partner
		if (lead.getPartner() != null) {
			Partner partner = (Partner) getObjectMapped(lead.getPartner(), Partner.class);
			propertyDetail.setPartner(partner);
		}
		// Customer
		Purpose purpose = (Purpose) getObjectMapped(lead.getPropertyPurpose(), Purpose.class);
		propertyDetail.setPurpose(purpose);
		// Customer
		Type type = (Type) getObjectMapped(lead.getPropertySubType().getPropertyType(), Type.class);
		propertyDetail.setType(type);
		// Customer
		SubType subType = (SubType) getObjectMapped(lead.getPropertySubType(), SubType.class);
		propertyDetail.setSubType(subType);
		// Area
		LOGGER.info("PropertyMapper >> lead.getArea() >> " + lead.getArea());
		PropArea propArea = (PropArea) getObjectMapped(lead.getArea(), PropArea.class);
		propertyDetail.setPropArea(propArea);
		// City
		LOGGER.info("PropertyMapper >> lead.getArea().getCity() >> " + lead.getArea().getCity());
		PropCity propCity = (PropCity) getObjectMapped(lead.getArea().getCity(), PropCity.class);
		propertyDetail.setPropCity(propCity);
		// State
		LOGGER.info("PropertyMapper >> lead.getArea().getCity().getState() >> " + lead.getArea().getCity().getState());
		PropState propState = (PropState) getObjectMapped(lead.getArea().getCity().getState(), PropState.class);
		propertyDetail.setPropState(propState);
		// Country
		LOGGER.info("PropertyMapper >> lead.getArea().getCity().getState().getCountry() >> "
				+ lead.getArea().getCity().getState().getCountry());
		PropCountry propCountry = (PropCountry) getObjectMapped(lead.getArea().getCity().getState().getCountry(),
				PropCountry.class);
		propertyDetail.setPropCountry(propCountry);
		// status
		PropStatus propStatus = (PropStatus) getObjectMapped(property.getPropertyStatus(), PropStatus.class);
		propertyDetail.setPropStatus(propStatus);
		// structure
		PropStructure propStructure = (PropStructure) getObjectMapped(property.getPropertyStructure(),
				PropStructure.class);
		propertyDetail.setPropStructure(propStructure);
		// feature
		PropFeature propFeature = (PropFeature) getObjectMapped(property.getPropertyFeature(), PropFeature.class);
		propertyDetail.setPropFeature(propFeature);

		// title
		propertyDetail.setTitle(lead.getTitle());
		// description
		propertyDetail.setDescription(lead.getDescription());
		// budget
		propertyDetail.setPrice(lead.getPrice());
		// address
		propertyDetail.setAddress(property.getAddress());
		// land_mark
		propertyDetail.setLandMark(property.getLandMark());
		// pin_code
		propertyDetail.setPinCode(property.getPinCode());
		// tags
		propertyDetail.setTags(property.getTags());
		// featured_property
		propertyDetail.setFeaturedProperty(lead.isFeaturedProperty());
		// comment_count
		propertyDetail.setCommentCount(lead.getCommentCount());
		// comments
		propertyDetail.setComments(lead.getComments());
		boolean isFavorite = false;
		long favoriteId = 0;
		if (user != null) {
			List<Favorite> favorites = user.getFavorites();
			for (Favorite favorite : favorites) {
				if (favorite.getLead().getId() == lead.getId()) {
					isFavorite = true;
					favoriteId = favorite.getId();
				}
			}
		}
		propertyDetail.setPublished(lead.isPublished());
		propertyDetail.setFavorite(isFavorite);
		propertyDetail.setFavoriteId(favoriteId);
		propertyDetail.setImages(lead.getPropertyImages());
		propertyDetail.setAttachments(property.getPropertyAttachments());
		propertyDetail.setVideos(property.getPropertyVideos());
		return propertyDetail;
	}

	public static Object getObjectMapped(Object obj, Class<?> mapClass) {
		ObjectMapper ObjM = new ObjectMapper();
		String strResult;
		try {
			ObjM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ObjM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			strResult = ObjM.writeValueAsString(obj);
			LOGGER.info("PropertyMapper >> getObjectMapped >> " + strResult);
			obj = ObjM.readValue(strResult, mapClass);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return obj;
	}

}
