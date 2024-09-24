package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.HomeService;

public interface ServiceRepository extends JpaRepository<HomeService, Long> {
	@Query("SELECT s FROM HomeService s WHERE s.id =:id AND s.isActive=true AND s.isDeleted=false")
	public Optional<HomeService> findByObjectId(@Param("id") long id);
	
	@Query("SELECT s FROM HomeService s WHERE s.id =:id AND s.isDeleted=false")
	public Optional<HomeService> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT s FROM HomeService s WHERE s.name =:name AND s.isActive=true AND s.isDeleted=false")
	public Optional<HomeService> findByName(@Param("name") String serviceName);

	@Query("SELECT s FROM HomeService s WHERE s.isActive =true AND s.isDeleted =false Order By s.updatedOn ASC")
	public Page<HomeService> findAllASC(Pageable pageable);
	
	@Query("SELECT s FROM HomeService s WHERE s.isActive =true AND s.isDeleted =false Order By s.updatedOn DESC")
	public Page<HomeService> findAllDESC(Pageable pageable);
}
