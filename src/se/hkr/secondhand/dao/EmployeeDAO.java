package se.hkr.secondhand.dao;

import se.hkr.secondhand.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection conn;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
    }

    public void createEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (address, city, first_name, last_name, postal_code) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, employee.address());
            statement.setString(2, employee.city());
            statement.setString(3, employee.firstName());
            statement.setString(4, employee.lastName());
            statement.setString(5, employee.postalCode());
            statement.executeUpdate();
        }
    }

    public List<Employee> listAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, address, city, first_name, last_name, postal_code FROM employee";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(new Employee(
                        rs.getLong("id"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("postal_code")
                ));
            }
        }
        return employees;
    }
}
