package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.City;
import com.all.homedesire.entities.State;

public interface CityRepository extends JpaRepository<City, Long> {
	@Query("SELECT c FROM City c WHERE c.id =:id AND c.isActive=true AND c.isDeleted=false")
	public Optional<City> findByObjectId(@Param("id") long id);

	@Query("SELECT c FROM City c WHERE c.id =:id AND c.isDeleted=false")
	public Optional<City> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT c FROM City c WHERE c.name =:name AND c.isActive=true AND c.isDeleted=false")
	public Optional<City> findByName(@Param("name") String cityName);

	@Query("SELECT c FROM City c WHERE c.state =:state AND c.isActive=true AND c.isDeleted=false")
	public List<City> findByState(@Param("state") State state);

	@Query("SELECT c FROM City c WHERE c.name =:cityName and c.state.id =:stateId AND c.isActive=true AND c.isDeleted=false")
	public Optional<City> findByNameAndState(@Param("cityName") String cityName, @Param("stateId") long stateId);

	@Query("SELECT c FROM City c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn ASC")
	public Page<City> findAllASC(Pageable pageable);

	@Query("SELECT c FROM City c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn DESC")
	public Page<City> findAllDESC(Pageable pageable);

	@Query("SELECT c FROM City c WHERE c.state =:state AND c.isActive =true AND c.isDeleted =false Order By c.updatedOn ASC")
	public Page<City> findAllByStateASC(@Param("state") State state, Pageable pageable);

	@Query("SELECT c FROM City c WHERE c.state =:state AND c.isActive =true AND c.isDeleted =false Order By c.updatedOn DESC")
	public Page<City> findAllByStateDESC(@Param("state") State state, Pageable pageable);

}
