package com.assignment.cabservice.repository;

import com.assignment.cabservice.model.Car;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,Integer> {
    //List<Car> findBySeatingCapacityAndavailableForBooking(int seatingCapacity,boolean availableForBooking);
    @Query(nativeQuery = true, value = "Select * from car where seating_capacity = ?1 and available_for_booking = ?2")
    List<Car> findBySeatingCapacityAndAvailableForBooking(int seatingCapacity, String isAvailable);

    List<Car> findByIdIn(List<Integer> carIds);


    @Query(nativeQuery = true, value = "SELECT * FROM car WHERE id = ?1")
    List<Car> findByCarId(Integer id);

    @Query(nativeQuery = true, value = "SELECT * from car where available_for_booking = 'Y'")
    List<Car> findByAvailableForBooking();

    @Query(nativeQuery = true, value = "SELECT * FROM car WHERE available_for_booking = 'Y' LIMIT 1")
    List<Integer> findAllCarIdsAvaialableForBooking();
}
