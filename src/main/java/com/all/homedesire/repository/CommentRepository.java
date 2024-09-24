package com.all.homedesire.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.all.homedesire.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("SELECT r FROM Comment r WHERE r.id =:id AND r.isActive=true AND r.isDeleted=false")
	public Optional<Comment> findByObjectId(@Param("id") long id);

	@Query("SELECT r FROM Comment r WHERE r.id =:id AND r.isDeleted=false")
	public Optional<Comment> findByObjectIdToActivate(@Param("id") long id);

	@Query("SELECT r FROM Comment r WHERE r.lead.id =:requestId AND r.parent IS NULL ORDER BY r.updatedOn desc")
	public List<Comment> getComments(@Param("requestId") long requestId);

	@Query("SELECT r FROM Comment r WHERE r.lead.id =:requestId AND (r.user.userType.eType !='PARTNER' OR r.user.id =:userId) "
			+ "AND r.isActive =true AND r.isDeleted =false AND r.parent IS NULL Order By r.updatedOn ASC")
	public Page<Comment> findAllUserAndRequestASC(@Param("userId") long userId, @Param("requestId") long requestId,
			Pageable pageable);

	@Query("SELECT r FROM Comment r WHERE r.lead.id =:requestId AND (r.user.userType.eType !='PARTNER' OR r.user.id =:userId) "
			+ "AND r.isActive =true AND r.isDeleted =false AND r.parent IS NULL Order By r.updatedOn DESC")
	public Page<Comment> findAllUserAndRequestDESC(@Param("userId") long userId, @Param("requestId") long requestId,
			Pageable pageable);

}
