package com.project.spring.controllers.admin;


import com.project.spring.model.AppUser;
import com.project.spring.model.Role;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.project.spring.model.Category;
import com.project.spring.repositories.CategoryRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryManamentController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @GetMapping("/category_manage")
    public String listCategory(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories/category_manage";
    }

    @GetMapping("/add")
    public String newCategory(Model model) {
        AppUser user = this.userRepository.getUserByUsername(userDetailsServiceImpl.getCurrentUserId());
        model.addAttribute("isAdmin", user.getName());


        model.addAttribute("category", new Category());
        return "admin/categories/add";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute Category category, Model model) {
        categoryRepository.save(category);
        return "redirect:/admin/categories/category_manage";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {


        model.addAttribute("category", categoryRepository.findById(id).get());
        return "admin/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, Model model, @ModelAttribute("category") Category category) {

        Category existed = categoryRepository.findById(id).get();
        existed.setName(category.getName());
        categoryRepository.save(existed);
        return "redirect:/admin/categories/category_manage";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories/category_manage";
    }

}