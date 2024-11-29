package com.project.spring.dto;


import com.project.spring.model.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationProductResponse {
    private List<Product> products;
    private Long numberOfItems;
    private int numberOfPages;
    private int numberTotalPages;
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	public Long getNumberOfItems() {
		return numberOfItems;
	}
	public void setNumberOfItems(Long numberOfItems) {
		this.numberOfItems = numberOfItems;
	}
	public int getNumberOfPages() {
		return numberOfPages;
	}
	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	public int getNumberTotalPages() {
		return numberTotalPages;
	}
	public void setNumberTotalPages(int numberTotalPages) {
		this.numberTotalPages = numberTotalPages;
	}
    // Builder pattern
    public static PaginationProductResponseBuilder builder() {
        return new PaginationProductResponseBuilder();
    }

    public static class PaginationProductResponseBuilder {
        private List<Product> products;
        private Long numberOfItems;
        private int numberOfPages;
        private int numberTotalPages;

        private PaginationProductResponseBuilder() {
            // private constructor to enforce the use of the builder() method
        }

        public PaginationProductResponseBuilder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public PaginationProductResponseBuilder numberOfItems(Long numberOfItems) {
            this.numberOfItems = numberOfItems;
            return this;
        }

        public PaginationProductResponseBuilder numberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        public PaginationProductResponseBuilder numberTotalPages(int numberTotalPages) {
            this.numberTotalPages = numberTotalPages;
            return this;
        }

        public PaginationProductResponse build() {
            PaginationProductResponse response = new PaginationProductResponse();
            response.setProducts(this.products);
            response.setNumberOfItems(this.numberOfItems);
            response.setNumberOfPages(this.numberOfPages);
            response.setNumberTotalPages(this.numberTotalPages);
            return response;
        }
    }
}
