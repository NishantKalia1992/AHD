package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.State;

public interface StateRepository extends JpaRepository<State, Long> {
	@Query("SELECT s FROM State s WHERE s.id =:id AND s.isActive=true AND s.isDeleted=false")
	public Optional<State> findByObjectId(@Param("id") long id);

	@Query("SELECT s FROM State s WHERE s.id =:id AND s.isDeleted=false")
	public Optional<State> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT s FROM State s WHERE s.name =:name AND s.isActive=true AND s.isDeleted=false")
	public State findByName(@Param("name") String stateName);

	@Query("SELECT s FROM State s WHERE s.country =:country AND s.isActive=true AND s.isDeleted=false")
	public List<State> findByCountry(@Param("country") Country country);

	@Query("SELECT s FROM State s WHERE s.country =:country AND s.name =:name AND s.isActive=true AND s.isDeleted=false")
	public Optional<State> findByCountryAndName(@Param("country") Country country, @Param("name") String name);

	@Query("SELECT s FROM State s WHERE s.name =:stateName and s.country.id =:countryId AND s.isActive=true AND s.isDeleted=false")
	public Optional<State> findByNameAndCountry(@Param("stateName") String stateName,
			@Param("countryId") long countryId);

	@Query("SELECT s FROM State s WHERE s.isActive =true AND s.isDeleted =false Order By s.updatedOn ASC")
	public Page<State> findAllASC(Pageable pageable);

	@Query("SELECT s FROM State s WHERE s.isActive =true AND s.isDeleted =false Order By s.updatedOn DESC")
	public Page<State> findAllDESC(Pageable pageable);

	@Query("SELECT s FROM State s WHERE s.country =:country AND s.isActive =true AND s.isDeleted =false Order By s.updatedOn ASC")
	public Page<State> findAllByCountryASC(@Param("country") Country country, Pageable pageable);

	@Query("SELECT s FROM State s WHERE s.country =:country AND s.isActive =true AND s.isDeleted =false Order By s.updatedOn DESC")
	public Page<State> findAllByCountryDESC(@Param("country") Country country, Pageable pageable);

}
