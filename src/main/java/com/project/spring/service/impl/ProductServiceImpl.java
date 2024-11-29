package com.project.spring.service.impl;

import com.project.spring.Specifications.ProductSpecification;
import com.project.spring.dto.PaginationProductResponse;
import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.service.ProductService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public PaginationProductResponse getAllProduct(Pageable pageable) {
        List<Product> products = productRepository.findAll(pageable).getContent();
        return PaginationProductResponse.builder()
                .products(products)
                .numberOfItems(Long.parseLong(String.valueOf(products.size())))
                .numberOfPages(pageable.getPageNumber() + 1)
                .numberTotalPages(productRepository.findAll(pageable).getTotalPages())
                .build();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product addOrUpdate(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findByProductName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public void deleteProductById(Long id) {
        boolean exist = productRepository.existsById(id);
        if (exist) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public List<Product> getAllProductByCategory(Long id) {
        return this.productRepository.findProductByCategory_Id(id);
    }

    @Override
    public List<Product> getAllProductByManufacture(Long id) {
        return this.productRepository.findAllProductByManufacture(id);
    }

    //    Filter
    @Override
    public PaginationProductResponse filterProducts(List<Double> price, String color, Category category, Set<Manufacture> manufactureSet, Pageable pageable, String[] colors) {
        Specification<Product> spec = Specification.where(null);
        if (category != null) {
            spec = spec.and(ProductSpecification.hasCategory(category));
        }
        if (manufactureSet != null && manufactureSet.size() >= 1) {
            spec = spec.and(ProductSpecification.hasManufactureSet(manufactureSet));
        }
        if (price.size() == 2) {
            spec = spec.and(ProductSpecification.priceInRange(price.get(0), price.get(1)));
        }
        if (colors != null) {
            spec = spec.and(ProductSpecification.hasColor(colors));
        }
        List<Product> products = productRepository.findAll(spec, pageable).getContent();
        return PaginationProductResponse.builder()
                .products(products)
                .numberOfItems(Long.parseLong(String.valueOf(products.size())))
                .numberOfPages(pageable.getPageNumber() + 1)
                .numberTotalPages((int) Math.ceil((double) productRepository.findAll(spec, pageable).getTotalElements() / pageable.getPageSize()))
                .build();
    }

    //    Search
    @Override
    public PaginationProductResponse searchProducts(String keyword, @Nullable Pageable pageable) {
        int getSizeProducts = this.productRepository.searchProducts(keyword, null).size();
        List<Product> products = this.productRepository.searchProducts(keyword, pageable);
        return PaginationProductResponse.builder()
                .numberOfItems(Long.parseLong(String.valueOf(products.size())))
                .numberOfPages(pageable.getPageNumber() + 1)
                .products(products)
                .numberTotalPages((int) Math.ceil((double) getSizeProducts / pageable.getPageSize()))
                .build();
//        return this.productRepository.searchProducts(keyword);
    }

    /*Count comments of Products by Id */
    @Override
    public Integer countCommentProduct(Long id) {
        return this.productRepository.findCommentCountByProduct_comment(id);
    }

    @Override
    public BigDecimal rating(Long id) {
        List<Integer[]> integer = this.productRepository.findStarByProduct(id);
        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (Integer[] o : integer) {
            BigDecimal rating = BigDecimal.valueOf(o[0]);
            BigDecimal occurrences = BigDecimal.valueOf(o[1]);
            sum = sum.add(rating.multiply(occurrences));
            count += o[1];
        }
        try {
            return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }

    /* update view */
    @Override
    public void incrementViewCount(Long id) {
        Product product = this.productRepository.findProductById(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() + 1);
            this.productRepository.save(product);
        }
    }

    @Override
    public void deleteAll() {
        productRepository.deleteAll();
    }

    @Override
    public List<Product> findProductByPriceBetween(Double minPrice, Double maxPrice) {
        return productRepository.findProductByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public List<Product> findProductByPrice(Double price) {
        return productRepository.findProductByPrice(price);
    }

    @Override
    public List<Product> findProductByColor(String color) {
        return productRepository.findProductByColor(color);
    }

    @Override
    public List<Product> findProductByDescriptionContaining(String description) {
        return productRepository.findProductByDescriptionContaining(description);
    }

    @Override
    public List<Product> findProductByInformationContaining(String information) {
        return productRepository.findProductByInformationContaining(information);
    }

    @Override
    public List<Product> findProductBySize(int size) {
        return productRepository.findProductBySize(size);
    }

    @Override
    public List<Product> findProductByViewCount(Long viewCount) {
        return productRepository.findProductByViewCount(viewCount);
    }

    @Override
    public List<Product> findProductByViewCountBetween(Long minViewCount, Long maxViewCount) {
        return productRepository.findProductByViewCountBetween(minViewCount, maxViewCount);
    }

    @Override
    public List<Product> findProductByNameContaining(String name) {
        return productRepository.findProductByNameContaining(name);
    }

	/*
	 * @Override public List<Product> findProductByCategoryNameContaining(String
	 * categoryName) { return
	 * productRepository.findProductByCategoryNameContaining(categoryName); }
	 */
    // Page
    @Override
    public Page<Product> pageFindAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> pageFindProductById(Long id, Pageable pageable) {
        return productRepository.findProductById(id, pageable);
    }

    @Override
    public Page<Product> pageFindProductByNameContaining(String name, Pageable pageable) {
        return productRepository.findProductByNameContaining(name, pageable);
    }

    @Override
    public Page<Product> pageFindProductByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findProductByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> pageFindProductByPrice(Double price, Pageable pageable) {
        return productRepository.findProductByPrice(price, pageable);
    }

    @Override
    public Page<Product> pageFindProductByColor(String color, Pageable pageable) {
        return productRepository.findProductByColor(color, pageable);
    }

    @Override
    public Page<Product> pageFindProductByDescriptionContaining(String description, Pageable pageable) {
        return productRepository.findProductByDescriptionContaining(description, pageable);
    }

    @Override
    public Page<Product> pageFindProductByInformationContaining(String information, Pageable pageable) {
        return productRepository.findProductByInformationContaining(information, pageable);
    }

    @Override
    public Page<Product> pageFindProductBySize(int size, Pageable pageable) {
        return productRepository.findProductBySize(size, pageable);
    }

    @Override
    public Page<Product> pageFindProductByViewCountBetween(Long minViewCount, Long maxViewCount, Pageable pageable) {
        return productRepository.findProductByViewCountBetween(minViewCount, maxViewCount, pageable);
    }

    @Override
    public Page<Product> pageFindProductByViewCount(Long viewCount, Pageable pageable) {
        return productRepository.findProductByViewCount(viewCount, pageable);
    }

    @Override
    public Page<Product> pageFindProductByCategoryNameContaining(String categoryName, Pageable pageable) {
        return productRepository.findProductByCategoryNameContaining(categoryName, pageable);
    }

    @Override
    public Page<Product> pageFindProductByManufacture(Set<Manufacture> manufactures, Pageable pageable) {
        return productRepository.findAllByManufactureIn(manufactures, pageable);
    }

    @Override
    public Page<Product> pageFindProductByIdIn(Collection<Long> ids, Pageable pageable) {
        return productRepository.findProductByIdIn(ids, pageable);
    }
    @Override
    public List<Product> findProductByCategoryNameContaining(String categoryName) {
        return productRepository.findProductByCategoryNameContaining(categoryName);
    }
}
//Store

// Specification
//        Specification<Product> specification = ProductSpecification.priceLessThan(price);
//        specification.and(ProductSpecification.categoryLike(category));
//        return productRepository.findAll(specification);
//        Pageable pageable;
//        Page<Product> products = productRepository.findAllByNameContains("123",pageable).and()


//    @Override
//    public List<Product> getAllProduct(Integer pageNo, Integer pageSize, String sortBy, String order) {
//        Sort sort = Sort.by(sortBy);
//        if (order.equals("desc")) {
//            sort = sort.descending();
//        } else {
//            sort = sort.ascending();
//        }
//        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
//        Page<Product> products = productRepository.findAll(pageable);
//        if (products.hasContent()) {
//            return products.getContent();
//        }
//        return new ArrayList<Product>();
//    }

// get all products
//        Sort sort = Sort.by(sortBy);
//        if (order.equals("desc")) {
//            sort = sort.descending();
//        } else {
//            sort = sort.ascending();
//        }
//        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
//        Page<Product> products = productRepository.findAll(pageable);
//        List<Product> rs = new ArrayList<Product>();
//        if (products.hasContent()) {
//            List<Product> products1 = products.getContent();
//            for (Product product : products1) {
//                if (product.getCategory().getId().equals(id)) {
//                    rs.add(product);
//                }
//            }
//        }