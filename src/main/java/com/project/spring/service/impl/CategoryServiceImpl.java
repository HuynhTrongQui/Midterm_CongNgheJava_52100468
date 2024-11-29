package com.project.spring.service.impl;
import com.project.spring.model.Category;
import com.project.spring.repositories.CategoryRepository;
import com.project.spring.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public void addOrUpdate(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void deleteAllCategory() {
        categoryRepository.deleteAll();
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findCategoryByName(name);
    }

    @Override
    public List<String> findAllNameCategory() {
        return categoryRepository.findAllNameCategory();
    }

    @Override
    public List<String> findAllURLCategory() {
        return categoryRepository.findAllURLCategory();
    }

    @Override
    public String findURLCategoryByName(String name) {
        return categoryRepository.findURLCategoryByName(name);
    }
}