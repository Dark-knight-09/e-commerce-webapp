import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlDataStoreUtilities {
    private static Connection getConnection() {
        Connection conn = null;
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open a connection
            String jdbcURL = "jdbc:mysql://127.0.0.1:3306/smarthomedemo?useSSL=false";
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(jdbcURL, user, password);

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
        return conn;
    }

    public static ResultSet getDoorbellProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_cat = 'Doorbell'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return rs;  // Return ResultSet for processing in Doorbell.java
    }


    // New function to get Doorlock products from the database
    public static ResultSet getDoorlockProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_cat = 'Doorlock'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return rs;  // Return ResultSet for processing in DoorLock.java
    }

    // New function to get Smart Speaker products from the database
    public static ResultSet getSmartSpeakerProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_cat = 'Speaker'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return rs;
    }

    // New function to get Smart Lighting products from the database
    public static ResultSet getSmartLightingProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_cat = 'Lights'";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return rs;
    }

    // Method to get Smart Thermostat products from the database
    public static ResultSet getSmartThermostatProducts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_cat = 'Thermostat'";
            System.out.println("Query: " + query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    //Method to get product by ID for view products page
    public static ResultSet getProductById(String prodId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
    
        try {
            conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_id = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, prodId);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    
    //to get accessories.
    public static ResultSet getProductsByCategory(String productCategory) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs1 = null;
    
        try {
            conn = getConnection();
            String query = "SELECT * FROM accessory WHERE prod_cat = ?";
            //System.out.println("Query: " + query); 
            ps = conn.prepareStatement(query);
            ps.setString(1, productCategory);  // Set the product category as parameter
            System.out.println("Executing query: " + query + " with parameter: " + productCategory);
            rs1 = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs1;  // Return ResultSet for processing in the viewproducts page
    }
    

}
