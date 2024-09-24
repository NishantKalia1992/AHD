package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertySubType;

public interface PropertySubTypeRepository extends JpaRepository<PropertySubType, Long> {

	@Query("SELECT p FROM PropertySubType p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertySubType> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertySubType p WHERE p.name =:name AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertySubType> findByName(@Param("name") String name);
	
	@Query("SELECT p FROM PropertySubType p WHERE p.name =:name AND p.propertyType.id =:typeId AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertySubType> findByNameAndType(@Param("name") String name, @Param("typeId") long typeId);

	@Query("SELECT p FROM PropertySubType p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertySubType> findAllASC(Pageable pageable);
	
	@Query("SELECT p FROM PropertySubType p WHERE p.propertyType.id =:typeId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertySubType> findAllASC(@Param("typeId") long typeId, Pageable pageable);

	@Query("SELECT p FROM PropertySubType p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertySubType> findAllDESC(Pageable pageable);
	
	@Query("SELECT p FROM PropertySubType p WHERE p.propertyType.id =:typeId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertySubType> findAllDESC(@Param("typeId") long typeId, Pageable pageable);

	@Query("SELECT p FROM PropertySubType p WHERE p.propertyType.id =:typeId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public List<PropertySubType> findAllByType(long typeId);
}
