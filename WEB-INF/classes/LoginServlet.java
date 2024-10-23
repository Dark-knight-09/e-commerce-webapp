import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/smarthomedemo"; // Replace with your database URL
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = "root"; // Replace with your database password

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String logintype = req.getParameter("logintype"); // Get the logintype from the form

        boolean userValid = false;

        try {
            // Establish connection to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare SQL query to validate user credentials and get login type
            String query = "SELECT logintype FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userValid = true;
                logintype = rs.getString("logintype"); // Get the logintype from the database if needed
                System.out.println(logintype);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userValid) {
            // Valid user, create session and redirect based on logintype
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            session.setAttribute("logintype", logintype); // Store login type in session

            // Redirect based on logintype
            if ("Customer".equalsIgnoreCase(logintype)) {
                res.sendRedirect("/demo"); // Redirect to the customer demo page
            } else if ("StoreManager".equalsIgnoreCase(logintype)) {
                res.sendRedirect("/demo/storemanagerservlet"); // Redirect to the store manager page
            } else if ("Salesmen".equalsIgnoreCase(logintype)) {
                res.sendRedirect("/sales"); // Redirect to the sales page
            }
        } else {
            // User not found or invalid credentials, redirect to registration page
            res.sendRedirect("register.html");
        }
    }
}
