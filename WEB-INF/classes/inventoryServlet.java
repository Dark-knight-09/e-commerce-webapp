import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/inventory")
public class inventoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a Map to hold the results
        Map<String, Object> result = new HashMap<>();

        try (Connection conn = getConnection()) {
            if (conn == null) {
                response.getWriter().write("{\"error\":\"Database connection failed.\"}");
                return;
            }

            // Query to get all products
            String queryAllProducts = "SELECT p.prod_name, p.prod_price, p.inventory_count - IFNULL(SUM(o.quantity), 0) AS total_available FROM allprods AS p LEFT JOIN orders AS o ON p.prod_name = o.item_name GROUP BY p.prod_name, p.prod_price, p.inventory_count";
            String querySales = "SELECT p.prod_name, p.prod_price, p.inventory_count,p.inventory_count - IFNULL(SUM(o.quantity), 0) AS total_available FROM allprods AS p LEFT JOIN orders AS o ON p.prod_name = o.item_name GROUP BY p.prod_name, p.prod_price, p.inventory_count";
            String queryOnSaleProducts = "SELECT p.prod_name, p.prod_price, p.prod_disc, IFNULL(SUM(o.quantity), 0) AS Sales FROM allprods AS p LEFT JOIN orders AS o ON p.prod_name = o.item_name Where prod_disc is not null GROUP BY p.prod_name, p.prod_price, p.inventory_count, p.prod_disc";
            String queryRebateProducts =  "SELECT p.prod_name, p.prod_price, IFNULL(SUM(o.quantity), 0) AS Sales FROM allprods AS p LEFT JOIN orders AS o ON p.prod_name = o.item_name WHERE prod_condition = 'New' GROUP BY p.prod_name, p.prod_price, p.inventory_count, p.prod_disc ";

            // 1. Fetch All Products for Table
            List<Map<String, Object>> allProductsList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(queryAllProducts);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("name", rs.getString("prod_name"));
                    product.put("price", rs.getString("prod_price"));
                    product.put("available", rs.getInt("total_available"));
                    allProductsList.add(product);
                }
            }

            // 2. Fetch Products on Sale
            List<Map<String, Object>> SaleList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(querySales);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("name", rs.getString("prod_name"));
                    product.put("price", rs.getString("prod_price"));
                    product.put("total", rs.getInt("inventory_count"));
                    product.put("available", rs.getInt("total_available"));


                    SaleList.add(product);
                }
            }

            List<Map<String, Object>> onSaleList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(queryOnSaleProducts);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("prod_name", rs.getString("prod_name"));
                    product.put("prod_price", rs.getString("prod_price"));
                    product.put("prod_disc", rs.getString("prod_disc"));
                    product.put("Sales", rs.getInt("Sales"));
                    onSaleList.add(product);
                }
            }
            // 3. Fetch Products with Manufacturer Rebates
            List<Map<String, Object>> rebateList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(queryRebateProducts);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("prod_name", rs.getString("prod_name"));
                    product.put("prod_price", rs.getString("prod_price"));
                    product.put("Sales", rs.getInt("Sales"));
                    rebateList.add(product);
                }
            }

            // Combine data
            result.put("allProducts", allProductsList);
            result.put("SaleProducts", SaleList);
            result.put("onSaleProducts", onSaleList);
            result.put("rebateProducts", rebateList);

            // Data for Bar Chart
            result.put("productsChart", allProductsList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Use Gson to convert the result Map to JSON and send it as the response
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(result);

        response.getWriter().write(jsonResponse);
    }
}