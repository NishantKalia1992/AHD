package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
	@Query("SELECT p FROM Promotion p WHERE p.id =:id AND p.isActive=true AND p.isDeleted=false")
	public Optional<Promotion> findByObjectId(@Param("id") long id);
}
