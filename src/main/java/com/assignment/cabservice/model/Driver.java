package com.assignment.cabservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String password;

    @OneToOne(mappedBy = "driver")
    private Car car;
    private int assignedCarId;
    private boolean isDriverAvailable;
    private String firstName;
    private String lastName;
}
