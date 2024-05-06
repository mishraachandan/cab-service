package com.assignment.cabservice.repository;

import com.assignment.cabservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver,Integer> {
    @Query(nativeQuery = true, value = "Select assigned_car_id from driver")
    List<String> getAllAssignedIds();

    Driver findByUsername(String username);
}
