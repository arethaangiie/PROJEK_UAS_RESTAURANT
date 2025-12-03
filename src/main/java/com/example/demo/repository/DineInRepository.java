package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DineIn;

public interface DineInRepository extends JpaRepository <DineIn, Long> {}