package com.project.spring.controllers;

import com.project.spring.dto.*;
import com.project.spring.model.*;
import com.project.spring.repositories.*;
import com.project.spring.service.ProductService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.text.StyleContext;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductService productService;
    @Autowired
    ManufactureRepository manufactureRepository;

    @Autowired
    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("hasBanner", "hasBanner");

        List<Product> products = this.productRepository.getMostPurchasedProduct();
        List<ProductDTO> productDTOS = products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());
            productDTO.setOriginalPicture(product.getLogoImage());
            return productDTO;
        }).toList();
        modelAndView.addObject("trendy", productDTOS);

//        List<Product> productsCategoty = this.productService.getAllProductByCategory()
        List<Category> categories = this.categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS = categories.stream().map(category -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setName(category.getName());
            List<Product> products1 = this.productService.getAllProductByCategory(category.getId());
            categoryDTO.setQuantity(products1.size());
            categoryDTO.setId(category.getId());
            categoryDTO.setUrl(category.getUrl());
            List<Manufacture> manufactures = this.manufactureRepository.findByCategory(category.getId());
            List<ManufactureDTO> manufactureDTOS = manufactures.stream().map(manufacture -> modelMapper.map(manufacture, ManufactureDTO.class)).toList();
            categoryDTO.setManufactures(manufactureDTOS);
            return categoryDTO;
        }).toList();
        modelAndView.addObject("categoriesDTOs", categoryDTOS);

        List<Product> products1 = this.productRepository.getTop8Product(1L);
        List<ProductDTO> productDTOS1 = products1.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());
            productDTO.setOriginalPicture(product.getLogoImage());
            return productDTO;
        }).toList();
        modelAndView.addObject("top8Mobile", productDTOS1);

        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        if (user != null) {
            List<Cart> carts = this.cartRepository.findByUserId(user.getId());
            if (carts.isEmpty()) {
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setTotal(0.0);
                cart.setCartItems(new ArrayList<>());
                Cart newCart = this.cartRepository.save(cart);
                modelAndView.addObject("numberItems", newCart.getCartItems().size());
                modelAndView.addObject("idCart", newCart.getId());
            } else {
                Cart cart = carts.get(0);
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                List<CartItemDTO> cartItemDTOs = cartDTO.getCartItems();
                modelAndView.addObject("numberItems", cartItemDTOs.size());
                modelAndView.addObject("idCart", cart.getId());
            }
            modelAndView.addObject("isLogin", user.getName());
        }
        return modelAndView;
    }
    @GetMapping("/403")
    public String error403(){
        return "403";
    }
}
