package com.project.spring.service;

import com.project.spring.dto.PaginationProductResponse;
import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface ProductService {
    PaginationProductResponse getAllProduct(Pageable pageable);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getAllProductByCategory(Long id);

    List<Product> getAllProductByManufacture(Long id);

    Product addOrUpdate(Product product);

    List<Product> findByProductName(String name);

    List<Product> findProductByNameContaining(String name);

    void deleteProductById(Long id);

    List<Product> findProductByPriceBetween(Double minPrice, Double maxPrice);

    List<Product> findProductByPrice(Double price);

    List<Product> findProductByColor(String color);

    List<Product> findProductByDescriptionContaining(String description);

    List<Product> findProductByInformationContaining(String information);

    List<Product> findProductBySize(int size);

    List<Product> findProductByViewCount(Long viewCount);

    List<Product> findProductByViewCountBetween(Long minViewCount, Long maxViewCount);

    List<Product> findProductByCategoryNameContaining(String categoryName);

    // Page

    Page<Product> pageFindAllProduct(Pageable pageable);

    Page<Product> pageFindProductById(Long id, Pageable pageable);

    Page<Product> pageFindProductByNameContaining(String name, Pageable pageable);

    Page<Product> pageFindProductByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> pageFindProductByPrice(Double price, Pageable pageable);

    Page<Product> pageFindProductByColor(String color, Pageable pageable);

    Page<Product> pageFindProductByDescriptionContaining(String description, Pageable pageable);

    Page<Product> pageFindProductByInformationContaining(String information, Pageable pageable);

    Page<Product> pageFindProductBySize(int size, Pageable pageable);

    Page<Product> pageFindProductByViewCountBetween(Long minViewCount,
                                                    Long maxViewCount,
                                                    Pageable pageable);

    Page<Product> pageFindProductByViewCount(Long viewCount, Pageable pageable);

    Page<Product> pageFindProductByCategoryNameContaining(String categoryName, Pageable pageable);

    Page<Product> pageFindProductByManufacture(Set<Manufacture> manufactures, Pageable pageable);

    Page<Product> pageFindProductByIdIn(Collection<Long> ids, Pageable pageable);




    //    FILTER
    PaginationProductResponse filterProducts(List<Double> price, String color, Category category, Set<Manufacture> manufactureSet, Pageable pageable,String[] colors);
    //    Search
    PaginationProductResponse searchProducts(String keyword, Pageable pageable);

    /*Count comment product*/
    Integer countCommentProduct(Long id);

    /* Rating product*/
    BigDecimal rating(Long id);

    void incrementViewCount(Long id);

    void deleteAll();


}