package com.project.spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product_comment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Product getProduct_comment() {
		return product_comment;
	}

	public void setProduct_comment(Product product_comment) {
		this.product_comment = product_comment;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Comment(Long id, int rating, String comment, Product product_comment, Date time) {
		super();
		this.id = id;
		this.rating = rating;
		this.comment = comment;
		this.product_comment = product_comment;
		this.time = time;
	}

	public Comment() {
		super();
	}
}
