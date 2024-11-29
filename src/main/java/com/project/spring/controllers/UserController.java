package com.project.spring.controllers;

import com.project.spring.model.AppUser;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.UserService;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import com.project.spring.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
//@RequestMapping("/account")
public class UserController {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/uploads/users/";

    @GetMapping("/account")
    public String profileUser(Model model) {
        AppUser appUser = this.userRepository.getUserByUsername(this.userDetailsService.getCurrentUserId());
        model.addAttribute("isLogin", appUser.getName());
        model.addAttribute("userRequest", appUser);
        return "profile";
    }

    @PostMapping("/account")
    public String updateInfo(@Valid @ModelAttribute("userRequest") AppUser userRequest,
                             BindingResult bindingResult,
                             @RequestParam("image") MultipartFile multipartFile,
                             Model model, RedirectAttributes redirectAttributes
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errorMessages);
            return "profile";
        }
        AppUser user = this.userRepository.getUserByUsername(this.userDetailsService.getCurrentUserId());
        if (!multipartFile.isEmpty()) {
            /*StringBuilder fileNames = new StringBuilder();
            String fileName = multipartFile.getOriginalFilename();
            Path uploadPath = Path.of(UPLOAD_DIRECTORY, fileName);
            Files.write(uploadPath, multipartFile.getBytes());
            user.setPhoto(fileName);*/
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = FileUploadUtil.generateUniqueFileName(fileExtension);
            user.setPhoto(uniqueFileName);
            this.userRepository.save(user);
            String uploadDir = "./upload/users/";
            FileUploadUtil.saveFile(uploadDir, uniqueFileName, multipartFile);
        }
//        BeanUtils.copyProperties(userRequest, user, getNullPropertyNames(userRequest));
        redirectAttributes.addFlashAttribute("success", "success");
        return "redirect:/account";
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
