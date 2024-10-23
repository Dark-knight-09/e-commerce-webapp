import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String logintype = req.getParameter("logintype"); // Get the login type from registration
        String street = req.getParameter("street"); // Get the street address
        String city = req.getParameter("city"); // Get the city
        String state = req.getParameter("state"); // Get the state
        String zipcode = req.getParameter("zipcode"); // Get the zip code
        
        boolean userExists = false;

        try {
            // Initialize database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smarthomedemo?useSSL=false", "root", "root");

            // Check if the user already exists in the database
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userExists = true;
            }

            if (userExists) {
                // If the user exists, display error message
                res.setContentType("text/html");
                PrintWriter pw = res.getWriter();
                pw.println("<html><body>");
                pw.println("<h2>Error: User already exists.</h2>");
                pw.println("<a href='register.html'>Back to Register</a>");
                pw.println("</body></html>");
            } else {
                // Insert new user into the database
                // Updated SQL query to include street, city, state, and zipcode
                        String insertQuery = "INSERT INTO users (username, password, logintype, street, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?)";

                        // Prepare the statement
                        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

                        // Set parameters for each field
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, password);
                        insertStmt.setString(3, logintype);
                        insertStmt.setString(4, street);
                        insertStmt.setString(5, city);
                        insertStmt.setString(6, state);
                        insertStmt.setString(7, zipcode);

                        // Execute the query
                        insertStmt.executeUpdate();


                // Redirect to login page after successful registration
                res.sendRedirect("login.html");
            }

            // Close the connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
