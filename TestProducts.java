import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestProducts {
    public static void main(String[] args) {
        String dbUrl = "jdbc:postgresql://localhost:5432/etl_database";
        String dbUser = "postgres";
        String dbPassword = "root";
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement stmt = conn.createStatement();
            
            System.out.println("=== Structure table PRODUCTS ===");
            ResultSet rs1 = stmt.executeQuery("SELECT * FROM products LIMIT 5");
            
            System.out.println("prod_id | category | title | actor | price");
            System.out.println("-".repeat(60));
            while (rs1.next()) {
                System.out.printf("%d | %d | %s | %s | %.2f%n",
                    rs1.getInt("prod_id"),
                    rs1.getInt("category"),
                    rs1.getString("title"),
                    rs1.getString("actor"),
                    rs1.getDouble("price"));
            }
            
            System.out.println("\n=== Structure table CATEGORIES ===");
            ResultSet rs2 = stmt.executeQuery("SELECT * FROM categories ORDER BY category");
            
            System.out.println("category | categoryname");
            System.out.println("-".repeat(30));
            while (rs2.next()) {
                System.out.printf("%d | %s%n",
                    rs2.getInt("category"),
                    rs2.getString("categoryname"));
            }
            
            // Compter les produits
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) as total FROM products");
            if (rs3.next()) {
                System.out.println("\nTotal produits: " + rs3.getInt("total"));
            }
            
            rs1.close();
            rs2.close();
            rs3.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}