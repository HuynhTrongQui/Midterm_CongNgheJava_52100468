package com.project.spring.api;

import com.project.spring.dto.OrderDTO;
import com.project.spring.dto.OrderDetailDTO;
import com.project.spring.dto.ResponseObject;
import com.project.spring.model.Order;
import com.project.spring.model.OrderDetail;
import com.project.spring.model.Product;
import com.project.spring.model.AppUser;
import com.project.spring.repositories.OrderRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderAPI {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    EntityManager entityManager;

    @PostMapping("")
    @Transactional
    public ResponseEntity<ResponseObject> createOrder(@RequestBody OrderDTO orderDTORequest) {
        Long idUser = orderDTORequest.getIdUser();
        List<OrderDetailDTO> orderDetailDTOS = orderDTORequest.getOrderDetails();
        /* User */
        AppUser user = this.userRepository.findById(idUser).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Not found user"));
        }

        Order order = new Order();
        order.setDate(new Date());
        order.setUser(user);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
            Long idP = orderDetailDTO.getIdProduct();
            Product product = this.productRepository.findById(idP).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Not found product"));
            }
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
        entityManager.persist(order);

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", orderDTO));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<ResponseObject> update(@RequestBody OrderDTO orderDTORequest) {
        List<OrderDetailDTO> orderDetailDTOS = orderDTORequest.getOrderDetails();
        Long idUserRequest = orderDTORequest.getIdUser();
        AppUser user = this.userRepository.findById(idUserRequest).get();

        Order order = entityManager.find(Order.class, orderDTORequest.getIdOrder());
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Not found order"));
        }
        List<OrderDetail> orderDetails = order.getOrderDetails();

        for (OrderDetailDTO orderDetailDTO : orderDetailDTOS) {
            Long idP = orderDetailDTO.getIdProduct();
            Product product = this.productRepository.findById(idP).orElse(null);
            if (product == null) {
                return null;
            }
            ;
            boolean found = false;
            for (OrderDetail orderDetail : orderDetails) {
                if (orderDetail.getProduct().getId().equals(product.getId())) {
                    orderDetail.setQuantity(orderDetailDTO.getQuantity());
                    found = true;
                    break;
                }
            }
            if (!found) {
                OrderDetail orderDetailNew = new OrderDetail();
                orderDetailNew.setProduct(this.productRepository.findById(orderDetailDTO.getIdProduct()).get());
                orderDetailNew.setQuantity(orderDetailDTO.getQuantity());
                orderDetailNew.setOrder(order);
                orderDetails.add(orderDetailNew);
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            total = total.add(BigDecimal.valueOf(orderDetail.getQuantity() * orderDetail.getProduct().getPrice()));
        }

        order.setUser(user);
        order.setTotal(total);
        order.setOrderDetails(orderDetails);

        entityManager.merge(order);

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", "ok "));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> get() {
        List<Order> orders = this.orderRepository.findAll();
        List<OrderDTO> orderDetailDTOS = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class)).toList();
        if (orderDetailDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1", "Success", orderDetailDTOS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getDetailByIdOrder(@PathVariable("id") Long id){
        Order order = this.orderRepository.findById(id).orElse(null);
        if(order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0", "Failure", "Not found order with id order:" + id));
        }
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1","Success",orderDTO));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<ResponseObject> getListOrderByIdUser(@PathVariable("id") Long id){
        AppUser user = this.userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0","Failure", "Not found user"));
        }
        List<Order> orders = this.orderRepository.findByUser(user);
        if(orders.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0","Failure","User not have order"));
        }
        List<OrderDTO> orderDTOS = orders.stream().map(order ->  modelMapper.map(order,OrderDTO.class)).toList();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1","Success",orderDTOS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable("id")Long id){
        Order order = this.orderRepository.findById(id).orElse(null);
        if(order == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("0","Failure", "Not found order"));
        }
        this.orderRepository.delete(order);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("1","Success","Delete order success"));
    }
}
