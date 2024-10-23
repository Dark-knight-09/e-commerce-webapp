import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.ResultSet;

public class SmartSpeaker extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();

        try {
            // Fetch Smart Speaker products from the database
            ResultSet rs = MysqlDataStoreUtilities.getSmartSpeakerProducts();

            pw.println("<html><head>");
            pw.println("<title>Smart Speaker Catalog</title>");
            pw.println("<link rel='stylesheet' type='text/css' href='style.css'>");
            pw.println("</head><body>");

            pw.println("<div id='content' class='postcard-container'>");

            // Iterate through the ResultSet and display smart speakers
            while (rs != null && rs.next()) {
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

                String productCategory = "SmartSpeaker"; // Set the product category to Speaker/
                pw.println("<button class='add-review' onclick=\"showReviewForm('" + name + "', '" + price + "', '" + manufacturer + "', '" + productCategory + "')\">Add Review</button>");
                
                // View Comments button
                pw.println("<button class='view-comments' onclick=\"viewComments('" + name + "', '" + productCategory + "')\">View Comments</button>");

                pw.println("<button class='buy-now' onclick=\"addToCart('" + name + "', '" + price + "')\">Buy Now</button>");
                pw.println("</div>");
                pw.println("</div>");
            }

            pw.println("</div>");
            pw.println("</body></html>");
        } catch (Exception e) {
            pw.println("<html><body>");
            pw.println("<p>Error displaying smart speaker products: " + e.getMessage() + "</p>");
            pw.println("</body></html>");
        } finally {
            pw.close();
        }
    }
}
