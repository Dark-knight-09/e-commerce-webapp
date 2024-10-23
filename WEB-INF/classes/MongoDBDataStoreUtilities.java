import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.json.JsonMode;
import java.time.LocalDate;

@WebServlet("/MongoDBDataStoreUtilities")
public class MongoDBDataStoreUtilities extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");



        
        try {
            String productModelName = request.getParameter("productModelName");
            String productCategory = request.getParameter("productCategory");
            String productPrice = request.getParameter("productPrice");
            String storeID = request.getParameter("storeID");
            String productOnSale = request.getParameter("productOnSale");
            String manufacturerName = request.getParameter("manufacturerName");
            String manufacturerRebate = request.getParameter("manufacturerRebate");
            String userID = request.getParameter("userID");
            String userAge = request.getParameter("userAge");
            String userGender = request.getParameter("userGender");
            String userOccupation = request.getParameter("userOccupation");
            String reviewRating = request.getParameter("reviewRating");
            String reviewText = request.getParameter("reviewText");

            LocalDate reviewDate = LocalDate.now();
            String reviewDateString = reviewDate.toString();
            // String reviewJson = "";
    
            // Connect to MongoDB
            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                // Get the database (create if it doesn't exist)
                MongoDatabase database = mongoClient.getDatabase(productCategory);
    
                // Get the collection (create if it doesn't exist)
                MongoCollection<Document> collection = database.getCollection(productModelName);
    
                // Create a document to store the review data
                Document review = new Document("productModelName", productModelName)
                    .append("productCategory", productCategory)
                    .append("productPrice", productPrice)
                    .append("storeID", storeID)
                    .append("productOnSale", productOnSale)
                    .append("manufacturerName", manufacturerName)
                    .append("manufacturerRebate", manufacturerRebate)
                    .append("userID", userID)
                    .append("userAge", userAge)
                    .append("userGender", userGender)
                    .append("userOccupation", userOccupation)
                    .append("reviewRating", reviewRating)
                    .append("reviewDate", reviewDateString)
                    .append("reviewText", reviewText);
    
                // Insert the document into the collection
                collection.insertOne(review);
                // reviewJson = review.toJson();
            }
    
            // Get the referer URL (previous page)
            String referer = request.getHeader("Referer");
    
            // Respond to the client with success message and back button
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h2>Review Submitted Successfully</h2>");
            
            // Add a back button to return to the previous page
            if (referer != null) {
                response.getWriter().println("<button onclick=\"window.location.href='" + referer + "';\">Go Back</button>");
            } else {
                response.getWriter().println("<button onclick=\"window.history.back();\">Go Back</button>");
            }
    
            response.getWriter().println("</body></html>");
    
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<html><body><h3>Error processing request: " + e.getMessage() + "</h3></body></html>");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productModelName = request.getParameter("productModelName");
        String productCategory = request.getParameter("productCategory");

        response.setContentType("application/json");
        PrintWriter pw = response.getWriter();

        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase(productCategory);
            MongoCollection<Document> collection = database.getCollection(productModelName);

            FindIterable<Document> documents = collection.find();
            JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();

            pw.print("[");
            boolean first = true;
            for (Document doc : documents) {
                if (!first) {
                    pw.print(",");
                }
                pw.print(doc.toJson(settings));
                first = false;
            }
            pw.print("]");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            pw.print("{\"error\":\"Unable to fetch comments\"}");
        }
    }
}