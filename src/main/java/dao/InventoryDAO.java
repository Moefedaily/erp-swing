package main.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Inventory;
import main.java.util.DatabaseManager;

public class InventoryDAO {
    
    public List<Inventory> getAllInventory() {
        List<Inventory> inventories = new ArrayList<>();
        String sql = """
            SELECT i.prod_id, i.quan_in_stock, i.sales,
                   p.title, p.price, c.categoryname
            FROM inventory i
            LEFT JOIN products p ON i.prod_id = p.prod_id
            LEFT JOIN categories c ON p.category = c.category
            ORDER BY i.prod_id
            LIMIT 200
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setProdId(rs.getInt("prod_id"));
                inventory.setQuantityInStock(rs.getInt("quan_in_stock"));
                inventory.setSales(rs.getInt("sales"));
                inventory.setProductTitle(rs.getString("title"));
                inventory.setProductPrice(rs.getDouble("price"));
                inventory.setCategoryName(rs.getString("categoryname"));
                
                inventories.add(inventory);
            }
            
            System.out.println("Loaded " + inventories.size() + " inventory items");
            
        } catch (SQLException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
        }
        
        return inventories;
    }
    
    public List<Inventory> getLowStockProducts(int threshold) {
        List<Inventory> lowStockItems = new ArrayList<>();
        String sql = """
            SELECT i.prod_id, i.quan_in_stock, i.sales,
                   p.title, p.price, c.categoryname
            FROM inventory i
            LEFT JOIN products p ON i.prod_id = p.prod_id
            LEFT JOIN categories c ON p.category = c.category
            WHERE i.quan_in_stock <= ?
            ORDER BY i.quan_in_stock ASC, p.title
        """;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setProdId(rs.getInt("prod_id"));
                inventory.setQuantityInStock(rs.getInt("quan_in_stock"));
                inventory.setSales(rs.getInt("sales"));
                inventory.setProductTitle(rs.getString("title"));
                inventory.setProductPrice(rs.getDouble("price"));
                inventory.setCategoryName(rs.getString("categoryname"));
                
                lowStockItems.add(inventory);
            }
            
            System.out.println("Found " + lowStockItems.size() + " low stock items (threshold: " + threshold + ")");
            
        } catch (SQLException e) {
            System.err.println("Error loading low stock items: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lowStockItems;
    }
    
    public boolean updateStock(int prodId, int quantityToSubtract) throws SQLException {
        String sql = "UPDATE inventory SET quan_in_stock = quan_in_stock - ? WHERE prod_id = ? AND quan_in_stock >= ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantityToSubtract);
            pstmt.setInt(2, prodId);
            pstmt.setInt(3, quantityToSubtract);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Updated stock for product " + prodId + ": -" + quantityToSubtract);
                return true;
            } else {
                System.out.println("Insufficient stock for product " + prodId + " (requested: " + quantityToSubtract + ")");
                return false;
            }
            
        }
    }
    
    public int getStockLevel(int prodId) {
        String sql = "SELECT quan_in_stock FROM inventory WHERE prod_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, prodId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("quan_in_stock");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting stock level: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public boolean restockProduct(int prodId, int quantity) throws SQLException {
        String sql = "UPDATE inventory SET quan_in_stock = quan_in_stock + ? WHERE prod_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, prodId);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Restocked product " + prodId + ": +" + quantity);
                return true;
            }
            
            return false;
        }
    }
    
    public int getTotalProducts() {
        String sql = "SELECT COUNT(*) as total FROM inventory";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting inventory: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static void main(String[] args) {
        System.out.println("=== Testing InventoryDAO ===");
        
        InventoryDAO dao = new InventoryDAO();
        
        System.out.println("Total products in inventory: " + dao.getTotalProducts());
        
        List<Inventory> lowStock = dao.getLowStockProducts(10);
        System.out.println("Low stock products (≤10): " + lowStock.size());
        
        for (int i = 0; i < Math.min(5, lowStock.size()); i++) {
            System.out.println((i+1) + ". " + lowStock.get(i));
        }
        
        if (!lowStock.isEmpty()) {
            int prodId = lowStock.get(0).getProdId();
            System.out.println("\nStock level for product " + prodId + ": " + dao.getStockLevel(prodId));
        }
    }
}