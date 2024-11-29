package com.project.spring.repositories;

import com.project.spring.model.Comment;
import com.project.spring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment,Long> {
    /* Find List Comment of Product*/
    @Query("select c from Comment c where c.product_comment.id = :id order by c.time desc")
    List<Comment> findCommentByProductId(@Param("id") Long id);
}
