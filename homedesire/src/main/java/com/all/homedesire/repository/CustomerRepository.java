package com.all.homedesire.repository;

import com.all.homedesire.entities.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	@Query("SELECT c FROM Customer c WHERE c.id =:id AND c.isActive=true AND c.isDeleted=false")
	public Optional<Customer> findByObjectId(@Param("id") long id);

	@Query("SELECT c FROM Customer c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn ASC")
	public Page<Customer> findAllASC(Pageable pageable);

	@Query("SELECT c FROM Customer c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn DESC")
	public Page<Customer> findAllDESC(Pageable pageable);
}
