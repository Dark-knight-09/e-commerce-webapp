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

@WebServlet("/salesReport")
public class SalesreportServlet extends HttpServlet {
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

            // Query to get all products sold and their sales data
            String queryProductsSold = "SELECT p.prod_name, p.prod_price, SUM(o.quantity) AS items_sold, SUM(o.quantity * p.prod_price) AS total_sales " +
                                       "FROM allprods AS p " +
                                       "JOIN orders AS o ON p.prod_name = o.item_name " +
                                       "GROUP BY p.prod_name, p.prod_price";

            // Query to get total daily sales transactions
            String queryDailySales = "SELECT DATE_FORMAT(o.order_date, '%Y-%m-%d') AS sale_date, SUM(o.quantity * p.prod_price) AS total_sales, SUM(o.quantity) As items_sold " +
                                     "FROM orders AS o " +
                                     "JOIN allprods AS p ON o.item_name = p.prod_name " +
                                     "GROUP BY DATE_FORMAT(o.order_date, '%Y-%m-%d')";
            // Fetch Products Sold Data
            List<Map<String, Object>> productsSoldList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(queryProductsSold);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("prod_name", rs.getString("prod_name"));
                    product.put("prod_price", rs.getString("prod_price"));
                    product.put("items_sold", rs.getInt("items_sold"));
                    product.put("total_sales", rs.getDouble("total_sales"));
                    productsSoldList.add(product);
                }
            }

            // Fetch Daily Sales Data
            List<Map<String, Object>> dailySalesList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(queryDailySales);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dailySale = new HashMap<>();
                    dailySale.put("sale_date", rs.getDate("sale_date"));
                    dailySale.put("total_sales", rs.getDouble("total_sales"));
                    dailySale.put("items_sold", rs.getInt("items_sold"));

                    dailySalesList.add(dailySale);
                }
            }

            // Combine data
            result.put("productsSold", productsSoldList);
            result.put("dailySales", dailySalesList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Use Gson to convert the result Map to JSON and send it as the response
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(result);

        response.getWriter().write(jsonResponse);
    }
}