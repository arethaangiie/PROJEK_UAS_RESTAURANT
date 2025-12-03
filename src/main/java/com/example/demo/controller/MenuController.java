package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Menu;
import com.example.demo.service.MenuService;

import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private MenuService menuService;

    // ------------------------- READ -------------------------
    @GetMapping({ "/menu", "/menu/{id}" })
    public String getMenuPage(Model model, @PathVariable(required = false) String id) {
        List<Menu> daftarMenu = menuService.getAllMenus();
        Menu menuEdit = (id != null) ? menuService.getMenuById(id) : new Menu();

        model.addAttribute("menuList", daftarMenu);
        model.addAttribute("menuInfo", menuEdit);

        return "menu.html";
    }

    // ------------------------- CREATE -------------------------
    @PostMapping(value = { "/menu/submit", "/menu/submit/{id}" }, params = "add")
    public String addMenu(@ModelAttribute("menuInfo") Menu menuInfo) {
        menuService.addMenu(menuInfo);
        return "redirect:/menu";
    }

    // ------------------------- UPDATE -------------------------
    @PostMapping(value = "/menu/submit/{id}", params = "edit")
    public String editMenu(@ModelAttribute("menuInfo") Menu menuInfo,
            @PathVariable("id") String id) {
        menuService.updateMenu(id, menuInfo);
        return "redirect:/menu";
    }

    // ------------------------- DELETE -------------------------
    @PostMapping(value = "/menu/submit/{id}", params = "delete")
    public String deleteMenu(@PathVariable("id") String id) {
        menuService.deleteMenu(id);
        return "redirect:/menu";
    }
}
