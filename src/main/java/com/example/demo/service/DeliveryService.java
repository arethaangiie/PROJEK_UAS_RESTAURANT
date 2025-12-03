package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Delivery;
import com.example.demo.repository.DeliveryRepository;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
    }

    public Delivery add(Delivery obj) {
        return deliveryRepository.save(obj);
    }

    public Delivery getById(long id) {
        return deliveryRepository.findById(id).orElse(null);
    }

    public Delivery update(long id, Delivery newData) {
        Delivery old = deliveryRepository.findById(id).orElse(null);
        if (old == null)
            return null;

        old.setLokasiPengantaran(newData.getLokasiPengantaran());
        old.setMetodePengantaran(newData.getMetodePengantaran());

        return deliveryRepository.save(old);
    }

    public void delete(long id) {
        deliveryRepository.deleteById(id);
    }

}
