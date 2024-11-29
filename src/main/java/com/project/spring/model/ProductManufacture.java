package com.project.spring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "pro_man")
@Data
public class ProductManufacture implements Serializable {

    @EmbeddedId
    private ProductManufactureID id;
    @ManyToOne
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    private Product product_id;

    @ManyToOne
    @MapsId("manufacture_id")
    @JoinColumn(name = "manufacture_id")
    private Manufacture manufacture_id;

	public ProductManufactureID getId() {
		return id;
	}

	public void setId(ProductManufactureID id) {
		this.id = id;
	}

	public Product getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Product product_id) {
		this.product_id = product_id;
	}

	public Manufacture getManufacture_id() {
		return manufacture_id;
	}

	public void setManufacture_id(Manufacture manufacture_id) {
		this.manufacture_id = manufacture_id;
	}

}
