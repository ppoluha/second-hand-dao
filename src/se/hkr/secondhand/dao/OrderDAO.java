package se.hkr.secondhand.dao;

import se.hkr.secondhand.model.OrderDetail;
import se.hkr.secondhand.model.OrderHead;
import se.hkr.secondhand.model.OrderLine;
import se.hkr.secondhand.model.OrderLineDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {
    private final Connection conn;

    public OrderDAO(Connection conn) {
        this.conn = conn;
    }

    public void createOrder(OrderHead order, List<OrderLine> orderLines) throws SQLException {
        String sqlOrder = "INSERT INTO order_head (order_date, customer_id, employee_id) VALUES (?, ?, ?)";
        String sqlOrderLine = "INSERT INTO order_line (furniture_id, order_id, quantity) VALUES (?, ?, ?)";
        try {
            conn.setAutoCommit(false);
            long orderId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, java.sql.Date.valueOf(order.orderDate()));
                ps.setLong(2, order.customerId());
                ps.setLong(3, order.employeeId());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    orderId = rs.next() ? rs.getLong(1) : -1;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlOrderLine)) {
                for (OrderLine line : orderLines) {
                    ps.setLong(1, line.furnitureId());
                    ps.setLong(2, orderId);
                    ps.setInt(3, line.quantity());
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<OrderHead> listOrdersForEmployee(int employeeId) throws SQLException {
        List<OrderHead> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_head WHERE employee_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new OrderHead(rs.getLong("id"), rs.getDate("order_date").toLocalDate(), rs.getLong("customer_id"), rs.getLong("employee_id")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
        return orders;
    }

    public List<OrderDetail> listOrdersWithCustomerNameForEmployee(int employeeId) throws SQLException {
        List<OrderDetail> orders = new ArrayList<>();
        String sql = """
                SELECT oh.id, oh.order_date, oh.customer_id, oh.employee_id, c.first_name, c.last_name \
                FROM order_head oh \
                JOIN customer c ON oh.customer_id = c.id \
                WHERE oh.employee_id = ?""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new OrderDetail(new OrderHead(rs.getLong("id"), rs.getDate("order_date").toLocalDate(), rs.getLong("customer_id"), rs.getLong("employee_id")), rs.getString("last_name") + ", " + rs.getString("first_name"), new ArrayList<>()));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders with customer names: " + e.getMessage());
        }
        return orders;
    }

    public List<OrderDetail> listOrdersWithDetailsForEmployee(int employeeId) throws SQLException {
        List<OrderDetail> orders = new ArrayList<>();
        Map<Long, OrderDetail> orderMap = new HashMap<>();
        String sql = """
                SELECT oh.id, oh.order_date, oh.customer_id, oh.employee_id, c.first_name, c.last_name, ol.furniture_id, ol.quantity, f.name \
                FROM order_head oh \
                JOIN order_line ol ON oh.id = ol.order_id \
                JOIN customer c ON oh.customer_id = c.id \
                JOIN furniture f ON ol.furniture_id = f.id \
                WHERE oh.employee_id = ?""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long orderId = rs.getLong("id");
                OrderDetail order = orderMap.get(orderId);
                if (order == null) {
                    order = new OrderDetail(new OrderHead(
                                orderId,
                                rs.getDate("order_date").toLocalDate(),
                                rs.getLong("customer_id"),
                                rs.getLong("employee_id")),
                            rs.getString("last_name") + ", " + rs.getString("first_name"),
                            new ArrayList<>());
                    orderMap.put(orderId, order);
                    orders.add(order);
                }
                order.lineDetails().add(new OrderLineDetail(new OrderLine(
                            -1,
                            rs.getLong("furniture_id"),
                            orderId,
                            rs.getInt("quantity")),
                        rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching detailed orders: " + e.getMessage());
        }
        return orders;
    }
}
