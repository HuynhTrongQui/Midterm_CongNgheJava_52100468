package com.project.spring.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.project.spring.model.Manufacture;

import com.project.spring.repositories.ManufactureRepository;
import com.project.spring.service.ManuafactureService;

@Controller
@RequestMapping("/admin/manufactures")
public class ProducerManamentController {
    @Autowired
    private ManufactureRepository manufactureRepository;
    @Autowired
    private ManuafactureService manufactureServiceImpl;

    @GetMapping("/producer_manage")
    public String listProducer(Model model) {

        model.addAttribute("manufactures", manufactureRepository.findAll());
        return "admin/manufactures/producer_manage";
    }
    @GetMapping("/add")
    public String newManufacture(Model model) {
        model.addAttribute("manufacture", new Manufacture());
        return "admin/manufactures/add";
    }
    @PostMapping("/add")
    public String save(@ModelAttribute Manufacture manufacture) {
        manufactureServiceImpl.saveAndUpdate(manufacture);
        return "redirect:/admin/manufactures/producer_manage";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("manufacture", manufactureServiceImpl.getManufactureById(id));
        return "admin/manufactures/edit";
    }


    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,Model model, @ModelAttribute("manufacture") Manufacture m) {
        Manufacture existingManufacture = manufactureServiceImpl.getManufactureById(id);
        existingManufacture.setName(m.getName());
        manufactureRepository.save(existingManufacture);
        return "redirect:/admin/manufactures/producer_manage";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        manufactureRepository.deleteById(id);
        return "redirect:/admin/manufactures/producer_manage";
    }


}