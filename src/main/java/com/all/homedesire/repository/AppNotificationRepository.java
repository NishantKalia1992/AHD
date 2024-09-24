package com.all.homedesire.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.all.homedesire.entities.AppNotification;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

	@Query("SELECT an FROM AppNotification an WHERE an.user.id=:userId and an.isActive =true AND an.isDeleted =false AND an.isRead =false")
	List<AppNotification> findAllUnreadByUser(long userId);
	
	@SuppressWarnings("rawtypes")
	@Query("SELECT an FROM AppNotification an WHERE an.isActive =true AND an.isDeleted =false Order By an.isRead DESC, an.updatedOn ASC")
	Page findAllASC(Pageable pageable);

	@SuppressWarnings("rawtypes")
	@Query("SELECT an FROM AppNotification an WHERE an.isActive =true AND an.isDeleted =false Order By an.isRead DESC, an.updatedOn DESC")
	Page findAllDESC(Pageable pageable);

	@SuppressWarnings("rawtypes")
	@Query("SELECT an FROM AppNotification an WHERE an.user.id=:userId and an.isActive =true AND an.isDeleted =false Order By an.isRead DESC, an.updatedOn ASC")
	Page findAllByUserASC(long userId, Pageable pageable);

	@SuppressWarnings("rawtypes")
	@Query("SELECT an FROM AppNotification an WHERE an.user.id=:userId and an.isActive =true AND an.isDeleted =false Order By an.isRead DESC, an.updatedOn DESC")
	Page findAllByUserDESC(long userId, Pageable pageable);

}
