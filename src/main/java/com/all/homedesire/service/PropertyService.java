package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.PropertyInfo;
import com.all.homedesire.entities.Comment;
import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyFeature;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertyStructure;
import com.all.homedesire.entities.PropertySubType;
import com.all.homedesire.entities.PropertyType;
import com.all.homedesire.entities.PropertyVideo;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.LeadRequest;
import com.all.homedesire.resources.dto.property.AllRequest;
import com.all.homedesire.resources.dto.property.CommonLeadRequest;
import com.all.homedesire.resources.dto.property.MySpaceRequest;
import com.all.homedesire.resources.dto.property.PreferredRequest;

public interface PropertyService {
	
	public DesireStatus leads(String authToken, DesireSearchRequest request);
	
	public DesireStatus leadList(CommonLeadRequest request);

	public DesireStatus leadById(String authToken, long leadId);

	public DesireStatus addLead(String authToken, Lead lead);
	
	public DesireStatus addLead(Lead lead);

	public DesireStatus editLead(String authToken, Lead lead);

	public DesireStatus deleteLead(String authToken, long leadId);
	
	public DesireStatus changeLeadState(String authToken, ChangeStateRequest request);
	
	public DesireStatus properties(String authToken, DesireSearchRequest request);

	public DesireStatus propertiesByUser(String authToken, long userId, DesireSearchRequest request);
	
	public DesireStatus listMySpace(String authToken, MySpaceRequest request);
	
	public DesireStatus listAll(String authToken, AllRequest request);
	
	public DesireStatus listPreferred(String authToken, PreferredRequest request);
	
	

	public DesireStatus latestProperties(String authToken, DesireSearchRequest request);

	public DesireStatus latestProperties();

	public DesireStatus viewProperty(String authToken, long propertyId);

	public DesireStatus viewProperty(long propertyId);

	public DesireStatus addProperty(String authToken, PropertyInfo propertyInfo);

	public DesireStatus editProperty(String authToken, PropertyInfo propertyInfo);

	public DesireStatus deleteProperty(String authToken, long propertyId);

	public DesireStatus editLeadPublish(String authToken, LeadRequest request);

	// PropertyType
	public DesireStatus propertyTypes(String authToken, DesireSearchRequest request);

	public DesireStatus propertyTypes();

	public DesireStatus viewPropertyType(String authToken, long typeId);

	public DesireStatus addPropertyType(String authToken, PropertyType propertyType);

	public DesireStatus addPropertyType(PropertyType propertyType);

	public DesireStatus editPropertyType(String authToken, PropertyType propertyType);

	public DesireStatus deletePropertyType(String authToken, long typeId);

	public DesireStatus changePropertyTypeState(String authToken, ChangeStateRequest request);

	// PropertyType
	public DesireStatus propertySubTypes(String authToken, DesireSearchRequest request, long typeId);

	public DesireStatus propertySubTypes(long typeId);
	
	public DesireStatus propertySubTypes(String authToken, long typeId);

	public DesireStatus viewPropertySubType(String authToken, long typeId);

	public DesireStatus addPropertySubType(String authToken, PropertySubType type);

	public DesireStatus addPropertySubType(PropertySubType type);

	public DesireStatus editPropertySubType(String authToken, PropertySubType type);

	public DesireStatus deletePropertySubType(String authToken, long typeId);

	public DesireStatus changePropertySubTypeState(String authToken, ChangeStateRequest request);

	// PropertyPurpose
	public DesireStatus propertyPurposes(String authToken, DesireSearchRequest request);

	public DesireStatus propertyPurposes();

	public DesireStatus viewPropertyPurpose(String authToken, long purposeId);

	public DesireStatus addPropertyPurpose(String authToken, PropertyPurpose propertyPurpose);

	public DesireStatus addPropertyPurpose(PropertyPurpose propertyPurpose);

	public DesireStatus editPropertyPurpose(String authToken, PropertyPurpose propertyPurpose);

	public DesireStatus deletePropertyPurpose(String authToken, long purposeId);

	public DesireStatus changePropertyPurposeState(String authToken, ChangeStateRequest request);

