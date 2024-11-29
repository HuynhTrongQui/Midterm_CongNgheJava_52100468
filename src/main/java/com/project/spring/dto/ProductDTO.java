package com.project.spring.dto;

import com.project.spring.model.Category;
import com.project.spring.model.Manufacture;
import com.project.spring.model.ProductImage;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Set;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String color;
    private String originalPicture;
    private double price;
    private String nameCategory;
    private Set<String> manufacture;
    private String information;
    private String description;
    private Long viewCount;
    public ProductDTO() {
        this.viewCount = 0L; // Set the default value to 0
    }
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
	public String getNameCategory() {
		return nameCategory;
	}
	public void setNameCategory(String nameCategory) {
		this.nameCategory = nameCategory;
	}
	public Set<String> getManufacture() {
		return manufacture;
	}
	public void setManufacture(Set<String> manufacture) {
		this.manufacture = manufacture;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getViewCount() {
		return viewCount;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
}
