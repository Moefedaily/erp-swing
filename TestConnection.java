import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à PostgreSQL ===");
        
        String dbUrl = "jdbc:postgresql://localhost:5432/etl_database";
        String dbUser = "postgres";
        String dbPassword = "root";
        
        try {
            Class.forName("org.postgresql.Driver");
            
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("✅ Connexion réussie !");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers LIMIT 5");
            
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
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}