package com.project.spring.api;

import com.project.spring.dto.PaginationProductResponse;
import com.project.spring.dto.ProductDTO;
import com.project.spring.model.Cart;
import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import com.project.spring.repositories.CategoryRepository;
import com.project.spring.repositories.ManufactureRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.dto.ResponseObject;
import com.project.spring.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

@RestController
//@Controller
@RequestMapping("api/products")
public class ProductAPI {
    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    ManufactureRepository manufactureRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping({"", "/"})
    ResponseEntity<ResponseObject> getAllProducts() {
        List<Product> list = this.productRepository.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject("1", "Success", "List Products Empty"));
        }
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("Number Page", pageNo + 1);
//        map.put("Number Product", pageSize);
//        List<ProductDTO> products = productService.getAllProduct(pageNo, pageSize, sortBy, order).stream().map(p -> mapper.map(p, ProductDTO.class)).collect(Collectors.toList());
//        if (products.size() == 0) {
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("0", "Not found products", map));
//        }
//        map.put("products", productService.getAllProduct(pageNo, pageSize, sortBy, order));
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Thành công", map));
        List<ProductDTO> productDTOs = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", productDTOs));
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getProductById(@PathVariable("id") String idString) {
        try {
            Long id = Long.parseLong(idString);
            Optional<Product> productOptional = this.productRepository.findById(id);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("1", "Failure", "Not found product " + id));
            }
            Product product = productOptional.get();
            ProductDTO dto = mapper.map(product, ProductDTO.class);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", dto));
        } catch (NumberFormatException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Failure", "Not valid format id Product"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("0", "Failure", "Error at server"));
        }
    }

    @PostMapping
    ResponseEntity<ResponseObject> create(@Valid @RequestBody ProductDTO productRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(
                        error -> errors.put(error.getField(), error.getDefaultMessage())
                );
                String errorMsg = "";
                for (String key : errors.keySet()) {
                    errorMsg += "Lỗi ở: " + key + ", lí do: " + errors.get(key) + "\n";
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Failure", errorMsg));
            }
            Product product = this.productRepository.save(convertToEntity(productRequest));
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", convertToDTO(product)));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("0", "Error", "Product exist"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Failure", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> delete(@PathVariable("id") String idString) {
        try {
            Long id = Long.parseLong(idString);
            Product product = this.productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Product not exist to delete"));
            }
            this.productRepository.delete(product);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Xóa sản phẩm thành công", ""));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Failure", "Id not valid format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("0", "Failure", "Serve error"));
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> update(@PathVariable("id") String idString, @RequestBody ProductDTO productRequest) {
        try {
            Long id = Long.parseLong(idString);
            Product product = this.productRepository.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Product not exist to update"));
            }
            Product source = convertToEntity(productRequest);
            Product target = product;
            BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
            target.setId(id);
            Product product1 = this.productRepository.save(target);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", convertToDTO(product1)));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("0", "Failure", "Id not valid format"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("0", "Error", "Duplicate name"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject("0", "Failure", "Serve error"));
        }
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    /*
    @GetMapping("/categories/{id}")
    ResponseEntity<ResponseObject> getAllProductByCategory(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "8") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String order) {
        List<Product> product = productService.getAllProductByCategory(id);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("products", product);
//        hashMap.put("Page No", pageNo + 1);
//        hashMap.put("Page Number", pageSize);
        if (product.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Fail! Not found products", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Successfull", product));
    }

    @GetMapping("/manufacture/{id}")
    ResponseEntity<ResponseObject> getAllProductByManufacture(@PathVariable Long id) {
        List<Product> products = this.productService.getAllProductByManufacture(id);
        if (products.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure! Not found products", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", products));
    }

//    @GetMapping("/filter")
//    ResponseEntity<ResponseObject> filter(
//            @RequestParam(name = "name", defaultValue = "") Optional<String> name,
//            @RequestParam(name = "price", required = false) Double price,
//            @RequestParam(name = "color", required = false) String color,
//            @RequestParam(name = "category", required = false) Category category,
//            @RequestParam(name = "manufacture", required = false) Set<Manufacture> manufacture,
//            @RequestParam(name = "currentPage", defaultValue = "0") Integer currentPage,
//            @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize,
//            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
//            @RequestParam(name = "orderField", defaultValue = "desc") String orderField,
//            Model model
//    ) {
//        Sort.Direction direction = orderField.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
//        Sort.Order order = new Sort.Order(direction, sortBy);
//        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(order));
//        PaginationProductResponse paginationProductResponse =
//                this.productService.filterProducts(price, color, category, manufacture, pageable);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", paginationProductResponse));
//    }*/
    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = mapper.map(product, ProductDTO.class);
        if (product.getManufacture() != null) {
            productDTO.setManufacture(product.getManufacture().stream()
                    .map(Manufacture::getName)
                    .collect(Collectors.toSet()));
        }
        return productDTO;
    }

    private Product convertToEntity(ProductDTO productDTO) {
        Product product = mapper.map(productDTO, Product.class);
        if (productDTO.getName() != null) {
            Category category = this.categoryRepository.findCategoriesByNameContainingIgnoreCase(productDTO.getNameCategory()).get();
            product.setCategory(category);
        }
        if (productDTO.getManufacture() != null) {
            Set<Manufacture> manufactureSet = productDTO.getManufacture().stream().map(manufacturerName -> {
                Manufacture manufacture = this.manufactureRepository.findManufactureByNameContainsIgnoreCase(manufacturerName).get();
                return manufacture;
            }).collect(Collectors.toSet());
            product.setManufacture(manufactureSet);
        }
        return product;
    }
}

