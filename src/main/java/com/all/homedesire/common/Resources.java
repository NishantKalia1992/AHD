package com.all.homedesire.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.all.homedesire.resources.dto.AreaSearchRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.SearchRequest;
import com.all.homedesire.resources.dto.property.AllRequest;
import com.all.homedesire.resources.dto.property.CommonLeadRequest;
import com.all.homedesire.resources.dto.property.MySpaceRequest;
import com.all.homedesire.resources.dto.property.PreferredRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class Resources {
	static Logger LOGGER = LoggerFactory.getLogger(Resources.class);

	/**
	 * This method to extract only required JSON properties to print.
	 * 
	 * @param obj
	 * @param properties
	 * @return mappingJacksonValue
	 */
	public static MappingJacksonValue formatedResponse(Object obj, String[] properties) {
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(obj);
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(properties);
		FilterProvider filters = new SimpleFilterProvider().addFilter("DesireStatusFilter", filter);
		mappingJacksonValue.setFilters(filters);
		return mappingJacksonValue;
	}

	/**public static DesireStatus setStatus(String type, String text) {
		DesireStatus status = new DesireStatus();
		status.setStatusType(type);
		status.setText(text);

		return status;
	}**/

	public static DesireStatus setStatus(String type, String text, String entity) {
		DesireStatus status = new DesireStatus();
		status.setStatusType(type);
		Map<String, String> tokenKeyVal = new HashMap<>();
		tokenKeyVal.put("ENTITY", entity);
		text = replaceTokens(text, tokenKeyVal);

		status.setText(text);

		return status;
	}

	public static String generatePassword() {
		PasswordGenerator gen = new PasswordGenerator();
		CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
		CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
		lowerCaseRule.setNumberOfCharacters(2);

		CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
		CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
		upperCaseRule.setNumberOfCharacters(2);

		CharacterData digitChars = EnglishCharacterData.Digit;
		CharacterRule digitRule = new CharacterRule(digitChars);
		digitRule.setNumberOfCharacters(2);

		CharacterData specialChars = new CharacterData() {
			public String getErrorCode() {
				return Constants.ERROR_CODE;
			}

			public String getCharacters() {
				return "!@#$%^&*()_+";
			}
		};
		CharacterRule splCharRule = new CharacterRule(specialChars);
		splCharRule.setNumberOfCharacters(2);

		String password = gen.generatePassword(10, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
		return password;
	}

	public static Object getObjectMapped(Object obj, Class<?> mapClass) {
		ObjectMapper ObjM = new ObjectMapper();
		String strResult;
		try {
			ObjM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ObjM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			strResult = ObjM.writeValueAsString(obj);
			LOGGER.info("strResult >> " + strResult);
			obj = ObjM.readValue(strResult, mapClass);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public static int getRandomNumber() {
		Random r = new Random();
		int low = 10;
		int high = 100;
		return r.nextInt(high - low) + low;
	}

	public static String removeSpace(String data) {
		return data.replaceAll("\\s", "");
	}
	public static String formatDateForName(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String strDate = dateFormat.format(date);
		return strDate;
	}
	public static String getFileName(String prefix) {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String strDate = dateFormat.format(date);
		return prefix + strDate;
	}

	public static String getPropertyFilePath(String userPart, String propertyPart, String assetType) {
		return assetType + "/" + userPart + "/" + propertyPart;
	}

	public static boolean movePropertyFile(String sourcePath, String destPath, String fileName) {
		boolean result = false;

		try {
			File srcFile = new File(sourcePath + "/" + fileName);
			if (srcFile.exists()) {
				File destDir = new File(destPath);
				Path dest = destDir.toPath();
				createDirs(dest);
				Files.move(Paths.get(sourcePath + "/" + fileName), Paths.get(destPath + "/" + fileName),
						StandardCopyOption.REPLACE_EXISTING);
			}
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static void createDirs(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}

	}

	public static String replaceTokens(String content, Map<String, String> tokenValues) {
		for (Map.Entry<String, String> entry : tokenValues.entrySet()) {

			String strKey = "|" + entry.getKey() + "|";
			String strValue = entry.getValue();
			LOGGER.info("Key >> " + strKey + "\tValue >> " + strValue);
			LOGGER.info("emailContent.indexOf >> " + content.indexOf(strKey));
			while (content.contains(strKey)) {
				content = content.replace(strKey, strValue);
			}
		}
		return content;
	}

	public static AreaSearchRequest getDefaultRequest(AreaSearchRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new AreaSearchRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static CommonLeadRequest getDefaultRequest(CommonLeadRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new CommonLeadRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static AllRequest getDefaultRequest(AllRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new AllRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static PreferredRequest getDefaultRequest(PreferredRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new PreferredRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static MySpaceRequest getDefaultRequest(MySpaceRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new MySpaceRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static DesireSearchRequest getDefaultRequest(DesireSearchRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new DesireSearchRequest();
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static SearchRequest getDefaultRequest(SearchRequest request) {
		if (request != null) {
			return request;
		} else {
			request = new SearchRequest();
			request.setSearchType("all");
			request.setOrderBy("ASC");
			request.setPageNumber(1);
			request.setPageSize(Constants.PAGE_SIZE);
			return request;
		}
	}
	
	public static String getFileName(String recFileName, String proposedName) {
		String fileExt = recFileName.substring(recFileName.lastIndexOf(".") + 1, recFileName.length());
		String fileName = removeSpace(proposedName) + "." + fileExt;
		return fileName;
	}
}
