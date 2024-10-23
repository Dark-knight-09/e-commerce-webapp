import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Trending")
public class Trending extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        StringBuilder trendingProducts = new StringBuilder();
        trendingProducts.append("[");

        try {
            // Establish database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/smarthomedemo", "root", "root");
            Statement stmt = conn.createStatement();
            
            
            // Select specific products by IDs
            String sql = "SELECT * FROM allprods WHERE prod_id IN (3, 11, 13, 15, 24)";
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean firstProduct = true;
            while (rs.next()) {
                if (!firstProduct) {
                    trendingProducts.append(",");
                }
                firstProduct = false;

                trendingProducts.append("{")
                    .append("\"prod_id\":").append(rs.getInt("prod_id")).append(",")
                    .append("\"prod_cat\":\"").append(rs.getString("prod_cat")).append("\",")
                    .append("\"prod_name\":\"").append(rs.getString("prod_name")).append("\",")
                    .append("\"prod_price\":\"").append(rs.getString("prod_price")).append("\",")
                    .append("\"prod_description\":\"").append(rs.getString("prod_description")).append("\",")
                    .append("\"prod_retailer\":\"").append(rs.getString("prod_retailer")).append("\",")
                    .append("\"prod_disc\":\"").append(rs.getString("prod_disc")).append("\",")
                    .append("\"prod_condition\":\"").append(rs.getString("prod_condition")).append("\",")
                    .append("\"prod_img\":\"").append(rs.getString("prod_img")).append("\"")
                    .append("}");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        trendingProducts.append("]");
        out.print(trendingProducts.toString());
        out.flush();
    }
}
