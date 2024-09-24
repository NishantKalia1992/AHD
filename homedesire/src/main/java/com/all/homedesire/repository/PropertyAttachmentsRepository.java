package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.all.homedesire.entities.PropertyAttachment;

public interface PropertyAttachmentsRepository extends JpaRepository<PropertyAttachment, Long> {
	@Query("SELECT p FROM PropertyAttachment p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyAttachment> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyAttachment p WHERE p.property.id =:propertyId AND p.isActive=true AND p.isDeleted=false")
	public List<PropertyAttachment> listByProperty(@Param("propertyId") long propertyId);

	@Query("SELECT p FROM PropertyAttachment p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyAttachment> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyAttachment p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyAttachment> findAllDESC(Pageable pageable);

	@Query("SELECT p FROM PropertyAttachment p WHERE p.property.id =:propertyId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyAttachment> findAllByPropertyASC(@Param("propertyId") long propertyId, Pageable pageable);

	@Query("SELECT p FROM PropertyAttachment p WHERE p.property.id =:propertyId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyAttachment> findAllByPropertyDESC(@Param("propertyId") long propertyId, Pageable pageable);
}
