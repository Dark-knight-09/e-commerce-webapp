import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class StoreManagerServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/smarthomedemo"; // Replace with your database URL
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = "root"; // Replace with your database password

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // HTML content with a horizontal navigation bar
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Store Manager Dashboard</title>");
        out.println("<h1><center>Smart Home Catalog</center></h1>");
        out.println("<h2><center> Store Manager Dashboard</center</h2>");
        out.println("<style>");
        
        // Basic CSS for horizontal navigation bar
        out.println("ul {");
        out.println("  list-style-type: none;");
        out.println("  margin: 0;");
        out.println("  padding: 0;");
        out.println("  overflow: hidden;");
        out.println("  background-color: #333;");
        out.println("}");

        out.println("li {");
        out.println("  float: left;");
        out.println("}");

        out.println("li a {");
        out.println("  display: block;");
        out.println("  color: white;");
        out.println("  text-align: center;");
        out.println("  padding: 14px 16px;");
        out.println("  text-decoration: none;");
        out.println("}");

        out.println("li a:hover {");
        out.println("  background-color: #111;");
        out.println("}");

        // Form CSS
        out.println("form {");
        out.println("  max-width: 600px;");
        out.println("  margin: 20px auto;");
        out.println("  padding: 20px;");
        out.println("  border: 1px solid #ccc;");
        out.println("  border-radius: 5px;");
        out.println("  background-color: #f9f9f9;");
        out.println("  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);");
        out.println("}");

        out.println("label {");
        out.println("  display: block;");
        out.println("  margin-bottom: 8px;");
        out.println("  font-weight: bold;");
        out.println("}");

        out.println("input[type='text'], textarea {");
        out.println("  width: 100%;");
        out.println("  padding: 10px;");
        out.println("  margin-bottom: 20px;");
        out.println("  border: 1px solid #ccc;");
        out.println("  border-radius: 4px;");
        out.println("}");

        out.println("input[type='submit'] {");
        out.println("  background-color: #4CAF50;");
        out.println("  color: white;");
        out.println("  padding: 10px 15px;");
        out.println("  border: none;");
        out.println("  border-radius: 4px;");
        out.println("  cursor: pointer;");
        out.println("}");

        out.println("input[type='submit']:hover {");
        out.println("  background-color: #45a049;");
        out.println("}");
        
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Navigation bar
        out.println("<ul>");
        out.println("  <li><a href='?action=addProduct'>Add Product</a></li>");
        out.println("  <li><a href='?action=updateProduct'>Update Product</a></li>");
        out.println("<li><button onclick=\"window.location.href='inventory.html'\">Go to Inventory Management</button></li>");
        out.println("</ul>");


        String action = req.getParameter("action");
        if ("addProduct".equals(action)) {
            // Display the form for adding a product
            out.println("<h2>Add Product</h2>");
            out.println("<form action='storemanagerservlet' method='post'>");
            out.println("  <label for='prod_id'>Product ID:</label>");
            out.println("  <input type='text' id='prod_id' name='prod_id' required>");
            out.println("  <label for='prod_cat'>Product Category:</label>");
            out.println("  <input type='text' id='prod_cat' name='prod_cat' required>");
            out.println("  <label for='prod_name'>Product Name:</label>");
            out.println("  <input type='text' id='prod_name' name='prod_name' required>");
            out.println("  <label for='prod_price'>Product Price:</label>");
            out.println("  <input type='text' id='prod_price' name='prod_price' required>");
            out.println("  <label for='prod_description'>Product Description:</label>");
            out.println("  <textarea id='prod_description' name='prod_description' required></textarea>");
            out.println("  <label for='prod_retailer'>Product Retailer:</label>");
            out.println("  <input type='text' id='prod_retailer' name='prod_retailer' required>");
            out.println("  <label for='prod_disc'>Product Discount:</label>");
            out.println("  <input type='text' id='prod_disc' name='prod_disc'>");
            out.println("  <label for='prod_condition'>Product Condition:</label>");
            out.println("  <input type='text' id='prod_condition' name='prod_condition' required>");
            out.println("  <label for='prod_img'>Product Image URL:</label>");
            out.println("  <input type='text' id='prod_img' name='prod_img' required>");
            out.println("  <input type='submit' value='Add Product'>");
            out.println("</form>");
        } else if ("updateProduct".equals(action)){
                // Display the form for Update a product
            out.println("<h2><center>Update Product</center></h2>");
            out.println("<form action='storemanagerservlet' method='post'>");
            out.println("  <label for='prod_id'>Product ID:</label>");
            out.println("  <input type='text' id='prod_id' name='prod_id' required>");
            out.println("  <label for='prod_cat'>Product Category:</label>");
            out.println("  <input type='text' id='prod_cat' name='prod_cat' required>");
            out.println("  <label for='prod_name'>Product Name:</label>");
            out.println("  <input type='text' id='prod_name' name='prod_name' required>");
            out.println("  <label for='prod_price'>Product Price:</label>");
            out.println("  <input type='text' id='prod_price' name='prod_price' required>");
            out.println("  <label for='prod_description'>Product Description:</label>");
            out.println("  <textarea id='prod_description' name='prod_description' required></textarea>");
            out.println("  <label for='prod_retailer'>Product Retailer:</label>");
            out.println("  <input type='text' id='prod_retailer' name='prod_retailer' required>");
            out.println("  <label for='prod_disc'>Product Discount:</label>");
            out.println("  <input type='text' id='prod_disc' name='prod_disc'>");
            out.println("  <label for='prod_condition'>Product Condition:</label>");
            out.println("  <input type='text' id='prod_condition' name='prod_condition' required>");
            out.println("  <label for='prod_img'>Product Image URL:</label>");
            out.println("  <input type='text' id='prod_img' name='prod_img' required>");
            out.println("  <input type='submit' value='Update Product'>");
            out.println("</form>");


        } 
        else {
            // Default dashboard content
            //out.println("<h1>Welcome, Store Manager</h1>");
            out.println("<p>Please choose an option from the menu.</p>");
        }

        // Check for success message
        String success = req.getParameter("success");
        if ("true".equals(success)) {
            out.println("<script>alert('Product added successfully!');</script>");
        }

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        String prod_id = req.getParameter("prod_id");
        String prod_cat = req.getParameter("prod_cat");
        String prod_name = req.getParameter("prod_name");
        String prod_price = req.getParameter("prod_price");
        String prod_description = req.getParameter("prod_description");
        String prod_retailer = req.getParameter("prod_retailer");
        String prod_disc = req.getParameter("prod_disc");
        String prod_condition = req.getParameter("prod_condition");
        String prod_img = req.getParameter("prod_img");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the product already exists
            String checkQuery = "SELECT COUNT(*) FROM allprods WHERE prod_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, prod_id);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    // Update the existing product
                    String updateQuery = "UPDATE allprods SET prod_cat=?, prod_name=?, prod_price=?, prod_description=?, "
                            + "prod_retailer=?, prod_disc=?, prod_condition=?, prod_img=? WHERE prod_id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, prod_cat);
                        updateStmt.setString(2, prod_name);
                        updateStmt.setString(3, prod_price);
                        updateStmt.setString(4, prod_description);
                        updateStmt.setString(5, prod_retailer);
                        updateStmt.setString(6, prod_disc);
                        updateStmt.setString(7, prod_condition);
                        updateStmt.setString(8, prod_img);
                        updateStmt.setString(9, prod_id);

                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            res.sendRedirect("storemanagerservlet?action=updateProduct&success=true");
                        } else {
                            res.sendRedirect("storemanagerservlet?action=updateProduct&success=false");
                        }
                    }
                } else {
                    // Insert the new product
                    String insertQuery = "INSERT INTO allprods (prod_id, prod_cat, prod_name, prod_price, prod_description, "
                            + "prod_retailer, prod_disc, prod_condition, prod_img) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, prod_id);
                        insertStmt.setString(2, prod_cat);
                        insertStmt.setString(3, prod_name);
                        insertStmt.setString(4, prod_price);
                        insertStmt.setString(5, prod_description);
                        insertStmt.setString(6, prod_retailer);
                        insertStmt.setString(7, prod_disc);
                        insertStmt.setString(8, prod_condition);
                        insertStmt.setString(9, prod_img);

                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            res.sendRedirect("storemanagerservlet?action=addProduct&success=true");
                        } else {
                            res.sendRedirect("storemanagerservlet?action=addProduct&success=false");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            res.sendRedirect("storemanagerservlet?action=" + action + "&success=false&error=" + e.getMessage());
        }
    }
}
