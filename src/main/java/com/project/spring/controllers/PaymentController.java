package com.project.spring.controllers;


import com.project.spring.config.VNPayService;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.dto.OrderDetailDTO;
import com.project.spring.dto.PaymentResDTO;
import com.project.spring.enums.OrderStatus;
import com.project.spring.model.*;
import com.project.spring.repositories.CartRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.CartService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private VNPayService vnPayService;
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

    @GetMapping
    public String submidOrder(@RequestParam("amount") int orderTotal, @RequestParam("orderInfo") String orderInfo, HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("ok");
        paymentResDTO.setMessage("Sucessful");
        paymentResDTO.setUrl(vnpayUrl);
        return  "redirect:" + vnpayUrl;
    }

        @GetMapping("/vnpay-payment")
        @Transactional
        public String GetMapping(HttpServletRequest request, Model model) {
            int paymentStatus = vnPayService.orderReturn(request);
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");
            model.addAttribute("orderId", orderInfo);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("paymentTime", paymentTime);
            model.addAttribute("transactionId", transactionId);

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

            return paymentStatus == 1 ? "ordersuccess" : "orderfail";
        }
}
