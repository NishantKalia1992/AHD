package com.all.homedesire.controller;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.ContactUs;
import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.DesiredQuery;
import com.all.homedesire.entities.Role;
import com.all.homedesire.entities.HomeService;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserType;
import com.all.homedesire.enums.ERole;
import com.all.homedesire.enums.EType;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.StorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class AdminRestController {
	Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);

	@Autowired
	AdminService adminService;
	@Autowired
	StorageService storageService;

	@GetMapping("user/{userName}")
	public MappingJacksonValue userByUserName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userName) {
		LOGGER.info("AdminRestController >> users with userName >> " + userName + "called.");
		DesireStatus desireStatus = adminService.userByUserName(authToken, userName);
		String[] properties = { "statusType", "text", "assetUrl", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("user/id/{userId}")
	public MappingJacksonValue userByUserId(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userId) {
		LOGGER.info("AdminRestController >> user with userId >> " + userId + "called.");
		DesireStatus desireStatus = adminService.viewUser(authToken, Long.parseLong(userId));
		String[] properties = { "statusType", "text", "assetUrl", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("users")
	public MappingJacksonValue users(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> users called for user list.");
		DesireStatus desireStatus = (request != null) ? adminService.users(authToken, request)
				: adminService.users(authToken);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "users" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addUser")
	public MappingJacksonValue addUser(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody User user) {
		LOGGER.info("AdminRestController >> addUser called.");
		DesireStatus desireStatus = adminService.addUser(authToken, user);
		String[] properties = { "statusType", "text", "assetUrl", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editUser")
	public MappingJacksonValue editUser(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody User user) {
		LOGGER.info("AdminRestController >> editUser called.");
		DesireStatus desireStatus = adminService.editUser(authToken, user);
		String[] properties = { "statusType", "text", "assetUrl", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteUser/{userId}")
	public MappingJacksonValue deleteUser(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userId) {
		LOGGER.info("AdminRestController >> deleteUser with userId >> " + userId + "called.");
		DesireStatus desireStatus = adminService.deleteUser(authToken, Long.parseLong(userId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addPrefrences")
	public MappingJacksonValue addUserPrefrences(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody User user) {
		LOGGER.info("AdminRestController >> addUserPrefrences called.");
		DesireStatus desireStatus = adminService.addUserPrefrences(authToken, user);
		String[] properties = { "statusType", "text", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("userType/{userTyeName}")
	public MappingJacksonValue userType(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userTyeName) {
		DesireStatus desireStatus = null;
		try {
			EType userType = EType.valueOf(userTyeName);
			LOGGER.info("AdminRestController >> userType with userTyeName >> " + userTyeName + "called.");
			desireStatus = adminService.userTypeByName(authToken, userType);
		} catch (Exception e) {
			desireStatus = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_ENUM, "");
		}
		String[] properties = { "statusType", "text", "userType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("userTypes")
	public MappingJacksonValue userTypes(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> userTypes called for user type list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.userTypes(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "userTypes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addUserType")
	public MappingJacksonValue addUserType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserType userType) {
		LOGGER.info("AdminRestController >> addUserType called.");
		DesireStatus desireStatus = adminService.addUserType(authToken, userType);
		String[] properties = { "statusType", "text", "userType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editUserType")
	public MappingJacksonValue editUserType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserType userType) {
		LOGGER.info("AdminRestController >> editUserType called.");
		DesireStatus desireStatus = adminService.editUserType(authToken, userType);
		String[] properties = { "statusType", "text", "userType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteUserType/{userTypeId}")
	public MappingJacksonValue deleteUserType(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userTypeId) {
		LOGGER.info("AdminRestController >> deleteUserType with userTypeId >> " + userTypeId + "called.");
		DesireStatus desireStatus = adminService.deleteUserType(authToken, Long.parseLong(userTypeId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/add/role")
	public MappingJacksonValue addUserRole(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Role role) {
		LOGGER.info("AdminRestController >> addUserRole called.");
		DesireStatus desireStatus = adminService.addUserRole(authToken, role);
		String[] properties = { "statusType", "text", "role" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/edit/role")
	public MappingJacksonValue editUserRole(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Role role) {
		LOGGER.info("AdminRestController >> editUserRole called.");
		DesireStatus desireStatus = adminService.editUserRole(authToken, role);
		String[] properties = { "statusType", "text", "role" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("role/{roleName}")
	public MappingJacksonValue userRoleByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable ERole roleName) {
		LOGGER.info("AdminRestController >> userRoleByName with role Name >> " + roleName + "called.");
		DesireStatus desireStatus = adminService.userRoleByName(authToken, roleName);
		String[] properties = { "statusType", "text", "role" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("role/id/{roleId}")
	public MappingJacksonValue viewUserRole(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String roleId) {
		LOGGER.info("AdminRestController >> viewUserRole with roleId >> " + roleId + "called.");
		DesireStatus desireStatus = adminService.viewUserRole(authToken, Long.parseLong(roleId));
		String[] properties = { "statusType", "text", "role" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("roles")
	public MappingJacksonValue userRoles(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> userRoles called for user role list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.userRoles(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "roles" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("delete/role/{roleId}")
	public MappingJacksonValue deleteUserRole(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String roleId) {
		LOGGER.info("AdminRestController >> deleteUserRole with roleId >> " + roleId + "called.");
		DesireStatus desireStatus = adminService.deleteUserRole(authToken, Long.parseLong(roleId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("contactus/{contactId}")
	public MappingJacksonValue contactUs(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String contactId) {
		LOGGER.info("AdminRestController >> contactUs with contactId >> " + contactId + "called.");
		DesireStatus desireStatus = adminService.contactUsById(authToken, Long.parseLong(contactId));
		String[] properties = { "statusType", "text", "contactUs" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("listContactUs")
	public MappingJacksonValue listContactUs(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> userTypes called for user type list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.listContactUs(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "contactUss" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addContactUs")
	public MappingJacksonValue addContactUs(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ContactUs contactUs) {
		LOGGER.info("AdminRestController >> addContactUs called.");
		DesireStatus desireStatus = adminService.addContactUs(authToken, contactUs);
		String[] properties = { "statusType", "text", "contactUs" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editContactUs")
	public MappingJacksonValue editContactUs(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ContactUs contactUs) {
		LOGGER.info("AdminRestController >> editContactUs called.");
		DesireStatus desireStatus = adminService.editContactUs(authToken, contactUs);
		String[] properties = { "statusType", "text", "contactUs" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteContactUs/{contactUsId}")
	public MappingJacksonValue deleteContactUs(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String contactUsId) {
		LOGGER.info("AdminRestController >> deleteContactUs with contactUsId >> " + contactUsId + "called.");
		DesireStatus desireStatus = adminService.deleteContactUs(authToken, Long.parseLong(contactUsId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("country/{countryName}")
	public MappingJacksonValue countryByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String countryName) {
		LOGGER.info("AdminRestController >> country with countryName >> " + countryName + "called.");
		DesireStatus desireStatus = adminService.countryByName(authToken, countryName);
		String[] properties = { "statusType", "text", "country" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("country/id/{countryId}")
	public MappingJacksonValue countryById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String countryId) {
		LOGGER.info("AdminRestController >> country with countryId >> " + countryId + "called.");
		DesireStatus desireStatus = adminService.viewCountry(authToken, Long.parseLong(countryId));
		String[] properties = { "statusType", "text", "country" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("countries")
	public MappingJacksonValue countries(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> countries called for country list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.countries(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "countries" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addCountry")
	public MappingJacksonValue addCountry(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Country country) {
		LOGGER.info("AdminRestController >> addCountry called.");
		DesireStatus desireStatus = adminService.addCountry(authToken, country);
		String[] properties = { "statusType", "text", "country" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editCountry")
	public MappingJacksonValue editCountry(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Country country) {
		LOGGER.info("AdminRestController >> editCountry called.");
		DesireStatus desireStatus = adminService.editCountry(authToken, country);
		String[] properties = { "statusType", "text", "country" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteCountry/{countryId}")
	public MappingJacksonValue deleteCountry(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String countryId) {
		LOGGER.info("AdminRestController >> deleteCountry with countryId >> " + countryId + "called.");
		DesireStatus desireStatus = adminService.deleteCountry(authToken, Long.parseLong(countryId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("state/{stateName}")
	public MappingJacksonValue stateByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String stateName) {
		LOGGER.info("AdminRestController >> state with cityName >> " + stateName + "called.");
		DesireStatus desireStatus = adminService.stateByName(authToken, stateName);
		String[] properties = { "statusType", "text", "state" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("state/id/{stateId}")
	public MappingJacksonValue stateById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String stateId) {
		LOGGER.info("AdminRestController >> state with stateId >> " + stateId + "called.");
		DesireStatus desireStatus = adminService.viewState(authToken, Long.parseLong(stateId));
		String[] properties = { "statusType", "text", "state" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("states")
	public MappingJacksonValue states(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> states called for state list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.states(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "states" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("states/{countryId}")
	public MappingJacksonValue states(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String countryId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> states called for state list with country id " + countryId + ".");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.states(authToken, Long.parseLong(countryId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "states" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addState")
	public MappingJacksonValue addState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody State state) {
		LOGGER.info("AdminRestController >> addState called.");
		DesireStatus desireStatus = adminService.addState(authToken, state);
		String[] properties = { "statusType", "text", "state" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editState")
	public MappingJacksonValue editState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody State state) {
		LOGGER.info("AdminRestController >> editState called.");
		DesireStatus desireStatus = adminService.editState(authToken, state);
		String[] properties = { "statusType", "text", "state" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteState/{stateId}")
	public MappingJacksonValue deleteState(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String stateId) {
		LOGGER.info("AdminRestController >> deleteState with stateId >> " + stateId + "called.");
		DesireStatus desireStatus = adminService.deleteState(authToken, Long.parseLong(stateId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("city/{cityName}")
	public MappingJacksonValue cityByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String cityName) {
		LOGGER.info("AdminRestController >> city with cityName >> " + cityName + "called.");
		DesireStatus desireStatus = adminService.cityByName(authToken, cityName);
		String[] properties = { "statusType", "text", "city" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("city/id/{cityId}")
	public MappingJacksonValue cityById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String cityId) {
		LOGGER.info("AdminRestController >> city with cityId >> " + cityId + "called.");
		DesireStatus desireStatus = adminService.viewCity(authToken, Long.parseLong(cityId));
		String[] properties = { "statusType", "text", "city" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("cities")
	public MappingJacksonValue cities(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> cities called for city list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.cities(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "cities" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("cities/{stateId}")
	public MappingJacksonValue cities(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String stateId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> cities called for city list with state id " + stateId + ".");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.cities(authToken, Long.parseLong(stateId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "cities" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addCity")
	public MappingJacksonValue addCity(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody City city) {
		LOGGER.info("AdminRestController >> addCity called.");
		DesireStatus desireStatus = adminService.addCity(authToken, city);
		String[] properties = { "statusType", "text", "city" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editCity")
	public MappingJacksonValue editCity(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody City city) {
		LOGGER.info("AdminRestController >> editCity called.");
		DesireStatus desireStatus = adminService.editCity(authToken, city);
		String[] properties = { "statusType", "text", "city" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteCity/{cityId}")
	public MappingJacksonValue deleteCity(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String cityId) {
		LOGGER.info("AdminRestController >> deleteCity with contactUsId >> " + cityId + "called.");
		DesireStatus desireStatus = adminService.deleteCity(authToken, Long.parseLong(cityId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("area/{areaName}")
	public MappingJacksonValue areaByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String areaName) {
		LOGGER.info("AdminRestController >> area with areaName >> " + areaName + "called.");
		DesireStatus desireStatus = adminService.areaByName(authToken, areaName);
		String[] properties = { "statusType", "text", "area" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("area/id/{areaId}")
	public MappingJacksonValue areaById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String areaId) {
		LOGGER.info("AdminRestController >> area with areaId >> " + areaId + "called.");
		DesireStatus desireStatus = adminService.viewArea(authToken, Long.parseLong(areaId));
		String[] properties = { "statusType", "text", "area" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("areas")
	public MappingJacksonValue areas(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> areas called for area list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.areas(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "areas" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("areas/{cityId}")
	public MappingJacksonValue areas(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String cityId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> areas called for area list with city id " + cityId + ".");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.areas(authToken, Long.parseLong(cityId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "areas" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("areasByName/{areaName}")
	public MappingJacksonValue areasByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String areaName, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> areas called for area list with areaName " + areaName + ".");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.areasByName(authToken, areaName, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "areas" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addArea")
	public MappingJacksonValue addArea(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Area area) {
		LOGGER.info("AdminRestController >> addArea called.");
		DesireStatus desireStatus = adminService.addArea(authToken, area);
		String[] properties = { "statusType", "text", "area" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editArea")
	public MappingJacksonValue editArea(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Area area) {
		LOGGER.info("AdminRestController >> editArea called.");
		DesireStatus desireStatus = adminService.editArea(authToken, area);
		String[] properties = { "statusType", "text", "area" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteArea/{areaId}")
	public MappingJacksonValue deleteArea(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String areaId) {
		LOGGER.info("AdminRestController >> deleteArea with areaId >> " + areaId + "called.");
		DesireStatus desireStatus = adminService.deleteArea(authToken, Long.parseLong(areaId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("service/{serviceName}")
	public MappingJacksonValue serviceByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String serviceName) {
		LOGGER.info("AdminRestController >> service with serviceName >> " + serviceName + "called.");
		DesireStatus desireStatus = adminService.serviceByName(authToken, serviceName);
		String[] properties = { "statusType", "text", "service" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("services")
	public MappingJacksonValue services(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> services called for service list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.services(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "services" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addService")
	public MappingJacksonValue addService(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody HomeService service) {
		LOGGER.info("AdminRestController >> addService called.");
		DesireStatus desireStatus = adminService.addService(authToken, service);
		String[] properties = { "statusType", "text", "service" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editService")
	public MappingJacksonValue editService(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody HomeService service) {
		LOGGER.info("AdminRestController >> editService called.");
		DesireStatus desireStatus = adminService.editService(authToken, service);
		String[] properties = { "statusType", "text", "service" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteService/{serviceId}")
	public MappingJacksonValue deleteService(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String serviceId) {
		LOGGER.info("AdminRestController >> deleteService with serviceId >> " + serviceId + "called.");
		DesireStatus desireStatus = adminService.deleteService(authToken, Long.parseLong(serviceId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("desiredQuery/{queryId}")
	public MappingJacksonValue desiredQueryByName(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String queryId) {
		LOGGER.info("AdminRestController >> desiredQuery with queryId >> " + queryId + "called.");
		DesireStatus desireStatus = adminService.desiredQueryById(authToken, Long.parseLong(queryId));
		String[] properties = { "statusType", "text", "desiredQuery" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("desiredQueries")
	public MappingJacksonValue desiredQueries(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> desiredQueries called for query list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.desiredQueries(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "desiredQueries" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addDesiredQuery")
	public MappingJacksonValue addDesiredQuery(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DesiredQuery desiredQuery) {
		LOGGER.info("AdminRestController >> addDesiredQuery called.");
		DesireStatus desireStatus = adminService.addDesiredQuery(authToken, desiredQuery);
		String[] properties = { "statusType", "text", "desiredQuery" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editDesiredQuery")
	public MappingJacksonValue editDesiredQuery(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DesiredQuery desiredQuery) {
		LOGGER.info("AdminRestController >> editDesiredQuery called.");
		DesireStatus desireStatus = adminService.editDesiredQuery(authToken, desiredQuery);
		String[] properties = { "statusType", "text", "desiredQuery" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteDesiredQuery/{queryId}")
	public MappingJacksonValue deleteDesiredQuery(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String queryId) {
		LOGGER.info("AdminRestController >> deleteDesiredQuery with queryId >> " + queryId + "called.");
		DesireStatus desireStatus = adminService.deleteDesiredQuery(authToken, Long.parseLong(queryId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/changePassword")
	public MappingJacksonValue saveResetPassword(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam("userId") String userId, @RequestParam("password") String password) {
		LOGGER.info("CommonController >> saveResetPassword called.");

		DesireStatus desireStatus = adminService.updatePassword(authToken, Long.parseLong(userId), password);
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/changePicture")
	public MappingJacksonValue saveProfilePicture(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
		LOGGER.info("CommonController >> saveResetPassword called.");
		DesireStatus desireStatus = null;
		DesireStatus profileStatus = adminService.viewUser(authToken, Long.parseLong(userId));
		if (profileStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
			String fileName = Resources.getFileName(file.getOriginalFilename(),
					profileStatus.getUser().getId() + "_" + profileStatus.getUser().getFirstName());
			LOGGER.info("CommonController >> fileName >> " + fileName);
			DesireStatus uploadStatus = storageService.storeProfilePicture(file, fileName);

			if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {

				LOGGER.info("CommonController >> final fileName with path >> " + uploadStatus.getFileName());
				desireStatus = adminService.updateProfilePicture(authToken, profileStatus.getUser(),
						uploadStatus.getFileName());
			} else {
				desireStatus = uploadStatus;
			}
		} else {
			desireStatus = profileStatus;
		}
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/user/state")
	public MappingJacksonValue changeUserState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeUserState called.");
		DesireStatus desireStatus = adminService.changeUserState(authToken, request);
		String[] properties = { "statusType", "text", "user" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/user/type/state")
	public MappingJacksonValue changeUserTypeState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeUserTypeState called.");
		DesireStatus desireStatus = adminService.changeUserTypeState(authToken, request);
		String[] properties = { "statusType", "text", "userType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/user/role/state")
	public MappingJacksonValue changeUserRoleState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeUserRoleState called.");
		DesireStatus desireStatus = adminService.changeUserRoleState(authToken, request);
		String[] properties = { "statusType", "text", "role" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/country/state")
	public MappingJacksonValue changeCountryState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeCountryState called.");
		DesireStatus desireStatus = adminService.changeCountryState(authToken, request);
		String[] properties = { "statusType", "text", "country" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/state/active/state")
	public MappingJacksonValue changeStateActive(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeStateActive called.");
		DesireStatus desireStatus = adminService.changeStateActive(authToken, request);
		String[] properties = { "statusType", "text", "state" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/city/state")
	public MappingJacksonValue changeCityState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeCityState called.");
		DesireStatus desireStatus = adminService.changeCityState(authToken, request);
		String[] properties = { "statusType", "text", "city" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/area/state")
	public MappingJacksonValue changeAreaState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeAreaState called.");
		DesireStatus desireStatus = adminService.changeAreaState(authToken, request);
		String[] properties = { "statusType", "text", "area" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/service/state")
	public MappingJacksonValue changeServiceState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeServiceState called.");
		DesireStatus desireStatus = adminService.changeServiceState(authToken, request);
		String[] properties = { "statusType", "text", "service" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/contact/us/state")
	public MappingJacksonValue changeContactUsState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeContactUsState called.");
		DesireStatus desireStatus = adminService.changeContactUsState(authToken, request);
		String[] properties = { "statusType", "text", "contactUs" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/query/state")
	public MappingJacksonValue changeDesiredQueryState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeDesiredQueryState called.");
		DesireStatus desireStatus = adminService.changeDesiredQueryState(authToken, request);
		String[] properties = { "statusType", "text", "desiredQuery" };
		return Resources.formatedResponse(desireStatus, properties);
	}

}
