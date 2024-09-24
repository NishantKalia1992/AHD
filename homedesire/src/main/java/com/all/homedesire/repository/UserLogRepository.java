package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.UserLog;


public interface UserLogRepository extends JpaRepository<UserLog, Long> {
	@Query("SELECT u FROM UserLog u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserLog> findByObjectId(@Param("id") long id);
	
	@Query("SELECT u FROM UserLog u WHERE u.isActive =:isActive AND u.isDeleted =:isDeleted Order By u.updatedOn DESC")
	List<UserLog> findAllByActiveAndDeleted(boolean isActive, boolean isDeleted);

	@SuppressWarnings("rawtypes")
	@Query("SELECT u FROM UserLog u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page findAllASC(Pageable pageable);
	
	@SuppressWarnings("rawtypes")
	@Query("SELECT u FROM UserLog u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page findAllDESC(Pageable pageable);

}
