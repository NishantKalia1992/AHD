package com.all.homedesire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.HomeService;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertySubType;
import com.all.homedesire.entities.PropertyType;
import com.all.homedesire.entities.Role;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserType;
import com.all.homedesire.enums.ERole;
import com.all.homedesire.enums.EType;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.PropertyService;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class HomeDesireApplication implements ApplicationRunner {
	Logger LOGGER = LoggerFactory.getLogger(HomeDesireApplication.class);
	@Autowired
	AdminService adminService;
	@Autowired
	PropertyService propertyService;

	private static final String superAdminEmail = "allhomedesire@gmail.com";
	private static final String superAdminMobile = "9818961783";

	public static void main(String[] args) {
		SpringApplication.run(HomeDesireApplication.class, args);
		System.out.println("Code containerize with docker and ci-cd pipeline using jenkins or docker");
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 1 - Property Purpose
		List<List<String>> purposes = new ArrayList<>();
		List<String> purpose = new ArrayList<>();
		purpose.add("Buy");
		purpose.add("Buy a property");
		purposes.add(purpose);
		purpose = new ArrayList<>();
		purpose.add("Sell");
		purpose.add("Sell a property");
		purposes.add(purpose);
		purpose = new ArrayList<>();
		purpose.add("Rent");
		purpose.add("Rent a property");
		purposes.add(purpose);
		for (List<String> data : purposes) {
			PropertyPurpose obj = new PropertyPurpose();
			obj.setName(data.get(0));
			obj.setDescription(data.get(1));
			propertyService.addPropertyPurpose(obj);
		}

		// 2 - Property Status
		List<List<String>> statusList = new ArrayList<>();
		List<String> status = new ArrayList<>();
		status.add("Available");
		status.add("Property available ");
		statusList.add(status);
		status = new ArrayList<>();
		status.add("Not Available");
		status.add("No property available ");
		statusList.add(status);
		status = new ArrayList<>();
		status.add("Sold");
		status.add("Property sold ");
		statusList.add(status);
		status = new ArrayList<>();
		status.add("Open House");
		status.add("Property is open house ");
		statusList.add(status);
		for (List<String> data : statusList) {
			PropertyStatus obj = new PropertyStatus();
			obj.setName(data.get(0));
			obj.setDescription(data.get(1));
			propertyService.addPropertyStatus(obj);
		}
		// 3 - Property Type
		
		PropertyType objTyp = new PropertyType();
		objTyp.setName("Residential");
		objTyp.setDescription("Residential properties");
		DesireStatus ds = propertyService.addPropertyType(objTyp);
		LOGGER.info("ds >> "+ds.getStatusType());
		if(ds.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<List<String>> typeList = new ArrayList<>();
			List<String> type = new ArrayList<>();
			type.add("Flat");
			type.add("Property type flat");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Villa");
			type.add("Property type villa");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("House");
			type.add("Property type house");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Plot");
			type.add("Property type plot");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Farm House");
			type.add("Property type farm house");
			typeList.add(type);
			
			for (List<String> data : typeList) {
				PropertySubType obj = new PropertySubType();
				obj.setPropertyTypeId(ds.getPropertyType().getId());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				propertyService.addPropertySubType(obj);
			}
		}
		
		PropertyType objTyp1 = new PropertyType();
		objTyp1.setName("Commercial");
		objTyp1.setDescription("Commercial properties");
		DesireStatus ds1 = propertyService.addPropertyType(objTyp1);
		LOGGER.info("ds1 >> "+ds1.getStatusType());
		if(ds1.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<List<String>> typeList = new ArrayList<>();
			List<String> type = new ArrayList<>();
			type.add("Shop");
			type.add("Property type shop");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Food Court");
			type.add("Property type food court");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Office Space");
			type.add("Property type office space");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Virtual Space");
			type.add("Property type virtual space");
			typeList.add(type);
						
			for (List<String> data : typeList) {
				PropertySubType obj = new PropertySubType();
				obj.setPropertyTypeId(ds1.getPropertyType().getId());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				propertyService.addPropertySubType(obj);
			}
		}
		
		PropertyType objTyp2 = new PropertyType();
		objTyp2.setName("Industrial");
		objTyp2.setDescription("Industrial properties");
		DesireStatus ds2 = propertyService.addPropertyType(objTyp2);
		LOGGER.info("ds2 >> "+ds2.getStatusType());
		if(ds2.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<List<String>> typeList = new ArrayList<>();
			List<String> type = new ArrayList<>();
			type.add("Plot");
			type.add("Property type plot");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Land");
			type.add("Property type land");
			typeList.add(type);
			type = new ArrayList<>();
			type.add("Factory");
			type.add("Property type factory");
			typeList.add(type);
									
			for (List<String> data : typeList) {
				PropertySubType obj = new PropertySubType();
				obj.setPropertyTypeId(ds2.getPropertyType().getId());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				propertyService.addPropertySubType(obj);
			}
		}
		
		PropertyType objTyp3 = new PropertyType();
		objTyp3.setName("Farming");
		objTyp3.setDescription("Farming properties");
		DesireStatus ds3 = propertyService.addPropertyType(objTyp3);
		LOGGER.info("ds3 >> "+ds3.getStatusType());
		if(ds3.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<List<String>> typeList = new ArrayList<>();
			List<String> type = new ArrayList<>();
			type.add("Farm Land");
			type.add("Property type farm land");
			typeList.add(type);
												
			for (List<String> data : typeList) {
				PropertySubType obj = new PropertySubType();
				obj.setPropertyTypeId(ds3.getPropertyType().getId());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				propertyService.addPropertySubType(obj);
			}
		}
		
		 
		// 4 - Services
		List<List<String>> serviceList = new ArrayList<>();
		List<String> service = new ArrayList<>();
		service.add("Buy");
		service.add("Property service to buy");
		serviceList.add(service);
		service = new ArrayList<>();
		service.add("Sell");
		service.add("Property service to sell");
		serviceList.add(service);
		service = new ArrayList<>();
		service.add("Rent");
		service.add("Property service for rent");
		serviceList.add(service);
		service = new ArrayList<>();
		service.add("Interior Design");
		service.add("Property service for interior design");
		serviceList.add(service);

		for (List<String> data : serviceList) {
			HomeService obj = new HomeService();
			obj.setName(data.get(0));
			obj.setDescription(data.get(1));
			adminService.addService(obj);
		}
		// 5 - Country
		List<List<String>> countryList = new ArrayList<>();
		List<String> country = new ArrayList<>();
		country.add("India");
		country.add("India");
		countryList.add(country);
		for (List<String> data : countryList) {
			Country obj = new Country();
			obj.setName(data.get(0));
			obj.setDescription(data.get(1));
			adminService.addCountry(obj);
		}
		// 6 - State
		List<List<String>> stateList = new ArrayList<>();
		DesireStatus countryStatus = adminService.viewCountry("India");
		if (countryStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<String> state = new ArrayList<>();
			state.add("Delhi");
			state.add("Delhi");
			stateList.add(state);
			state = new ArrayList<>();
			state.add("Uttar Pradesh");
			state.add("Uttar Pradesh");
			stateList.add(state);
			for (List<String> data : stateList) {
				State obj = new State();
				obj.setCountry(countryStatus.getCountry());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				adminService.addState(obj);
			}
		}
		// 7 - City
		List<List<String>> cityList = new ArrayList<>();
		DesireStatus stateStatus = adminService.viewState("Delhi");
		if (stateStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<String> city = new ArrayList<>();
			city.add("Laxmi Nagar");
			city.add("Laxmi Nagar");
			cityList.add(city);
			city = new ArrayList<>();
			city.add("Anand Vihar");
			city.add("Anand Vihar");
			cityList.add(city);
			for (List<String> data : cityList) {
				City obj = new City();
				obj.setState(stateStatus.getState());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				adminService.addCity(obj);
			}
		}
		cityList = new ArrayList<>();
		DesireStatus stateStatus1 = adminService.viewState("Uttar Pradesh");
		if (stateStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			List<String> city = new ArrayList<>();
			city.add("Gautam Buddha Nagar");
			city.add("Gautam Buddha Nagar");
			cityList.add(city);
			city = new ArrayList<>();
			city.add("Ghaziabad");
			city.add("Ghaziabad");
			cityList.add(city);
			for (List<String> data : cityList) {
				City obj = new City();
				obj.setState(stateStatus1.getState());
				obj.setName(data.get(0));
				obj.setDescription(data.get(1));
				adminService.addCity(obj);
			}
		}
		// 8 - Area

		// 9 - Add User Types
		List<String> eTypes = new ArrayList<>();
		eTypes.add("SUPER_ADMIN");
		eTypes.add("ALL_HOME_DESIRE");
		eTypes.add("PARTNER");
		eTypes.add("CUSTOMER");
		for (String eType : eTypes) {
			UserType uType = new UserType();
			uType.setName(eType);
			adminService.addUserType(uType);
		}

		// 10 - Add User Roles
		List<ERole> eRoles = new ArrayList<>();
		eRoles.add(ERole.ROLE_ADMIN);
		eRoles.add(ERole.ROLE_LEAD);
		eRoles.add(ERole.ROLE_TEAM_MEMBER);
		eRoles.add(ERole.ROLE_FRONT_DESK);
		eRoles.add(ERole.ROLE_USER);
		for (ERole eRole : eRoles) {
			Role role = new Role();
			role.setName(eRole);
			adminService.addUserRole(role);
		}

		// 11 - Super Admin
		// Super Admin
		UserType userType = new UserType();
		userType.setName(EType.SUPER_ADMIN.toString());
		Set<String> userRoles = new HashSet<>();
		userRoles.add(ERole.ROLE_ADMIN.toString());
		userRoles.add(ERole.ROLE_LEAD.toString());
		userRoles.add(ERole.ROLE_TEAM_MEMBER.toString());
		userRoles.add(ERole.ROLE_FRONT_DESK.toString());
		userRoles.add(ERole.ROLE_USER.toString());
		User user = new User();
		user.setEmailId(superAdminEmail);
		user.setMobileNo(superAdminMobile);
		user.setFirstName("Super");
		user.setLastName("Admin");
		user.setPhoto("");
		user.setUserType(userType);
		user.setRole(userRoles);
		user.setPassword("tiwari123");
		adminService.addUser(user);
	}
}
