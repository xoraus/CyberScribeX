package com.xoraus.cyberscribex.repository;

import com.xoraus.cyberscribex.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
