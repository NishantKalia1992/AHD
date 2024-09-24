package com.all.homedesire.repository;

import com.all.homedesire.entities.User;
import com.all.homedesire.enums.EType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<User> findByObjectId(@Param("id") long id);
	
	@Query("SELECT u FROM User u WHERE u.id =:id AND u.isDeleted=false")
	public Optional<User> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT u FROM User u WHERE u.emailId =:emailId AND u.isActive=true AND u.isDeleted=false")
	public Optional<User> findByEmailId(@Param("emailId") String emailId);

	@Query("SELECT u FROM User u WHERE u.mobileNo =:mobileNo AND u.isActive=true AND u.isDeleted=false")
	public Optional<User> findByMobileNo(@Param("mobileNo") String mobileNo);

	// @Query("SELECT u FROM User u WHERE u.emailId =:emailId AND u.isActive=true
	// AND u.isDeleted=false")
	// public Boolean existsByEmailId(@Param("emailId") String email);

	// @Query("SELECT u FROM User u WHERE u.mobileNo =:mobileNo AND u.isActive=true
	// AND u.isDeleted=false")
	// public boolean existsByMobileNo(@Param("mobileNo") String mobileNo);

	@Query("SELECT u FROM User u WHERE u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn ASC")
	public List<User> findAllUsers();

	@Query("SELECT u FROM User u WHERE u.isDeleted=false ORDER BY u.updatedOn ASC")
	public List<User> findAllActiveStateUser();

	@Query("SELECT u FROM User u WHERE u.isActive=:isActive and u.isDeleted=false ORDER BY u.updatedOn ASC")
	public List<User> findAllActiveStateUser(@Param("isActive") boolean isActive);

	@Query("SELECT u FROM User u WHERE u.parentUser>0 AND u.parentUser=:userId and u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn ASC")
	public List<User> findByParentUser(@Param("userId") long userId);

	@Query("SELECT u FROM User u WHERE u.parentUser>0 AND u.parentUser=:userId and u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findByPartnerUserASC(@Param("userId") long userId, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findAllActiveStateUserASC(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findAllUsersASC(Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.parentUser=:userId and u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findByPartnerUserDESC(@Param("userId") long userId, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findAllActiveStateUserDESC(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isActive=true and u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findAllUsersDESC(Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.userType.eType=:userType AND u.isActive=:isActive AND u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findAllUserASC(@Param("userType") EType userType, @Param("isActive") Boolean isActive, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.userType.eType=:userType AND u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findAllUserASC(@Param("userType") EType valueOf, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isActive=:isActive AND u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<User> findAllUserASC(@Param("isActive") Boolean isActive, Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.userType.eType=:userType AND u.isActive=:isActive AND u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findAllUserDESC(@Param("userType") EType userType, @Param("isActive") Boolean isActive, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.userType.eType=:userType AND u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findAllUserDESC(@Param("userType") EType valueOf, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.isActive=:isActive AND u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<User> findAllUserDESC(@Param("isActive") Boolean isActive, Pageable pageable);

}
