package com.xoraus.cyberscribex.repository;

import com.xoraus.cyberscribex.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.deleted = false")
    List<Comment> findByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.deleted = false")
    Optional<Comment> findActiveById(@Param("id") Long id);
}
