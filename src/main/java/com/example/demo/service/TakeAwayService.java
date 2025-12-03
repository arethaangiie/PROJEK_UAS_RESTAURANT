package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Takeaway;
import com.example.demo.repository.TakeAwayRepository;

@Service
public class TakeAwayService {
    @Autowired
    private TakeAwayRepository takeAwayRepository;

    public List<Takeaway> getAll() {
        return takeAwayRepository.findAll();
    }

    public Takeaway add(Takeaway obj) {
        return takeAwayRepository.save(obj);
    }

    public Takeaway getById(long id) {
        return takeAwayRepository.findById(id).orElse(null);
    }

    public Takeaway update(long id, Takeaway newData) {
        Takeaway old = takeAwayRepository.findById(id).orElse(null);
        if (old == null)
            return null;

        old.setPackagingType(newData.getPackagingType());
        old.setPickupTime(newData.getPickupTime());

        return takeAwayRepository.save(old);
    }

    public void delete(long id) {
        takeAwayRepository.deleteById(id);
    }
}
