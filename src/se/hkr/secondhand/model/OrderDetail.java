package se.hkr.secondhand.model;

import java.util.ArrayList;
import java.util.List;

public record OrderDetail(OrderHead order, String customerName, List<OrderLineDetail> lineDetails) {
    public OrderDetail {
        lineDetails = new ArrayList<>(lineDetails);
    }
}
