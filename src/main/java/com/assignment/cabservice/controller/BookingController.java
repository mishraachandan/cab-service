package com.assignment.cabservice.controller;

import com.assignment.cabservice.dao.BookingDetailDao;
import com.assignment.cabservice.dto.CarDto;
import com.assignment.cabservice.exception.CarNotFoundException;
import com.assignment.cabservice.exception.InvalidSeatingCapacityException;
import com.assignment.cabservice.model.Booking;
import com.assignment.cabservice.model.Car;
import com.assignment.cabservice.model.Driver;
import com.assignment.cabservice.repository.BookingRepository;
import com.assignment.cabservice.repository.CarRepository;
import com.assignment.cabservice.repository.DriverRepository;
import com.assignment.cabservice.requests.BookCarRequest;
import com.assignment.cabservice.service.CacheService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
//@SessionAttributes({"username","id"})
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CacheService cacheService;

    //http://localhost:8080/book-car?carId=503&username=cust1
    @PostMapping(value = "book-car", consumes = "application/json")
//    @ResponseBody
    public ResponseEntity<BookingDetailDao> bookCar(@RequestBody BookCarRequest bookCarRequest) throws  CarNotFoundException {

        // requirement :::
        // get the number of seater, budget, isavailable for booking


        List<Car> car= carRepository.findByCarId(bookCarRequest.getCarId());
        BookingDetailDao bookingDetailDao;

//        Book car>

//        To book the car >>
//
//        1st thing to check >> If car is available?
//
//        If car is available we will book it and then assign driver to it.
//
//        But we can only confirm the booking if the driver is available as of now.
//
//        if not available then give exception message.
//
//        If the driver is available then book the car and assign the driver to that car.



        // If noo car is available.
        if(!car.isEmpty() && car.get(0) != null){
            boolean isBooked = car.get(0).getAvailableForBooking().equalsIgnoreCase("N") ? true : false;
            if(isBooked){
                throw new CarNotFoundException("Sorry, " + bookCarRequest.getUserName() + " this is not available for " +
                        "booking as it is already occupied. Please check with different cars/cabs available. Sorry, " +
                        "for inconveniece.");
            }
        }

        // If the car is not present.
        if(car.isEmpty()){
            throw new CarNotFoundException("Car not found for the particular Car Id. Please check and give the correct Id.");
        }
        else{

            // proceed with the booking.
            if(car.get(0) != null)
            car.get(0).setAvailableForBooking("N");


            Booking newBooking = new Booking();
            newBooking.setCarId(bookCarRequest.getCarId());

            // to set the driver.

            // to get the driver id.

            // TODO : will need to optimise this at a later point of time.
            List<Driver> availableDrivers =  driverRepository.findAll();
            Driver newDriver = null;
            if(availableDrivers.isEmpty()){
                // Create a new driver
                newDriver = Driver.builder().firstName("ContractDriver").lastName("").isDriverAvailable(false)
                        .car(car.get(0)).username("subcon").password("temp").assignedCarId(car.get(0).getId()).
                        build();
                driverRepository.save(newDriver);
            }
            else{
                // check for the drivers who are available.
                for(Driver driver : availableDrivers){
                    if(driver.isDriverAvailable()){
                        newDriver = driver;
                    }
                }

                if(newDriver == null){
                    newDriver = Driver.builder().firstName("ContractDriver").lastName("").isDriverAvailable(false)
                            .car(car.get(0)).username("subcon").password("temp").assignedCarId(car.get(0).getId()).
                            build();
                    driverRepository.save(newDriver);
                }
            }
            newBooking.setDriverId(newDriver.getId());
            newBooking.setStatus("Booked");
            newBooking.setUsername(bookCarRequest.getUserName());
            if(car.get(0).getDriver().getId() != newDriver.getId() ){
                car.get(0).setDriver(newDriver);
            }
            carRepository.save(car.get(0));
            bookingRepository.save(newBooking);
            String cancelCarUrl="localhost:8080/cancel-car?bookingId="+newBooking.getId();
            bookingDetailDao=new BookingDetailDao(newBooking,cancelCarUrl);
        }
        return new ResponseEntity<>(bookingDetailDao, HttpStatus.OK);
    }

    @GetMapping(value = "/allCabs")
    @Cacheable("allCabs")
    public ResponseEntity<MappingJacksonValue> getAllCabsAvailable(){
        List<Car> carList = carRepository.findByAvailableForBooking();
        if(carList.isEmpty()){
            throw new CarNotFoundException("Sorry, currently we don't have any cabs available for booking.");
        }
        List<CarDto> carDtos = new ArrayList<>();
        for(Car car : carList){
            carDtos.add(CarDto.builder().name(car.getName()).model(car.getModel()).seatingCapacity(car.getSeatingCapacity())
                    .availableForBooking(car.getAvailableForBooking()).build());
        }

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(carDtos);

        SimpleBeanPropertyFilter filter =
                SimpleBeanPropertyFilter.filterOutAllExcept("name", "model", "seatingCapacity");

        FilterProvider filters = new SimpleFilterProvider().addFilter("CabFilter", filter);
        mappingJacksonValue.setFilters(filters);

        return new ResponseEntity<>(mappingJacksonValue, HttpStatus.OK);
    }

    @GetMapping(value = "/availableCabs")
    public ResponseEntity<String> getAvailableDesiredVehicle(@RequestParam int numberOfSeater){
        if(numberOfSeater == 0 || numberOfSeater > 8){
            throw new CarNotFoundException("Please enter the correct seat capacity that is required. We only serve for 5" +
                    ",3,8 and 2 seater bookings");
        }
        String result = getStringResponseEntity(numberOfSeater);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Cacheable(value = "availableCabs", key = "#numberOfSeater")
    private String getStringResponseEntity(int numberOfSeater) {
        List<Car> carList = carRepository.findBySeatingCapacityAndAvailableForBooking(numberOfSeater, "Y");

        List<CarDto> carDtos = new ArrayList<>();
        for(Car car : carList){
            CarDto carDto = CarDto.builder().name(car.getName()).name(car.getName()).availableForBooking(car.getAvailableForBooking())
                    .model(car.getModel()).seatingCapacity(car.getSeatingCapacity()).carId(String.valueOf(car.getId())).build();
            carDtos.add(carDto);
        }
        if(carList.isEmpty()){
            return "Sorry, no cars are available for the desired selection. Please try" +
                    " with a different seating capacity.";
        }

        String result = carDtos.stream()
                .map(CarDto::getName) // Assuming CarDTO has overridden toString() method
                .collect(Collectors.joining("\n")); // Join elements with newline separator

        return "Here is the list of available cabs for booking for the desired seat selection" +
                ":\n" + result;
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
        car.setAvailableForBooking("Y");
        carRepository.save(car);
        bookingRepository.deleteById(bookingId);
        return new ResponseEntity<>("<h1>Booking Canceled Successfully</h1>", HttpStatus.OK);
    }

    @GetMapping(value = "/test")
    public ResponseEntity<String> getCaches(){
        System.out.println("Printing the cache in console:: "+cacheService.getAllCacheEntries().toString());
        return new ResponseEntity<>(cacheService.getAllCacheEntries().toString(), HttpStatus.OK);
    }
}
