package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyStructure;

public interface PropertyStructureRepository extends JpaRepository<PropertyStructure, Long> {
	@Query("SELECT p FROM PropertyStructure p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyStructure> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyStructure p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyStructure> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyStructure p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyStructure> findAllDESC(Pageable pageable);
}
