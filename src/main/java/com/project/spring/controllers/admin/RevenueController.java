package com.project.spring.controllers.admin;

import com.project.spring.dto.*;
import com.project.spring.model.*;
import com.project.spring.repositories.*;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/revenue")
public class RevenueController {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderDetailRepositopry orderDetailRepositopry;
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/category")
    public String index(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());


        List<Category> category = this.categoryRepository.findAll();
        List<RevenueCategoryDTO> revenueCategoryDTOS = new ArrayList<RevenueCategoryDTO>();
        for (Category category1 : category) {
            RevenueCategoryDTO categoryDTO = new RevenueCategoryDTO();
            categoryDTO.setNameCategory(category1.getName());

            /* Number category */
            List<Product> products1 = this.productRepository.findAll().stream().filter(product -> product.getCategory().equals(category1)).toList();
            categoryDTO.setQuantity(products1.size());

            /*Price Min*/
            Optional<Product> productMin = products1.stream().min(Comparator.comparingDouble(Product::getPrice));
            productMin.ifPresent(product -> categoryDTO.setPriceMin(product.getPrice()));

            /*Price Max*/
            Optional<Product> productMax = products1.stream().max(Comparator.comparingDouble(Product::getPrice));
            productMax.ifPresent(product -> categoryDTO.setPriceMax(product.getPrice()));

            /*Total*/
            BigDecimal total = BigDecimal.ZERO;
            int d = 0;
            List<OrderDetail> orderDetails = this.orderDetailRepositopry.findAll();
            for (Product product : products1) {
                for (OrderDetail orderDetail : orderDetails) {
                    if (product.equals(orderDetail.getProduct())) {
                        BigDecimal price = BigDecimal.valueOf(product.getPrice());
                        BigDecimal quantity = new BigDecimal(orderDetail.getQuantity());
                        BigDecimal productTotal = price.multiply(quantity);
                        d += 1;
                        total = total.add(productTotal);
                    }
                }
            }
            categoryDTO.setTotal(total);

            /*GPA*/
            categoryDTO.setGpa(total.divide(new BigDecimal(d), 2, RoundingMode.HALF_UP));

            revenueCategoryDTOS.add(categoryDTO);
        }
        model.addAttribute("category", revenueCategoryDTOS);

        /*Chart*/

        Map<Object, Object> map = null;
        List<Map<Object, Object>> dataPoints1 = new ArrayList<Map<Object, Object>>();
        List<List<Map<Object, Object>>> list = new ArrayList<List<Map<Object, Object>>>();

