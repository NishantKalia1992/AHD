package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.all.homedesire.entities.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

	@Query("SELECT p FROM Property p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false AND p.isPublished=true")
	public List<Property> listByObjectId(@Param("id") long id);
	
	@Query("SELECT p FROM Property p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<Property> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM Property p WHERE p.isActive=true AND p.isDeleted=false AND p.isPublished=true ORDER BY p.updatedOn ASC")
	public Page<Property> findAllASC(Pageable pageable);

	@Query("SELECT p FROM Property p WHERE p.isActive=true AND p.isDeleted=false AND p.isPublished=true ORDER BY p.updatedOn DESC")
	public Page<Property> findAllDESC(Pageable pageable);

	@Query("SELECT p FROM Property p WHERE p.lead.id =:leadId AND p.isActive=true AND p.isDeleted=false AND p.isPublished=true")
	public Optional<Property> findByLeadId(@Param("leadId") long leadId);

	@Query("SELECT p FROM Property p WHERE p.lead.customer.id =:userId AND p.isActive=true AND p.isDeleted=false AND p.isPublished=true")
	public List<Property> findAllByUser(@Param("userId") long userId);
	
	@Query("SELECT p FROM Property p WHERE p.propertyStatus.id = :statusId AND p.isActive = true AND p.isDeleted = false")
	public List<Property> findByStatusId(@Param("statusId") long statusId);
	
	@Query("SELECT COUNT(p) FROM Property p WHERE p.lead.customer.id = ?1 AND p.isActive = true AND p.isDeleted = false")
	long countByCustomerId(Long customerId);
	
	@Query("SELECT p FROM Property p WHERE p.lead.partner.id = ?1 AND p.isActive = true AND p.isDeleted = false")
	List<Property> findSharedPropertiesByPartner(long partnerId);

}
