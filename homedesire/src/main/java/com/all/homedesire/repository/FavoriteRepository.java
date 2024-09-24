package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	@Query("SELECT w FROM Favorite w WHERE w.id=:id and w.isActive =true AND w.isDeleted =false")
	public Optional<Favorite> findObjectById(@Param("id") long id);
	
	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.id=:id and w.isActive =true AND w.isDeleted =false")
	public Optional<Favorite> findObjectByUserAndId(@Param("user") User user, @Param("id") long id);
	
	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.lead.id=:leadId and w.isActive =true AND w.isDeleted =false")
	public Optional<Favorite> findObjectByUserAndLead(@Param("user") User user, @Param("leadId") long leadId);
	
	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.lead=:lead and w.isActive =true AND w.isDeleted =false")
	public Optional<Favorite> findObjectByUserAndLead(@Param("user") User user, @Param("lead") Lead lead);
	
	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.isActive =true AND w.isDeleted =false Order By w.updatedOn ASC")
	public Page<Favorite> findAllASC(@Param("user") User user, Pageable pageable);

	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.isActive =true AND w.isDeleted =false Order By w.updatedOn DESC")
	public Page<Favorite> findAllDESC(@Param("user") User user, Pageable pageable);

	@Query("SELECT w FROM Favorite w WHERE w.user=:user and w.isActive =true AND w.isDeleted =false")
	public List<Favorite> findAllByUser(@Param("user") User user);
}
