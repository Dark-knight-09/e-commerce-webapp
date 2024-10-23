import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class OrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String customerName = req.getParameter("customerName");
        String address = req.getParameter("address");
        String zipcode = req.getParameter("zipcode");
        String paymentMethod = req.getParameter("paymentMethod");
        String cardNumber = req.getParameter("cardNumber"); // Ensure you handle this securely
        String deliveryMethod = req.getParameter("deliveryMethod");
        String storeLocation = req.getParameter("storeLocation");
        //double cartTotalPrice = Double.parseDouble(req.getParameter("cartTotalPrice"));

        HttpSession session = req.getSession();
        List<Map<String, String>> cart = (List<Map<String, String>>) session.getAttribute("cart");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Set up your database connection
            Class.forName("com.mysql.cj.jdbc.Driver"); // or your specific driver
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smarthomedemo", "root", "root");

            String sql = "INSERT INTO orders (username, item_name, quantity, price, total_price, address, payment_method, delivery_method, store_location, zipcode) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);

            // Loop through each cart item and insert into the orders table
            for (Map<String, String> item : cart) {
                String itemName = item.get("name");
                int quantity = Integer.parseInt(item.get("quantity"));
                double price = Double.parseDouble(item.get("price"));
                double totalPrice = Double.parseDouble(item.get("totalPrice"));

                pstmt.setString(1, customerName);
                pstmt.setString(2, itemName);
                pstmt.setInt(3, quantity);
                pstmt.setDouble(4, price);
                pstmt.setDouble(5, totalPrice);
                pstmt.setString(6, address);
                pstmt.setString(7, paymentMethod);
                pstmt.setString(8, deliveryMethod);
                pstmt.setString(9, storeLocation);
                pstmt.setString(10, zipcode);

                pstmt.addBatch();  // Add to batch for bulk execution
            }

            // Execute the batch
            pstmt.executeBatch();

            // Clear the cart after successful order placement
            session.removeAttribute("cart");

            // Send success response
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
