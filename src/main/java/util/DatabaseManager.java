package main.java.util;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseManager {
    private static Dotenv dotenv;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        try {
            dotenv = Dotenv.load();
            dbUrl = dotenv.get("DB_URL");
            dbUser = dotenv.get("DB_USER");
            dbPassword = dotenv.get("DB_PASSWORD");
            System.out.println("Configuration chargée depuis .env");
        } catch (Exception e) {
            dbUrl = "jdbc:postgresql://localhost:5432/etl_database";
            dbUser = "postgres";
            dbPassword = "root";
            System.out.println("Configuration par défaut utilisée");
        }
    }
    
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }
    }
    
    
    public static void testConnection() {
        System.out.println("=== Test de connexion à PostgreSQL ===");
        System.out.println("URL: " + dbUrl);
        System.out.println("User: " + dbUser);
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers LIMIT 5")) {
            
            System.out.println("✅ Connexion réussie !");
            System.out.println("\nPremiers clients:");
            System.out.println("ID | Firstname | Lastname | Email");
            System.out.println("-".repeat(50));
            
            while (rs.next()) {
                int id = rs.getInt("customerid");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String email = rs.getString("email");
                
                System.out.printf("%d | %s | %s | %s%n", 
                    id, firstname, lastname, email);
            }
            
        } catch (SQLException e) {
            System.err.println(" Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connexion fermée");
            } catch (SQLException e) {
                System.err.println("Erreur fermeture connexion: " + e.getMessage());
            }
        }
    }
    
   
    public static void main(String[] args) {
        testConnection();
    }
}