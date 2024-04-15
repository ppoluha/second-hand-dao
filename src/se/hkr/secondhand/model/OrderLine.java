package se.hkr.secondhand.model;

public record OrderLine(long id, long furnitureId, long orderId, int quantity) {}
