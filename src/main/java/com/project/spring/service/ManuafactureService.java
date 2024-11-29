package com.project.spring.service;

import org.springframework.stereotype.Service;

import com.project.spring.model.Manufacture;


@Service
public interface ManuafactureService {
    public Manufacture saveAndUpdate(Manufacture manufacture);
    public Manufacture getManufactureById(Long id);
}