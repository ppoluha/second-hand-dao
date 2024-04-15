package se.hkr.secondhand.model;

import java.time.LocalDate;

public record Customer(long id, String address, LocalDate birthDate, String city, String firstName, String lastName, String postalCode) {}
