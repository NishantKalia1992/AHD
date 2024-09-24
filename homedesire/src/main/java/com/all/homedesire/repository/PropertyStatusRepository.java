package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyStatus;

public interface PropertyStatusRepository extends JpaRepository<PropertyStatus, Long> {
	@Query("SELECT p FROM PropertyStatus p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyStatus> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyStatus p WHERE p.name =:name AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyStatus> findByName(@Param("name") String name);

	@Query("SELECT p FROM PropertyStatus p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyStatus> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyStatus p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyStatus> findAllDESC(Pageable pageable);
	
	@Query("SELECT p FROM PropertyStatus p WHERE p.name = :name AND p.isActive = true AND p.isDeleted = false")
    public List<PropertyStatus> findAllByName(@Param("name") String name);

}
