package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyVideo;

public interface PropertyVideoRepository extends JpaRepository<PropertyVideo, Long> {
	@Query("SELECT p FROM PropertyVideo p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyVideo> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyVideo p WHERE p.property.id =:propertyId AND p.isActive=true AND p.isDeleted=false")
	public List<PropertyVideo> listByProperty(@Param("propertyId") long propertyId);

	@Query("SELECT p FROM PropertyVideo p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyVideo> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyVideo p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyVideo> findAllDESC(Pageable pageable);

	@Query("SELECT p FROM PropertyVideo p WHERE p.property.id =:propertyId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyVideo> findAllByPropertyASC(@Param("propertyId") long propertyId, Pageable pageable);

	@Query("SELECT p FROM PropertyVideo p WHERE p.property.id =:propertyId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyVideo> findAllByPropertyDESC(@Param("propertyId") long propertyId, Pageable pageable);
}
