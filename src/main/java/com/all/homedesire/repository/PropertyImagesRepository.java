package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.PropertyImage;

public interface PropertyImagesRepository extends JpaRepository<PropertyImage, Long> {
	@Query("SELECT p FROM PropertyImage p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<PropertyImage> findByObjectId(@Param("id") long id);

	@Query("SELECT p FROM PropertyImage p WHERE p.lead.id =:leadId AND p.isActive=true AND p.isDeleted=false")
	public List<PropertyImage> listByLead(@Param("leadId") long leadId);

	@Query("SELECT p FROM PropertyImage p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyImage> findAllASC(Pageable pageable);

	@Query("SELECT p FROM PropertyImage p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyImage> findAllDESC(Pageable pageable);

	@Query("SELECT p FROM PropertyImage p WHERE p.lead.id =:leadId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<PropertyImage> findAllByLeadASC(@Param("leadId") long leadId, Pageable pageable);

	@Query("SELECT p FROM PropertyImage p WHERE p.lead.id =:leadId AND p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<PropertyImage> findAllByLeadDESC(@Param("leadId") long leadId, Pageable pageable);
}