	// PropertyStatus
	public DesireStatus propertyStatusList(String authToken, DesireSearchRequest request);

	public DesireStatus viewPropertyStatus(String authToken, long statusId);

	public DesireStatus addPropertyStatus(String authToken, PropertyStatus propertyStatus);

	public DesireStatus addPropertyStatus(PropertyStatus propertyStatus);

	public DesireStatus editPropertyStatus(String authToken, PropertyStatus propertyStatus);

	public DesireStatus deletePropertyStatus(String authToken, long statusId);

	public DesireStatus changePropertyStatusState(String authToken, ChangeStateRequest request);

	// PropertyStructure
	public DesireStatus PropertyStructureList(String authToken, DesireSearchRequest request);

	public DesireStatus viewPropertyStructure(String authToken, long structureId);

	public DesireStatus addPropertyStructure(String authToken, PropertyStructure propertyStructure);

	public DesireStatus editPropertyStructure(String authToken, PropertyStructure propertyStructure);

	public DesireStatus deletePropertyStructure(String authToken, long structureId);

	// PropertyFeature
	public DesireStatus propertyFeatures(String authToken, DesireSearchRequest request);

	public DesireStatus viewPropertyFeature(String authToken, long featureId);

	public DesireStatus addPropertyFeature(String authToken, PropertyFeature propertyFeature);

	public DesireStatus editPropertyFeature(String authToken, PropertyFeature propertyFeature);

	public DesireStatus deletePropertyFeature(String authToken, long featureId);

	// PropertyImage
	public DesireStatus propertyImages(String authToken, DesireSearchRequest request);

	public DesireStatus propertyImages(String authToken, long leadId, DesireSearchRequest request);

	public DesireStatus viewPropertyImage(String authToken, long imageId);

	public DesireStatus addPropertyImage(String authToken, PropertyImage propertyImage);
	
	public DesireStatus addPropertyImage(PropertyImage propertyImage);

	public DesireStatus editPropertyImage(String authToken, PropertyImage propertyImage);

	public DesireStatus deletePropertyImage(String authToken, long imageId);

	// PropertyAttachment
	public DesireStatus propertyAttachments(String authToken, DesireSearchRequest request);

	public DesireStatus propertyAttachments(String authToken, long propertyId, DesireSearchRequest request);

	public DesireStatus viewPropertyAttachment(String authToken, long attachmentId);

	public DesireStatus addPropertyAttachment(String authToken, PropertyAttachment propertyAttachment);

	public DesireStatus editPropertyAttachment(String authToken, PropertyAttachment propertyAttachment);

	public DesireStatus deletePropertyAttachment(String authToken, long attachmentId);

	// PropertyVideo
	public DesireStatus propertyVideos(String authToken, DesireSearchRequest request);

	public DesireStatus propertyVideos(String authToken, long propertyId, DesireSearchRequest request);

	public DesireStatus viewPropertyVideo(String authToken, long videoId);

	public DesireStatus addPropertyVideo(String authToken, PropertyVideo propertyVideo);

	public DesireStatus editPropertyVideo(String authToken, PropertyVideo propertyVideo);

	public DesireStatus deletePropertyVideo(String authToken, long videoId);
	
	public DesireStatus requestCommentsByUserAndRequest(String authToken, long userId, long requestId, DesireSearchRequest request);

	public DesireStatus requestCommentById(String authToken, long commentId);

	public DesireStatus addRequestComment(String authToken, Comment comment);

	public DesireStatus editRequestComment(String authToken, Comment comment);

	public DesireStatus deleteRequestComment(String authToken, long commentId);
	
	public DesireStatus changeRequestCommentState(String authToken, ChangeStateRequest request);
	
	public DesireStatus favorites(String authToken, DesireSearchRequest request);

	public DesireStatus viewFavorite(String authToken, long favoriteId);

	public DesireStatus addFavorite(String authToken, Favorite favorite);

	public DesireStatus editFavorite(String authToken, Favorite favorite);

	public DesireStatus deleteFavorite(String authToken, long favoriteId);
	
	public DesireStatus togglePropertyPublishStatus(String authToken, Long propertyId, Boolean isPublished);
}
