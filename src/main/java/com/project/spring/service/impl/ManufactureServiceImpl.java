package com.project.spring.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.spring.model.Manufacture;

import com.project.spring.repositories.ManufactureRepository;
import com.project.spring.service.ManuafactureService;

@Service
public class ManufactureServiceImpl implements ManuafactureService {
    @Autowired
    private ManufactureRepository mr;

    @Override
    public Manufacture saveAndUpdate(Manufacture manufacture) {
        return mr.save(manufacture);
    }

    public Manufacture getManufactureById(Long id) {
        List<Manufacture> list = mr.findAll();
        for (Manufacture m : list) {
            if (m.getId() == id)
                return m;
        }
        return null;

    }
}