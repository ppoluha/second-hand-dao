package se.hkr.secondhand.model;

import java.time.LocalDate;

public record Furniture(long id, String color, String comment, String name, double price, LocalDate purchaseDate, int shelfNumber, double weight) {}
