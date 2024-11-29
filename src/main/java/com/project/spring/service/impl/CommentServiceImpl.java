package com.project.spring.service.impl;

import com.project.spring.model.Comment;
import com.project.spring.repositories.CommentRepository;
import com.project.spring.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CommentServiceImpl implements CommentService{
    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment addOrUpdate(Comment comment){
        return this.commentRepository.save(comment);
    }

}
