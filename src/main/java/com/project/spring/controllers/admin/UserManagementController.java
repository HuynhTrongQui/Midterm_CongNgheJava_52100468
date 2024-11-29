package com.project.spring.controllers.admin;

import com.project.spring.model.AppUser;
import com.project.spring.model.Role;
import com.project.spring.repositories.RoleRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.UserManagementService;
import com.project.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @GetMapping
    public String listUsers(Model model) {
        /*List<AppUser> users = userService.getAllUsers().stream().filter(appUser -> !appUser.isHide()).toList();*/
        List<AppUser> users = userRepository.findAll().stream().filter(appUser -> !appUser.isHide()).toList();
        model.addAttribute("users", users);
        return "admin/user/user-list";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new AppUser());
        List<Role> roles = this.roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("title", "Thêm người dùng.");
        return "admin/user/user-form";
    }


    @PostMapping("/add")
    public String addUser(@ModelAttribute AppUser user, Model model) {
        if (this.userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("username", "Tên đăng nhập đã tồn tại.");
            return "admin/user/user-form";
        }
        if (this.userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("email", "Email đã tồn tại");
            return "admin/user/user-form";
        }
        AppUser appUser = new AppUser();
        appUser.setName(user.getName());
        appUser.setUsername(user.getUsername());
        appUser.setEmail(user.getEmail());
        appUser.setPhoneNumber(user.getPhoneNumber());
        appUser.setAddress(user.getAddress());
        appUser.setRoles(user.getRoles());
        this.userRepository.save(appUser);
        return "admin/user/user-form";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Thay đổi thông tin người dùng.");
        model.addAttribute("user", this.userRepository.findById(id).get());
        model.addAttribute("roles", this.roleRepository.findAll());
        return "admin/user/edit";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute AppUser user, Model model, @RequestParam("id") Long idUser, RedirectAttributes redirectAttributes) {
        Optional<AppUser> appUserUserName = this.userRepository.findByUsername(user.getUsername());
        if (appUserUserName.isPresent() && !Objects.equals(appUserUserName.get().getId(), idUser)) {
            redirectAttributes.addFlashAttribute("username", "Tên đăng nhập đã tồn tại.");
            return "redirect:/admin/users/edit/" + idUser;
        }
        Optional<AppUser> appUserEmail = this.userRepository.findByEmail(user.getEmail());
        if (appUserEmail.isPresent() && !appUserEmail.get().getId().equals(idUser)) {
            redirectAttributes.addFlashAttribute("email", "Email đã tồn tại");
            return "redirect:/admin/users/edit/" + idUser;
        }
        AppUser appUser = this.userRepository.findById(idUser).get();
        appUser.setEmail(user.getEmail());
        appUser.setUsername(user.getUsername());
        appUser.setGender(user.isGender());
        appUser.setPhoneNumber(user.getPhoneNumber());
        appUser.setAddress(user.getAddress());
        appUser.setEnable(user.isEnable());
        appUser.setRoles(user.getRoles());
        System.out.println(user.getRoles().size());
        AppUser appUser1 = this.userRepository.save(appUser);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/admin/users/edit/" + idUser;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        AppUser user = this.userRepository.findById(id).get();
        user.setEnable(false);
        user.setHide(true);
        this.userRepository.save(user);

        return "redirect:/admin/users";
    }

    @PostMapping("/isEnable")
    public String block(@RequestParam("id") String s) {
        Long id = Long.parseLong(s);
        AppUser user = this.userRepository.findById(id).get();
        this.userRepository.blocked(id);
        return "redirect:/admin/users";
    }
}