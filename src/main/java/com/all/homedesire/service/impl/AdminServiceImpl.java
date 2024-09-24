package com.all.homedesire.service.impl;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.PasswordGenerator;
import com.all.homedesire.common.Resources;
import com.all.homedesire.common.RestValidation;
import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.ContactUs;
import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.DesiredQuery;
import com.all.homedesire.entities.HomeService;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.Role;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserType;
import com.all.homedesire.enums.ERole;
import com.all.homedesire.enums.EType;
import com.all.homedesire.repository.AreaRepository;
import com.all.homedesire.repository.CityRepository;
import com.all.homedesire.repository.ContactUsRepository;
import com.all.homedesire.repository.CountryRepository;
import com.all.homedesire.repository.DesiredQueryRepository;
import com.all.homedesire.repository.PropertyPurposeRepository;
import com.all.homedesire.repository.RoleRepository;
import com.all.homedesire.repository.ServiceRepository;
import com.all.homedesire.repository.StateRepository;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.repository.UserTypeRepository;
import com.all.homedesire.resources.dto.AreaSearchRequest;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.LoginRequest;
import com.all.homedesire.resources.dto.LoginRequestDTO;
import com.all.homedesire.security.jwt.JwtUtils;
import com.all.homedesire.security.service.UserDetailsImpl;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.EmailService;
import com.all.homedesire.service.LogService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminServiceImpl implements AdminService {
	Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Value("${upload.work-dir}")
	private String uploadWorkdir;
	@Value("${image.base-dir}")
	private String imageBasedir;
	@Value("${site.url}")
	private String siteUrl;

	private Path root;

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;
	@Autowired
	EmailService emailService;

	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	PropertyPurposeRepository propertyPurposeRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	UserTypeRepository userTypeRepository;
	@Autowired
	ContactUsRepository contactUsRepository;
	@Autowired
	AreaRepository areaRepository;
	@Autowired
	CityRepository cityRepository;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	StateRepository stateRepository;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	DesiredQueryRepository desiredQueryRepository;

	@Override
	public DesireStatus signIn(LoginRequest loginRequest, boolean isPassword, User user) {
		LOGGER.info("UserServiceImpl >> User Name >> " + loginRequest.getUsername() + " Password >> "
				+ loginRequest.getPassword());
		DesireStatus status = new DesireStatus();
		try {
			Authentication authentication = null;
			if (isPassword) {

				authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(), loginRequest.getPassword()));

			} else {
				List<GrantedAuthority> authorities = user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());
				UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getEmailId(), user.getEmailId(), user.getFirstName(), user.getLastName(),
						user.getPassword(), authorities, user.getUserType().geteType());
				authentication = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
			}
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			userDetails.setRoles(roles);
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SIGN_IN_SUCCESS, "User");
			status.setToken(jwt);
			status.setUserDetails(userDetails);
			status.setFirstName(userDetails.getFirstName());
			status.setLastName(userDetails.getLastName());
			Optional<User> optUser = userRepository.findByObjectId(userDetails.getId());
			if (optUser.isPresent()) {
				logService.createLog(optUser.get(), LogConstants.USER, LogConstants.LOGGED_IN, null, null);
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SIGN_IN_FAILURE, "User");

		}
		return status;
	}

	@Override
	public DesireStatus signInWithSocialMedia(LoginRequestDTO loginRequest) {
		LOGGER.info("UserServiceImpl >> User email >> " + loginRequest.getEmailAddress() + " Name >> "
				+ loginRequest.getFirstName() + " " + loginRequest.getLastName());
		DesireStatus status = new DesireStatus();
		String password = "DuMmY123XyZ";
		try {
			User userObj = null;
			Optional<User> optUser = userRepository.findByEmailId(loginRequest.getEmailAddress());
			if (optUser.isPresent()) {
				userObj = optUser.get();

			} else {
				UserType userType = new UserType();
				Set<String> roles = new HashSet<>();
				roles.add(String.valueOf(ERole.ROLE_USER));
				userType.setName(EType.CUSTOMER.toString());
				User user = new User();
				user.setUserType(userType);
				user.setRole(roles);
				user.setEmailId(loginRequest.getEmailAddress());
				user.setFirstName(loginRequest.getFirstName());
				user.setLastName(loginRequest.getLastName());
				user.setPassword(password);
				user.setMobileNo("");
				user.setPhoto("");
				user.setResetPasswordToken("");
				userObj = userRepository.save(user);
			}
			LoginRequest request = new LoginRequest();
			request.setUsername(loginRequest.getEmailAddress());
			request.setPassword(password);
			status = signIn(request, false, userObj);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SIGN_IN_FAILURE, "User");
		}
		return status;
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}

	}

	@Override
	public void save(MultipartFile file, String fileName, String uploadType) {
		try {
			this.root = Paths.get(uploadWorkdir).resolve(uploadType);
			init();
			Files.copy(file.getInputStream(), this.root.resolve(fileName));
		} catch (Exception e) {
			// e.printStackTrace();
			if (e instanceof FileAlreadyExistsException) {
				deleteFile(this.root.resolve(fileName));
				save(file, fileName, uploadType);
				// throw new RuntimeException("A file of that name already exists.");
			}

			// throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	public Resource load(String filename) {
		try {
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteFile(Path filePath) {
		try {
			FileSystemUtils.deleteRecursively(filePath);
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());

	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}

	@Override
	public DesireStatus signUp(User user) {
		LOGGER.info("UserService >> Signup called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> User object recieved >> " + user);
		
		try {
			String password = user.getPassword();

			Optional<User> optUser = userRepository.findByEmailId(user.getEmailId());
			if (optUser.isPresent()) {

	            status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
	                    "Email address " + user.getEmailId());
	        } else {
				Optional<User> optUserWithMobile = userRepository.findByMobileNo(user.getMobileNo());
				if (optUserWithMobile.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
							"Mobile number " + user.getMobileNo());
				} else {
					LOGGER.info("UserService >> UserType >> " + user.getUserType().geteType());
					UserType userType = getUserType(user.getUserType().getName());
					if (userType != null) {
						user.setPassword(encoder.encode(user.getPassword()));
						user.setUserType(userType);
						user.setCreatedOn(dtNow);
						user.setUpdatedOn(dtNow);
						user.setActive((userType.getName().equals(EType.PARTNER.toString())) ? false : true);
						 if (userType.getName().equals(EType.PARTNER.toString())) {
		                        user.setStatus("NEW");
		                    }
						user.setDeleted(false);

						LOGGER.info("UserService >> User Role >> " + user.getRole());
						Set<String> userRoles = user.getRole();
						Set<Role> roles = new HashSet<>();
						if (userRoles == null) {
							Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
							if (userRole.isPresent()) {
								roles.add(userRole.get());
							} else {
								throw new RuntimeException("Role is not found.");
							}
						} else {
							roles = getRole(userRoles);
						}
						user.setRoles(roles);
						if (user.getServiceId() > 0) {
							Optional<HomeService> optService = serviceRepository.findByObjectId(user.getServiceId());
							if (optService.isPresent()) {
								HomeService homeService = optService.get();
								user.setHomeService(homeService);
							}
						}
						User savedUser = userRepository.save(user);
						 if (savedUser != null) {
		                        logService.createLog(savedUser, LogConstants.USER, LogConstants.SIGN_UP, null, null);
		                        String roleName = user.getUserType().getName(); // Get the user's role name
		                        String subject = emailService.generateEmailSubject(roleName);
		                        String messageBody = emailService.generateStatusChangeEmailContent(savedUser, roleName, password);
		                        emailService.saveEmail(savedUser.getEmailId(), messageBody, subject, "");
		                        status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SIGN_UP_SUCCESS, "User");
		                        status.setUser(savedUser);
		                    } else {

	                        status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SIGN_UP_FAILURE, "User");
	                    }
	                } else {
	                    status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User type");
	                }
	            }
	        }
	    } catch (RuntimeException e) {
			// e.printStackTrace();
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Signup user");
		} catch (Exception e) {
			// e.printStackTrace();
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Signup user");
		}

		return status;
	}

	@Override
	public DesireStatus signIn(User user) {
	    LOGGER.info("SignIn called!");
	    DesireStatus status = new DesireStatus();
	    try {
	        Optional<User> optUser = userRepository.findByEmailId(user.getEmailId());
	        if (optUser.isPresent()) {
	            User foundUser = optUser.get();
	            
	            if (!foundUser.isActive()) {
	                status = Resources.setStatus(Constants.STATUS_FAILURE, "User account is inactive.", "User");
	                return status;
	            }

	            if (encoder.matches(user.getPassword(), foundUser.getPassword())) {
	                status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SIGN_IN_SUCCESS, "User");
	                status.setUser(foundUser);
	            } else {
	                status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SIGN_IN_FAILURE, "Invalid password.");
	                status.setUser(foundUser);
	            }

	        } else {
	            status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SIGN_IN_FAILURE, "User not found.");
	        }
	    } catch (Exception e) {
	        LOGGER.error("SignIn failed due to an exception: ", e);
	        status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SIGN_IN_FAILURE, "User");
	    }
	    return status;
	}


	@Override
	public DesireStatus users(String authToken) {
		LOGGER.info("Users called!");
		DesireStatus status = new DesireStatus();
		List<User> users = null;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (authStatus.getUser().getUserType().geteType().equals(EType.CUSTOMER)) {
					status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_USAGE,
							authStatus.getUser().getUserType().geteType().toString().toLowerCase());
				} else {
					LOGGER.info("authStatus.getUser().getEmailId() >> " + authStatus.getUser().getEmailId());
					LOGGER.info("authStatus.getUser().getUserType() >> "
							+ authStatus.getUser().getUserType().geteType().toString());
					if (authStatus.getUser().getUserType().geteType().equals(EType.PARTNER)) {
						users = userRepository.findByParentUser(authStatus.getUser().getId());
					} else {
						if (authStatus.getUser().getUserType().geteType().equals(EType.SUPER_ADMIN)
								|| authStatus.getUser().getUserType().geteType().equals(EType.ALL_HOME_DESIRE)) {
							users = userRepository.findAllActiveStateUser();
						} else {
							users = userRepository.findAllUsers();
						}
					}
					logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.LIST, null, null);
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User");
					status.setUsers(users);
					status.setAssetUrl(siteUrl);
					status.setTotalRecord(users.size());
					status.setTotalPage(1);
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List User");
		}
		return status;
	}

	@Override
	public DesireStatus users(String authToken, DesireSearchRequest request) {
		LOGGER.info("Users called!");
		DesireStatus status = new DesireStatus();
		List<User> users = null;
		Page<User> page = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				boolean isUserType = RestValidation.isTextData(request.getUserType());
				boolean isActive = RestValidation.isBooleanData(request.getIsActive());
				if (authStatus.getUser().getUserType().geteType().equals(EType.CUSTOMER)) {
					status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_USAGE,
							authStatus.getUser().getUserType().geteType().toString().toLowerCase());
				} else {
					LOGGER.info("authStatus.getUser().getEmailId() >> " + authStatus.getUser().getEmailId());
					LOGGER.info("authStatus.getUser().getUserType() >> "
							+ authStatus.getUser().getUserType().geteType().toString());
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						if (authStatus.getUser().getUserType().geteType().equals(EType.PARTNER)) {
							LOGGER.info("Partner condition");
							// users = userRepository.findByParentUser(authStatus.getUser().getId());
							page = userRepository.findByPartnerUserASC(authStatus.getUser().getId(), pageable);
						} else {
							if (authStatus.getUser().getUserType().geteType().equals(EType.SUPER_ADMIN)
									|| authStatus.getUser().getUserType().geteType().equals(EType.ALL_HOME_DESIRE)) {

								if (isUserType && isActive) {
									page = userRepository.findAllUserASC(EType.valueOf(request.getUserType()),
											request.getIsActive(), pageable);
								} else if (isUserType) {
									page = userRepository.findAllUserASC(EType.valueOf(request.getUserType()),
											pageable);
								} else if (isActive) {
									page = userRepository.findAllUserASC(request.getIsActive(), pageable);
								} else {
									page = userRepository.findAllActiveStateUserASC(pageable);
								}
							} else {
								// users = userRepository.findAllUsers();
								page = userRepository.findAllUsersASC(pageable);
							}
						}
					} else {
						if (authStatus.getUser().getUserType().geteType().equals(EType.PARTNER)) {
							// users = userRepository.findByParentUser(authStatus.getUser().getId());
							page = userRepository.findByPartnerUserDESC(authStatus.getUser().getId(), pageable);
						} else {
							if (authStatus.getUser().getUserType().geteType().equals(EType.SUPER_ADMIN)
									|| authStatus.getUser().getUserType().geteType().equals(EType.ALL_HOME_DESIRE)) {
								if (isUserType && isActive) {
									page = userRepository.findAllUserDESC(EType.valueOf(request.getUserType()),
											request.getIsActive(), pageable);
								} else if (isUserType) {
									page = userRepository.findAllUserDESC(EType.valueOf(request.getUserType()),
											pageable);
								} else if (isActive) {
									page = userRepository.findAllUserDESC(request.getIsActive(), pageable);
								} else {
									page = userRepository.findAllActiveStateUserDESC(pageable);
								}
							} else {
								// users = userRepository.findAllUsers();
								page = userRepository.findAllUsersDESC(pageable);
							}
						}
					}
					users = page.getContent();
					logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.LIST, null, null);
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User");
					status.setUsers(users);
					status.setAssetUrl(siteUrl);
					status.setTotalRecord(page.getTotalElements());
					status.setTotalPage(page.getTotalPages());
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List User");
		}
		return status;
	}

	@Override
	public DesireStatus userByUserName(String authToken, String userName) {
		LOGGER.info("userByUserName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> optUser = userRepository.findByEmailId(userName);
				if (optUser.isPresent()) {
					logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.DETAIL, null, null);
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User");
					status.setUser(optUser.get());
					status.setAssetUrl(siteUrl);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User");
		}
		return status;
	}

	@Override
	public DesireStatus viewUser(String authToken, long userId) {
		LOGGER.info("viewUser called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userId > 0) {
					Optional<User> foundUser = userRepository.findByObjectIdToActivate(userId);
					if (foundUser != null) {
						logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.DETAIL, null, null);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User");
						status.setUser(foundUser.get());
						status.setAssetUrl(siteUrl);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User");
		}
		return status;
	}

	@Override
	public DesireStatus addUser(String authToken, User user) {
		LOGGER.info("addUser called!");
		// DesireStatus status = new DesireStatus();
		DesireStatus status = null;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (authStatus.getUser().getUserType().equals(EType.CUSTOMER)) {
					status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_USAGE,
							authStatus.getUser().getUserType().toString().toLowerCase());
				} else {
					if (authStatus.getUser().getUserType().geteType().equals(EType.PARTNER)) {
						LOGGER.info("addUser >> EType.PARTNER! ");
						user.setUserType(authStatus.getUser().getUserType());
						user.setParentUser(authStatus.getUser().getId());
						LOGGER.info("addUser >> authStatus.getUser().getId() >> " + authStatus.getUser().getId());
					}
					LOGGER.info("addUser >> User Type >> " + user.getUserType());
					LOGGER.info("addUser >> Parent User >>  " + user.getParentUser());
					user.setPassword(Resources.generatePassword());
					status = signUp(user);

				}
				status.setAssetUrl(siteUrl);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (RuntimeException e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR, "Add user");
			e.printStackTrace();
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR, "Add user");
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public DesireStatus addUser(User user) {
		LOGGER.info("addUser called!");
		user.setPassword((user.getPassword() != null && !user.getPassword().equals("")) ? user.getPassword()
				: Resources.generatePassword());
		return signUp(user);
	}

	@Override
	public DesireStatus editUser(String authToken, User user) {
	    LOGGER.info("editUser called!");
	    DesireStatus status = new DesireStatus();
	    Date dtNow = new Date();

	    try {
	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
	            User currentUser = authStatus.getUser();

	            // Check if the current user is an admin
	            boolean isAdmin = currentUser.getRoles().stream()
	                                         .anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

	            if (isAdmin) { // Only admins can edit users
	                if (user != null && user.getId() != null && user.getId() > 0) {
	                    Optional<User> optUser = userRepository.findByObjectIdToActivate(user.getId());
	                    if (optUser.isPresent()) {
	                        User foundUser = optUser.get();

	                        // Check if the user being edited has the UserType of 'PARTNER'
	                        boolean isPartner = foundUser.getUserType().geteType() == EType.PARTNER;

	                        // Update status only if a new status is provided and the user is a 'PARTNER'
	                        if (user.getStatus() != null && !user.getStatus().isEmpty()) {
	                            if (isPartner) {
	                                String currentStatus = foundUser.getStatus();
	                                String newStatus = user.getStatus();

	                                LOGGER.info("Current status: " + currentStatus + ", New status: " + newStatus);

	                                switch (newStatus.toUpperCase()) {
	                                    case "APPROVE":
	                                        foundUser.setStatus("APPROVE");
	                                        foundUser.setActive(true);
	                                        PasswordGenerator generator = new PasswordGenerator(
	                                            Arrays.asList(
	                                                new PasswordGenerator.PasswordCharacterSet() {
	                                                    @Override
	                                                    public char[] getCharacters() {
	                                                        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	                                                    }
	                                                    @Override
	                                                    public int getMinCharacters() {
	                                                        return 1;
	                                                    }
	                                                },
	                                                new PasswordGenerator.PasswordCharacterSet() {
	                                                    @Override
	                                                    public char[] getCharacters() {
	                                                        return "abcdefghijklmnopqrstuvwxyz".toCharArray();
	                                                    }
	                                                    @Override
	                                                    public int getMinCharacters() {
	                                                        return 1;
	                                                    }
	                                                },
	                                                new PasswordGenerator.PasswordCharacterSet() {
	                                                    @Override
	                                                    public char[] getCharacters() {
	                                                        return "0123456789".toCharArray();
	                                                    }
	                                                    @Override
	                                                    public int getMinCharacters() {
	                                                        return 1;
	                                                    }
	                                                },
	                                                new PasswordGenerator.PasswordCharacterSet() {
	                                                    @Override
	                                                    public char[] getCharacters() {
	                                                        return "!@#$%^&*()-_=+[]{}|;:,.<>?/~`".toCharArray();
	                                                    }
	                                                    @Override
	                                                    public int getMinCharacters() {
	                                                        return 1;
	                                                    }
	                                                }
	                                            ), 8, 12 // Set the desired password length range
	                                        );
	                                        char[] generatedPassword = generator.generatePassword();
	                                        String generatedPasswordString = new String(generatedPassword);

	                                        // Hash the password before saving
	                                        String hashedPassword = encoder.encode(generatedPasswordString);
	                                        foundUser.setPassword(hashedPassword);

	                                        // Send email with the generated password
	                                        emailService.sendStatusUpdateEmail(foundUser, "APPROVE", generatedPasswordString);
	                                        break;
	                                    case "HOLD":
	                                        foundUser.setStatus("HOLD");
	                                        foundUser.setActive(false);
	                                        emailService.sendStatusUpdateEmail(foundUser, "HOLD", "");
	                                        break;
	                                    case "REJECT":
	                                        foundUser.setStatus("REJECT");
	                                        foundUser.setActive(false);
	                                        emailService.sendStatusUpdateEmail(foundUser, "REJECT", "");
	                                        break;
	                                    case "DEACTIVATE":
	                                        foundUser.setStatus("DEACTIVATE");
	                                        foundUser.setActive(false);
	                                        emailService.sendStatusUpdateEmail(foundUser, "DEACTIVATE", "");
	                                        break;
	                                    case "DELETE":
	                                        userRepository.delete(foundUser);
	                                        status = Resources.setStatus(Constants.STATUS_SUCCESS, "User deleted successfully", "User");
	                                        return status;
	                                    default:
	                                        status = Resources.setStatus(Constants.STATUS_FAILURE, "Invalid status provided: " + newStatus, "User");
	                                        return status;
	                                }
	                            } else {
	                                // Prevent status change if the user is not a partner
	                                status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, "Status changes are only allowed for users with the user type PARTNER", "");
	                                return status;
	                            }
	                        }

	                        // Update other user details
	                        foundUser.setFirstName(user.getFirstName());
	                        foundUser.setLastName(user.getLastName());
	                        // foundUser.setEmailId(user.getEmailId()); // Uncomment if needed
	                        // foundUser.setMobileNo(user.getMobileNo()); // Uncomment if needed
	                        foundUser.setUpdatedOn(dtNow);

	                        User savedUser = userRepository.save(foundUser);

	                        if (savedUser != null) {
	                            status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "User");
	                            status.setUser(savedUser);
	                            status.setAssetUrl(siteUrl);
	                            logService.createLog(currentUser, LogConstants.USER, LogConstants.EDIT, null, dtNow);
	                        } else {
	                            status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "User");
	                        }
	                    } else {
	                        status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
	                    }
	                } else {
	                    status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
	                }
	            } else {
	                status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, "Only admins can change", "");
	            }
	        } else {
	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
	        }
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Edit User");
	        e.printStackTrace();
	    }
	    return status;
	}




