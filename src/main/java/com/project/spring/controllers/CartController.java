package com.project.spring.controllers;

import com.project.spring.dto.*;
import com.project.spring.enums.OrderStatus;
import com.project.spring.model.*;
import com.project.spring.repositories.CartRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.CartService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.utils.CartUtils;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager entityManager;

    @GetMapping("/cart")
    public ModelAndView showCart(HttpServletRequest request, HttpServletResponse response) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        ModelAndView modelAndView = new ModelAndView("/cart");
        int totalProduct = 0;
        int numberItems = 0;
        BigDecimal[] total = {BigDecimal.ZERO};
        if (user != null) {
            Long idUser = user.getId();
            List<Cart> carts = this.cartRepository.findByUserId(idUser);
            Cart cart = new Cart();
            if (!carts.isEmpty()) {
                cart = carts.get(0);
            }
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<CartItemDTO> cartItemDTOs = cartDTO.getCartItems();
            cartItemDTOs.stream().map(cartItemDTO -> {
                cartItemDTO.setProductOriginalPicture(
                        this.productRepository.findById(cartItemDTO.getProductId()).get().getLogoImage());
                return cartItemDTO;
            }).toList();
            numberItems = cartItemDTOs.size();
            for (CartItemDTO cartItemDTO : cartItemDTOs) {
                total[0] = total[0].add(BigDecimal.valueOf(cartItemDTO.getProductPrice() * cartItemDTO.getQuantity()));
            }
            List<CartItemDTO> cartCookies = new ArrayList<CartItemDTO>();
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("cart".equals(cookie.getName())) {
                        String encodedValue = cookie.getValue();
                        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));
                        cartCookies = CartUtils.convertJsonToCart(decodedValue);
                        Map<Long, CartItemDTO> mergedMap = new HashMap<>();
                        for (CartItemDTO item : cartItemDTOs) {
                            Long productId = item.getProductId();
                            mergedMap.put(productId, item);
                        }
                        for (CartItemDTO item : cartCookies) {
                            Long productId = item.getProductId();
                            if (mergedMap.containsKey(productId)) {
                                CartItemDTO existItem = mergedMap.get(productId);
                                existItem.setQuantity(existItem.getQuantity() + item.getQuantity());
                            } else {
                                mergedMap.put(productId, item);
                            }
                        }
                        List<CartItemDTO> mergedList = new ArrayList<>(mergedMap.values());
                        Cart finalCart = cart;
                        List<CartItem> cartItems = mergedList.stream().map(cartItemDTO -> {
                            CartItem item = new CartItem();
                            item.setQuantity(cartItemDTO.getQuantity());
                            item.setProduct(this.productRepository.findById(cartItemDTO.getProductId()).get());
                            item.setCart(finalCart);
                            return item;
                        }).toList();
                        Cart cart1 = new Cart();
                        cart1.setId(cart.getId());
                        cart1.setUser(user);
                        double v = 0.0;
                        for (CartItem cartItem : cartItems) {
                            v += cartItem.getQuantity() * cartItem.getProduct().getPrice();
                        }
                        cart1.setTotal(v);
                        cart1.setCartItems(cartItems);
                        this.cartRepository.save(cart1);

                        Cookie cookie1 = new Cookie("cart", null);
                        cookie1.setMaxAge(0);
                        response.addCookie(cookie1);
                    }
                }
            }
            modelAndView.addObject("cartItems", cartItemDTOs);
            modelAndView.addObject("isLogin", user.getName());
            modelAndView.addObject("idCart", cart.getId());
            modelAndView.addObject("numberItems", numberItems);
            modelAndView.addObject("total", total[0]);
            return modelAndView;
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("cart".equals(cookie.getName())) {
                        String encodedValue = cookie.getValue();
                        String decodedValue = new String(Base64.getDecoder().decode(encodedValue));
                        List<CartItemDTO> cart = CartUtils.convertJsonToCart(decodedValue);
                        cart.forEach(cartItemDTO -> {
                            BigDecimal itemPrice = BigDecimal.valueOf(cartItemDTO.getProductPrice())
                                    .multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
                            total[0] = total[0].add(itemPrice);
                        });
                        modelAndView.addObject("cartItems", cart);
                        modelAndView.addObject("cookie", "cookie");
                        break;
                    }
                }
            }
        }
        modelAndView.addObject("numberItems", numberItems);
        modelAndView.addObject("total", total[0]);
        return modelAndView;

    }

    @RequestMapping(value = {"/cart/add"}, method = RequestMethod.POST)
    public String addToCart(@ModelAttribute("cartItemDTO") CartItemDTO cartItemDTO, Model model,
                            HttpServletRequest request, HttpServletResponse response) {
        /* send request CartItemDTO */
        Long idProduct = cartItemDTO.getProductId();
        Product product = productRepository.findById(idProduct).get();
        int quantity = cartItemDTO.getQuantity();

        /* get user */
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        if (user != null) {
            /* Get list cart of user */
            List<Cart> carts = this.cartRepository.findByUserId(user.getId());
            if (carts == null) {
                Cart cart = new Cart();
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(quantity);
                cart.setTotal(product.getPrice() * quantity);
                cart.setUser(user);
                this.cartRepository.save(cart);
            } else {
                Cart cart = carts.get(0);
                List<CartItem> cartItems = cart.getCartItems();
                boolean found = false;
                for (CartItem cartItem : cartItems) {
                    if (cartItem.getProduct().getId().equals(product.getId())) {
                        cartItem.setQuantity(cartItem.getQuantity() + quantity);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    CartItem item = new CartItem();
                    item.setCart(cart);
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    cartItems.add(item);
                }
                double total = 0.0;
                for (CartItem cartItem : cartItems) {
                    total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
                }
                cart.setTotal(total);
                cart.setCartItems(cartItems);
                this.cartRepository.save(cart);
            }
        } else {
            List<CartItemDTO> cartDTO = (List<CartItemDTO>) request.getSession().getAttribute("cart");
            if (cartDTO == null) {
                cartDTO = new ArrayList<CartItemDTO>();
                request.getSession().setAttribute("cart", new ArrayList<CartItemDTO>());
            }
            CartItemDTO cartItemDTO1 = new CartItemDTO();
            cartItemDTO1.setProductId(product.getId());
            cartItemDTO1.setProductName(product.getName());
            cartItemDTO1.setProductPrice(product.getPrice());
            cartItemDTO1.setProductOriginalPicture(product.getOriginalPicture());
            cartItemDTO1.setQuantity(quantity);
            cartDTO.add(cartItemDTO1);
            request.getSession().setAttribute("cart", cartDTO);
            String cartJson = CartUtils.convertCartToJson(cartDTO);
            String encodedCartJson = Base64.getEncoder().encodeToString(cartJson.getBytes());
            Cookie cookie = new Cookie("cart", encodedCartJson);
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return "redirect:/cart";
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public ModelAndView checkOut() {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());

        ModelAndView modelAndView = new ModelAndView("/checkout");
        int totalProduct = 0;
        int numberItems = 0;
        BigDecimal total = BigDecimal.ZERO;
        if (user != null) {
            try {
                Long idUser = user.getId();
                List<Cart> carts = this.cartRepository.findByUserId(idUser);
                /* get cart [0] */
                Cart cart = carts.get(0);
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                List<CartItemDTO> cartItemDTOs = cartDTO.getCartItems();
                numberItems = cartItemDTOs.size();
                for (CartItemDTO cartItemDTO : cartItemDTOs) {
                    total = total.add(BigDecimal.valueOf(cartItemDTO.getProductPrice() * cartItemDTO.getQuantity()));
                }
                modelAndView.addObject("cartItems", cartItemDTOs);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        modelAndView.addObject("numberItems", numberItems);
        modelAndView.addObject("total", total);
        modelAndView.addObject("isLogin", user.getName());
        return modelAndView;
    }

    @Transactional
    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String payment(Model model, @RequestParam(name = "payment", required = false) String payment,
                          @RequestParam(name = "amount", required = false) String amount
    ) {
        if (payment.equals("paypal")) {

            return "redirect:/api/payment?" + "amount=" + amount.toString() + "&orderInfo=CheckOrder";
        }
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("isLogin", user.getName());
        Long idUser = user.getId();
        List<Cart> carts = this.cartRepository.findByUserId(idUser);
        Cart cart = carts.get(0);
        List<CartItem> cartItems = cart.getCartItems();
        List<CartItemDTO> cartItemDTOS = cartItems.stream()
                .map(cartItem -> modelMapper.map(cartItem, CartItemDTO.class)).toList();

        List<OrderDetailDTO> orderDetailDTOSGetFromCartItem = cartItemDTOS.stream().map(cartItemDTO -> {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setIdProduct(cartItemDTO.getProductId());
            orderDetailDTO.setQuantity(cartItemDTO.getQuantity());
            return orderDetailDTO;
        }).toList();
        List<OrderDetailDTO> orderDetailDTOS = orderDetailDTOSGetFromCartItem;

        Order order = new Order();
        order.setDate(new Date());
        order.setUser(user);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
            Long idP = orderDetailDTO.getIdProduct();
            Product product = this.productRepository.findById(idP).orElse(null);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setOrder(order);
            orderDetail.setQuantity(orderDetailDTO.getQuantity());

            orderDetails.add(orderDetail);

            double priceProduct = product.getPrice();
            int quantity = orderDetailDTO.getQuantity();
            total = total.add(BigDecimal.valueOf(priceProduct * quantity));
        }
        order.setTotal(total);
        order.setOrderDetails(orderDetails);
        order.setStatus(OrderStatus.PENDING);
        entityManager.persist(order);
        /* clear cart */
        this.cartRepository.delete(cart);
        return "checkoutsuccess";
    }

    @PostMapping("/cart")
    public String updateCart(@ModelAttribute("cartItemDTO") CartItemDTO cartItemDTO) {
        Product product = this.productRepository.findById(cartItemDTO.getProductId()).get();
        int quantity = cartItemDTO.getQuantity();
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        if (user != null) {
            List<Cart> carts = this.cartRepository.findByUserId(user.getId());
            Cart cart = carts.get(0);
            List<CartItem> cartItems = cart.getCartItems();
            boolean found = false;
            for (CartItem item : cartItems) {
                if (product.getId().equals(item.getProduct().getId())) {
                    item.setQuantity(quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                CartItem item = new CartItem();
                item.setQuantity(quantity);
                item.setProduct(product);
                item.setCart(cart);
                cartItems.add(item);
            }
            cart.setCartItems(cartItems);
            this.cartRepository.save(cart);
        }
        return "redirect:/cart";

    }
}
