package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyType;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long> {
	@Query("SELECT p FROM PropertyType p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyType> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyType p WHERE p.name =:name AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyType> findByName(@Param("name") String name);

	@Query("SELECT p FROM PropertyType p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyType> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyType p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyType> findAllDESC(Pageable pageable);

}
