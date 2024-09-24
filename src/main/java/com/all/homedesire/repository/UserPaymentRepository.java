package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.UserPayment;

public interface UserPaymentRepository extends JpaRepository<UserPayment, Long> {
	@Query("SELECT u FROM UserPayment u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserPayment> findByObjectId(@Param("id") long id);
	
	@Query("SELECT u FROM UserPayment u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserPayment> findAllASC(Pageable pageable);

	@Query("SELECT u FROM UserPayment u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserPayment> findAllDESC(Pageable pageable);
	
	@Query("SELECT u FROM UserPayment u WHERE u.user.id=:userId AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserPayment> findAllASC(@Param("userId") long userId, Pageable pageable);

	@Query("SELECT u FROM UserPayment u WHERE u.user.id=:userId AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserPayment> findAllDESC(@Param("userId") long userId, Pageable pageable);
}