        BigDecimal total = BigDecimal.ZERO;
        for (RevenueCategoryDTO categoryDTO : revenueCategoryDTOS) {
            total = total.add(categoryDTO.getTotal());
        }
        for (RevenueCategoryDTO categoryDTO : revenueCategoryDTOS) {
            map = new HashMap<Object, Object>();
            map.put("name", categoryDTO.getNameCategory());
            map.put("y", categoryDTO.getTotal().divide(total, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            dataPoints1.add(map);
        }
        list.add(dataPoints1);
        model.addAttribute("dataPointsList", list);
        return "admin/revenue/category";
    }

    @Autowired
    OrderRepository orderRepository;

    @GetMapping("/year")
    public String year(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());

        List<Integer> years = this.orderRepository.findAll().stream().map(order -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getDate());
            return calendar.get(Calendar.YEAR);
        }).distinct().sorted(Comparator.reverseOrder()).toList();

        List<Order> orders = this.orderRepository.findAll();

        List<RevenueYearDTO> revenueYearDTOS = new ArrayList<RevenueYearDTO>();
        for (Integer year : years) {
            RevenueYearDTO revenueYearDTO = new RevenueYearDTO();
            /* order */
            List<Order> orders1 = this.orderRepository.findOrderByYear(year);

            Optional<Order> orderMax = orders1.stream().max(Comparator.comparingDouble(value -> value.getTotal().doubleValue()));
            orderMax.ifPresent(order -> revenueYearDTO.setPriceMax(order.getTotal()));

            Optional<Order> orderMin = orders1.stream().min(Comparator.comparingDouble(value -> value.getTotal().doubleValue()));
            orderMin.ifPresent(order -> revenueYearDTO.setPriceMin(order.getTotal()));

            BigDecimal totalSum = orders1.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal gpa = totalSum.divide(BigDecimal.valueOf(orders1.size()), 2, RoundingMode.HALF_UP);

            revenueYearDTO.setYear(year);
            revenueYearDTO.setQuantity(this.orderRepository.findOrderByYear(year).size());
            revenueYearDTO.setTotal(totalSum);
            revenueYearDTO.setGpa(gpa);

            revenueYearDTOS.add(revenueYearDTO);
        }
        model.addAttribute("data", revenueYearDTOS);

        return "admin/revenue/year";
    }

    @GetMapping("/product")
    public String product(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());


        List<RevenueProductDTO> revenueProductDTOs = new ArrayList<RevenueProductDTO>();

        List<Product> products1 = this.productRepository.findAll();
        for (Product product : products1) {
            List<OrderDetail> orderDetails = this.orderDetailRepositopry.findByProduct(product.getId());

            int quantity = orderDetails.stream().mapToInt(OrderDetail::getQuantity).sum();

            BigDecimal total = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));

            RevenueProductDTO productDTO = new RevenueProductDTO();
            productDTO.setName(product.getName());
            productDTO.setQuantity(quantity);
            productDTO.setTotal(total);
            productDTO.setPrice(product.getPrice());

            if (quantity == 0) {
                productDTO.setGpa(new BigDecimal(0));
            } else {
                productDTO.setGpa(total.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP));
            }
            revenueProductDTOs.add(productDTO);
        }
        model.addAttribute("data", revenueProductDTOs);

        return "admin/revenue/product";
    }

    @Autowired
    UserRepository userRepository;

    @GetMapping("customer")
    public String customer(Model model) {
        AppUser user1 = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user1.getName());


        List<AppUser> appUsers = this.userRepository.findAll();
        List<RevenueCustomerDTO> revenueCustomerDTOS = new ArrayList<RevenueCustomerDTO>();
        for (AppUser user : appUsers) {
            List<Order> orders = this.orderRepository.findByUser(user);

            RevenueCustomerDTO revenueCustomerDTO = new RevenueCustomerDTO();
            revenueCustomerDTO.setId(user.getId());
            revenueCustomerDTO.setName(user.getName());
            revenueCustomerDTO.setQuantity(orders.size());

            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueCustomerDTO.setTotal(total);

            BigDecimal priceMax = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::max);
            revenueCustomerDTO.setPriceMax(priceMax);

            BigDecimal priceMin = orders.stream().map(Order::getTotal).reduce(total, BigDecimal::min);
            revenueCustomerDTO.setPriceMin(priceMin);

            if (!total.equals(BigDecimal.ZERO)) {
                BigDecimal average = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
                revenueCustomerDTO.setGpa(average);
            } else {
                revenueCustomerDTO.setGpa(BigDecimal.ZERO);
            }
            revenueCustomerDTOS.add(revenueCustomerDTO);
        }
        model.addAttribute("revenueCustomerDTOS", revenueCustomerDTOS);

        Map<Object, Object> map = null;
        List<List<Map<Object, Object>>> list = new ArrayList<List<Map<Object, Object>>>();
        List<Map<Object, Object>> dataPoints1 = new ArrayList<Map<Object, Object>>();
        for (RevenueCustomerDTO revenueCustomerDTO : revenueCustomerDTOS) {
            map = new HashMap<Object, Object>();
            map.put("label", revenueCustomerDTO.getName());
            map.put("y", revenueCustomerDTO.getTotal());
            dataPoints1.add(map);
        }
        list.add(dataPoints1);
        model.addAttribute("dataPointsList", list);
        return "admin/revenue/customer";
    }


    @PostMapping("/day")
    public String day(Model model, @RequestParam String month) {

        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());

        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        List<LocalDate> localDates = DateUtils.getDaysInMonth(yearMonth.getYear(), yearMonth.getMonthValue());
        List<RevenueDayDTO> revenueMonthDTOS = new ArrayList<RevenueDayDTO>();
        for (LocalDate localDate : localDates) {
            RevenueDayDTO revenueMonthDTO = new RevenueDayDTO();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            revenueMonthDTO.setDate(date);
            List<Order> orders = this.orderRepository.findByDate(date);
            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueMonthDTO.setTotal(total);
            revenueMonthDTOS.add(revenueMonthDTO);
        }
        model.addAttribute("dataPoints", revenueMonthDTOS);
        model.addAttribute("time", yearMonth);
        return "admin/revenue/day";
    }

    @GetMapping("/day")
    public String day(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());


        LocalDate localDate1 = LocalDate.now();
        List<LocalDate> localDates = DateUtils.getDaysInMonth(localDate1.getYear(), localDate1.getMonthValue());
        List<RevenueDayDTO> revenueMonthDTOS = new ArrayList<RevenueDayDTO>();
        for (LocalDate localDate : localDates) {
            RevenueDayDTO revenueMonthDTO = new RevenueDayDTO();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            revenueMonthDTO.setDate(date);
            List<Order> orders = this.orderRepository.findByDate(date);
            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueMonthDTO.setTotal(total);
            revenueMonthDTOS.add(revenueMonthDTO);
        }
        model.addAttribute("dataPoints", revenueMonthDTOS);
        return "admin/revenue/day";
    }

    @GetMapping("/month")
    public String month(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());


        List<RevenueMonthDTO> revenueMonthDTOS = new ArrayList<RevenueMonthDTO>();
        LocalDate localDate1 = LocalDate.now();
        List<Integer> months = new ArrayList<Integer>();
        for (Month month : Month.values()) {
            months.add(month.getValue());
        }
        for (Integer month : months) {
            RevenueMonthDTO revenueMonthDTO = new RevenueMonthDTO();
            revenueMonthDTO.setMonth(month);
            List<Order> orders = this.orderRepository.findByYearAndMonth(localDate1.getYear(), month);
            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueMonthDTO.setTotal(total);
            revenueMonthDTO.setYear(localDate1.getYear());
            revenueMonthDTOS.add(revenueMonthDTO);
        }
        model.addAttribute("dataPoints", revenueMonthDTOS);
        return "admin/revenue/month";
    }

    @PostMapping("/month")
    public String month(Model model, @RequestParam int year) {
        List<RevenueMonthDTO> revenueMonthDTOS = new ArrayList<RevenueMonthDTO>();
        List<Integer> months = new ArrayList<Integer>();
        for (Month month : Month.values()) {
            months.add(month.getValue());
        }
        for (Integer month : months) {
            RevenueMonthDTO revenueMonthDTO = new RevenueMonthDTO();
            revenueMonthDTO.setMonth(month);
            List<Order> orders = this.orderRepository.findByYearAndMonth(year, month);
            BigDecimal total = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueMonthDTO.setTotal(total);
            revenueMonthDTO.setYear(year);
            revenueMonthDTOS.add(revenueMonthDTO);
        }
        model.addAttribute("dataPoints", revenueMonthDTOS);
        model.addAttribute("year", year);
        return "admin/revenue/month";
    }

}