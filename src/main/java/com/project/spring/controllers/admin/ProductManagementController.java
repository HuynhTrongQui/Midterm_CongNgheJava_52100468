package com.project.spring.controllers.admin;

import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.Product;
import com.project.spring.repositories.CategoryRepository;
import com.project.spring.repositories.ManufactureRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.service.impl.CategoryServiceImpl;
import com.project.spring.service.impl.ProductServiceImpl;
import com.project.spring.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequestMapping("/admin/products")
public class ProductManagementController {

    @Autowired
    ProductServiceImpl productService;
    @Autowired
    CategoryServiceImpl categoryService;
    @Autowired
    ManufactureRepository manufactureRepository;

    @GetMapping("/list")
    public String listProducts(Model model,
                               @RequestParam(value = "resultIdSearch", defaultValue = "") String resultIdSearch,
                               @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
                               @RequestParam(name = "numberElementInOnePage", defaultValue = "5") String numberElementInOnePage,
                               @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                               @RequestParam(name = "orderField", defaultValue = "acs") String orderField) {

        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));
        model.addAttribute("ids", resultIdSearch);

        if (!resultIdSearch.isEmpty()) {
            resultIdSearch = resultIdSearch.replace('[', ' ');
            resultIdSearch = resultIdSearch.replace(", ", ",");
            String[] ids = resultIdSearch.strip().split(",");
            List<Long> ids1 = new ArrayList<Long>();
            for (String id : ids) {
                ids1.add(Long.parseLong(id));
            }
            model.addAttribute("products", productService.pageFindProductByIdIn(ids1, pageable).getContent());
            model.addAttribute("totalPages", productService.pageFindProductByIdIn(ids1, pageable).getTotalPages());

        } else {
            model.addAttribute("products", productService.pageFindAllProduct(pageable).getContent());
            model.addAttribute("totalPages", productService.pageFindAllProduct(pageable).getTotalPages());
        }


        model.addAttribute("currentPage", Integer.parseInt(currentPage));
        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "admin/products/list";
    }

    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id).orElse(null));
        return "admin/products/view";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllCategory());
        model.addAttribute("manufactures", manufactureRepository.findAll());
        return "admin/products/new";
    }

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/upload/";

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("image") MultipartFile multipartFile,
                              @RequestParam("categoryName") String categoryName,
                              @RequestParam("manufactureName") String manufactureName,
                              Model model,
                              @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
                              @RequestParam(name = "numberElementInOnePage", defaultValue = "3") String numberElementInOnePage,
                              @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                              @RequestParam(name = "orderField", defaultValue = "acs") String orderField) throws IOException {
        /*if (!multipartFile.isEmpty()) {
            try {
                String originalFileName = multipartFile.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = generateUniqueFileName(fileExtension);
                Path targetPath = Path.of(UPLOAD_DIRECTORY, uniqueFileName);
                Files.copy(imageFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                product.setOriginalPicture(uniqueFileName);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }*/


        Set<Manufacture> manufactures = new HashSet<Manufacture>();
        if (manufactureName.contains(",")) {
            for (String name : manufactureName.strip().split(",")) {
                Manufacture manufacture = this.manufactureRepository.findManufactureByNameContainsIgnoreCase(name).get();
                manufactures.add(manufacture);
            }
            product.setManufacture(manufactures);
        } else {
            Manufacture manufacture = this.manufactureRepository.findManufactureByNameContainsIgnoreCase(manufactureName).get();
            manufactures.add(manufacture);
            product.setManufacture(manufactures);
        }


        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        product.setOriginalPicture(fileName);
        product.setViewCount(0L);
        product.setCategory(categoryService.findCategoryByName(categoryName).orElse(null));

        productService.addOrUpdate(product);


        String uploadDir = "./upload/products/";
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

        model.addAttribute("products", productService.pageFindAllProduct(pageable).getContent());
        model.addAttribute("totalPages", productService.pageFindAllProduct(pageable).getTotalPages());
        model.addAttribute("currentPage", Integer.parseInt(currentPage));
        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "/admin/products/list";

    }

    private String generateUniqueFileName(String fileExtension) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = currentTime.format(formatter);
        return "upload_" + formattedDateTime + fileExtension;
    }

    @Autowired
    ProductRepository productRepository;

    @PostMapping("update")
    public String updateProduct(@RequestParam("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("price") String price,
                                @RequestParam("color") String color,
                                @RequestParam("description") String description,
                                @RequestParam("information") String information,
                                @RequestParam("image") MultipartFile multipartFile,
                                @RequestParam("size") String size,
                                @RequestParam("categoryName") String categoryName,
                                @RequestParam("manufactureName") String manufactureName,
                                Model model,
                                @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
                                @RequestParam(name = "numberElementInOnePage", defaultValue = "3") String numberElementInOnePage,
                                @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                @RequestParam(name = "orderField", defaultValue = "acs") String orderField) throws IOException {
        manufactureName = manufactureName.replace('[', ' ');
        manufactureName = manufactureName.replace(']', ' ');
        manufactureName = manufactureName.replace(", ", ",");
        Set<Manufacture> manufactures = new HashSet<Manufacture>();
        for (String manufacture : manufactureName.strip().split(",")) {
            Manufacture manufacture1 = manufactureRepository.findManufactureByNameContainsIgnoreCase(manufacture).get();
            manufactures.add(manufacture1);
        }
        Product product = this.productRepository.findById(Long.parseLong(id)).get();
        /*product.setId(Long.parseLong(id));*/
        product.setName(name);
        product.setPrice(Double.parseDouble(price));
        product.setColor(color);
        product.setDescription(description);
        product.setInformation(information);
        product.setSize(Integer.parseInt(size));
        product.setCategory(categoryService.findCategoryByName(categoryName).orElse(null));
        product.setManufacture(manufactures);

        /* upload image */
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            product.setOriginalPicture(fileName);
            String uploadDir = "./upload/products/";
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        productService.addOrUpdate(product);

        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

        model.addAttribute("products", productService.pageFindAllProduct(pageable).getContent());
        model.addAttribute("totalPages", productService.pageFindAllProduct(pageable).getTotalPages());
        model.addAttribute("currentPage", Integer.parseInt(currentPage));
        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "/admin/products/list";

    }

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {

        model.addAttribute("product", productService.getProductById(id).orElse(null));
        /*Set<Category> categories = new HashSet<Category>();
        for (String name : categoryService.findAllNameCategory()) {
            categories.add(new Category(name, categoryService.findURLCategoryByName(name)));
        }*/
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("manufacturesName", manufactureRepository.findAll());

        return "admin/products/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                Model model,
                                @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
                                @RequestParam(name = "numberElementInOnePage", defaultValue = "3") String numberElementInOnePage,
                                @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                @RequestParam(name = "orderField", defaultValue = "acs") String orderField) {

        if (productService.getProductById(id).orElse(null).getOrderDetails().isEmpty()) {
            productService.deleteProductById(id);
        } else {
            model.addAttribute("errorMessage", "Can't delete product because this product have order");
        }

//        productService.deleteProductById(id);
        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);
        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

        model.addAttribute("products", productService.pageFindAllProduct(pageable).getContent());
        model.addAttribute("totalPages", productService.pageFindAllProduct(pageable).getTotalPages());
        model.addAttribute("currentPage", Integer.parseInt(currentPage));
        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "admin/products/list";
    }

    @PostMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword,
                                 Model model,
                                 @RequestParam(name = "currentPage", defaultValue = "0") String currentPage,
                                 @RequestParam(name = "numberElementInOnePage", defaultValue = "3") String numberElementInOnePage,
                                 @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                 @RequestParam(name = "orderField", defaultValue = "acs") String orderField) {

        if (keyword.contains("=")) {
            String key = keyword.replace(" ", "").strip().split("=")[0].strip();
            String value = keyword.strip().split("=")[1].strip();

            // id = ...
            if (key.equalsIgnoreCase("id")) {
                try {
                    Long id = Long.parseLong(value);
                    Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                    Sort.Order order = new Sort.Order(direction, sortBy);
                    Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                    model.addAttribute("products", productService.pageFindProductById(id, pageable));
                    model.addAttribute("totalPages", productService.pageFindProductById(id, pageable).getTotalPages());
                    model.addAttribute("currentPage", Integer.parseInt(currentPage));
                    model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                    model.addAttribute("sortBy", sortBy);
                    model.addAttribute("orderField", orderField);
                    return "admin/products/list";
                } catch (NumberFormatException e) {
                    model.addAttribute("errorMessage", "id value must be a number");
                    model.addAttribute("products", null);
                    return "admin/products/list";
                }

                // name = ...
            } else if (key.equalsIgnoreCase("name")) {
                Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sortBy);
                Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                List<Long> ids = new ArrayList<Long>();
                for (Product product : productService.findProductByNameContaining(value)) {
                    ids.add(product.getId());
                }

                model.addAttribute("products", productService.pageFindProductByNameContaining(value, pageable));
                model.addAttribute("ids", ids);
                model.addAttribute("totalPages", productService.pageFindProductByNameContaining(value, pageable).getTotalPages());
                model.addAttribute("currentPage", Integer.parseInt(currentPage));
                model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("orderField", orderField);
                return "admin/products/list";

                // price = ...
            } else if (key.equalsIgnoreCase("price")) {
                if (value.contains("-")) {
                    try {
                        Double minPrice = Double.parseDouble(value.strip().split("-")[0]);
                        Double maxPrice = Double.parseDouble(value.strip().split("-")[1]);
                        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                        Sort.Order order = new Sort.Order(direction, sortBy);
                        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                        List<Long> ids = new ArrayList<Long>();
                        for (Product product : productService.findProductByPriceBetween(minPrice, maxPrice)) {
                            ids.add(product.getId());
                        }

                        model.addAttribute("products", productService.pageFindProductByPriceBetween(minPrice, maxPrice, pageable));
                        model.addAttribute("ids", ids);
                        model.addAttribute("totalPages", productService.pageFindProductByPriceBetween(minPrice, maxPrice, pageable).getTotalPages());
                        model.addAttribute("currentPage", Integer.parseInt(currentPage));
                        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                        model.addAttribute("sortBy", sortBy);
                        model.addAttribute("orderField", orderField);
                        return "admin/products/list";
                    } catch (NumberFormatException e) {
                        model.addAttribute("errorMessage", "price value must be a number");
                        model.addAttribute("products", null);
                        return "admin/products/list";
                    }
                } else {
                    try {
                        Double price = Double.parseDouble(value);
                        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                        Sort.Order order = new Sort.Order(direction, sortBy);
                        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                        List<Long> ids = new ArrayList<Long>();
                        for (Product product : productService.findProductByPrice(price)) {
                            ids.add(product.getId());
                        }

                        model.addAttribute("products", productService.pageFindProductByPrice(price, pageable));
                        model.addAttribute("ids", ids);
                        model.addAttribute("totalPages", productService.pageFindProductByPrice(price, pageable).getTotalPages());
                        model.addAttribute("currentPage", Integer.parseInt(currentPage));
                        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                        model.addAttribute("sortBy", sortBy);
                        model.addAttribute("orderField", orderField);
                        return "admin/products/list";
                    } catch (NumberFormatException e) {
                        model.addAttribute("errorMessage", "price value must be a number");
                        model.addAttribute("products", null);
                        return "admin/products/list";
                    }
                }

                // color = ...
            } else if (key.equalsIgnoreCase("color")) {
                Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sortBy);
                Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                List<Long> ids = new ArrayList<Long>();
                for (Product product : productService.findProductByColor(value)) {
                    ids.add(product.getId());
                }

                model.addAttribute("products", productService.pageFindProductByColor(value, pageable));
                model.addAttribute("ids", ids);
                model.addAttribute("totalPages", productService.pageFindProductByColor(value, pageable).getTotalPages());
                model.addAttribute("currentPage", Integer.parseInt(currentPage));
                model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("orderField", orderField);
                return "admin/products/list";

                // description = ...
            } else if (key.equalsIgnoreCase("description")) {
                Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sortBy);
                Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                List<Long> ids = new ArrayList<Long>();
                for (Product product : productService.findProductByDescriptionContaining(value)) {
                    ids.add(product.getId());
                }

                model.addAttribute("products", productService.pageFindProductByDescriptionContaining(value, pageable));
                model.addAttribute("ids", ids);
                model.addAttribute("totalPages", productService.pageFindProductByDescriptionContaining(value, pageable).getTotalPages());
                model.addAttribute("currentPage", Integer.parseInt(currentPage));
                model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("orderField", orderField);
                return "admin/products/list";

                // information = ...
            } else if (key.equalsIgnoreCase("information")) {
                Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sortBy);
                Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                List<Long> ids = new ArrayList<Long>();
                for (Product product : productService.findProductByInformationContaining(value)) {
                    ids.add(product.getId());
                }

                model.addAttribute("products", productService.pageFindProductByInformationContaining(value, pageable));
                model.addAttribute("ids", ids);
                model.addAttribute("totalPages", productService.pageFindProductByInformationContaining(value, pageable).getTotalPages());
                model.addAttribute("currentPage", Integer.parseInt(currentPage));
                model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("orderField", orderField);
                return "admin/products/list";

                // size = ...
            } else if (key.equalsIgnoreCase("size")) {
                try {
                    int size = Integer.parseInt(value);
                    Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                    Sort.Order order = new Sort.Order(direction, sortBy);
                    Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                    List<Long> ids = new ArrayList<Long>();
                    for (Product product : productService.findProductBySize(size)) {
                        ids.add(product.getId());
                    }

                    model.addAttribute("products", productService.pageFindProductBySize(size, pageable));
                    model.addAttribute("ids", ids);
                    model.addAttribute("totalPages", productService.pageFindProductBySize(size, pageable).getTotalPages());
                    model.addAttribute("currentPage", Integer.parseInt(currentPage));
                    model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                    model.addAttribute("sortBy", sortBy);
                    model.addAttribute("orderField", orderField);
                    return "admin/products/list";
                } catch (NumberFormatException e) {
                    model.addAttribute("errorMessage", "size value must be a number");
                    model.addAttribute("products", null);
                    return "admin/products/list";
                }

                // view count = ....
            } else if (key.equalsIgnoreCase("viewcount")) {
                if (value.contains("-")) {
                    try {
                        Long lowestValue = Long.parseLong(value.strip().split("-")[0]);
                        Long highestValue = Long.parseLong(value.strip().split("-")[1]);
                        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                        Sort.Order order = new Sort.Order(direction, sortBy);
                        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                        List<Long> ids = new ArrayList<Long>();
                        for (Product product : productService.findProductByViewCountBetween(lowestValue, highestValue)) {
                            ids.add(product.getId());
                        }

                        model.addAttribute("products", productService.pageFindProductByViewCountBetween(lowestValue, highestValue, pageable));
                        model.addAttribute("ids", ids);
                        model.addAttribute("totalPages", productService.pageFindProductByViewCountBetween(lowestValue, highestValue, pageable).getTotalPages());
                        model.addAttribute("currentPage", Integer.parseInt(currentPage));
                        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                        model.addAttribute("sortBy", sortBy);
                        model.addAttribute("orderField", orderField);
                        return "admin/products/list";
                    } catch (NumberFormatException e) {
                        model.addAttribute("errorMessage", "price value must be a number");
                        model.addAttribute("products", null);
                        return "admin/products/list";
                    }
                } else {
                    try {
                        Long viewCount = Long.parseLong(value);
                        Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                        Sort.Order order = new Sort.Order(direction, sortBy);
                        Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                        List<Long> ids = new ArrayList<Long>();
                        for (Product product : productService.findProductByViewCount(viewCount)) {
                            ids.add(product.getId());
                        }

                        model.addAttribute("products", productService.pageFindProductByViewCount(viewCount, pageable));
                        model.addAttribute("ids", ids);
                        model.addAttribute("totalPages", productService.pageFindProductByViewCount(viewCount, pageable).getTotalPages());
                        model.addAttribute("currentPage", Integer.parseInt(currentPage));
                        model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                        model.addAttribute("sortBy", sortBy);
                        model.addAttribute("orderField", orderField);
                        return "admin/products/list";
                    } catch (NumberFormatException e) {
                        model.addAttribute("errorMessage", "price value must be a number");
                        model.addAttribute("products", null);
                        return "admin/products/list";
                    }
                }

                // category
            } else if (key.equalsIgnoreCase("category")) {
                Sort.Direction direction = orderField.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Sort.Order order = new Sort.Order(direction, sortBy);
                Pageable pageable = PageRequest.of(Integer.parseInt(currentPage), Integer.parseInt(numberElementInOnePage), Sort.by(order));

                List<Long> ids = new ArrayList<Long>();
                for (Product product : productService.findProductByCategoryNameContaining(value)) {
                    ids.add(product.getId());
                }

                model.addAttribute("products", productService.pageFindProductByCategoryNameContaining(value, pageable));
                model.addAttribute("ids", ids);
                model.addAttribute("totalPages", productService.pageFindProductByCategoryNameContaining(value, pageable).getTotalPages());
                model.addAttribute("currentPage", Integer.parseInt(currentPage));
                model.addAttribute("numberElementInOnePage", Integer.parseInt(numberElementInOnePage));
                model.addAttribute("sortBy", sortBy);
                model.addAttribute("orderField", orderField);
                return "admin/products/list";

                // manufacture
            } else if (key.equalsIgnoreCase("manufacture")) {
                model.addAttribute("errorMessage", "Feature find manufacture not support, please try to find another keyword");
                model.addAttribute("products", null);
                return "admin/products/list";

            } else {
                model.addAttribute("errorMessage", "please enter correct keyword");
                model.addAttribute("products", null);
                return "admin/products/list";
            }
        } else {
            model.addAttribute("errorMessage", "value must containing '=' ");
            model.addAttribute("products", null);
            return "admin/products/list";
        }
    }
}