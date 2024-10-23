import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class DeleteOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int orderId = Integer.parseInt(req.getParameter("orderId"));

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Set up the database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smarthomedemo", "root", "root");

            // SQL query to delete the order
            String sql = "DELETE FROM orders WHERE order_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();

            // Redirect back to the orders page
            res.sendRedirect("http://localhost:8080/demo/index.html#myorders"); // Adjust the path if necessary
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error (e.g., send an error message to the user)
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
