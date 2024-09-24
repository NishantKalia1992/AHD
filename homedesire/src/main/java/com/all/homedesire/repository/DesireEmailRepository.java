package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.DesireEmail;

public interface DesireEmailRepository extends JpaRepository<DesireEmail, Long> {
	@Query("SELECT d FROM DesireEmail d WHERE d.id =:id AND d.isActive=true AND d.isDeleted=false")
	public Optional<DesireEmail> findByObjectId(@Param("id") long id);

	@Query("SELECT e FROM DesireEmail e WHERE e.isActive =:isActive AND e.isDeleted =:isDeleted")
	public List<DesireEmail> findAllByActiveAndDeleted(@Param("isActive") boolean isActive,
			@Param("isDeleted") boolean isDeleted);
}
