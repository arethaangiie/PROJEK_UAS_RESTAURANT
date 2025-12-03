package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, String> {}