//	private void sendStatusUpdateEmail(User user, String status) {
//	    String subject = "Account Status Update";
//	    String messageBody = String.format("Dear %s,\n\nYour account status has been updated to: %s.\n\nThank you.", user.getFirstName(), status);
//
//	    // Send email
//	    boolean emailSent = emailService.saveEmail(user.getEmailId(), messageBody, subject, "");
//
//	    if (!emailSent) {
//	        LOGGER.error("Failed to send status update email to " + user.getEmailId());
//	    }
//	}

	@Override
	public DesireStatus deleteUser(String authToken, long userId) {
		LOGGER.info("deleteUser called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userId > 0) {
					Optional<User> optUser = userRepository.findByObjectId(userId);
					if (optUser.isPresent()) {
						User foundUser = optUser.get();
						foundUser.setDeleted(true);
						userRepository.save(foundUser);
						logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.DELETE, null, null);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "User");
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "User");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete User");
		}
		return status;
	}

	@Override
	public DesireStatus addUserPrefrences(String authToken, User user) {
		LOGGER.info("addUserPrefrences called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addUserPrefrences object recieved >> " + user);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (user.getId() != null) {
					Optional<User> foundUser = userRepository.findByObjectId(user.getId());
					if (foundUser != null) {
						User usr = foundUser.get();

						Set<PropertyPurpose> userPurpose = user.getUserPurposes();
						Set<PropertyPurpose> purposes = new HashSet<>();
						for (PropertyPurpose purpose : userPurpose) {
							Optional<PropertyPurpose> foundPropertyPurpose = propertyPurposeRepository
									.findByObjectId(purpose.getId());
							if (foundPropertyPurpose != null) {
								purposes.add(foundPropertyPurpose.get());
							}
						}

						Set<State> userStates = user.getUserStates();
						Set<State> states = new HashSet<>();
						for (State state : userStates) {
							Optional<State> foundState = stateRepository.findByObjectId(state.getId());
							if (foundState != null) {
								states.add(foundState.get());
							}
						}
						Set<City> userCities = user.getUserCities();
						Set<City> cities = new HashSet<>();
						for (City city : userCities) {
							Optional<City> foundCity = cityRepository.findByObjectId(city.getId());
							if (foundCity != null) {
								cities.add(foundCity.get());
							}
						}
						Set<Area> userArea = user.getUserAreas();
						Set<Area> areas = new HashSet<>();
						for (Area area : userArea) {
							Optional<Area> foundArea = areaRepository.findByObjectId(area.getId());
							if (foundArea != null) {
								areas.add(foundArea.get());
							}
						}
						if (purposes.size() > 0) {
							usr.setPropertyPurposes(purposes);
						}
						if (states.size() > 0) {
							usr.setStates(states);
						}
						if (cities.size() > 0) {
							usr.setCities(cities);
						}
						if (areas.size() > 0) {
							usr.setAreas(areas);
						}
						usr.setUpdatedOn(dtNow);

						User savedUser = userRepository.save(usr);
						if (savedUser != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
									"User prefrence");
							status.setUser(savedUser);
							logService.createLog(authStatus.getUser(), LogConstants.USER_PREFRENCE, LogConstants.EDIT,
									null, savedUser.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
									"User prefrence");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add User prefrence");
		}

		return status;
	}

	@Override
	public DesireStatus userTypes(String authToken, DesireSearchRequest request) {
		LOGGER.info("userTypes called!");
		DesireStatus status = new DesireStatus();
		List<UserType> userTypes = null;
		Page<UserType> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = userTypeRepository.findAllASC(pageable);
				} else {
					page = userTypeRepository.findAllDESC(pageable);
				}
				userTypes = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User type");
				status.setUserTypes(userTypes);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List User type");
		}
		return status;
	}

	@Override
	public DesireStatus userTypeByName(String authToken, EType userTypName) {
		LOGGER.info("userTypeByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<UserType> foundUserType = userTypeRepository.findByName(userTypName);
				if (foundUserType.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User type");
					status.setUserType(foundUserType.get());
					logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User type");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User type");
		}
		return status;
	}

	@Override
	public DesireStatus viewUserType(String authToken, long userTypeId) {
		LOGGER.info("viewUserType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userTypeId > 0) {
					Optional<UserType> optUserType = userTypeRepository.findByObjectId(userTypeId);
					if (optUserType.isPresent()) {
						UserType foundUserType = optUserType.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User type");
						status.setUserType(foundUserType);
						logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User type");
		}
		return status;
	}

	@Override
	public DesireStatus addUserType(String authToken, UserType userType) {
		LOGGER.info("addUserType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addUserType object recieved >> " + userType);
		try {
			EType eType = null;
			try {
				eType = EType.valueOf(userType.getName());
			} catch (Exception e) {
				throw new Exception(Constants.INVALID_ENUM);
				// status = Resources.setStatus(Constants.STATUS_FAILURE,
				// Constants.INVALID_ENUM, "");
			}
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<UserType> foundUserType = userTypeRepository.findByName(eType);
				if (foundUserType.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "User type");
					status.setUserType(foundUserType.get());
				} else {
					userType.seteType(eType);
					userType.setCreatedOn(dtNow);
					userType.setUpdatedOn(dtNow);
					userType.setActive(true);
					userType.setDeleted(false);
					UserType savedUserType = userTypeRepository.save(userType);
					if (savedUserType != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User type");
						status.setUserType(savedUserType);
						logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.ADD,
								savedUserType.getCreatedOn(), savedUserType.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User type");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add User type");
		}

		return status;
	}

	@Override
	public DesireStatus addUserType(UserType userType) {
		LOGGER.info("addUserType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addUserType object recieved >> " + userType);
		try {
			EType eType = null;
			try {
				eType = EType.valueOf(userType.getName());
			} catch (Exception e) {
				throw new Exception(Constants.INVALID_ENUM);
				// status = Resources.setStatus(Constants.STATUS_FAILURE,
				// Constants.INVALID_ENUM, "");
			}
			Optional<UserType> foundUserType = userTypeRepository.findByName(eType);
			if (foundUserType.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "User type");
				status.setUserType(foundUserType.get());
			} else {
				userType.seteType(eType);
				userType.setCreatedOn(dtNow);
				userType.setUpdatedOn(dtNow);
				userType.setActive(true);
				userType.setDeleted(false);
				UserType savedUserType = userTypeRepository.save(userType);
				if (savedUserType != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User type");
					status.setUserType(savedUserType);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User type");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add User type");
		}

		return status;
	}

	@Override
	public DesireStatus editUserType(String authToken, UserType userType) {
		LOGGER.info("editUserType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			EType eType = null;
			try {
				eType = EType.valueOf(userType.getName());
			} catch (Exception e) {
				throw new Exception(Constants.INVALID_ENUM);
				// status = Resources.setStatus(Constants.STATUS_FAILURE,
				// Constants.INVALID_ENUM, "");
			}
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userType.getId() != null && userType.getId() > 0) {
					Optional<UserType> optUserType = userTypeRepository.findByObjectId(userType.getId());
					if (optUserType.isPresent()) {
						UserType foundUserType = optUserType.get();
						foundUserType.seteType(eType);
						foundUserType.setDescription(userType.getDescription());
						foundUserType.setUpdatedOn(dtNow);
						UserType savedUserType = userTypeRepository.save(foundUserType);
						if (savedUserType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User type");
							status.setUserType(savedUserType);
							logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.EDIT, null,
									savedUserType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User type id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit User type");
		}
		return status;
	}

	@Override
	public DesireStatus deleteUserType(String authToken, long userTypeId) {
		LOGGER.info("deleteUserType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userTypeId > 0) {
					Optional<UserType> optUserType = userTypeRepository.findByObjectId(userTypeId);
					if (optUserType.isPresent()) {
						UserType foundUserType = optUserType.get();
						userTypeRepository.delete(foundUserType);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "User type");
						logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "User type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete User type");
		}
		return status;
	}

	@Override
	public DesireStatus userRoles(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserService >> userRoles called!");
		DesireStatus status = new DesireStatus();
		List<Role> roles = null;
		Page<Role> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = roleRepository.findAllASC(pageable);
				} else {
					page = roleRepository.findAllDESC(pageable);
				}
				roles = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User role");
				status.setRoles(roles);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List User role");
		}
		return status;
	}

	@Override
	public DesireStatus userRoleByName(String authToken, ERole role) {
		LOGGER.info("UserService >> userRoleByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<Role> foundRole = roleRepository.findByName(role);
				if (foundRole.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User role");
					status.setRole(foundRole.get());
					logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User role");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User role");
		}
		return status;
	}

	@Override
	public DesireStatus viewUserRole(String authToken, long roleId) {
		LOGGER.info("UserService >> viewUserType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (roleId > 0) {
					Optional<Role> optRole = roleRepository.findByObjectId(roleId);
					if (optRole.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User role");
						status.setRole(optRole.get());
						logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User role");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User role id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail User role");
		}
		return status;
	}

	@Override
	public DesireStatus addUserRole(String authToken, Role role) {
		LOGGER.info("UserService >> addUserRole called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserRole object recieved >> " + role);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<Role> foundRole = roleRepository.findByName(role.getName());
				if (foundRole.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "User role");
					status.setRole(foundRole.get());
				} else {
					role.setCreatedOn(dtNow);
					role.setUpdatedOn(dtNow);
					role.setActive(true);
					role.setDeleted(false);
					Role savedRole = roleRepository.save(role);
					if (savedRole != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User role");
						status.setRole(savedRole);
						logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.ADD,
								savedRole.getCreatedOn(), savedRole.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User role");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add User role");
		}

		return status;
	}

	@Override
	public DesireStatus addUserRole(Role role) {
		LOGGER.info("UserService >> addUserRole called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserRole object recieved >> " + role);
		try {
			Optional<Role> foundRole = roleRepository.findByName(role.getName());
			if (foundRole.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "User role");
				status.setRole(foundRole.get());
			} else {
				role.setCreatedOn(dtNow);
				role.setUpdatedOn(dtNow);
				role.setActive(true);
				role.setDeleted(false);
				Role savedRole = roleRepository.save(role);
				if (savedRole != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User role");
					status.setRole(savedRole);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User role");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add User role");
		}

		return status;
	}

	@Override
	public DesireStatus editUserRole(String authToken, Role role) {
		LOGGER.info("UserService >> editUserRole called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (role.getId() != null && role.getId() > 0) {
					Optional<Role> optRole = roleRepository.findByObjectId(role.getId());
					if (optRole.isPresent()) {
						Role foundRole = optRole.get();
						foundRole.setName(role.getName());
						foundRole.setUpdatedOn(dtNow);
						Role savedRole = roleRepository.save(foundRole);
						if (savedRole != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User role");
							status.setRole(savedRole);
							logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.EDIT, null,
									savedRole.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User role");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User role id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit User role");
		}
		return status;
	}

	@Override
	public DesireStatus deleteUserRole(String authToken, long roleId) {
		LOGGER.info("UserService >> deleteUserRole called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (roleId > 0) {
					Optional<Role> optRole = roleRepository.findByObjectId(roleId);
					if (optRole.isPresent()) {
						Role foundRole = optRole.get();
						roleRepository.delete(foundRole);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "User role");
						logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "User role");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User role id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete User role");
		}
		return status;
	}

	@Override
	public DesireStatus listContactUs(String authToken, DesireSearchRequest request) {
		LOGGER.info("listContactUs called!");
		DesireStatus status = new DesireStatus();
		List<ContactUs> contactUss = null;
		Page<ContactUs> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = contactUsRepository.findAllASC(pageable);
				} else {
					page = contactUsRepository.findAllDESC(pageable);
				}
				contactUss = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Contact us");
				status.setContactUss(contactUss);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Contact us");
		}
		return status;
	}

	@Override
	public DesireStatus contactUsByName(String authToken, String contactName) {
		LOGGER.info("contactUsByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				ContactUs foundContactUs = contactUsRepository.findByName(contactName);
				if (foundContactUs != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Contact us");
					status.setContactUs(foundContactUs);
					logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.DETAIL, null,
							null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Contact us");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Contact us");
		}
		return status;
	}

	@Override
	public DesireStatus contactUsById(String authToken, long contactUsId) {
		LOGGER.info("contactUsById called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contactUsId > 0) {
					Optional<ContactUs> foundContactUs = contactUsRepository.findByObjectId(contactUsId);
					if (foundContactUs != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Contact us");
						status.setContactUs(foundContactUs.get());
						logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Contact us");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Contact us id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Contact us");
		}
		return status;
	}

	@Override
	public DesireStatus addContactUs(String authToken, ContactUs contactUs) {
		LOGGER.info("addContactUs called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addContactUs object recieved >> " + contactUs);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				contactUs.setCreatedOn(dtNow);
				contactUs.setUpdatedOn(dtNow);
				contactUs.setActive(true);
				contactUs.setDeleted(false);
				ContactUs savedContactUs = contactUsRepository.save(contactUs);
				if (savedContactUs != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Contact us");
					status.setContactUs(savedContactUs);
					logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.ADD,
							savedContactUs.getCreatedOn(), savedContactUs.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Contact us");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Contact us");
		}

		return status;
	}

	@Override
	public DesireStatus addContactUs(ContactUs contactUs) {
		LOGGER.info("addContactUs called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addContactUs object recieved >> " + contactUs);
		try {
			contactUs.setCreatedOn(dtNow);
			contactUs.setUpdatedOn(dtNow);
			contactUs.setActive(true);
			contactUs.setDeleted(false);
			ContactUs savedContactUs = contactUsRepository.save(contactUs);
			if (savedContactUs != null) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Contact us");
				status.setContactUs(savedContactUs);
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Contact us");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Contact us");
		}

		return status;
	}

	@Override
	public DesireStatus editContactUs(String authToken, ContactUs contactUs) {
		LOGGER.info("editContactUs called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contactUs.getId() != null && contactUs.getId() > 0) {
					Optional<ContactUs> optContactUs = contactUsRepository.findByObjectId(contactUs.getId());
					if (optContactUs.isPresent()) {
						ContactUs foundContactUs = optContactUs.get();
						foundContactUs.setName(contactUs.getName());
						foundContactUs.setMobileNumber(contactUs.getMobileNumber());
						foundContactUs.setEmailAddress(contactUs.getEmailAddress());
						foundContactUs.setMessage(contactUs.getMessage());
						foundContactUs.setUpdatedOn(dtNow);
						ContactUs savedContactUs = contactUsRepository.save(foundContactUs);
						if (savedContactUs != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Contact us");
							status.setContactUs(savedContactUs);
							logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.EDIT, null,
									savedContactUs.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Contact us");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Contact us id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Contact us");
		}
		return status;
	}

	@Override
	public DesireStatus deleteContactUs(String authToken, long contactUsId) {
		LOGGER.info("deleteContactUs called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contactUsId > 0) {
					Optional<ContactUs> optContactUs = contactUsRepository.findByObjectId(contactUsId);
					if (optContactUs.isPresent()) {
						ContactUs foundContactUs = optContactUs.get();
						contactUsRepository.delete(foundContactUs);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Contact us");
						logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Contact us");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Contact us id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Contact us");
		}
		return status;
	}

	@Override
	public DesireStatus countries(String authToken, DesireSearchRequest request) {
		LOGGER.info("countries called!");
		DesireStatus status = new DesireStatus();
		List<Country> countries = null;
		Page<Country> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = countryRepository.findAllASC(pageable);
				} else {
					page = countryRepository.findAllDESC(pageable);
				}
				countries = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Country");
				status.setCountries(countries);
				logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Country");
		}
		return status;
	}

	@Override
	public DesireStatus countries() {
		LOGGER.info("countries called!");
		DesireStatus status = new DesireStatus();
		try {
			List<Country> countries = countryRepository.findAll();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Country");
			status.setCountries(countries);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Country");
		}
		return status;
	}

	@Override
	public DesireStatus countryByName(String authToken, String countryName) {
		LOGGER.info("countryByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<Country> optCountry = countryRepository.findByName(countryName);
				if (optCountry.isPresent()) {
					Country foundCountry = optCountry.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Country");
					status.setCountry(foundCountry);
					logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Country");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Country");
		}
		return status;
	}

	@Override
	public DesireStatus viewCountry(String authToken, long countryId) {
		LOGGER.info("viewCountry called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (countryId > 0) {
					Optional<Country> optCountry = countryRepository.findByObjectId(countryId);
					if (optCountry.isPresent()) {
						Country foundCountry = optCountry.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Country");
						status.setCountry(foundCountry);
						logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Country");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Country id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Country");
		}
		return status;
	}

	@Override
	public DesireStatus viewCountry(String authToken, String countryName) {
		LOGGER.info("viewCountry called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (countryName != null && !countryName.equals("")) {
					Optional<Country> optCountry = countryRepository.findByName(countryName);
					if (optCountry.isPresent()) {
						Country foundCountry = optCountry.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Country");
						status.setCountry(foundCountry);
						logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Country");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Country id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Country");
		}
		return status;
	}

	@Override
	public DesireStatus viewCountry(String countryName) {
		LOGGER.info("viewCountry called!");
		DesireStatus status = new DesireStatus();
		try {
			if (countryName != null && !countryName.equals("")) {
				Optional<Country> optCountry = countryRepository.findByName(countryName);
				if (optCountry.isPresent()) {
					Country foundCountry = optCountry.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Country");
					status.setCountry(foundCountry);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Country");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Country id");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Country");
		}
		return status;
	}

	@Override
	public DesireStatus addCountry(String authToken, Country country) {
		LOGGER.info("addCountry called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addCountry object recieved >> " + country);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<Country> optCountry = countryRepository.findByName(country.getName());
				if (optCountry.isPresent()) {
					Country foundCountry = optCountry.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "Country");
					status.setCountry(foundCountry);
				} else {
					country.setCreatedOn(dtNow);
					country.setUpdatedOn(dtNow);
					country.setActive(true);
					country.setDeleted(false);
					Country savedCountry = countryRepository.save(country);
					if (savedCountry != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Country");
						status.setCountry(savedCountry);
						logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.ADD,
								savedCountry.getCreatedOn(), savedCountry.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Country");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Country");
		}

		return status;
	}

	@Override
	public DesireStatus addCountry(Country country) {
		LOGGER.info("addCountry called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addCountry object recieved >> " + country);
		try {
			Optional<Country> optCountry = countryRepository.findByName(country.getName());
			if (optCountry.isPresent()) {
				Country foundCountry = optCountry.get();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "Country");
				status.setCountry(foundCountry);
			} else {
				country.setCreatedOn(dtNow);
				country.setUpdatedOn(dtNow);
				country.setActive(true);
				country.setDeleted(false);
				Country savedCountry = countryRepository.save(country);
				if (savedCountry != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Country");
					status.setCountry(savedCountry);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Country");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Country");
		}

		return status;
	}

	@Override
	public DesireStatus editCountry(String authToken, Country country) {
		LOGGER.info("editCountry called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (country.getId() != null && country.getId() > 0) {
					Optional<Country> optCountry = countryRepository.findByObjectId(country.getId());
					if (optCountry.isPresent()) {
						Country foundCountry = optCountry.get();
						foundCountry.setName(country.getName());
						foundCountry.setDescription(country.getDescription());
						foundCountry.setUpdatedOn(dtNow);
						Country savedCountry = countryRepository.save(foundCountry);
						if (savedCountry != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Country");
							status.setCountry(savedCountry);
							logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.EDIT, null,
									savedCountry.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Country");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Country id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Country");
		}
		return status;
	}

	@Override
	public DesireStatus deleteCountry(String authToken, long countryId) {
		LOGGER.info("deleteCountry called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (countryId > 0) {
					Optional<Country> optCountry = countryRepository.findByObjectId(countryId);
					if (optCountry.isPresent()) {
						Country foundCountry = optCountry.get();
						countryRepository.delete(foundCountry);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Country");
						logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Country");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Country id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Country");
		}
		return status;
	}

	@Override
	public DesireStatus states(String authToken, DesireSearchRequest request) {
		LOGGER.info("states called!");
		DesireStatus status = new DesireStatus();
		List<State> states = null;
		Page<State> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = stateRepository.findAllASC(pageable);
				} else {
					page = stateRepository.findAllDESC(pageable);
				}
				states = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "State");
				status.setStates(states);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List State");
		}
		return status;
	}

	@Override
	public DesireStatus states() {
		LOGGER.info("states called!");
		DesireStatus status = new DesireStatus();
		try {
			List<State> states = stateRepository.findAll();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "State");
			status.setStates(states);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List State");
		}
		return status;
	}

	@Override
	public DesireStatus states(String authToken, long countryId, DesireSearchRequest request) {
		LOGGER.info("states called!");
		DesireStatus status = new DesireStatus();
		List<State> states = null;
		Page<State> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<Country> optCountry = countryRepository.findByObjectId(countryId);
				if (optCountry.isPresent()) {
					Country country = optCountry.get();
					pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
					pageNumber = pageNumber - 1;
					pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
					Pageable pageable = PageRequest.of(pageNumber, pageSize);

					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = stateRepository.findAllByCountryASC(country, pageable);
					} else {
						page = stateRepository.findAllByCountryDESC(country, pageable);
					}
					states = page.getContent();

					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "State");
					status.setStates(states);
					status.setTotalRecord(page.getTotalElements());
					status.setTotalPage(page.getTotalPages());
					logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.LIST, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Country");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List State");
		}
		return status;
	}

	@Override
	public DesireStatus states(long countryId) {
		LOGGER.info("states called!");
		DesireStatus status = new DesireStatus();
		try {
			Optional<Country> optCountry = countryRepository.findByObjectId(countryId);
			if (optCountry.isPresent()) {
				Country country = optCountry.get();
				List<State> states = stateRepository.findByCountry(country);
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "State");
				status.setStates(states);
			} else {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_NOT_EXIST, "Country");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List State");
		}
		return status;
	}

	@Override
	public DesireStatus stateByName(String authToken, String stateName) {
		LOGGER.info("stateByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				State foundState = stateRepository.findByName(stateName);
				if (foundState != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "State");
					status.setState(foundState);
					logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "State");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail State");
		}
		return status;
	}

	@Override
	public DesireStatus viewState(String authToken, long stateId) {
		LOGGER.info("viewState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (stateId > 0) {
					Optional<State> optState = stateRepository.findByObjectId(stateId);
					if (optState.isPresent()) {
						State foundState = optState.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "State");
						status.setState(foundState);
						logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "State");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail State");
		}
		return status;
	}

	@Override
	public DesireStatus viewState(long stateId) {
		LOGGER.info("viewState called!");
		DesireStatus status = new DesireStatus();
		try {
			if (stateId > 0) {
				Optional<State> optState = stateRepository.findByObjectId(stateId);
				if (optState.isPresent()) {
					State foundState = optState.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "State");
					status.setState(foundState);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "State");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail State");
		}
		return status;
	}

	@Override
	public DesireStatus viewState(String authToken, String stateName) {
		LOGGER.info("viewState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (stateName != null && !stateName.equals("")) {
					State foundState = stateRepository.findByName(stateName);
					if (foundState != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "State");
						status.setState(foundState);
						logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "State");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail State");
		}
		return status;
	}

	@Override
	public DesireStatus viewState(String stateName) {
		LOGGER.info("viewState called!");
		DesireStatus status = new DesireStatus();
		try {
			if (stateName != null && !stateName.equals("")) {
				State foundState = stateRepository.findByName(stateName);
				if (foundState != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "State");
					status.setState(foundState);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "State");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail State");
		}
		return status;
	}

	@Override
	public DesireStatus addState(String authToken, State state) {
		LOGGER.info("addState called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		State savedState = null;
		LOGGER.info("addState object recieved >> " + state);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				LOGGER.info("addState >> country id >> " + state.getCountry().getId());
				Optional<Country> optCountry = countryRepository.findByObjectId(state.getCountry().getId());
				if (optCountry.isPresent()) {
					Country country = optCountry.get();
					Optional<State> optState = stateRepository.findByCountryAndName(country, state.getName());
					if (optState.isPresent()) {
						savedState = optState.get();
					} else {
						state.setCreatedOn(dtNow);
						state.setUpdatedOn(dtNow);
						state.setActive(true);
						state.setDeleted(false);
						savedState = stateRepository.save(state);
					}
					if (savedState != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "State");
						status.setState(savedState);
						logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.ADD,
								savedState.getCreatedOn(), savedState.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "State");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Country");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add State");
		}

		return status;
	}

	@Override
	public DesireStatus addState(State state) {
		LOGGER.info("addState called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addState object recieved >> " + state);
		try {
			State savedState = null;
			Optional<Country> optCountry = countryRepository.findByObjectId(state.getCountry().getId());
			if (optCountry.isPresent()) {
				Country country = optCountry.get();
				Optional<State> optState = stateRepository.findByCountryAndName(country, state.getName());
				if (optState.isPresent()) {
					savedState = optState.get();
				} else {
					state.setCreatedOn(dtNow);
					state.setUpdatedOn(dtNow);
					state.setActive(true);
					state.setDeleted(false);
					savedState = stateRepository.save(state);
				}
				if (savedState != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "State");
					status.setState(savedState);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "State");
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Country");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add State");
		}

		return status;
	}

	@Override
	public DesireStatus editState(String authToken, State state) {
		LOGGER.info("editState called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (state.getId() != null && state.getId() > 0) {
					Optional<State> optState = stateRepository.findByObjectId(state.getId());
					if (optState.isPresent()) {
						State foundState = optState.get();
						foundState.setName(state.getName());
						foundState.setDescription(state.getDescription());
						foundState.setUpdatedOn(dtNow);
						State savedState = stateRepository.save(foundState);
						if (savedState != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "State");
							status.setState(savedState);
							logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.EDIT, null,
									savedState.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "State");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit State");
		}
		return status;
	}

	@Override
	public DesireStatus deleteState(String authToken, long stateId) {
		LOGGER.info("deleteState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (stateId > 0) {
					Optional<State> optState = stateRepository.findByObjectId(stateId);
					if (optState.isPresent()) {
						State foundState = optState.get();
						stateRepository.delete(foundState);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "State");
						logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "State");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete State");
		}
		return status;
	}

	@Override
	public DesireStatus cities(String authToken, DesireSearchRequest request) {
		LOGGER.info("cities called!");
		DesireStatus status = new DesireStatus();
		List<City> cities = null;
		Page<City> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = cityRepository.findAllASC(pageable);
				} else {
					page = cityRepository.findAllDESC(pageable);
				}
				cities = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "City");
				status.setCities(cities);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List City");
		}
		return status;
	}

	@Override
	public DesireStatus cities() {
		LOGGER.info("cities called!");
		DesireStatus status = new DesireStatus();
		try {
			List<City> cities = cityRepository.findAll();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "City");
			status.setCities(cities);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List City");
		}
		return status;
	}

	@Override
	public DesireStatus cities(String authToken, long stateId, DesireSearchRequest request) {
		LOGGER.info("cities called!");
		DesireStatus status = new DesireStatus();
		List<City> cities = null;
		Page<City> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<State> optState = stateRepository.findByObjectId(stateId);
				if (optState.isPresent()) {
					State state = optState.get();
					pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
					pageNumber = pageNumber - 1;
					pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
					Pageable pageable = PageRequest.of(pageNumber, pageSize);

					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = cityRepository.findAllByStateASC(state, pageable);
					} else {
						page = cityRepository.findAllByStateDESC(state, pageable);
					}
					cities = page.getContent();

					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "City");
					status.setCities(cities);
					status.setTotalRecord(page.getTotalElements());
					status.setTotalPage(page.getTotalPages());
					logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.LIST, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "State");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List City");
		}
		return status;
	}

	@Override
	public DesireStatus cities(long stateId) {
		LOGGER.info("cities called!");
		DesireStatus status = new DesireStatus();
		try {
			Optional<State> optState = stateRepository.findByObjectId(stateId);
			if (optState.isPresent()) {
				State state = optState.get();
				List<City> cities = cityRepository.findByState(state);
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "City");
				status.setCities(cities);
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "State");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List City");
		}
		return status;
	}

	@Override
	public DesireStatus cityByName(String authToken, String cityName) {
		LOGGER.info("cityByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				Optional<City> optCity = cityRepository.findByName(cityName);
				if (optCity.isPresent()) {
					City foundCity = optCity.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "City");
					status.setCity(foundCity);
					logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "City");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail City");
		}
		return status;
	}

	@Override
	public DesireStatus viewCity(String authToken, long cityId) {
		LOGGER.info("viewCity called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (cityId > 0) {
					Optional<City> optCity = cityRepository.findByObjectId(cityId);
					if (optCity != null) {
						City foundCity = optCity.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "City");
						status.setCity(foundCity);
						logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "City");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "City id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail City");
		}
		return status;
	}

	@Override
	public DesireStatus addCity(String authToken, City city) {
		LOGGER.info("addCity called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addCity object recieved >> " + city);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<State> optState = stateRepository.findByObjectId(city.getState().getId());
				if (optState.isPresent()) {
					Optional<City> optCity = cityRepository.findByNameAndState(city.getName(), city.getState().getId());
					if (optCity.isEmpty()) {
						city.setCreatedOn(dtNow);
						city.setUpdatedOn(dtNow);
						city.setActive(true);
						city.setDeleted(false);
						City savedCity = cityRepository.save(city);
						if (savedCity != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "City");
							status.setCity(savedCity);
							logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.ADD,
									savedCity.getCreatedOn(), savedCity.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "City");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST, "City with state");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "State");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add City");
		}

		return status;
	}

	@Override
	public DesireStatus addCity(City city) {
		LOGGER.info("addCity called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addCity object recieved >> " + city);
		try {
			Optional<State> optState = stateRepository.findByObjectId(city.getState().getId());
			if (optState.isPresent()) {
				Optional<City> optCity = cityRepository.findByNameAndState(city.getName(), city.getState().getId());
				if (optCity.isEmpty()) {
					city.setCreatedOn(dtNow);
					city.setUpdatedOn(dtNow);
					city.setActive(true);
					city.setDeleted(false);
					City savedCity = cityRepository.save(city);
					if (savedCity != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "City");
						status.setCity(savedCity);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "City");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST, "City with state");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "State");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add City");
		}

		return status;
	}

	@Override
	public DesireStatus editCity(String authToken, City city) {
		LOGGER.info("editCity called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (city.getId() != null && city.getId() > 0) {
					Optional<City> optCity = cityRepository.findByObjectId(city.getId());
					if (optCity.isPresent()) {
						City foundCity = optCity.get();
						foundCity.setName(city.getName());
						foundCity.setDescription(city.getDescription());
						foundCity.setUpdatedOn(dtNow);
						City savedCity = cityRepository.save(foundCity);
						if (savedCity != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "City");
							status.setCity(savedCity);
							logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.EDIT, null,
									savedCity.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "City");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "City id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit City");
		}
		return status;
	}

	@Override
	public DesireStatus deleteCity(String authToken, long cityId) {
		LOGGER.info("deleteCity called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (cityId > 0) {
					Optional<City> optCity = cityRepository.findByObjectId(cityId);
					if (optCity.isPresent()) {
						City foundCity = optCity.get();
						cityRepository.delete(foundCity);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "City");
						logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "City");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "City id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete City");
		}
		return status;
	}

	@Override
	public DesireStatus areas(String authToken, DesireSearchRequest request) {
		LOGGER.info("areas called!");
		DesireStatus status = new DesireStatus();
		List<Area> areas = null;
		Page<Area> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = areaRepository.findAllASC(pageable);
				} else {
					page = areaRepository.findAllDESC(pageable);
				}
				areas = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Area");
				status.setAreas(areas);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Area");
		}
		return status;
	}

	@Override
	public DesireStatus areas(String authToken, long cityId, DesireSearchRequest request) {
		LOGGER.info("areas called!");
		DesireStatus status = new DesireStatus();
		List<Area> areas = null;
		Page<Area> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<City> optCity = cityRepository.findByObjectId(cityId);
				if (optCity.isPresent()) {
					City city = optCity.get();
					pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
					pageNumber = pageNumber - 1;
					pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
					Pageable pageable = PageRequest.of(pageNumber, pageSize);

					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = areaRepository.findAllByCityASC(city, pageable);
					} else {
						page = areaRepository.findAllByCityDESC(city, pageable);
					}
					areas = page.getContent();

					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Area");
					status.setAreas(areas);
					status.setTotalRecord(page.getTotalElements());
					status.setTotalPage(page.getTotalPages());
					logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.LIST, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.OBJ_NOT_EXIST, "City");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Area");
		}
		return status;
	}

	@Override
	public DesireStatus areasByName(String authToken, String areaName, DesireSearchRequest request) {
		LOGGER.info("areas called!");
		DesireStatus status = new DesireStatus();
		List<Area> areas = null;
		Page<Area> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = areaRepository.findAllByNameASC(areaName, pageable);
				} else {
					page = areaRepository.findAllByNameDESC(areaName, pageable);
				}
				areas = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Area");
				status.setAreas(areas);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Area");
		}
		return status;
	}

	@Override
	public DesireStatus areasByName(AreaSearchRequest request) {
		LOGGER.info("areas called!");
		DesireStatus status = new DesireStatus();
		List<Area> areas = null;
		Page<Area> page = null;
		int pageNumber = 0, pageSize = 0;
		try {

			pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
			pageNumber = pageNumber - 1;
			pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
			Pageable pageable = PageRequest.of(pageNumber, pageSize);

			if (request.getOrderBy().equalsIgnoreCase("ASC")) {
				page = areaRepository.findAllByNameASC(request.getAreaName(), request.getCityId(), pageable);
			} else {
				page = areaRepository.findAllByNameDESC(request.getAreaName(), request.getCityId(), pageable);
			}
			areas = page.getContent();

			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Area");
			status.setAreas(areas);
			status.setTotalRecord(page.getTotalElements());
			status.setTotalPage(page.getTotalPages());

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Area");
		}
		return status;
	}

	@Override
	public DesireStatus areaByName(String authToken, String areaName) {
		LOGGER.info("areaByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Area foundArea = areaRepository.findByName(areaName);
				if (foundArea != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Area");
					status.setArea(foundArea);
					logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Area");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Area");
		}
		return status;
	}

	@Override
	public DesireStatus viewArea(String authToken, long areaId) {
		LOGGER.info("viewArea called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (areaId > 0) {
					Optional<Area> optArea = areaRepository.findByObjectId(areaId);
					if (optArea.isPresent()) {
						Area foundArea = optArea.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Area");
						status.setArea(foundArea);
						logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Area");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Area id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Area");
		}
		return status;
	}

	@Override
	public DesireStatus addArea(String authToken, Area area) {
		LOGGER.info("addArea called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addArea object recieved >> " + area);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<City> optCity = cityRepository.findByObjectId(area.getCity().getId());
				City foundCity = null;
				if (optCity.isEmpty()) {
					City city = new City();
					city.setName(area.getCity().getName());
					city.setDescription(area.getCity().getName() + " area.");
					DesireStatus cityStatus = addCity(city);
					foundCity = cityStatus.getCity();
				} else {
					foundCity = optCity.get();
				}
				area.setCity(foundCity);
				area.setCreatedOn(dtNow);
				area.setUpdatedOn(dtNow);
				area.setActive(true);
				area.setDeleted(false);
				Area savedArea = areaRepository.save(area);
				if (savedArea != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Area");
					status.setArea(savedArea);
					logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.ADD,
							savedArea.getCreatedOn(), savedArea.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Area");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Area");
		}

		return status;
	}

	@Override
	public DesireStatus addArea(Area area) {
		LOGGER.info("addArea called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addArea object recieved >> " + area);
		try {
			Optional<City> optCity = cityRepository.findByName(area.getCity().getName());
			City foundCity = null;
			if (optCity.isEmpty()) {

				City city = new City();
				city.setName(area.getCity().getName());
				city.setDescription(area.getCity().getName() + " area.");
				DesireStatus cityStatus = addCity(city);
				foundCity = cityStatus.getCity();
			} else {
				foundCity = optCity.get();
			}
			area.setCity(foundCity);
			area.setCreatedOn(dtNow);
			area.setUpdatedOn(dtNow);
			area.setActive(true);
			area.setDeleted(false);
			Area savedArea = areaRepository.save(area);
			if (savedArea != null) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Area");
				status.setArea(savedArea);
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Area");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Area");
		}

		return status;
	}

	@Override
	public DesireStatus editArea(String authToken, Area area) {
		LOGGER.info("editArea called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (area.getId() != null && area.getId() > 0) {
					Optional<Area> optArea = areaRepository.findByObjectId(area.getId());
					if (optArea.isPresent()) {
						Area foundArea = optArea.get();
						foundArea.setName(area.getName());
						foundArea.setDescription(area.getDescription());
						foundArea.setUpdatedOn(dtNow);
						Area savedArea = areaRepository.save(foundArea);
						if (savedArea != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Area");
							status.setArea(savedArea);
							logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.EDIT, null,
									savedArea.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Area");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Area id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Area");
		}
		return status;
	}

	@Override
	public DesireStatus deleteArea(String authToken, long areaId) {
		LOGGER.info("deleteArea called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (areaId > 0) {
					Optional<Area> optArea = areaRepository.findByObjectId(areaId);
					if (optArea.isPresent()) {
						Area foundArea = optArea.get();
						areaRepository.delete(foundArea);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Area");
						logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Area");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Area id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Area");
		}
		return status;
	}

	@Override
	public DesireStatus services(String authToken, DesireSearchRequest request) {
		LOGGER.info("services called!");
		DesireStatus status = new DesireStatus();
		List<HomeService> services = null;
		Page<HomeService> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = serviceRepository.findAllASC(pageable);
				} else {
					page = serviceRepository.findAllDESC(pageable);
				}
				services = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Service");
				status.setServices(services);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Service");
		}
		return status;
	}

	@Override
	public DesireStatus services() {
	    LOGGER.info("services called!");
	    DesireStatus status = new DesireStatus();
	    try {
	        List<String> serviceKeywords = Arrays.asList("property", "interior design");
	        
	        List<com.all.homedesire.entities.HomeService> allServices = serviceRepository.findAll();
	        
	        List<com.all.homedesire.entities.HomeService> filteredServices = allServices.stream()
	            .filter(service -> serviceKeywords.stream()
	                .anyMatch(keyword -> service.getName() != null && service.getName().toLowerCase().contains(keyword.toLowerCase())))
	            .collect(Collectors.toList());
	        
	        status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Service");
	        status.setServices(filteredServices);
	        
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
	                "List Service");
	    }
	    return status;
	}

	@Override
	public DesireStatus serviceByName(String authToken, String serviceName) {
		LOGGER.info("serviceByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<HomeService> optService = serviceRepository.findByName(serviceName);
				if (optService.isPresent()) {
					HomeService foundService = optService.get();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Service");
					status.setService(foundService);
					logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.DETAIL, null, null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Service");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (

		Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Service");
		}
		return status;
	}

	@Override
	public DesireStatus viewService(String authToken, long serviceId) {
		LOGGER.info("viewService called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (serviceId > 0) {
					Optional<HomeService> optService = serviceRepository.findByObjectId(serviceId);
					if (optService.isPresent()) {
						HomeService foundService = optService.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Service");
						status.setService(foundService);
						logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Service");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Service id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Service");
		}
		return status;
	}

	@Override
	public DesireStatus addService(String authToken, HomeService service) {
		LOGGER.info("addService called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addService object recieved >> " + service);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<HomeService> optService = serviceRepository.findByName(service.getName());
				if (optService.isEmpty()) {
					service.setCreatedOn(dtNow);
					service.setUpdatedOn(dtNow);
					service.setActive(true);
					service.setDeleted(false);
					HomeService savedService = serviceRepository.save(service);
					if (savedService != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Service");
						status.setService(savedService);
						logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.ADD,
								savedService.getCreatedOn(), savedService.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Service");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
							"Service with name (" + service.getName() + ")");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Service");
		}

		return status;
	}

	@Override
	public DesireStatus addService(HomeService service) {
		LOGGER.info("addService called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addService object recieved >> " + service);
		try {
			Optional<HomeService> optService = serviceRepository.findByName(service.getName());
			if (optService.isEmpty()) {
				service.setCreatedOn(dtNow);
				service.setUpdatedOn(dtNow);
				service.setActive(true);
				service.setDeleted(false);
				HomeService savedService = serviceRepository.save(service);
				if (savedService != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Service");
					status.setService(savedService);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Service");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
						"Service with name (" + service.getName() + ")");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Service");
		}

		return status;
	}

	@Override
	public DesireStatus editService(String authToken, HomeService service) {
		LOGGER.info("editService called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (service.getId() != null && service.getId() > 0) {
					Optional<HomeService> optService = serviceRepository.findByObjectId(service.getId());
					if (optService.isPresent()) {
						HomeService foundService = optService.get();
						foundService.setName(service.getName());
						foundService.setDescription(service.getDescription());
						foundService.setUpdatedOn(dtNow);
						HomeService savedService = serviceRepository.save(foundService);
						if (savedService != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Service");
							status.setService(savedService);
							logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.EDIT, null,
									savedService.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Service");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Service id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status.setStatusType(Constants.STATUS_ERROR);
			status.setText("Edit Service got error : " + e.getMessage());
		}
		return status;
	}

	@Override
	public DesireStatus deleteService(String authToken, long serviceId) {
		LOGGER.info("deleteService called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (serviceId > 0) {
					Optional<HomeService> optService = serviceRepository.findByObjectId(serviceId);
					if (optService.isPresent()) {
						HomeService foundService = optService.get();
						serviceRepository.delete(foundService);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Service");
						logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Service");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Service id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Service");
		}
		return status;
	}

	@Override
	public DesireStatus desiredQueries(String authToken, DesireSearchRequest request) {
		LOGGER.info("desiredQueries called!");
		DesireStatus status = new DesireStatus();
		List<DesiredQuery> desiredQueries = null;
		Page<DesiredQuery> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = desiredQueryRepository.findAllASC(pageable);
				} else {
					page = desiredQueryRepository.findAllDESC(pageable);
				}
				desiredQueries = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Query");
				status.setDesiredQueries(desiredQueries);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Query");
		}
		return status;
	}

	@Override
	public DesireStatus desiredQueryByName(String authToken, String desiredQueryName) {
		LOGGER.info("desiredQueryByName called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				DesiredQuery foundDesiredQuery = desiredQueryRepository.findByName(desiredQueryName);
				if (foundDesiredQuery != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Query");
					status.setDesiredQuery(foundDesiredQuery);
					logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.DETAIL, null,
							null);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Query");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Query");
		}
		return status;
	}

	@Override
	public DesireStatus desiredQueryById(String authToken, long desiredQueryId) {
		LOGGER.info("desiredQueryById called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (desiredQueryId > 0) {
					Optional<DesiredQuery> foundDesiredQuery = desiredQueryRepository.findByObjectId(desiredQueryId);
					if (foundDesiredQuery != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Query");
						status.setDesiredQuery(foundDesiredQuery.get());
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Query");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Query id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Query");
		}
		return status;
	}

	@Override
	public DesireStatus addDesiredQuery(String authToken, DesiredQuery desiredQuery) {
		LOGGER.info("addDesiredQuery called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addDesiredQuery object recieved >> " + desiredQuery);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				Optional<City> optCity = cityRepository.findByName(desiredQuery.getCity().getName());
				if (optCity.isPresent()) {
					City foundCity = optCity.get();
					Area foundArea = areaRepository.findByName(desiredQuery.getArea().getName());
					if (foundArea == null) {
						Area area = new Area();
						area.setCity(foundCity);
						area.setName(desiredQuery.getArea().getName());
						area.setDescription(desiredQuery.getArea().getName() + " area.");
						DesireStatus cityStatus = addArea(authToken, area);
						foundArea = cityStatus.getArea();
					}
					Optional<HomeService> optService = serviceRepository
							.findByName(desiredQuery.getService().getName());
					if (optService.isPresent()) {
						HomeService foundService = optService.get();
						desiredQuery.setCity(foundCity);
						desiredQuery.setArea(foundArea);
						desiredQuery.setService(foundService);
						desiredQuery.setCreatedOn(dtNow);
						desiredQuery.setUpdatedOn(dtNow);
						desiredQuery.setActive(true);
						desiredQuery.setDeleted(false);
						DesiredQuery savedDesiredQuery = desiredQueryRepository.save(desiredQuery);
						if (savedDesiredQuery != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Query");
							status.setDesiredQuery(savedDesiredQuery);
							logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.ADD,
									savedDesiredQuery.getCreatedOn(), savedDesiredQuery.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Query");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Service");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "City");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Query");
		}

		return status;
	}

	@Override
	public DesireStatus addDesiredQuery(DesiredQuery desiredQuery) {
		LOGGER.info("addDesiredQuery called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addDesiredQuery object recieved >> " + desiredQuery);
		try {
			Optional<City> optCity = cityRepository.findByName(desiredQuery.getCity().getName());
			if (optCity.isPresent()) {
				City foundCity = optCity.get();
				Area foundArea = areaRepository.findByName(desiredQuery.getArea().getName());
				if (foundArea == null) {
					Area area = new Area();
					area.setCity(foundCity);
					area.setName(desiredQuery.getArea().getName());
					area.setDescription(desiredQuery.getArea().getName() + " area.");
					DesireStatus cityStatus = addArea(area);
					foundArea = cityStatus.getArea();
				}
				Optional<HomeService> optService = serviceRepository.findByName(desiredQuery.getService().getName());
				if (optService.isPresent()) {
					HomeService foundService = optService.get();
					desiredQuery.setCity(foundCity);
					desiredQuery.setArea(foundArea);
					desiredQuery.setService(foundService);
					desiredQuery.setCreatedOn(dtNow);
					desiredQuery.setUpdatedOn(dtNow);
					desiredQuery.setActive(true);
					desiredQuery.setDeleted(false);
					DesiredQuery savedDesiredQuery = desiredQueryRepository.save(desiredQuery);
					if (savedDesiredQuery != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Query");
						status.setDesiredQuery(savedDesiredQuery);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Query");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Service");
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "City");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Query");
		}

		return status;
	}

	@Override
	public DesireStatus editDesiredQuery(String authToken, DesiredQuery desiredQuery) {
		LOGGER.info("editDesiredQuery called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (desiredQuery.getId() != null && desiredQuery.getId() > 0) {
					Optional<DesiredQuery> optDesiredQuery = desiredQueryRepository
							.findByObjectId(desiredQuery.getId());
					if (optDesiredQuery.isPresent()) {
						DesiredQuery foundDesiredQuery = optDesiredQuery.get();
						Optional<City> optCity = cityRepository.findByName(desiredQuery.getCity().getName());
						if (optCity.isPresent()) {
							City foundCity = optCity.get();
							Area foundArea = areaRepository.findByName(desiredQuery.getArea().getName());
							if (foundArea != null) {
								Optional<HomeService> optService = serviceRepository
										.findByName(desiredQuery.getService().getName());
								if (optService.isPresent()) {
									HomeService foundService = optService.get();
									foundDesiredQuery.setCity(foundCity);
									foundDesiredQuery.setArea(foundArea);
									foundDesiredQuery.setService(foundService);
									foundDesiredQuery.setName(desiredQuery.getName());
									foundDesiredQuery.setMobileNumber(desiredQuery.getMobileNumber());
									foundDesiredQuery.setEmailAddress(desiredQuery.getEmailAddress());
									foundDesiredQuery.setQuery(desiredQuery.getQuery());
									foundDesiredQuery.setUpdatedOn(dtNow);
									DesiredQuery savedDesiredQuery = desiredQueryRepository.save(foundDesiredQuery);
									if (savedDesiredQuery != null) {
										status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
												"Query");
										status.setDesiredQuery(savedDesiredQuery);
										logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY,
												LogConstants.EDIT, null, savedDesiredQuery.getUpdatedOn());
									} else {
										status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
												"Query");
									}
								} else {
									status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
											"Service");
								}
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Area");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "City");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Query id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Query");
		}
		return status;
	}

	@Override
	public DesireStatus deleteDesiredQuery(String authToken, long desiredQueryId) {
		LOGGER.info("deleteDesiredQuery called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (desiredQueryId > 0) {
					Optional<DesiredQuery> optDesiredQuery = desiredQueryRepository.findByObjectId(desiredQueryId);
					if (optDesiredQuery.isPresent()) {
						DesiredQuery foundDesiredQuery = optDesiredQuery.get();
						desiredQueryRepository.delete(foundDesiredQuery);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Query");
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Query");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Query id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Query");
		}
		return status;
	}

	private UserType getUserType(String typeName) {
		Optional<UserType> userType = null;
		switch (typeName) {
		case "SUPER_ADMIN":
			userType = userTypeRepository.findByName(EType.SUPER_ADMIN);
			if (!userType.isPresent()) {
				throw new RuntimeException("Type is not found.");
			}

			break;
		case "ALL_HOME_DESIRE":
			userType = userTypeRepository.findByName(EType.ALL_HOME_DESIRE);
			if (!userType.isPresent()) {
				throw new RuntimeException("Type is not found.");
			}

			break;
		case "PARTNER":
			userType = userTypeRepository.findByName(EType.PARTNER);
			if (!userType.isPresent()) {
				throw new RuntimeException("Type is not found.");
			}

			break;
		case "CUSTOMER":
			userType = userTypeRepository.findByName(EType.CUSTOMER);
			if (!userType.isPresent()) {
				throw new RuntimeException("Type is not found.");
			}

			break;
		default:
			userType = userTypeRepository.findByName(EType.CUSTOMER);
			if (!userType.isPresent()) {
				throw new RuntimeException("Type is not found.");
			}
		}
		return (userType.isPresent()) ? userType.get() : null;
	}

	private Set<Role> getRole(Set<String> userRoles) {
		Set<Role> roles = new HashSet<>();
		userRoles.forEach(role -> {
			switch (role) {
			case "ROLE_ADMIN":
				Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
				if (adminRole.isPresent()) {
					roles.add(adminRole.get());
				} else {
					throw new RuntimeException("Role is not found.");
				}

				break;
			case "ROLE_LEAD":
				Optional<Role> leadRole = roleRepository.findByName(ERole.ROLE_LEAD);
				if (leadRole.isPresent()) {
					roles.add(leadRole.get());
				} else {
					throw new RuntimeException("Role is not found.");
				}

				break;
			case "ROLE_TEAM_MEMBER":
				Optional<Role> memeberRole = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER);
				if (memeberRole.isPresent()) {
					roles.add(memeberRole.get());
				} else {
					throw new RuntimeException("Role is not found.");
				}

				break;
			case "ROLE_FRONT_DESK":
				Optional<Role> frontRole = roleRepository.findByName(ERole.ROLE_FRONT_DESK);
				if (frontRole.isPresent()) {
					roles.add(frontRole.get());
				} else {
					throw new RuntimeException("Role is not found.");
				}

				break;
			default:
				Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);
				if (userRole.isPresent()) {
					roles.add(userRole.get());
				} else {
					throw new RuntimeException("Role is not found.");
				}
			}
		});
		return roles;
	}

	@Override
	public DesireStatus updatePassword(String authToken, long userId, String newPassword) {
		LOGGER.info("updatePassword called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> foundUser = userRepository.findByObjectId(userId);
				if (foundUser.isPresent()) {
					User user = foundUser.get();
					user.setPassword(encoder.encode(newPassword));

					user.setResetPasswordToken("");
					User savedUser = userRepository.save(user);
					if (savedUser != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Password");
						logService.createLog(authStatus.getUser(), LogConstants.PASSWORD, LogConstants.EDIT, null,
								savedUser.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Password");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Update password");
		}
		return status;
	}

	@Override
	public DesireStatus updateProfilePicture(String authToken, User user, String fileName) {
		LOGGER.info("UserService >> updateProfilePicture called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				user.setPhoto(fileName);
				User savedUser = userRepository.save(user);
				if (savedUser != null) {
					logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.PROFILE_PICTURE, null,
							savedUser.getUpdatedOn());
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
							"User profile picture");
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
							"User profile picture");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user profile picture");
		}
		return status;
	}

	@Override
	public City getCity(String authToken, String countryName, String stateName, String cityName) {
		Country country = null;
		State state = null;
		City city = null;

		countryName = (countryName != null) ? countryName : "NONE";
		stateName = (stateName != null) ? stateName : "NONE";
		cityName = (cityName != null) ? cityName : "NONE";

		Optional<Country> optCountry = countryRepository.findByName(countryName);
		if (optCountry.isPresent()) {
			country = optCountry.get();
		} else {
			Country newCountry = new Country();
			newCountry.setName(countryName);
			newCountry.setShortCode("");
			newCountry.setCountryCode("");
			DesireStatus status = addCountry(authToken, newCountry);
			country = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCountry() : null;
		}
		if (country != null) {
			Optional<State> optState = stateRepository.findByNameAndCountry(stateName, country.getId());
			if (optState.isPresent()) {
				state = optState.get();
			} else {
				State newState = new State();
				newState.setName(stateName);
				newState.setCountry(country);
				DesireStatus status = addState(authToken, newState);
				state = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getState() : null;
			}

			if (state != null) {
				Optional<City> optCity = cityRepository.findByNameAndState(cityName, state.getId());
				if (optCity.isPresent()) {
					city = optCity.get();
				} else {
					City newCity = new City();
					newCity.setName(cityName);
					newCity.setState(state);
					DesireStatus status = addCity(authToken, newCity);
					city = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCity() : null;
				}
			}
		}
		return city;
	}

	@Override
	public City getCity(String countryName, String stateName, String cityName) {
		Country country = null;
		State state = null;
		City city = null;

		countryName = (countryName != null) ? countryName : "NONE";
		stateName = (stateName != null) ? stateName : "NONE";
		cityName = (cityName != null) ? cityName : "NONE";

		Optional<Country> optCountry = countryRepository.findByName(countryName);
		if (optCountry.isPresent()) {
			country = optCountry.get();
		} else {
			Country newCountry = new Country();
			newCountry.setName(countryName);
			newCountry.setShortCode("");
			newCountry.setCountryCode("");
			DesireStatus status = addCountry(newCountry);
			country = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCountry() : null;
		}
		if (country != null) {
			Optional<State> optState = stateRepository.findByNameAndCountry(stateName, country.getId());
			if (optState.isPresent()) {
				state = optState.get();
			} else {
				State newState = new State();
				newState.setName(stateName);
				newState.setCountry(country);
				DesireStatus status = addState(newState);
				state = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getState() : null;
			}

			if (state != null) {
				Optional<City> optCity = cityRepository.findByNameAndState(cityName, state.getId());
				if (optCity.isPresent()) {
					city = optCity.get();
				} else {
					City newCity = new City();
					newCity.setName(cityName);
					newCity.setState(state);
					DesireStatus status = addCity(newCity);
					city = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCity() : null;
				}
			}
		}
		return city;
	}

	@Override
	public Area getArea(String authToken, String countryName, String stateName, String cityName, String areaName) {
		Area area = null;

		countryName = (countryName != null) ? countryName : "NONE";
		stateName = (stateName != null) ? stateName : "NONE";
		cityName = (cityName != null) ? cityName : "NONE";
		areaName = (areaName != null) ? areaName : "NONE";
		City city = getCity(authToken, countryName, stateName, cityName);
		if (city != null) {
			Optional<Area> optArea = areaRepository.findByNameAndCity(areaName, city.getId());
			if (optArea.isPresent()) {
				area = optArea.get();
			} else {
				Area addArea = new Area();
				addArea.setCity(city);
				addArea.setName(areaName);
				addArea.setDescription(areaName);
				DesireStatus status = addArea(authToken, addArea);
				area = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getArea() : null;
			}
		}

		return area;
	}

	@Override
	public Area getArea(String countryName, String stateName, String cityName, String areaName) {
		Area area = null;

		countryName = (countryName != null) ? countryName : "NONE";
		stateName = (stateName != null) ? stateName : "NONE";
		cityName = (cityName != null) ? cityName : "NONE";
		areaName = (areaName != null) ? areaName : "NONE";
		City city = getCity(countryName, stateName, cityName);
		if (city != null) {
			Optional<Area> optArea = areaRepository.findByNameAndCity(areaName, city.getId());
			if (optArea.isPresent()) {
				area = optArea.get();
			} else {
				Area addArea = new Area();
				addArea.setCity(city);
				addArea.setName(areaName);
				addArea.setDescription(areaName);
				DesireStatus status = addArea(addArea);
				area = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getArea() : null;
			}
		}

		return area;
	}

	@Override
	public DesireStatus changeUserState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeUserState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<User> optObj = userRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						User user = optObj.get();
						user.setActive(request.isActive());
						User savedUser = userRepository.save(user);
						if (savedUser != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "User");
							status.setUser(savedUser);
							logService.createLog(authStatus.getUser(), LogConstants.USER, LogConstants.ACTIVE, null,
									savedUser.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "User");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change user active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeUserTypeState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeUserTypeState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<UserType> optObj = userTypeRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						UserType userType = optObj.get();
						userType.setActive(request.isActive());
						UserType savedUserType = userTypeRepository.save(userType);
						if (savedUserType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User type");
							status.setUserType(savedUserType);
							logService.createLog(authStatus.getUser(), LogConstants.USER_TYPE, LogConstants.ACTIVE,
									null, savedUserType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change user type active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeUserRoleState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeUserRoleState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<Role> optObj = roleRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						Role role = optObj.get();
						role.setActive(request.isActive());
						Role savedRole = roleRepository.save(role);
						if (savedRole != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Role");
							status.setRole(savedRole);
							logService.createLog(authStatus.getUser(), LogConstants.USER_ROLE, LogConstants.ACTIVE,
									null, savedRole.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Role");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User role");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User role id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change user role active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeCountryState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeCountryState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<Country> optObj = countryRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						Country country = optObj.get();
						country.setActive(request.isActive());
						Country savedCountry = countryRepository.save(country);
						if (savedCountry != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Country");
							status.setCountry(savedCountry);
							logService.createLog(authStatus.getUser(), LogConstants.COUNTRY, LogConstants.ACTIVE, null,
									savedCountry.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Country");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Country");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Country id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change country active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeStateActive(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeStateActive called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<State> optObj = stateRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						State state = optObj.get();
						state.setActive(request.isActive());
						State savedState = stateRepository.save(state);
						if (savedState != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "State");
							status.setState(savedState);
							logService.createLog(authStatus.getUser(), LogConstants.STATE, LogConstants.ACTIVE, null,
									savedState.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "State");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "State");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "State id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change states active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeCityState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeCityState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<City> optObj = cityRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						City comment = optObj.get();
						comment.setActive(request.isActive());
						City savedCity = cityRepository.save(comment);
						if (savedCity != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "City");
							status.setCity(savedCity);
							logService.createLog(authStatus.getUser(), LogConstants.CITY, LogConstants.ACTIVE, null,
									savedCity.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "City");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "City");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "City id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change city active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeAreaState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeAreaState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<Area> optObj = areaRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						Area area = optObj.get();
						area.setActive(request.isActive());
						Area savedArea = areaRepository.save(area);
						if (savedArea != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Area");
							status.setArea(savedArea);
							logService.createLog(authStatus.getUser(), LogConstants.AREA, LogConstants.ACTIVE, null,
									savedArea.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Area");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Area");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Area id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change area active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeServiceState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeServiceState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<HomeService> optObj = serviceRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						HomeService service = optObj.get();
						service.setActive(request.isActive());
						HomeService savedService = serviceRepository.save(service);
						if (savedService != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Service");
							status.setService(savedService);
							logService.createLog(authStatus.getUser(), LogConstants.SERVICE, LogConstants.ACTIVE, null,
									savedService.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Service");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Service");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Service id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change service active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeContactUsState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeContactUsState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<ContactUs> optObj = contactUsRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						ContactUs contactUs = optObj.get();
						contactUs.setActive(request.isActive());
						ContactUs savedContactUs = contactUsRepository.save(contactUs);
						if (savedContactUs != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Contact us");
							status.setContactUs(savedContactUs);
							logService.createLog(authStatus.getUser(), LogConstants.CONTACT_US, LogConstants.ACTIVE,
									null, savedContactUs.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Contact us");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Contact us");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Contact us id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change contact us active state");
		}
		return status;
	}

	@Override
	public DesireStatus changeDesiredQueryState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeDesiredQueryState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<DesiredQuery> optObj = desiredQueryRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						DesiredQuery query = optObj.get();
						query.setActive(request.isActive());
						DesiredQuery savedQuery = desiredQueryRepository.save(query);
						if (savedQuery != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Query");
							status.setDesiredQuery(savedQuery);
							logService.createLog(authStatus.getUser(), LogConstants.DESIRED_QUERY, LogConstants.ACTIVE,
									null, savedQuery.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Query");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Query");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Query id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change desired query active state");
		}
		return status;
	}
	
//	=========================================================================
	//  generate method subject for already registered users
	private String alreadyRegisteredEmailSubject() {
	    return " Customer already exist";
	}

	//  generate method message body for already registered users
	private String alreadyRegisteredEmailBody(User existingUser, String identifier) {
		 return String.format("<html><body>" +
                 "<p>Dear %s,</p>" +
                 "<p>We noticed that you're trying to create a new account with us, but it seems you already have an\n"
                 + " existing account.</p>" +
                 "<p>To proceed with your enrollment, please log in to your existing account using your email address\n"
                 + " and password.</p>" +
                 "<p> If you believe there's been a mistake or if you're having trouble accessing your account, please\n"
                 + " contact our customer support at allhomedesire@gmail.com or +91 9818961783.</p>" +
                 "<p> Thank you for your understanding.</p>"+
                 "<p>Best regards,<br>" +
                 "The All Home Desire Team<br>" +
                 "www.allhomedesire.com<br>" +
                 "+91 9818961783</p>" +
                 "</body></html>", existingUser.getFirstName().toUpperCase());
	}

//	=========================================================================
	

}
