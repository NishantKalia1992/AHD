package com.all.homedesire.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.all.homedesire.entities.ContactUs;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {
	@Query("SELECT c FROM ContactUs c WHERE c.id =:id AND c.isActive=true AND c.isDeleted=false")
	public Optional<ContactUs> findByObjectId(@Param("id") long id);

	@Query("SELECT c FROM ContactUs c WHERE c.id =:id AND c.isDeleted=false")
	public Optional<ContactUs> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT c FROM ContactUs c WHERE c.name =:name AND c.isActive=true AND c.isDeleted=false")
	public ContactUs findByName(@Param("name") String name);

	@Query("SELECT c FROM ContactUs c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn ASC")
	public Page<ContactUs> findAllASC(Pageable pageable);

	@Query("SELECT c FROM ContactUs c WHERE c.isActive =true AND c.isDeleted =false Order By c.updatedOn DESC")
	public Page<ContactUs> findAllDESC(Pageable pageable);

}
