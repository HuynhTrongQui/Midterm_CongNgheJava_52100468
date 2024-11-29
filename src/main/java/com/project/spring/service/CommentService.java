package com.project.spring.service;

import com.project.spring.model.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    public Comment addOrUpdate(Comment comment);

}
