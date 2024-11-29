package com.project.spring.repositories;

import com.project.spring.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> getCategoriesByName(String name);
    Optional<Category> findCategoriesByNameContainingIgnoreCase(String name);

    Optional<Category> findCategoryByName(String name);
    @Query("SELECT DISTINCT c.name FROM Category c")
    List<String> findAllNameCategory();
    @Query("SELECT DISTINCT c.url FROM Category c")
    List<String> findAllURLCategory();
    @Query("SELECT DISTINCT c.url FROM Category c WHERE c.name = :name")
    String findURLCategoryByName(@Param("name") String name);
}
