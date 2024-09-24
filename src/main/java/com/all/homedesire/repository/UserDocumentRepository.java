package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.UserDocument;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Long> {
	@Query("SELECT u FROM UserDocument u WHERE u.id =:id AND u.isActive=true AND u.isDeleted=false")
	public Optional<UserDocument> findByObjectId(@Param("id") long id);
	
	@Query("SELECT u FROM UserDocument u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserDocument> findAllASC(Pageable pageable);

	@Query("SELECT u FROM UserDocument u WHERE u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserDocument> findAllDESC(Pageable pageable);
	
	@Query("SELECT u FROM UserDocument u WHERE u.user.id=:userId AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn ASC")
	Page<UserDocument> findAllASC(@Param("userId") long userId, Pageable pageable);

	@Query("SELECT u FROM UserDocument u WHERE u.user.id=:userId AND u.isActive =true AND u.isDeleted =false Order By u.updatedOn DESC")
	Page<UserDocument> findAllDESC(@Param("userId") long userId, Pageable pageable);
}
