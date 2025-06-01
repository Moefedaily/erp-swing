package main.java.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.java.model.Category;
import main.java.model.Product;

public class ProductDAO {
    
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
    
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.prod_id, p.category, p.title, p.actor, p.price, c.categoryname 
            FROM products p 
            LEFT JOIN categories c ON p.category = c.category 
            ORDER BY p.prod_id 
            LIMIT 100
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProdId(rs.getInt("prod_id"));
                product.setCategory(rs.getInt("category"));
                product.setTitle(rs.getString("title"));
                product.setActor(rs.getString("actor"));
                product.setPrice(rs.getDouble("price"));
                product.setCategoryName(rs.getString("categoryname"));
                
                products.add(product);
            }
            
            System.out.println("Chargé " + products.size() + " produits depuis la base de données");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des produits: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    /**
     * Récupérer les produits filtrés par catégorie
     */
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.prod_id, p.category, p.title, p.actor, p.price, c.categoryname 
            FROM products p 
            LEFT JOIN categories c ON p.category = c.category 
            WHERE p.category = ? 
            ORDER BY p.prod_id 
            LIMIT 100
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProdId(rs.getInt("prod_id"));
                product.setCategory(rs.getInt("category"));
                product.setTitle(rs.getString("title"));
                product.setActor(rs.getString("actor"));
                product.setPrice(rs.getDouble("price"));
                product.setCategoryName(rs.getString("categoryname"));
                
                products.add(product);
            }
            
            System.out.println("Chargé " + products.size() + " produits pour la catégorie " + categoryId);
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des produits: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
  
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category, categoryname FROM categories ORDER BY category";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category"));
                category.setCategoryName(rs.getString("categoryname"));
                
                categories.add(category);
            }
            
            System.out.println("Chargé " + categories.size() + " catégories");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des catégories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    /**
     * Rechercher des produits par titre
     */
    public List<Product> searchProductsByTitle(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.prod_id, p.category, p.title, p.actor, p.price, c.categoryname 
            FROM products p 
            LEFT JOIN categories c ON p.category = c.category 
            WHERE LOWER(p.title) LIKE LOWER(?) 
            ORDER BY p.prod_id 
            LIMIT 50
        """;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProdId(rs.getInt("prod_id"));
                product.setCategory(rs.getInt("category"));
                product.setTitle(rs.getString("title"));
                product.setActor(rs.getString("actor"));
                product.setPrice(rs.getDouble("price"));
                product.setCategoryName(rs.getString("categoryname"));
                
                products.add(product);
            }
            
            System.out.println("Trouvé " + products.size() + " produits pour '" + searchTerm + "'");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
   
    public int getProductCount() {
        String sql = "SELECT COUNT(*) as total FROM products";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des produits: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
   
    public static void main(String[] args) {
        System.out.println("=== Test ProductDAO ===");
        
        ProductDAO dao = new ProductDAO();
        
        List<Category> categories = dao.getAllCategories();
        System.out.println("Catégories disponibles: " + categories.size());
        for (int i = 0; i < Math.min(3, categories.size()); i++) {
            System.out.println("- " + categories.get(i));
        }
        
        List<Product> products = dao.getAllProducts();
        System.out.println("\nPremiers produits: " + products.size());
        for (int i = 0; i < Math.min(3, products.size()); i++) {
            System.out.println((i+1) + ". " + products.get(i));
        }
        
        if (!categories.isEmpty()) {
            List<Product> actionProducts = dao.getProductsByCategory(1);
            System.out.println("\nProduits Action: " + actionProducts.size());
        }
    }
}