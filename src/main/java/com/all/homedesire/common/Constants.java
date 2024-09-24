package com.all.homedesire.common;

import java.util.List;

public class Constants {
	
	public static final Integer PAGE_SIZE = 500;
	
	public static final String WORK_IMAGE = "image";
	public static final String PROFILE_PIC_IMAGE = "profile_pics";
	public static final String CONTACT_FILE = "contact_files";
	public static final String DOCUMENT_FILE = "document_files";
	public static final String PAYMENT_FILE = "payment_files";
	
	
	public static final String FILE_ATTACHMENT = "attachments";
	public static final String FILE_IMAGE = "images";
	
	public static final List<String> IMAGE_FILE_EXT = List.of("IMAGE/JPEG", "IMAGE/JPG", "IMAGE/PNG", "VIDEO/MP4");
	public static final List<String> ATTACHMENT_FILE_EXT = List.of("IMAGE/JPEG", "IMAGE/JPG", "IMAGE/PNG", "APPLICATION/PDF");
	public static final List<String> EXCEL_FILE_EXT = List.of("text/csv","XLSX", "XLS", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel");
	
	public static final String ALLOWED_SPL_CHARACTERS = "!@#$%^&*()_+";
    public static final String ERROR_CODE = "ERRONEOUS_SPECIAL_CHARS";
    
    public static final String STATUS_CREATED = "CREATED";
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAILURE = "FAILURE";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_UNAUTHORIZE = "UNAUTHORIZED";

	public static final String INVALID_ENUM = "Provided value is invalid!";
	public static final String INVALID_TOKEN = "Provided token is invalid!";
	public static final String INVALID_USAGE = "User type (|ENTITY|) not authorized to access this feature";
	public static final String INVALID_CSV = "Provided file is not valid csv/excel!";
	public static final String INVALID_DOCUMENT = "Provided file is not valid for |ENTITY|!";
	public static final String EXECUTION_ERROR = "|ENTITY| got error : ";
	public static final String PARAMETER_MISSING = "Please provide missing \"|ENTITY|\"!";
	
	public static final String OBJ_EXIST = "|ENTITY| already available!";
	public static final String OBJ_NOT_EXIST = "|ENTITY| not exist!";

	public static final String LIST_SUCCESS = "|ENTITY| list found!";
	public static final String DETAIL_SUCCESS = "|ENTITY| detail found!";
	public static final String SAVE_SUCCESS = "|ENTITY| saved succesfully!";
	public static final String UPDATE_SUCCESS = "|ENTITY| updated succesfully!";
	public static final String DELETE_SUCCESS = "|ENTITY| deleted succesfully!";
	public static final String UPLOAD_SUCCESS = "|ENTITY| upload successfully!";
	public static final String CONTACT_UPLOAD__SUCCESS = "Contacts upload successfully!";
	public static final String NETWORK_UPLOAD__SUCCESS = "Networks upload successfully!";
	public static final String SIGN_IN_SUCCESS = "|ENTITY| signin successfully!";
	public static final String SIGN_UP_SUCCESS = "|ENTITY| signup successfully!!";
	public static final String ACTIVE_SUCCESS = "|ENTITY| active state successfully!!";
	public static final String EMAIL_SUCCESS = "|ENTITY| sent successfully!";

	public static final String LIST_FAILURE = "|ENTITY| list not found!";
	public static final String DETAIL_FAILURE = "|ENTITY| detail not found!";
	public static final String SAVE_FAILURE = "|ENTITY| not saved!";
	public static final String UPDATE_FAILURE = "|ENTITY| not updated!";
	public static final String DELETE_FAILURE = "|ENTITY| unable to delete!";
	public static final String UPLOAD_FAILURE = "|ENTITY| upload failed!";
	public static final String CONTACT_UPLOAD_FAILURE = "Contacts upload failed!";
	public static final String NETWORK_UPLOAD_FAILURE = "Networks upload failed!";
	public static final String SIGN_IN_FAILURE = "The |ENTITY| is not associated with the system or this needs to be activated. Please contact with system administrator";
	public static final String SIGN_UP_FAILURE = "|ENTITY| signup failed!!";
	public static final String ACTIVE_FAILURE = "|ENTITY| active state failed!!";
	public static final String EMAIL_FAILURE = "|ENTITY| sent failed!";

	public static final String INVALID_STATUS_CHANGE = "Invalid Status";
	public static final String EMAIL_SAVED_SUCCESS = "Email success";
	public static final String EMAIL_SAVED_FAILURE = "Email failure";

	public static final String MAX_PROPERTY_LIMIT_REACHED = "Maximum property limit reached";

}
