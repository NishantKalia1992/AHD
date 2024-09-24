package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
	@Query("SELECT c FROM Country c WHERE c.id =:id AND c.isActive=true AND c.isDeleted=false")
	public Optional<Country> findByObjectId(@Param("id") long id);
	
	@Query("SELECT c FROM Country c WHERE c.id =:id AND c.isDeleted=false")
	public Optional<Country> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT c FROM Country c WHERE c.name =:name AND c.isActive=true AND c.isDeleted=false")
	public Optional<Country> findByName(@Param("name") String countryName);

	@Query("SELECT c FROM Country c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn ASC")
	public Page<Country> findAllASC(Pageable pageable);

	@Query("SELECT c FROM Country c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn DESC")
	public Page<Country> findAllDESC(Pageable pageable);

}
