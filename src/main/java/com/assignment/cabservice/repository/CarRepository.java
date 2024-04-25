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
    List<Car> findBySeatingCapacityAndAvailableForBookingTrue(int seatingCapacity);

    List<Car> findByIdIn(List<Integer> carIds);


    @Query(nativeQuery = true, value = "SELECT * FROM car WHERE id = ?1")
    List<Car> findByCarId(Integer id);

}
