package com.assignment.cabservice.controller;

import com.assignment.cabservice.dao.BookingDetailDao;
import com.assignment.cabservice.exception.CarNotFoundException;
import com.assignment.cabservice.exception.InvalidSeatingCapacityException;
import com.assignment.cabservice.model.Booking;
import com.assignment.cabservice.model.Car;
import com.assignment.cabservice.repository.BookingRepository;
import com.assignment.cabservice.repository.CarRepository;
import com.assignment.cabservice.requests.BookCarRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
//@SessionAttributes({"username","id"})
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;

    //http://localhost:8080/book-car?carId=503&username=cust1
    @PostMapping(value = "book-car", consumes = "application/json")
//    @ResponseBody
    public ResponseEntity<BookingDetailDao> bookCar(@RequestBody BookCarRequest bookCarRequest) throws  CarNotFoundException {

        List<Car> car= carRepository.findByCarId(bookCarRequest.getCarId());
        BookingDetailDao bookingDetailDao;

        if(car.isEmpty()){
            throw  new CarNotFoundException("Car not found for the particular Car Id. Please check and give the correct Id.");
        }
        else{
            car.get(0).setAvailableForBooking(false);
            Booking newBooking = new Booking();
            newBooking.setCarId(bookCarRequest.getCarId());
            newBooking.setDriverId(car.get(0).getDriverId());
            newBooking.setStatus("Booked");
            newBooking.setUsername(bookCarRequest.getUserName());
            carRepository.save(car.get(0));
            bookingRepository.save(newBooking);
            String cancelCarUrl="localhost:8080/cancel-car?bookingId="+newBooking.getId();
            bookingDetailDao=new BookingDetailDao(newBooking,cancelCarUrl);
        }

        return new ResponseEntity<>(bookingDetailDao, HttpStatus.OK);
    }

    @RequestMapping("cancel-car")
    public ResponseEntity<Object> cancelCar(@RequestParam int bookingId,ModelMap modelMap) {
        Optional<Booking> bookingOptional=bookingRepository.findById(bookingId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();
        // Booking doesn't exist or not the same user
        if(bookingOptional.isEmpty() || !bookingOptional.get().getUsername().equals((loggedInUsername))) {
            return new ResponseEntity<>("<h1>BAD REQUEST</h1>", HttpStatus.BAD_REQUEST);
        }

        Booking booking=bookingOptional.get();
        Car car=carRepository.findById(booking.getCarId()).get();
        car.setAvailableForBooking(true);
        carRepository.save(car);
        bookingRepository.deleteById(bookingId);
        return new ResponseEntity<>("<h1>Booking Canceled Successfully</h1>", HttpStatus.OK);
    }
}
