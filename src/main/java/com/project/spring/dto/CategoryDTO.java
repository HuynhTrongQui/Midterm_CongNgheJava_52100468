package com.project.spring.dto;

import com.project.spring.model.Manufacture;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Long id;

	private String name;
    private Integer quantity;
    private String url;
    private List<ManufactureDTO> manufactures;
    public CategoryDTO() {
		super();
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
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<ManufactureDTO> getManufactures() {
		return manufactures;
	}
	public void setManufactures(List<ManufactureDTO> manufactures) {
		this.manufactures = manufactures;
	}
}