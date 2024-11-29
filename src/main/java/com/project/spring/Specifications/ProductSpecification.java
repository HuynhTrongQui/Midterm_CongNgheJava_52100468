package com.project.spring.Specifications;

import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductSpecification {
    public static Specification<Product> hasPrice(Double price) {
        return (root, query, criteriaBuilder) -> {
            if (price != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
            }
            return null;
        };
    }

    public static Specification<Product> priceInRange(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, criteriaBuilder) -> {
            if (color != null) {
                return criteriaBuilder.equal(root.get("color"), color);
            }
            return null;
        };
    }

    public static Specification<Product> hasCategory(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category != null) {
                return criteriaBuilder.equal(root.get("category").get("id"), category.getId());
            }
            return null;
        };
    }

    public static Specification<Product> hasManufactureSet(Set<Manufacture> manufactureSet) {
        return (root, query, criteriaBuilder) -> {
            if (manufactureSet != null && !manufactureSet.isEmpty()) {
                return root.join("manufacture").in(manufactureSet);
            }
            return null;
        };
    }

    public static Specification<Product> hasColor(String[] colors) {
        return ((root, query, criteriaBuilder) -> root.get("color").in((Object[]) colors));
    }
}
