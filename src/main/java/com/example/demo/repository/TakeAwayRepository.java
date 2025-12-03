package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Takeaway;

public interface TakeAwayRepository extends JpaRepository <Takeaway, Long> {}