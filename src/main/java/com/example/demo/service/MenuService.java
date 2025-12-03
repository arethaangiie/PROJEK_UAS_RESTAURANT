package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Menu;
import com.example.demo.repository.MenuRepository;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    // -------- READ ----------
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    public Menu getMenuById(String id) {
        return menuRepository.findById(id).orElse(null);
    }

    // -------- CREATE ----------
    public void addMenu(Menu menu) {
        menuRepository.save(menu);
    }

    // -------- UPDATE ----------
    public void updateMenu(String id, Menu updatedMenu) {
        Menu existingMenu = menuRepository.findById(id).orElse(null);

        if (existingMenu != null) {
            existingMenu.setNamaMenu(updatedMenu.getNamaMenu());
            existingMenu.setHarga(updatedMenu.getHarga());
            menuRepository.save(existingMenu);
        }
    }

    // -------- DELETE ----------
    public void deleteMenu(String id) {
        menuRepository.deleteById(id);
    }
}
