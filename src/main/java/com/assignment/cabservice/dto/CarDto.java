package com.assignment.cabservice.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonFilter("CabFilter")
public class CarDto {

    private String carId;
    private String availableForBooking;
    private String name;
    private String model;
    private Integer seatingCapacity;
}
