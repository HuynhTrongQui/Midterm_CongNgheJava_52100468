package com.project.spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_image;
    private String url;
    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product_image;
	public int getId_image() {
		return id_image;
	}
	public void setId_image(int id_image) {
		this.id_image = id_image;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Product getProduct_image() {
		return product_image;
	}
	public void setProduct_image(Product product_image) {
		this.product_image = product_image;
	}
	public ProductImage() {
		super();
	}
}
