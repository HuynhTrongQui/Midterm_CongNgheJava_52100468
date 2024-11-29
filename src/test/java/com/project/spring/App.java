package com.project.spring;

import com.project.spring.Specifications.ProductSpecification;
import com.project.spring.dto.PaginationProductResponse;
import com.project.spring.model.*;
import com.project.spring.repositories.*;
import com.project.spring.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class App {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ManufactureRepository manufactureRepository;

    //    @Test
    void test() {
//        List<Product> products = (List<Product>) productRepository.findAll();
//        products.forEach(System.out::println);
//
//        Product product = new Product();
//        product.setName("Apple");
//        product.setColor("Blue");
//        Category category = new Category(1L, "Mobile", "Điện thoại");
    }

    //    @Test
    void testImage() {
//        Optional<Product> product = this.productRepository.findById(10L);
//        if (product.isPresent()) {
//            ProductImage image1 = new ProductImage();
//            image1.setUrl("3");
//            ProductImage image2 = new ProductImage();
//            image2.setUrl("5");
//            Set<ProductImage> images = new HashSet<ProductImage>();
//            images.add(image1);
//            images.add(image2);
//            product.get().setImages(images);
//            image1.setProduct_image(product.get());
//            image2.setProduct_image(product.get());
//            this.productRepository.save(product.get());
//        }
    }
//    @Test
//    void testManProduct(){
//        List<Product> products = productRepository.findProductByManufacture(1L);
//        products.forEach(System.out::println);
//    }

    @Test
    void findAllByCateCategory() {
//        Optional<Category> category = this.categoryRepository.findById(3L);
//        if (category.isPresent()) {
//            List<Product> products = this.productRepository.findAllByCategoryContaining(category.get());
//        }
//        String keyword ="20000";
//        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"id");
//        Pageable pageable = PageRequest.of(0,1,Sort.by(order));
//        Page<Product> products = productRepository.findAllByNameContains(keyword,pageable);
//        try {
//            Double aDouble = Double.parseDouble(keyword);
//            products.and(productRepository.findAllByPriceContains(aDouble,pageable));
//        }
//        catch (Exception aDouble){
//        }
//        products.forEach(System.out::println);
    }

    @Autowired
    ProductService productService;

    @Test
//    done
    void testFilter() {
        Pageable pageable = PageRequest.of(0,8 , Sort.by("price").ascending());
//        Optional<Category> category = this.categoryRepository.findById(1L);
        Optional<Manufacture> manufacture1 = this.manufactureRepository.findManufactureByNameContainsIgnoreCase("apple");
//        Optional<Manufacture> manufacture2 = this.manufactureRepository.findById(2L);
        Set<Manufacture> manufactureSet = new HashSet<Manufacture>();
        manufacture1.ifPresent(manufactureSet::add);
//        manufacture2.ifPresent(manufactureSet::add);
//        if (category.isPresent()) {
//            PaginationProductResponse products = productService.filterProducts("", null, null,
//                    null, manufactureSet, pageable);
//            products.getProducts().forEach(System.out::println);
        }

    @Test
    void testSearch() {
        String keyword = "Apple";
        Pageable pageable = PageRequest.of(1, 2, Sort.by("id").descending());
        PaginationProductResponse paginationProductResponse = this.productService.searchProducts(keyword, pageable);
        System.out.println(paginationProductResponse);
    }

    @Test
    void test1(){
        List<Manufacture> manufacture = this.manufactureRepository.findManufactureById(1L);
        Optional<Manufacture> list = this.manufactureRepository.findManufactureByNameContainsIgnoreCase("Apple");
        list.ifPresent(System.out::println);
    }

    @Test
    void testFilter2(){
        Optional<Category> category = this.categoryRepository.findById(1L);
        Pageable pageable = PageRequest.of(0,8 , Sort.by("price").ascending());
        Optional<Manufacture> manufacture1 = this.manufactureRepository.findManufactureByNameContainsIgnoreCase("apple");
        Optional<Manufacture> manufacture2 = this.manufactureRepository.findById(2L);
        Set<Manufacture> manufactureSet = new HashSet<Manufacture>();
        manufacture1.ifPresent(manufactureSet::add);
        Page<Product>products = productRepository.findProductByName(" ",pageable);
        products.and(this.productRepository.findAllByManufactureIn(manufactureSet,pageable));
        products.and(this.productRepository.findProductByCategory(category.get(),pageable));
    }

    @Test
    void testFilter3(){
        Optional<Category> category = this.categoryRepository.findById(1L);
        Pageable pageable = PageRequest.of(0,8 , Sort.by("price").ascending());

        Optional<Manufacture> manufacture1 = this.manufactureRepository.findManufactureByNameContainsIgnoreCase("apple");
        Optional<Manufacture> manufacture2 = this.manufactureRepository.findById(2L);
        Set<Manufacture> manufactureSet = new HashSet<Manufacture>();
        manufacture1.ifPresent(manufactureSet::add);
//        manufacture2.ifPresent(manufactureSet::add);

        Specification<Product> spec = Specification.where(null);
        spec = spec.and(ProductSpecification.hasPrice(0.0));
//        spec = spec.and(ProductSpecification.hasCategory(category.get()));
//         spec = spec.and(ProductSpecification.hasManufactureSet(manufactureSet));
        List<Product>products = productRepository.findAll(spec);
        products.forEach(System.out::println);
    }

    @Autowired
    CommentRepository commentRepository;
    @Test
    void testComment(){
        Optional<Product> product = this.productService.getProductById(2L);
        Integer numberOfComment = this.productRepository.findCommentCountByProduct_comment(product.get().getId());
        System.out.println(numberOfComment);
    }

    @Test
    void testRating(){
        List<Integer[]> integer = this.productRepository.findStarByProduct(2L);

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (Integer[] o : integer) {
            BigDecimal rating = BigDecimal.valueOf(o[0]);
            BigDecimal occurrences = BigDecimal.valueOf(o[1]);

            sum = sum.add(rating.multiply(occurrences));
            count += o[1];
        }
        BigDecimal averageRating = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        System.out.println(averageRating);
    }

    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Test
    void checkCart(){
        List<Cart> carts =  this.cartRepository.findByUserId(1L);
        Map<Product,List<CartItem>> cartItems = this.cartItemRepository.findByCartIn(carts).stream().collect(Collectors.groupingBy(CartItem::getProduct));
        Map<Product, Integer> productSizes = cartItems.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Product
                        entry -> entry.getValue().size() // List<CartItem> size
                ));
    }


    @Test
    public void testAddProduct(){
//        Product expectedProduct = new Product();
//        expectedProduct.setName("test");
//        Long id = this.productRepository.save(expectedProduct).getId();
        System.out.println(123);
    }
}
