package se.hkr.secondhand;

import se.hkr.secondhand.dao.CustomerDAO;
import se.hkr.secondhand.dao.OrderDAO;
import se.hkr.secondhand.model.Customer;
import se.hkr.secondhand.model.OrderDetail;
import se.hkr.secondhand.model.OrderHead;
import se.hkr.secondhand.model.OrderLine;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            // Initialize DAOs
            CustomerDAO customerDao = new CustomerDAO(conn);
            OrderDAO orderDao = new OrderDAO(conn);

            // Example: Create a new customer
            Customer newCustomer = new Customer(0, "Lindhagensgatan 76", LocalDate.of(1992, 7, 21), "Stockholm", "Lisa", "Nilsson", "112 18");
            customerDao.createCustomer(newCustomer);

            // Fetch all customers and print them
            System.out.println("All Customers:");
            List<Customer> customers = customerDao.listAllCustomers();
            customers.forEach(System.out::println);

            // Example: Create a new order with order lines for an existing employee and customer
            // Assuming IDs for employee and customer are known and valid
            OrderHead newOrder = new OrderHead(0, LocalDate.now(), 1, 1); // customer ID and employee ID need to exist in DB
            List<OrderLine> orderLines = List.of(
                    new OrderLine(0, 1, 0, 2), // furniture ID and quantity
                    new OrderLine(0, 2, 0, 1)
            );
            orderDao.createOrder(newOrder, orderLines);

            // Fetch all orders for a specified employee and print them
            System.out.println("All Orders for Employee 1:");
            List<OrderHead> orders = orderDao.listOrdersForEmployee(1);
            orders.forEach(order -> System.out.println(order));

            // Fetch all orders with customer names for a specified employee
            System.out.println("All Orders with Customer Names for Employee 1:");
            List<OrderDetail> detailedOrdersWithCustomerNames = orderDao.listOrdersWithCustomerNameForEmployee(1);
            detailedOrdersWithCustomerNames.forEach(order -> System.out.println(order));

            // Fetch all orders with details for a specified employee
            System.out.println("All Detailed Orders for Employee 1:");
            List<OrderDetail> detailedOrders = orderDao.listOrdersWithDetailsForEmployee(1);
            detailedOrders.forEach(order -> {
                System.out.println("Order ID: " + order.order().id() + ", Customer Name: " + order.customerName());
                order.lineDetails().forEach(line -> System.out.println("  Line: " + line.line().quantity() + "x " + line.furnitureName()));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
