package se.hkr.secondhand;

import se.hkr.secondhand.dao.EmployeeDAO;
import se.hkr.secondhand.dao.OrderDAO;
import se.hkr.secondhand.model.Employee;
import se.hkr.secondhand.model.OrderHead;
import se.hkr.secondhand.model.OrderLine;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            EmployeeDAO employeeDAO = new EmployeeDAO(conn);
            OrderDAO orderDAO = new OrderDAO(conn);

            // Example: Create a new employee
            Employee newEmployee = new Employee(4, "123 Elm St", "New York", "John", "Doe", "10001");
            employeeDAO.createEmployee(newEmployee);

            // Example: Create a new order
            OrderHead newOrder = new OrderHead(-1, LocalDate.now(), 1, 1);
            List<OrderLine> lines = new ArrayList<>();
            lines.add(new OrderLine(-1, 1, -1, 3));
            orderDAO.createOrder(newOrder, lines);

            // Display all employees
            List<Employee> employees = employeeDAO.listAllEmployees();
            employees.forEach(System.out::println);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
