package se.hkr.secondhand.dao;

import se.hkr.secondhand.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO(Connection conn) {
        this.conn = conn;
    }

    public void createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (address, birth_date, city, first_name, last_name, postal_code) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, customer.address());
            statement.setDate(2, Date.valueOf(customer.birthDate()));
            statement.setString(3, customer.city());
            statement.setString(4, customer.firstName());
            statement.setString(5, customer.lastName());
            statement.setString(6, customer.postalCode());
            statement.executeUpdate();
        }
    }

    public List<Customer> listAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, address, birth_date, city, first_name, last_name, postal_code FROM customer";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getLong("id"),
                        rs.getString("address"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getString("city"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("postal_code")
                ));
            }
        }
        return customers;
    }
}
