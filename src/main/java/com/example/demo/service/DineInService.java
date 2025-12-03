package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.DineIn;
import com.example.demo.repository.DineInRepository;

@Service
public class DineInService {
    @Autowired
    private DineInRepository dineInRepository;

    public List<DineIn> getAll() {
        return dineInRepository.findAll();
    }

    public DineIn add(DineIn obj) {
        return dineInRepository.save(obj);
    }

    public DineIn getById(long id) {
        return dineInRepository.findById(id).orElse(null);
    }

    public DineIn update(long id, DineIn newData) {
        DineIn old = dineInRepository.findById(id).orElse(null);
        if (old == null)
            return null;

        old.setSitNo(newData.getSitNo());
        old.setPax(newData.getPax());

        return dineInRepository.save(old);
    }

    public void delete(long id) {
        dineInRepository.deleteById(id);
    }
}
