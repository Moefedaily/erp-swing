package main.java.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.model.Customer;

public class CustomerDAO {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/etl_database";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";
    
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }
    }
    
   
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customerid, firstname, lastname, email, city, address1 FROM customers ORDER BY customerid LIMIT 100";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerid"));
                customer.setFirstname(rs.getString("firstname"));
                customer.setLastname(rs.getString("lastname"));
                customer.setEmail(rs.getString("email"));
                customer.setCity(rs.getString("city"));
                customer.setAddress1(rs.getString("address1"));
                
                customers.add(customer);
            }
            
            System.out.println("Chargé " + customers.size() + " clients depuis la base de données");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des clients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }
    
  
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT customerid, firstname, lastname, email, city, address1 FROM customers WHERE customerid = " + customerId;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerid"));
                customer.setFirstname(rs.getString("firstname"));
                customer.setLastname(rs.getString("lastname"));
                customer.setEmail(rs.getString("email"));
                customer.setCity(rs.getString("city"));
                customer.setAddress1(rs.getString("address1"));
                return customer;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du client: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
   
    public int getCustomerCount() {
        String sql = "SELECT COUNT(*) as total FROM customers";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des clients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    
    public static void main(String[] args) {
        System.out.println("=== Test CustomerDAO ===");
        
        CustomerDAO dao = new CustomerDAO();
        
        int count = dao.getCustomerCount();
        System.out.println("Nombre total de clients: " + count);
        
        List<Customer> customers = dao.getAllCustomers();
        System.out.println("Premiers clients chargés: " + customers.size());
        
        for (int i = 0; i < Math.min(3, customers.size()); i++) {
            System.out.println((i+1) + ". " + customers.get(i));
        }
    }
}