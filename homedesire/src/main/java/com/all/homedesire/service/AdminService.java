package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
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
import com.all.homedesire.resources.dto.AreaSearchRequest;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.LoginRequest;
import com.all.homedesire.resources.dto.LoginRequestDTO;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

	public DesireStatus signIn(LoginRequest loginRequest, boolean isPassword, User user);

	public DesireStatus signInWithSocialMedia(LoginRequestDTO loginRequest);

	public void init();

	public void save(MultipartFile file, String fileName, String uploadType);

	public Resource load(String filename);

	public void deleteFile(Path filePath);

	public void deleteAll();

	public Stream<Path> loadAll();

	public DesireStatus signUp(User user);

	public DesireStatus signIn(User user);

	public DesireStatus users(String authToken);

	public DesireStatus users(String authToken, DesireSearchRequest request);

	public DesireStatus userByUserName(String authToken, String userName);

	public DesireStatus viewUser(String authToken, long userId);

	public DesireStatus addUser(String authToken, User user);

	public DesireStatus addUser(User user);

	public DesireStatus editUser(String authToken, User user);

	public DesireStatus deleteUser(String authToken, long userId);

	public DesireStatus changeUserState(String authToken, ChangeStateRequest request);

	public DesireStatus addUserPrefrences(String authToken, User user);

	public DesireStatus userTypes(String authToken, DesireSearchRequest request);

	public DesireStatus userTypeByName(String authToken, EType userTypName);

	public DesireStatus viewUserType(String authToken, long userTypeId);

	public DesireStatus addUserType(String authToken, UserType userType);

	public DesireStatus addUserType(UserType userType);

	public DesireStatus editUserType(String authToken, UserType userType);

	public DesireStatus deleteUserType(String authToken, long userTypeId);

	public DesireStatus changeUserTypeState(String authToken, ChangeStateRequest request);

	public DesireStatus userRoles(String authToken, DesireSearchRequest request);

	public DesireStatus userRoleByName(String authToken, ERole role);

	public DesireStatus viewUserRole(String authToken, long roleId);

	public DesireStatus addUserRole(String authToken, Role role);

	public DesireStatus addUserRole(Role role);

	public DesireStatus editUserRole(String authToken, Role role);

	public DesireStatus deleteUserRole(String authToken, long roleId);

	public DesireStatus changeUserRoleState(String authToken, ChangeStateRequest request);

	public DesireStatus countries(String authToken, DesireSearchRequest request);

	public DesireStatus countries();

	public DesireStatus countryByName(String authToken, String countryName);

	public DesireStatus viewCountry(String authToken, long countryId);

	public DesireStatus viewCountry(String authToken, String countryName);

	public DesireStatus viewCountry(String countryName);

	public DesireStatus addCountry(String authToken, Country country);

	public DesireStatus addCountry(Country country);

	public DesireStatus editCountry(String authToken, Country country);

	public DesireStatus deleteCountry(String authToken, long countryId);

	public DesireStatus changeCountryState(String authToken, ChangeStateRequest request);

	public DesireStatus states(String authToken, DesireSearchRequest request);

	public DesireStatus states();

	public DesireStatus states(String authToken, long countryId, DesireSearchRequest request);

	public DesireStatus states(long countryId);

	public DesireStatus stateByName(String authToken, String stateName);

	public DesireStatus viewState(String authToken, long stateId);

	public DesireStatus viewState(long stateId);

	public DesireStatus viewState(String authToken, String stateName);

	public DesireStatus viewState(String stateName);

	public DesireStatus addState(String authToken, State state);

	public DesireStatus addState(State state);

	public DesireStatus editState(String authToken, State state);

	public DesireStatus deleteState(String authToken, long stateId);

	public DesireStatus changeStateActive(String authToken, ChangeStateRequest request);

	public DesireStatus cities(String authToken, DesireSearchRequest request);

	public DesireStatus cities();

	public DesireStatus cities(String authToken, long stateId, DesireSearchRequest request);

	public DesireStatus cities(long stateId);

	public DesireStatus cityByName(String authToken, String cityName);

	public DesireStatus viewCity(String authToken, long cityId);

	public DesireStatus addCity(String authToken, City city);

	public DesireStatus addCity(City city);

	public DesireStatus editCity(String authToken, City city);

	public DesireStatus deleteCity(String authToken, long cityId);

	public DesireStatus changeCityState(String authToken, ChangeStateRequest request);

	public DesireStatus areas(String authToken, DesireSearchRequest request);

	public DesireStatus areas(String authToken, long cityId, DesireSearchRequest request);

	public DesireStatus areasByName(String authToken, String areaName, DesireSearchRequest request);
	
	public DesireStatus areasByName(AreaSearchRequest request);

	public DesireStatus areaByName(String authToken, String areaName);

	public DesireStatus viewArea(String authToken, long areaId);

	public DesireStatus addArea(String authToken, Area area);

	public DesireStatus addArea(Area area);

	public DesireStatus editArea(String authToken, Area area);

	public DesireStatus deleteArea(String authToken, long areaId);

	public DesireStatus changeAreaState(String authToken, ChangeStateRequest request);

	public DesireStatus services(String authToken, DesireSearchRequest request);

	public DesireStatus services();

	public DesireStatus serviceByName(String authToken, String serviceName);

	public DesireStatus viewService(String authToken, long serviceId);

	public DesireStatus addService(String authToken, HomeService service);

	public DesireStatus addService(HomeService service);

	public DesireStatus editService(String authToken, HomeService service);

	public DesireStatus deleteService(String authToken, long serviceId);

	public DesireStatus changeServiceState(String authToken, ChangeStateRequest request);

	public DesireStatus listContactUs(String authToken, DesireSearchRequest request);

	public DesireStatus contactUsByName(String authToken, String contactName);

	public DesireStatus contactUsById(String authToken, long contactUsId);

	public DesireStatus addContactUs(String authToken, ContactUs contactUs);

	public DesireStatus addContactUs(ContactUs contactUs);

	public DesireStatus editContactUs(String authToken, ContactUs contactUs);

	public DesireStatus deleteContactUs(String authToken, long contactUsId);

	public DesireStatus changeContactUsState(String authToken, ChangeStateRequest request);

	public DesireStatus desiredQueries(String authToken, DesireSearchRequest request);

	public DesireStatus desiredQueryByName(String authToken, String desiredQueryName);

	public DesireStatus desiredQueryById(String authToken, long desiredQueryId);

	public DesireStatus addDesiredQuery(String authToken, DesiredQuery desiredQuery);

	public DesireStatus addDesiredQuery(DesiredQuery desiredQuery);

	public DesireStatus editDesiredQuery(String authToken, DesiredQuery desiredQuery);

	public DesireStatus deleteDesiredQuery(String authToken, long desiredQueryId);

	public DesireStatus changeDesiredQueryState(String authToken, ChangeStateRequest request);

	public DesireStatus updatePassword(String authToken, long userId, String newPassword);

	public DesireStatus updateProfilePicture(String authToken, User user, String fileName);

	public City getCity(String authToken, String countryName, String stateName, String cityName);
	
	public City getCity(String countryName, String stateName, String cityName);

	public Area getArea(String authToken, String countryName, String stateName, String cityName, String areaName);
	
	public Area getArea(String countryName, String stateName, String cityName, String areaName);
}
