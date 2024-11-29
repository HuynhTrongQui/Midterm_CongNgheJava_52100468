package com.project.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;
    private String originalPicture;
    private double price;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount; // Set default value to 0

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "product_image", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<ProductImage> images    ;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pro_man", inverseJoinColumns = @JoinColumn(name = "manufacture_id"), joinColumns = @JoinColumn(name = "product_id"))
    @ToString.Exclude
    private Set<Manufacture> manufacture;

    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    private String information;
    private int size;

    @OneToMany(mappedBy = "product_comment", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private Set<Comment> comments;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<CartItem> cartItems;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<OrderDetail> orderDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOriginalPicture() {
        return originalPicture;
    }

    public void setOriginalPicture(String originalPicture) {
        this.originalPicture = originalPicture;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<ProductImage> getImages() {
        return images;
    }

    public void setImages(Set<ProductImage> images) {
        this.images = images;
    }

    public Set<Manufacture> getManufacture() {
        return manufacture;
    }

    public void setManufacture(Set<Manufacture> manufacture) {
        this.manufacture = manufacture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    public Set<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(Set<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Transient
    public String getLogoImage() {
        if (originalPicture == null) {
            return null;
        }
        return "/upload/products/" + originalPicture;
    }
}
