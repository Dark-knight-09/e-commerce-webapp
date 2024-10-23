import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class ViewProductServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();
        String prodId = req.getParameter("prod_id"); // Get product ID from the request
        String productCategory = req.getParameter("productCategory"); // Get product category from the request

        try {
            // Fetch product details from the database based on the product ID
            ResultSet rs = MysqlDataStoreUtilities.getProductById(prodId);
            
            pw.println("<html><head>");
            pw.println("<title>View Product</title>");
            pw.println("<h1>View Product</h1>");
            pw.println("<link rel='stylesheet' type='text/css' href='style.css'>");
            pw.println("</head><body>");
            pw.println("<div class='postcard-container'>");

            if(rs != null && rs.next()) {
                String name = rs.getString("prod_name");
                String price = rs.getString("prod_price");
                String description = rs.getString("prod_Description");
                String manufacturer = rs.getString("prod_Retailer");
                String condition = rs.getString("prod_condition");
                String discount = rs.getString("prod_disc");
                String image = rs.getString("prod_img");

                // Display product details using postcard style
                pw.println("<div class='postcard'>");
                pw.println("<img src='images/" + image + "' alt='" + name + "' class='postcard-image'>");
                pw.println("<div class='postcard-content'>");
                pw.println("<h2>" + name + "</h2>");
                pw.println("<p class='price'>$" + price + "</p>");
                pw.println("<p class='description'>" + description + "</p>");
                pw.println("<p class='manufacturer'>Manufacturer: " + manufacturer + "</p>");
                pw.println("<p class='condition'>Condition: " + condition + "</p>");
                pw.println("<p class='discount'>Discount: " + discount + "%</p>");
                pw.println("<button class='buy-now' onclick=\"addToCart('" + name + "', '" + price + "')\">Buy Now</button>");
                pw.println("</div>");
                pw.println("</div>");
                pw.println("<br>");
            }  else {
                pw.println("<p>No product found with the provided ID.</p>");
            }

            // Display accessories
            pw.println("<br>");
            pw.println("<h2>Accessories</h2>");
            pw.println("<div class='postcard-container'>");
            ResultSet rs1 = MysqlDataStoreUtilities.getProductsByCategory(productCategory);
            System.out.println(rs1);
            if (rs1 != null ) {
                while(rs1.next()){
                String Name = rs1.getString("acc_name");
                String Price = rs1.getString("acc_price");
                String Image = rs1.getString("acc_img");

                
                System.out.println("Accessory Name: " + Name);
                System.out.println("Accessory Price: " + Price);
                System.out.println("Accessory Image: " + Image);

                pw.println("<div class='postcard'>");
                pw.println("<img src='images/" + Image + "' alt='" + Name + "' class='postcard-image'>");
                pw.println("<div class='postcard-content'>");
                pw.println("<h3>" + Name + "</h3>");
                pw.println("<p class='price'>" + Price + "</p>");
                pw.println("<button class='buy-now' onclick=\"addToCart('" + Name + "', '" + Price + "')\">Buy Now</button>");
                pw.println("</div>");
                pw.println("</div>");
                }
            } else {
                pw.println("<p>No product found with the provided .</p>");
            }
            pw.println("</div>"); // Closing accessories container

            pw.println("</div>");
            pw.println("</body></html>");
        } catch (Exception e) {
            pw.println("<html><body>");
            pw.println("<p>Error displaying product: " + e.getMessage() + "</p>");
            pw.println("</body></html>");
        } finally {
            pw.close();
        }
    }
}
