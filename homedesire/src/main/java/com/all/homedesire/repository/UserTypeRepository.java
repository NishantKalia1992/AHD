package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.all.homedesire.entities.UserType;
import com.all.homedesire.enums.EType;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long>{
	@Query("SELECT u FROM UserType u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserType> findByObjectId(@Param("id") long id);
	
	@Query("SELECT u FROM UserType u WHERE u.id =:id AND u.isDeleted=false")
	public Optional<UserType> findByObjectIdToActivate(@Param("id") long id);
	
	@Query("SELECT u FROM UserType u WHERE u.eType =:eType AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserType> findByName(@Param("eType") EType userTypeName);

	@Query("SELECT u FROM UserType u WHERE u.isActive=true AND u.isDeleted=false ORDER BY u.updatedOn ASC")
	public Page<UserType> findAllASC(Pageable pageable);

	@Query("SELECT u FROM UserType u WHERE u.isActive=true AND u.isDeleted=false ORDER BY u.updatedOn DESC")
	public Page<UserType> findAllDESC(Pageable pageable);
}
