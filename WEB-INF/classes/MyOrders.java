import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class MyOrders extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the logged-in user's name from the session
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            out.println("<h3>Please log in to view your orders.</h3>");
            return;  // Exit if user is not logged in
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Set up the database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smarthomedemo", "root", "root");

            // SQL query to fetch orders based on the username
            String sql = "SELECT order_id, item_name, quantity, price, total_price, order_date, address, payment_method, delivery_method, store_location, zipcode, delivery_date "
                       + "FROM orders WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            // Start output HTML
            out.println("<html><body>");
            out.println("<h2><center>My Orders</center></h2>");

            // Check if there are any orders
            if (!rs.isBeforeFirst()) {
                out.println("<p>No orders found.</p>");
            } else {
                // Display orders in a table
                out.println("<table border='1'>");
                out.println("<tr style='font-size: large;background: burlywood;'><th>Order ID</th><th>Item Name</th><th>Quantity</th><th>Price</th><th>Total Price</th>"
                          + "<th>Order Date</th><th>Address</th><th>Payment Method</th><th>Delivery Method</th><th>Store Location</th><th>Zipcode</th><th>Delivery Date</th><th>Actions</th></tr>");

                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String itemName = rs.getString("item_name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    double totalPrice = rs.getDouble("total_price");
                    Timestamp orderDate = rs.getTimestamp("order_date");
                    String address = rs.getString("address");
                    String paymentMethod = rs.getString("payment_method");
                    String deliveryMethod = rs.getString("delivery_method");
                    String storeLocation = rs.getString("store_location");
                    String zipcode = rs.getString("zipcode");
                    String deliveryDate = rs.getDate("delivery_date").toString();


                    out.println("<tr><td>" + orderId + "</td>"
                                + "<td>" + itemName + "</td>"
                                + "<td>" + quantity + "</td>"
                                + "<td>" + price + "</td>"
                                + "<td>" + totalPrice + "</td>"
                                + "<td>" + orderDate + "</td>"
                                + "<td>" + address + "</td>"
                                + "<td>" + paymentMethod + "</td>"
                                + "<td>" + deliveryMethod + "</td>"
                                + "<td>" + storeLocation + "</td>"
                                + "<td>" + zipcode + "</td>"
                                + "<td>" + deliveryDate + "</td>"
                                + "<td>"
                                + "<form action='deleteOrder' method='post'>"
                                + "<input type='hidden' name='orderId' value='" + orderId + "'>"
                                + "<button type='submit' style='background-color: green; color: white; border: none; padding: 10px 10px; cursor: pointer;font-size: large;margin: 10px;'>Delete</button>"
                                + "</form>"
                                + "</td></tr>");
                }

                out.println("</table>");
            }

            // End HTML
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error retrieving orders: " + e.getMessage() + "</p>");
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
