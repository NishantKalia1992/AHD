package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyPurpose;

public interface PropertyPurposeRepository extends JpaRepository<PropertyPurpose, Long> {
	@Query("SELECT p FROM PropertyPurpose p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyPurpose> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyPurpose p WHERE p.name =:name AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyPurpose> findByName(@Param("name") String name);

	@Query("SELECT p FROM PropertyPurpose p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyPurpose> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyPurpose p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyPurpose> findAllDESC(Pageable pageable);

}
