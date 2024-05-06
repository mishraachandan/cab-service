package com.assignment.cabservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String password;
    @OneToOne(mappedBy = "driver")
    private Car car;
    private String usedCarIds;
    private boolean isDriverAvailable;
    private String firstName;
    private String lastName;
}
