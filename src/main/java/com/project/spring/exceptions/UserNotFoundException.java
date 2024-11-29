package com.project.spring.exceptions;


public class UserNotFoundException extends RuntimeException {
	 public UserNotFoundException(Long id) {
		// TODO Auto-generated constructor stub
		 super("Could not find user " + id);
	}	
}
