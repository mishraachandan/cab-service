package com.assignment.cabservice.controller;

import com.assignment.cabservice.dao.DriverUseCarsDao;
import com.assignment.cabservice.dto.DriverDto;
import com.assignment.cabservice.exception.DriverAlreadyAvailableException;
import com.assignment.cabservice.model.Car;
import com.assignment.cabservice.model.CarRequest;
import com.assignment.cabservice.model.Driver;
import com.assignment.cabservice.repository.CarRepository;
import com.assignment.cabservice.repository.CarRequestRepository;
import com.assignment.cabservice.repository.DriverRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class DriverController {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarRequestRepository carRequestRepository;

    @RequestMapping("list-drivers")
    public String listAllDrivers(ModelMap modelMap) {
        List<Driver> drivers=driverRepository.findAll();
        modelMap.put("drivers",drivers);
        return "listDrivers";
    }

//    @GetMapping("driver/used-cars")
//    @ResponseBody
//    public DriverUseCarsDao getCarsUsedByDriver(@RequestParam int driverId) throws Exception {
//        Optional<Driver> driverOptional=driverRepository.findById(driverId);
//        if(driverOptional.isPresent()) {
//            Driver driver=driverOptional.get();
//            String[] usedCars=driver.getAssignedCarId().split(",");
//            List<Integer> carIds=new ArrayList<>();
//            for(String cardId:usedCars) {
//                carIds.add(Integer.parseInt(cardId));
//            }
//
//            List<Car> carList=carRepository.findByIdIn(carIds);
//            DriverUseCarsDao driverUseCarsDao=new DriverUseCarsDao(driverId,driver.getUsername(),carList);
//
//            return driverUseCarsDao;
//        }
//
//        throw new Exception("Driver not found");
//    }

//    @RequestMapping(value="add-driver",method= RequestMethod.GET)
//    public String showNewDriverPage(Driver driver) {
//        return "driver";
//    }

    //public String addNewTodo(@Valid Todo todo, ModelMap modelMap, BindingResult bindingResult) {
    @PostMapping(value="add-driver", consumes = "application/json")
    public ResponseEntity<String> addNewDriver(@RequestBody DriverDto driverDto) throws DriverAlreadyAvailableException {

        Driver isExistingDriver = driverRepository.findByUsername(driverDto.getUsername());
        if(isExistingDriver != null && !isExistingDriver.getUsername().equalsIgnoreCase("subcon")){
            throw new DriverAlreadyAvailableException("Sorry, please use a different username " +
                    "this username is already taken by different user.");
        }
        Driver driver = Driver.builder().password(driverDto.getUsername() + "@" + generateRandomString(4))
                .username(driverDto.getUsername()).firstName(driverDto.getFirstName()).lastName(driverDto.getLastName())
                .isDriverAvailable(true).build();
        driverRepository.save(driver);

        return new ResponseEntity<>(String.format("Welcome, %s! you details are added. We will inform you for " +
                "your assigned car once new booking comes.", driverDto.getUsername()), HttpStatus.OK);
    }
    //http://localhost:8080/delete-driver?id=102
    @RequestMapping(value="delete-driver")
    public String deleteDriver(@RequestParam int id) throws Exception {
        Driver driver=driverRepository.findById(id).orElseThrow(() ->
                new Exception("Driver not found with driverID - " + id));
        Car car=carRepository.findById(driver.getCar().getId()).orElseThrow(() ->
                new Exception("Car not found with carID - " + driver.getCar().getId()));
        car.setAvailableForBooking("Y");
        car.setDriver(Driver.builder().build());
        carRepository.save(car);
        driverRepository.deleteById(id);
        return "redirect:list-drivers";
    }

    //http://localhost:8080/request-car?driverId=102&carId=402
    @GetMapping(value="request-car")
    public String requestNewCar(@RequestParam int driverId,@RequestParam int carId) {
        CarRequest newCarRequest=new CarRequest();
        newCarRequest.setDriverId(driverId);
        newCarRequest.setCarId(carId);
        newCarRequest.setRequestStatus("PENDING");
        carRequestRepository.save(newCarRequest);
        return "redirect:list-car-requests";
    }




    // Method to generate a random string of specified length
    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
