package com.project.spring.controllers;

import com.project.spring.dto.CartDTO;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.dto.PasswordChangeRequest;
import com.project.spring.model.AppUser;
import com.project.spring.model.Cart;
import com.project.spring.repositories.CartRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.EmailService;
import com.project.spring.service.UserService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class PasswordController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public ModelAndView displayForgotPasswordPage() {
        return new ModelAndView("forgotpassword");
    }

    // Process form submission from forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public ModelAndView processForgotPasswordForm(ModelAndView modelAndView, @RequestParam("email") String userEmail, HttpServletRequest request) {
        Optional<AppUser> appUser = this.userService.findUserByEmail(userEmail);
        if (appUser.isEmpty()) {
            modelAndView.addObject("errorMessage", "We didn't find an account for that e-mail address.");
        } else {
            AppUser user = appUser.get();
            user.setResetToken(UUID.randomUUID().toString());
            this.userService.save(user);
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            // Email message
            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("nguyenhuyhoa2003@gmail.com");
            passwordResetEmail.setTo(user.getEmail());
            passwordResetEmail.setSubject("Password Reset Request");
            passwordResetEmail.setText("To reset your password, click the link below:" + appUrl + "/reset?token=" + user.getResetToken());
            emailService.sendEmail(passwordResetEmail);
            modelAndView.addObject("successMessage", "A password reset link has been sent to " + userEmail);
        }
        modelAndView.setViewName("forgotpassword");
        return modelAndView;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ModelAndView displayResetPasswordPage(ModelAndView modelAndView, @RequestParam("token") String token) {
        Optional<AppUser> appUser = userService.findUserByResetToken(token);
        if (appUser.isPresent()) {
            modelAndView.addObject("resetToken", token);
        } else {
            modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
            modelAndView.setViewName("forgotpassword");
            return modelAndView;
        }
        modelAndView.setViewName("resetPassword");
        return modelAndView;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ModelAndView setNewPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams) {

        // Find the user associated with the reset token
        Optional<AppUser> user = userService.findUserByResetToken(requestParams.get("token"));
        // This should always be non-null but we check just in case
        if (user.isPresent()) {
            AppUser resetUser = user.get();
            // Set new password
            resetUser.setPassword(bCryptPasswordEncoder.encode(requestParams.get("passwordNew")));
            //Set the reset token to null so it cannot be used again
            resetUser.setResetToken(null);
            // Save user
            userService.save(resetUser);
            // In order to set a model attribute on a redirect, we must use
            // RedirectAttributes
            modelAndView.addObject("successMessage", "You have successfully reset your password.  You may now login.");
            modelAndView.setViewName("login");
            return modelAndView;

        } else {
            modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
            modelAndView.setViewName("resetPassword");
        }
        return modelAndView;
    }

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ModelMapper modelMapper;
    @GetMapping("/change")
    public String changePass(Model model) {
         AppUser user = this.userRepository.getUserByUsername(this.userDetailsService.getCurrentUserId());
        if (user != null) {
            List<Cart> carts = this.cartRepository.findByUserId(user.getId());
            if (carts.isEmpty()) {
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setTotal(0.0);
                cart.setCartItems(new ArrayList<>());
                Cart newCart = this.cartRepository.save(cart);
                model.addAttribute("numberItems", newCart.getCartItems().size());
                model.addAttribute("idCart", newCart.getId());
            } else {
                Cart cart = carts.get(0);
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                List<CartItemDTO> cartItemDTOs = cartDTO.getCartItems();
                model.addAttribute("numberItems", cartItemDTOs.size());
                model.addAttribute("idCart", cart.getId());
            }
            model.addAttribute("isLogin", user.getName());
        }
        model.addAttribute("passwordRequest", new PasswordChangeRequest());
        return "changepassword";
    }

    @PostMapping("/change")
    public String changePassPost(Model model, @Valid @ModelAttribute("passwordRequest") PasswordChangeRequest passwordChangeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errorMessages);
            return "changepassword";
        }
        AppUser user = this.userRepository.getUserByUsername(this.userDetailsService.getCurrentUserId());
        if (bCryptPasswordEncoder.matches(passwordChangeRequest.getPasswordOld(), user.getPassword())) {
            user.setPassword(this.bCryptPasswordEncoder.encode(passwordChangeRequest.getPasswordNew()));
            this.userRepository.save(user);
            model.addAttribute("success", "password updated");
            model.addAttribute("passwordRequest", new PasswordChangeRequest());
            return "changepassword";
        } else {
            model.addAttribute("error", "Password old not match ???");
            return "changepassword";
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:login");
    }

}
