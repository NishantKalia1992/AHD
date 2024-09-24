package com.all.homedesire.repository;

import com.all.homedesire.entities.Partner;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
	@Query("SELECT p FROM Partner p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<Partner> findByObjectId(@Param("id") long id);
	@Query("SELECT p FROM Partner p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn ASC")
	public Page<Partner> findAllASC(Pageable pageable);
	@Query("SELECT p FROM Partner p WHERE p.isActive =true AND p.isDeleted =false Order By p.updatedOn DESC")
	public Page<Partner> findAllDESC(Pageable pageable);
}
