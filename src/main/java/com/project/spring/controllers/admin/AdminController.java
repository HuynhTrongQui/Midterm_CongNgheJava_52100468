package com.project.spring.controllers.admin;

import com.project.spring.dto.OrderDTO;
import com.project.spring.dto.OrderDetailDTO;
import com.project.spring.model.*;
import com.project.spring.repositories.OrderRepository;
import com.project.spring.repositories.ProductRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.impl.ReportService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.utils.ExportPdf;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;


    @GetMapping
    public String index(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (role.getName().equals("user")) {
                model.addAttribute("roleUser", true);
            }
            if (role.getName().equals("admin")) {
                model.addAttribute("roleAdmin", true);
            }
        }

        List<Order> orders = this.orderRepository.findAll();
        model.addAttribute("numberOfOrder", orders.size());

        List<AppUser> appUsers = this.userRepository.findAll();
        model.addAttribute("numberOfUser", appUsers.size());

        List<Product> products = this.productRepository.findAll();
        model.addAttribute("numberOfProduct", products.size());


        return "admin/index";
    }

    @GetMapping("/order")
    public String order(Model model, @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(name = "sortBy", defaultValue = "date") String sortBy, @RequestParam(name = "orderField", defaultValue = "desc") String orderField) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());

        Sort.Direction direction = orderField.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by(order));

        Page<OrderDTO> page = this.orderRepository.findAll(pageable).stream().map(order1 -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setIdOrder(order1.getId());
            orderDTO.setTotal(order1.getTotal());
            orderDTO.setNameUser(order1.getUser().getName());
            orderDTO.setDate(order1.getDate());
            orderDTO.setIdUser(order1.getUser().getId());
            orderDTO.setStatus(order1.getStatus());
            orderDTO.setAddress(order1.getAddress());
            orderDTO.setPhone(order1.getPhone());
            return orderDTO;
        }).collect(Collectors.collectingAndThen(Collectors.toList(), PageImpl::new));

        model.addAttribute("orders", page.getContent());
        model.addAttribute("totalPages", this.orderRepository.findAll(pageable).getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "admin/order/list";
    }

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/order/details/{id}")
    public String details(@PathVariable("id") Long id, Model model) {

        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());

        Order order = this.orderRepository.findById(id).get();
        List<OrderDetail> orderDetails = order.getOrderDetails();
        List<OrderDetailDTO> orderDetailDTOS = orderDetails.stream().map(orderDetail -> {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setQuantity(orderDetail.getQuantity());
            orderDetailDTO.setOrderId(orderDetail.getOrder().getId());
            orderDetailDTO.setNameProduct(orderDetail.getProduct().getName());
            orderDetailDTO.setPriceProduct(orderDetail.getProduct().getPrice());
            orderDetailDTO.setUrl(orderDetail.getProduct().getOriginalPicture());
            orderDetailDTO.setColor(orderDetail.getProduct().getColor());
            return orderDetailDTO;
        }).toList();

        model.addAttribute("orderDetailDTOS", orderDetailDTOS);

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        model.addAttribute("orderDTO", orderDTO);
        return "admin/order/details";
    }

    @PostMapping("/order/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {

        Order order = this.orderRepository.findById(id).get();
        this.orderRepository.delete(order);
        return "redirect:/admin/order";
    }

    @GetMapping("/order/edit/{id}")
    public String editOrder(@PathVariable("id") Long id, Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());

        Order order = this.orderRepository.findById(id).get();
        List<OrderDetail> orderDetail = order.getOrderDetails();
        List<OrderDetailDTO> orderDetailDTOS = orderDetail.stream().map(orderDetail1 -> {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setIdOrderDetail(orderDetail1.getId());
            orderDetailDTO.setIdProduct(orderDetail1.getProduct().getId());
            orderDetailDTO.setNameProduct(orderDetail1.getProduct().getName());
            orderDetailDTO.setPriceProduct(orderDetail1.getProduct().getPrice());
            orderDetailDTO.setQuantity(orderDetail1.getQuantity());
            return orderDetailDTO;
        }).toList();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setIdOrder(order.getId());
        orderDTO.setNameUser(order.getName());
        orderDTO.setEmail(order.getUser().getEmail());
        orderDTO.setPhone(order.getPhone());
        orderDTO.setAddress(order.getAddress());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setTotal(order.getTotal());
        orderDTO.setDate(order.getDate());
        orderDTO.setOrderDetails(orderDetailDTOS);
        orderDTO.setIdUser(order.getUser().getId());

        model.addAttribute("order", orderDTO);

        return "admin/order/edit";
    }

    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/order/edit")
    public String editOrderPost(@ModelAttribute("order") OrderDTO orderDTORequest) {

        Long idOrder = orderDTORequest.getIdOrder();

        Order order = new Order();
        order.setId(idOrder);
        order.setName(orderDTORequest.getNameUser());
        order.setPhone(orderDTORequest.getPhone());
        order.setAddress(orderDTORequest.getAddress());
        order.setStatus(orderDTORequest.getStatus());
        order.setUser(userRepository.findById(orderDTORequest.getIdUser()).get());
        order.setDate(this.orderRepository.findById(idOrder).get().getDate());

        List<OrderDetail> orderDetails = orderDTORequest.getOrderDetails().stream().map(orderDetailDTO -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(orderDetailDTO.getIdOrderDetail());
            orderDetail.setProduct(this.productRepository.findById(orderDetailDTO.getIdProduct()).get());
            orderDetail.setQuantity(orderDetailDTO.getQuantity());
            orderDetail.setOrder(order);
            return orderDetail;
        }).toList();


        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            BigDecimal subtotal = BigDecimal.valueOf(orderDetail.getProduct().getPrice()).multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
            total = total.add(subtotal);
        }
        order.setOrderDetails(orderDetails);
        order.setTotal(total);
        this.orderRepository.save(order);
        return "redirect:/admin/order/edit/" + orderDTORequest.getIdOrder();
    }

    @GetMapping(value = "/exports", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> employeeReports(HttpServletResponse response) throws IOException {
        List<AppUser> allEmployees = this.userRepository.findAll();
        ByteArrayInputStream bis = ExportPdf.employeesReport(allEmployees);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=employees.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    @Autowired
    ReportService exportReport;

    @GetMapping("/report/order/{id}/{format}")
    public String generateReport(@PathVariable String format, @PathVariable Long id) throws FileNotFoundException, JRException {
        exportReport.exportReport(format, id);
        return "admin/index";
    }
}
