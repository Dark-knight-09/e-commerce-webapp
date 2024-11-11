import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.sql.*;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/customerService")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class CustomerServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    Random random = new Random();
    private static final String OPENAI_API_KEY = "OPEN_API_KEY";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_RETRIES = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if a success message exists in the session
        HttpSession session = request.getSession();
        String successMessage = (String) session.getAttribute("successMessage");
        session.removeAttribute("successMessage");  // Clear the message after displaying

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<style>");
        // CSS styles for the page
        out.println("body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; background: #ffefdb; color: black; }");
        out.println(".container { text-align: center; }");
        out.println(".form-container { background-color: rgba(255, 255, 255, 0.9); padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); width: 300px; margin-top: 20px; }");
        out.println(".form-container h2 { text-align: center; color: #333; }");
        out.println(".form-container input, .form-container textarea { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; }");
        out.println(".form-container textarea { height: 20%; }");
        out.println(".form-container button { width: 100%; padding: 10px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; }");
        out.println(".form-container button:hover { background-color: #45a049; }");
        out.println(".main-button { padding: 10px 20px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; margin: 10px; }");
        out.println(".main-button:hover { background-color: #45a049; }");
        out.println(".back-button { position: absolute; top: 10px; left: 10px; background-color: #f0f0f0; border: none; padding: 10px 20px; font-size: 14px; cursor: pointer; }");
        out.println(".success-message {  font-weight: bold; margin-top: 15px; font-size:larger; }");
        out.println("</style>");
        out.println("<button class='back-button' onclick=\"window.location.href='http://localhost:8080/demo';\">Back</button>");
        out.println("</head><body>");

        out.println("<div class='container'>");
        out.println("<h1>Customer Service</h1>");
        out.println("<button class='main-button' onclick=\"showForm('openTicketForm'); hideMessage();\">Open a Ticket</button>");
        out.println("<button class='main-button' onclick=\"showForm('statusTicketForm'); hideMessage();\">Status of a Ticket</button>");

        // Display success message if present
        if (successMessage != null) {
            out.println("<div id='successMessage' class='success-message'>" + successMessage + "</div>");
        }

        // Open Ticket Form
        out.println("<div id='openTicketForm' class='form-container' style='display:none;'>");
        out.println("<h2>Open a Ticket</h2>");
        out.println("<form action='customerService' method='post' enctype='multipart/form-data'>");
        out.println("Name: <input type='text' name='name' required><br>");
        out.println("Order ID: <input type='text' name='orderID' required><br>");
        out.println("Description: <textarea name='description' required></textarea><br>");
        out.println("Request Type: <select name='requestType' required>");
        out.println("<option value='refund'>Refund</option>");
        out.println("<option value='replace'>Replace</option>");
        out.println("</select><br>");
        out.println("Upload Image: <input type='file' name='image' accept='image/*'><br>");
        out.println("<button type='submit' name='action' value='submitTicket'>Submit Ticket</button>");
        out.println("</form>");
        out.println("</div>");

        // Status Ticket Form
        out.println("<div id='statusTicketForm' class='form-container' style='display:none;'>");
        out.println("<h2>Status of a Ticket</h2>");
        out.println("<form action='customerService' method='post'>");
        out.println("Ticket Number: <input type='text' name='ticketNumber' required><br>");
        out.println("<button type='submit' name='action' value='checkStatus'>Check Status</button>");
        out.println("</form>");
        out.println("</div>");

        // JavaScript to handle form display and hide success message
        out.println("<script>");
        out.println("function showForm(formId) {");
        out.println("  document.getElementById('openTicketForm').style.display = 'none';");
        out.println("  document.getElementById('statusTicketForm').style.display = 'none';");
        out.println("  document.getElementById(formId).style.display = 'block';");
        out.println("}");

        out.println("function hideMessage() {");
        out.println("  var message = document.getElementById('successMessage');");
        out.println("  if (message) {");
        out.println("    message.style.display = 'none';");
        out.println("  }");
        out.println("}");
        out.println("</script>");

        out.println("</div>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("submitTicket".equals(action)) {
            submitTicket(request, response);
        } else if ("checkStatus".equals(action)) {
            checkTicketStatus(request, response);
        }
    }

    private void submitTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String orderID = request.getParameter("orderID");
        String description = request.getParameter("description");
        Part filePart = request.getPart("image");
        String requestType = request.getParameter("requestType");

        String base64Image = null;
        if (filePart != null && filePart.getSize() > 0) {
            try (InputStream fileContent = filePart.getInputStream()) {
                byte[] imageBytes = fileContent.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            }
        }

        String decision = null;

        String prompt = "Evaluate the image and decide if it qualifies for Replace Order or Escalate to Human Agent. in Response, only provide value in 'replace' or 'escalate' accordingly.";
        String model = "gpt-4o-mini"; 

        try {
            decision = sendOpenAIImageRequest(base64Image, prompt, model);
        } catch (IOException e) {
            System.out.println("Error encoding image or sending request: " + e.getMessage());
        }

        if ( decision.toUpperCase().equals("REPLACE") ) {
            decision = requestType.toUpperCase();
        }

        String jdbcURL = "jdbc:mysql://127.0.0.1:3306/smarthomedemo?useSSL=false";
        String dbUser = "root";
        String dbPassword = "root";

        String sql = "INSERT INTO tickets (Ticket_ID, name, orderID, description, imageBase64, Status) VALUES (?, ?, ?, ?, ?, ?)";

        int ticketNumber = generateRandomTicketNumber();
        try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketNumber);
            statement.setString(2, name);
            statement.setString(3, orderID);
            statement.setString(4, description);
            statement.setString(5, base64Image);
            statement.setString(6, decision);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error", e);
        }

        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "Ticket submitted successfully! Status: "+ decision + " Your Ticket ID is: " + ticketNumber);
        response.sendRedirect("customerService");
    }


    private void checkTicketStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ticketNumber = Integer.parseInt(request.getParameter("ticketNumber"));

        String jdbcURL = "jdbc:mysql://127.0.0.1:3306/smarthomedemo?useSSL=false";
        String dbUser = "root";
        String dbPassword = "root";

        String status = "Ticket not found";
        String sql = "SELECT Status FROM tickets WHERE Ticket_ID = ?";

        try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticketNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                status = resultSet.getString("Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error", e);
        }

        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "Status of Ticket " + ticketNumber + ": " + status);
        response.sendRedirect("customerService");
    }

               private static String sendOpenAIImageRequest(String base64Image, String prompt, String model) throws IOException {
            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            textContent.addProperty("text", prompt);
        
            JsonObject imageUrlContent = new JsonObject();
            imageUrlContent.addProperty("type", "image_url");
            JsonObject imageUrl = new JsonObject();
            imageUrl.addProperty("url", "data:image/png;base64," + base64Image);
            imageUrlContent.add("image_url", imageUrl);
        
            JsonArray contentArray = new JsonArray();
            contentArray.add(textContent);
            contentArray.add(imageUrlContent);
        
            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.add("content", contentArray);
        
            JsonArray messagesArray = new JsonArray();
            messagesArray.add(message);
        
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.add("messages", messagesArray);
            requestBody.addProperty("max_tokens", 300);
        
            String requestBodyString = requestBody.toString();
        
            int retries = 0;
            String messageContent = null;
            while (retries < MAX_RETRIES) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(OPENAI_API_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);
        
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBodyString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
        
                    int statusCode = connection.getResponseCode();
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        try (InputStream is = connection.getInputStream();
                             BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                            StringBuilder responseBody = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                responseBody.append(responseLine.trim());
                            }
                            JsonObject jsonResponse = JsonParser.parseString(responseBody.toString()).getAsJsonObject();
                            messageContent = jsonResponse
                                    .getAsJsonArray("choices")
                                    .get(0)
                                    .getAsJsonObject()
                                    .getAsJsonObject("message")
                                    .get("content")
                                    .getAsString();
                            System.out.println("Response: " + messageContent);
                            break;
                        }
                    } else if (statusCode == 429) {
                        System.out.println("Rate limit exceeded. Retrying...");
                        retries++;
                        long backoff = (long) Math.pow(2, retries);
                        TimeUnit.SECONDS.sleep(backoff);
                    } else {
                        System.out.println("Error: " + statusCode + " - " + connection.getResponseMessage());
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted during backoff", e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        
            if (retries == MAX_RETRIES) {
                System.out.println("Max retries reached. Request failed.");
            }
        
            return messageContent.toUpperCase();
        }


    private int generateRandomTicketNumber() {
        return 1000 + random.nextInt(9000);
    }
}