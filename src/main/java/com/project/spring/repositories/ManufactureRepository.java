package com.project.spring.repositories;

import com.project.spring.model.Manufacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufactureRepository extends JpaRepository<Manufacture, Long> {
    List<Manufacture> findManufactureById(Long id);

    Optional<Manufacture> findManufactureByNameContainsIgnoreCase(String name);

    @Query("SELECT m FROM Manufacture m " +
            "JOIN ProductManufacture p ON m.id = p.manufacture_id.id " +
            "JOIN Product pr ON p.product_id.id = pr.id " +
            "WHERE pr.category.id = :idCategory")
    List<Manufacture> findByCategory(@Param("idCategory") Long idCategory);
}
