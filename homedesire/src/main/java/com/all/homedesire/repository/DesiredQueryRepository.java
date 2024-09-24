package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.all.homedesire.entities.DesiredQuery;

public interface DesiredQueryRepository extends JpaRepository<DesiredQuery, Long> {
	@Query("SELECT d FROM DesiredQuery d WHERE d.id =:id AND d.isActive=true AND d.isDeleted=false")
	public Optional<DesiredQuery> findByObjectId(@Param("id") long id);

	@Query("SELECT d FROM DesiredQuery d WHERE d.id =:id AND d.isDeleted=false")
	public Optional<DesiredQuery> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT d FROM DesiredQuery d WHERE d.name =:name AND d.isActive=true AND d.isDeleted=false")
	public DesiredQuery findByName(@Param("name") String desiredQueryName);

	@Query("SELECT d FROM DesiredQuery d WHERE d.isActive =true AND d.isDeleted =false Order By d.updatedOn ASC")
	public Page<DesiredQuery> findAllASC(Pageable pageable);

	@Query("SELECT d FROM DesiredQuery d WHERE d.isActive =true AND d.isDeleted =false Order By d.updatedOn DESC")
	public Page<DesiredQuery> findAllDESC(Pageable pageable);
}
