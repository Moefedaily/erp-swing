package main.java.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.model.Order;
import main.java.model.OrderLine;
import main.java.util.DatabaseManager;

public class OrderDAO {
  
    public int createOrder(Order order) throws SQLException {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement lineStmt = null;
        
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            
            String orderSql = """
                INSERT INTO orders (orderdate, customerid, netamount, tax, totalamount) 
                VALUES (?, ?, ?, ?, ?) 
                RETURNING orderid
            """;
            
            orderStmt = conn.prepareStatement(orderSql);
            if (order.getOrderDate() != null) {
                orderStmt.setDate(1, Date.valueOf(order.getOrderDate()));
            } else {
                orderStmt.setDate(1, Date.valueOf(LocalDate.now()));
            }
            orderStmt.setInt(2, order.getCustomerId());
            orderStmt.setDouble(3, order.getNetAmount());
            orderStmt.setDouble(4, order.getTax());
            orderStmt.setDouble(5, order.getTotalAmount());
            
            ResultSet rs = orderStmt.executeQuery();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt("orderid");
                order.setOrderId(orderId);
            }
            rs.close();
            
            String lineSql = """
                INSERT INTO orderlines (orderlineid, orderid, prod_id, quantity, orderdate) 
                VALUES (?, ?, ?, ?, CURRENT_DATE)
            """;
            
            lineStmt = conn.prepareStatement(lineSql);
            
            int nextOrderLineId = getNextOrderLineId(conn);
            
            for (OrderLine line : order.getOrderLines()) {
                lineStmt.setInt(1, nextOrderLineId++);     
                lineStmt.setInt(2, orderId);               
                lineStmt.setInt(3, line.getProdId());      
                lineStmt.setInt(4, line.getQuantity());    
                lineStmt.addBatch();
            }
            
            lineStmt.executeBatch();
            
            conn.commit();
            System.out.println("Order created successfully with ID: " + orderId);
            
            return orderId;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Failed to create order: " + e.getMessage(), e);
            
        } finally {
            try {
                if (lineStmt != null) lineStmt.close();
                if (orderStmt != null) orderStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); 
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.orderid, o.orderdate, o.customerid, o.netamount, o.tax, o.totalamount,
                   c.firstname, c.lastname
            FROM orders o
            LEFT JOIN customers c ON o.customerid = c.customerid
            ORDER BY o.orderdate DESC, o.orderid DESC
            LIMIT 100
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderid"));
                order.setOrderDate(rs.getDate("orderdate").toLocalDate());
                order.setCustomerId(rs.getInt("customerid"));
                order.setNetAmount(rs.getDouble("netamount"));
                order.setTax(rs.getDouble("tax"));
                order.setTotalAmount(rs.getDouble("totalamount"));
                
                String customerName = rs.getString("firstname") + " " + rs.getString("lastname");
                order.setCustomerName(customerName);
                
                orders.add(order);
            }
            
            System.out.println("Loaded " + orders.size() + " orders from database");
            
        } catch (SQLException e) {
            System.err.println("Error loading orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.orderid, o.orderdate, o.customerid, o.netamount, o.tax, o.totalamount,
                   c.firstname, c.lastname
            FROM orders o
            LEFT JOIN customers c ON o.customerid = c.customerid
            WHERE o.customerid = ?
            ORDER BY o.orderdate DESC, o.orderid DESC
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("orderid"));
                order.setOrderDate(rs.getDate("orderdate").toLocalDate());
                order.setCustomerId(rs.getInt("customerid"));
                order.setNetAmount(rs.getDouble("netamount"));
                order.setTax(rs.getDouble("tax"));
                order.setTotalAmount(rs.getDouble("totalamount"));
                
                String customerName = rs.getString("firstname") + " " + rs.getString("lastname");
                order.setCustomerName(customerName);
                
                orders.add(order);
            }
            
            System.out.println("Loaded " + orders.size() + " orders for customer " + customerId);
            
        } catch (SQLException e) {
            System.err.println("Error loading customer orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public Order getOrderWithDetails(int orderId) {
        Order order = null;
        
        String orderSql = """
            SELECT o.orderid, o.orderdate, o.customerid, o.netamount, o.tax, o.totalamount,
                   c.firstname, c.lastname
            FROM orders o
            LEFT JOIN customers c ON o.customerid = c.customerid
            WHERE o.orderid = ?
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                order = new Order();
                order.setOrderId(rs.getInt("orderid"));
                order.setOrderDate(rs.getDate("orderdate").toLocalDate());
                order.setCustomerId(rs.getInt("customerid"));
                order.setNetAmount(rs.getDouble("netamount"));
                order.setTax(rs.getDouble("tax"));
                order.setTotalAmount(rs.getDouble("totalamount"));
                
                String customerName = rs.getString("firstname") + " " + rs.getString("lastname");
                order.setCustomerName(customerName);
                
                loadOrderLines(conn, order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading order details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return order;
    }
    
   
    private void loadOrderLines(Connection conn, Order order) throws SQLException {
        String linesSql = """
            SELECT ol.orderlineid, ol.prod_id, ol.quantity, ol.orderdate,
                   p.title, p.price
            FROM orderlines ol
            LEFT JOIN products p ON ol.prod_id = p.prod_id
            WHERE ol.orderid = ?
            ORDER BY ol.orderlineid
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(linesSql)) {
            pstmt.setInt(1, order.getOrderId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderLine line = new OrderLine();
                line.setOrderLineId(rs.getInt("orderlineid"));
                line.setOrderId(order.getOrderId());
                line.setProdId(rs.getInt("prod_id"));
                line.setQuantity(rs.getInt("quantity"));
                line.setOrderDate(rs.getDate("orderdate").toString()); 
                line.setProductTitle(rs.getString("title"));
                line.setProductPrice(rs.getDouble("price"));
                line.calculateLineTotal();
                
                order.addOrderLine(line);
            }
        }
    }
    
    
    private int getNextOrderLineId(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(orderlineid), 0) + 1 FROM orderlines";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1; 
        }
    }
    
    
    public int getOrderCount() {
        String sql = "SELECT COUNT(*) as total FROM orders";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    
    public static void main(String[] args) {
        System.out.println("=== Testing OrderDAO ===");
        
        OrderDAO dao = new OrderDAO();
        
        System.out.println("Total orders in database: " + dao.getOrderCount());
        
        List<Order> orders = dao.getAllOrders();
        System.out.println("Recent orders: " + orders.size());
        
        for (int i = 0; i < Math.min(3, orders.size()); i++) {
            Order order = orders.get(i);
            System.out.println((i+1) + ". " + order);
            
            if (i == 0) {
                Order detailedOrder = dao.getOrderWithDetails(order.getOrderId());
                if (detailedOrder != null) {
                    System.out.println("   Order lines: " + detailedOrder.getOrderLines().size());
                    detailedOrder.getOrderLines().forEach(line -> 
                        System.out.println("   - " + line));
                }
            }
        }
    }
}