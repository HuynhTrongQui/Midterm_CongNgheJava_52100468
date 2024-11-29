package com.project.spring.service;

import com.project.spring.model.Category;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface CategoryService {

    void addOrUpdate(Category category);

    void deleteAllCategory();

    List<Category> findAllCategory();

    Optional<Category> findCategoryByName(String name);

    List<String> findAllNameCategory();

    List<String> findAllURLCategory();

    String findURLCategoryByName(@Param("name") String name);

}