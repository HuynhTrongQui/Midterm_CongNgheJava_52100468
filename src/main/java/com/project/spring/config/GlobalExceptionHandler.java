package com.project.spring.config;

import com.project.spring.exceptions.ProductNotFoundException;
import com.project.spring.exceptions.UserEnableException;
import com.project.spring.exceptions.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView handleMyCustomException(ProductNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler(UserEnableException.class)
    public ModelAndView handleUserEnable(UserEnableException userEnableException){
        ModelAndView modelAndView = new ModelAndView("/login");
        modelAndView.addObject("isEnable",true);
        return  modelAndView;
    }
    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundException ex) {
      return ex.getMessage();
    }
}
