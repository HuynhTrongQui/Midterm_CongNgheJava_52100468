package com.project.spring.repositories;

import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByName(String name);

    @Query("SELECT p from Product p where p.category.id = :id")
    List<Product> findProductByCategory_Id(@Param("id") Long id);

    @Query("SELECT p from Product p inner join ProductManufacture pm on p.id = pm.product_id.id where pm.manufacture_id.id = :idMan")
    List<Product> findAllProductByManufacture(@Param("idMan") Long id);


//    @Query(value = "select p from  Product p where  (:name is null or  LOWER(p.name) like %:name%)")
//    Page<Product> filterProduct(String name, Pageable pageable);

    //    List<Product> findAllByCategoryContaining(Category category);
//    List<Product> filterProducts(@Param("price") Double price,@Param("category") Category category);
    //    Search

//    Page<Product> findAllByPriceAndCategory(Double aDouble,Category category,Pageable pageable);

//    Page<Product> findAllByManufactureInAndCategoryAndColorAndPrice(Set<Manufacture> manufacture,Category category,String color,Double aDouble,Pageable pageable);



    //    PaginationProductResponse filterProducts(Double price, String color, Category category, Set<Manufacture> manufactureSet, Pageable pageable);
    @Query(value = "select p from Product p " +
            "join ProductManufacture pm on  p.id = pm.product_id.id " +
            "join Manufacture m on m.id = pm.manufacture_id.id " +
            "where (p.name like %?1% ) " +
            "or  (concat (p.price,'' ) like %?1%) " +
            "or (p.category.name like %?1% )" +
            "or m.name like %?1% "
    )
//    @Query(value = "select  p from Product  p " +
//            "where (p.name like %?1% ) " +
//            "or  (concat (p.price,'' ) like %?1%) " +
//            "or (p.category.name like %?1% )" )
    List<Product> searchProducts(@Param("keyword") String keyword, @Nullable Pageable pageable);

    /* Numbers comment of Products */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.product_comment.id = :id")
    Integer findCommentCountByProduct_comment(@Param("id") Long id);

    /*Number star by Product and star */
    @Query("SELECT c.rating,COUNT(c.rating) from Comment c WHERE c.product_comment.id = :id group by c.rating")
    List<Integer[]> findStarByProduct(@Param("id") Long id);

    /* get product by id*/
    Product findProductById(Long id);

    @Query("SELECT p from Product p INNER JOIN " +
            "OrderDetail o ON p.id = o.product.id " +
            "GROUP BY p.id " +
            "ORDER BY SUM(o.quantity) DESC " +
            "LIMIT 8")
    List<Product> getMostPurchasedProduct();

    /* top 8 product most view by category/*/
    @Query("select p from  Product p where p.category.id = :idCategory order by  p.viewCount limit 8 ")
    List<Product> getTop8Product(@Param("idCategory") Long idCategory);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    /*Quang*/
    List<Product> findProductByNameContaining(String name);

    List<Product> findByCategory(Category category);

    List<Product> findByPriceLessThan(Double maxPrice);

    List<Product> findProductByPrice(Double price);

    List<Product> findProductByPriceBetween(Double minPrice, Double maxPrice);

    List<Product> findProductByColor(String color);

    List<Product> findProductByDescriptionContaining(String description);

    List<Product> findProductByInformationContaining(String information);

    List<Product> findProductBySize(int size);

    List<Product> findProductByViewCount(Long viewCount);

    List<Product> findProductByViewCountBetween(Long minViewCount, Long maxViewCount);

    List<Product> findProductByCategoryNameContaining(String categoryName);

    // Page

    Page<Product> findAllByManufactureIn(Set<Manufacture> manufactures, Pageable pageable);

    Page<Product> findProductByCategory(Category category, Pageable pageable);

    Page<Product> findProductByName(String name, Pageable pageable);

    Page<Product> findProductById(Long id, Pageable pageable);

    Page<Product> findProductByNameContaining(String name, Pageable pageable);

    Page<Product> findProductByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findProductByPrice(Double aDouble, Pageable pageable);

    Page<Product> findProductByColor(String color, Pageable pageable);

    Page<Product> findProductByDescriptionContaining(String description, Pageable pageable);

    Page<Product> findProductByInformationContaining(String information, Pageable pageable);

    Page<Product> findProductBySize(int size, Pageable pageable);

    Page<Product> findProductByViewCountBetween(Long minViewCount, Long maxViewCount, Pageable pageable);

    Page<Product> findProductByViewCount(Long viewCount, Pageable pageable);

    Page<Product> findProductByCategoryNameContaining(String categoryName, Pageable pageable);

    Page<Product> findProductByIdIn(Collection<Long> ids, Pageable pageable);


}