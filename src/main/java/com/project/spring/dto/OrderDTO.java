package com.project.spring.dto;

import com.project.spring.enums.OrderStatus;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long idOrder;
    private Long idUser;
    private BigDecimal total;

    @Temporal(TemporalType.DATE)
    private Date date;

    private List<OrderDetailDTO> orderDetails;
    private String nameUser;
    private OrderStatus status;
    private String email;
    private String phone;
    private String address;
	public Long getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(Long idOrder) {
		this.idOrder = idOrder;
	}
	public Long getIdUser() {
		return idUser;
	}
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<OrderDetailDTO> getOrderDetails() {
		return orderDetails;
	}
	public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
		this.orderDetails = orderDetails;
	}
	public String getNameUser() {
		return nameUser;
	}
	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public OrderDTO(Long idOrder, Long idUser, BigDecimal total, Date date, List<OrderDetailDTO> orderDetails,
			String nameUser, OrderStatus status, String email, String phone, String address) {
		super();
		this.idOrder = idOrder;
		this.idUser = idUser;
		this.total = total;
		this.date = date;
		this.orderDetails = orderDetails;
		this.nameUser = nameUser;
		this.status = status;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}
	public OrderDTO() {
		super();
	}
}
