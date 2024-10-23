import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.DriverManager;
import java.sql.SQLException;


@WebServlet("/search")
public class SearchServlet extends HttpServlet {


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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        String productName = request.getParameter("query");


        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM allprods WHERE prod_name = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, productName);
            ResultSet rs = ps.executeQuery();

            pw.println("<html><head>");
            pw.println("<title>Product Search Result</title>");
            pw.println("<link rel='stylesheet' type='text/css' href='style.css'>"); // Link to external CSS
            pw.println("</head><body>");
            
            pw.println("<div id='content' class='postcard-container'>");

            // Iterate through the result set and display the product
            if (rs != null && rs.next()) {
                String name = rs.getString("prod_name");
                String price = rs.getString("prod_price");
                String manufacturer = rs.getString("prod_Retailer");
                String condition = rs.getString("prod_condition");
                String discount = rs.getString("prod_disc");
                String image = rs.getString("prod_img");

                pw.println("<div class='postcard'>");
                pw.println("<img src='images/" + image + "' alt='" + name + "' class='postcard-image'>");
                pw.println("<div class='postcard-content'>");
                pw.println("<h2>" + name + "</h2>");
                pw.println("<p class='price'>$" + price + "</p>");
                pw.println("<p class='manufacturer'>Manufacturer: " + manufacturer + "</p>");
                pw.println("<p class='condition'>Condition: " + condition + "</p>");
                pw.println("<p class='discount'>Discount: " + discount + "%</p>");
                pw.println("<button class='view-product' onclick=\"window.location.href='viewproducts?prod_id=" + rs.getString("prod_id") + "&productCategory=" + rs.getString("prod_name") + "'\">View Product</button>");
                String productCategory = "SmartDoorlock"; // Set the product category to Speaker/
                pw.println("<button class='add-review' onclick=\"showReviewForm('" + name + "', '" + price + "', '" + manufacturer + "', '" + productCategory + "')\">Add Review</button>");
                pw.println("<button class='view-comments' onclick=\"viewComments('" + name + "', '" + productCategory + "')\">View Comments</button>");
                pw.println("<button class='buy-now' onclick=\"addToCart('" + name + "', '" + price + "')\">Buy Now</button>");
                pw.println("</div>");
                pw.println("</div>");
            } else {
                pw.println("<p>No product found with the name: " + productName + "</p>");
            }

            pw.println("</div>");
            pw.println("</body></html>");
        } catch (Exception e) {
            pw.println("<html><body>");
            pw.println("<p>Error displaying product: " + e.getMessage() + "</p>");
            pw.println("</body></html>");
        } finally {
            pw.close(); // Ensure the PrintWriter is closed
        }
    }
}
