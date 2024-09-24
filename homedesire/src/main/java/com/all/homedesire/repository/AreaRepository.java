package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;

public interface AreaRepository extends JpaRepository<Area, Long> {
	@Query("SELECT a FROM Area a WHERE a.id =:id AND a.isActive=true AND a.isDeleted=false")
	public Optional<Area> findByObjectId(@Param("id") long id);

	@Query("SELECT a FROM Area a WHERE a.id =:id AND a.isDeleted=false")
	public Optional<Area> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT a FROM Area a WHERE a.name =:name AND a.isActive=true AND a.isDeleted=false")
	public Area findByName(@Param("name") String areaName);

	@Query("SELECT a FROM Area a WHERE a.name =:name AND a.city.id =:cityId AND a.isActive=true AND a.isDeleted=false")
	public Optional<Area> findByNameAndCity(@Param("name") String areaName, @Param("cityId") long cityId);

	@Query("SELECT a FROM Area a WHERE a.city =:city AND a.isActive=true AND a.isDeleted=false")
	public List<Area> findByCity(@Param("city") City city);

	@Query("SELECT a FROM Area a WHERE a.name like %:name%")
	public List<Area> listByName(@Param("name") String areaName);

	@Query("SELECT a FROM Area a WHERE a.isActive =true AND a.isDeleted =false Order By a.updatedOn ASC")
	public Page<Area> findAllASC(Pageable pageable);

	@Query("SELECT a FROM Area a WHERE a.isActive =true AND a.isDeleted =false Order By a.updatedOn DESC")
	public Page<Area> findAllDESC(Pageable pageable);

	@Query("SELECT a FROM Area a WHERE a.city =:city AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn ASC")
	public Page<Area> findAllByCityASC(@Param("city") City city, Pageable pageable);

	@Query("SELECT a FROM Area a WHERE a.city =:city AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn DESC")
	public Page<Area> findAllByCityDESC(@Param("city") City city, Pageable pageable);
	@Query("SELECT a FROM Area a WHERE a.name like %:name% AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn ASC")
	public Page<Area> findAllByNameASC(@Param("name") String areaName, Pageable pageable);
	@Query("SELECT a FROM Area a WHERE a.name like %:name% AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn DESC")
	public Page<Area> findAllByNameDESC(@Param("name") String areaName, Pageable pageable);

	@Query("SELECT a FROM Area a WHERE a.name like %:name% AND a.city.id=:cityId AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn ASC")
	public Page<Area> findAllByNameASC(@Param("name") String areaName, @Param("cityId") int cityId, Pageable pageable);

	@Query("SELECT a FROM Area a WHERE a.name like %:name% AND a.city.id=:cityId AND a.isActive =true AND a.isDeleted =false Order By a.updatedOn DESC")
	public Page<Area> findAllByNameDESC(@Param("name") String areaName, @Param("cityId") int cityId, Pageable pageable);
}
