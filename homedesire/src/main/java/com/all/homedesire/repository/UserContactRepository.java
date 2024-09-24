package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserContact;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {
	@Query("SELECT u FROM UserContact u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserContact> findByObjectId(@Param("id") long id);

	@Query("SELECT u FROM UserContact u WHERE u.isActive=true AND u.isDeleted=false AND u.user =:user AND (u.mobileNo =:mobileNo OR u.emailId =:emailId)")
	public List<UserContact> findByPhoneOrEmail(@Param("user") User user, @Param("mobileNo") String mobileNo,
			@Param("emailId") String emailId);

	@Query("SELECT u FROM UserContact u WHERE u.isActive = true AND u.isDeleted = false Order By u.updatedOn DESC")
	List<UserContact> findAllContact();

	@Query("SELECT u FROM UserContact u WHERE u.isActive =:isActive AND u.isDeleted =:isDeleted Order By u.updatedOn DESC")
	List<UserContact> findAllByActiveAndDeleted(boolean isActive, boolean isDeleted);

	@Query("SELECT u FROM UserContact u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserContact> findAllASC(Pageable pageable);

	@Query("SELECT u FROM UserContact u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserContact> findAllDESC(Pageable pageable);

	@Query("SELECT u FROM UserContact u WHERE u.user=:user AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserContact> findAllASC(@Param("user") User user, Pageable pageable);

	@Query("SELECT u FROM UserContact u WHERE u.user=:user AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserContact> findAllDESC(@Param("user") User user, Pageable pageable);

	@Query("SELECT u FROM UserContact u WHERE u.id =?1 AND u.isActive=true AND u.isDeleted=false")
	List<UserContact> findByCustomerId(Long customerId);

}
