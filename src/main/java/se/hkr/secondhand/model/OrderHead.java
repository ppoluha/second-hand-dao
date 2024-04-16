package se.hkr.secondhand.model;

import java.time.LocalDate;

public record OrderHead(long id, LocalDate orderDate, long customerId, long employeeId) {}
