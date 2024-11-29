package com.project.spring.service.impl;

import com.project.spring.dto.OrderDetailDTO;
import com.project.spring.model.Order;
import com.project.spring.model.OrderDetail;
import com.project.spring.repositories.OrderDetailRepositopry;
import com.project.spring.repositories.OrderRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Autowired
    private OrderDetailRepositopry repository;
    @Autowired
    private OrderRepository orderRepository;

    public String exportReport(String reportFormat, Long id) throws FileNotFoundException, JRException {
        String path = "D:\\Report";
        double total = 0.0;
        Order order = this.orderRepository.findById(id).get();
        List<OrderDetail> orderDetails = order.getOrderDetails();

        List<OrderDetailDTO> employees = orderDetails.stream().map(orderDetail -> {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
            orderDetailDTO.setNameProduct(orderDetail.getProduct().getName());
            orderDetailDTO.setPriceProduct(orderDetail.getProduct().getPrice());
            orderDetailDTO.setQuantity(orderDetail.getQuantity());

            return orderDetailDTO;
        }).toList();

        for (OrderDetailDTO orderDetailDTO : employees) {
            double price = orderDetailDTO.getPriceProduct();
            int quantity = orderDetailDTO.getQuantity();
            total += price * quantity;
        }
        System.out.println(total
        );

        //load file and compile it
        File file = ResourceUtils.getFile("classpath:order.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("total", total);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        if (reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\order.html");
        }
        if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\order.pdf");
        }
        return "report generated in path : " + path;
    }
}