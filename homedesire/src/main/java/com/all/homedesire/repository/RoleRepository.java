package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Role;
import com.all.homedesire.enums.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Query("SELECT r FROM Role r WHERE r.id =:id AND r.isActive=true AND r.isDeleted=false")
	public Optional<Role> findByObjectId(@Param("id") long id);
	
	@Query("SELECT r FROM Role r WHERE r.id =:id AND r.isDeleted=false")
	public Optional<Role> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT r FROM Role r WHERE r.name =:name AND r.isActive=true AND r.isDeleted=false")
	public Optional<Role> findByName(@Param("name") ERole name);

	@Query("SELECT r FROM Role r WHERE r.isActive =true AND r.isDeleted =false Order By r.updatedOn ASC")
	public Page<Role> findAllASC(Pageable pageable);

	@Query("SELECT r FROM Role r WHERE r.isActive =true AND r.isDeleted =false Order By r.updatedOn DESC")
	public Page<Role> findAllDESC(Pageable pageable);
}
