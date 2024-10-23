import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;

public class CartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        List<Map<String, String>> cart = (List<Map<String, String>>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        String action = req.getParameter("action");

        if ("add".equals(action)) {
            String name = req.getParameter("name");
            String priceStr = req.getParameter("price");
            double price = Double.parseDouble(priceStr);

            boolean found = false;
            for (Map<String, String> item : cart) {
                if (item.get("name").equals(name)) {
                    int quantity = Integer.parseInt(item.get("quantity"));
                    item.put("quantity", String.valueOf(quantity + 1));
                    double totalPrice = (quantity + 1) * price;
                    item.put("totalPrice", String.valueOf(totalPrice));
                    found = true;
                    break;
                }
            }
            if (!found) {
                Map<String, String> newItem = new HashMap<>();
                newItem.put("name", name);
                newItem.put("price", priceStr);
                newItem.put("quantity", "1");
                newItem.put("totalPrice", priceStr);
                cart.add(newItem);
            }
        } else if ("delete".equals(action)) {
            String nameToDelete = req.getParameter("name");
            cart.removeIf(item -> item.get("name").equals(nameToDelete));
        }

        // Set content type to HTML
        res.setContentType("text/html");
        PrintWriter pw = res.getWriter();
        double totalCartPrice = 0.0;

        pw.println("<h2>Cart</h2>");
        pw.println("<ul id='cart-items'>");

        for (Map<String, String> item : cart) {
            String itemName = item.get("name");
            double itemPrice = Double.parseDouble(item.get("price"));
            int itemQuantity = Integer.parseInt(item.get("quantity"));
            double itemTotalPrice = Double.parseDouble(item.get("totalPrice"));

            totalCartPrice += itemTotalPrice;

            pw.println("<li>" + itemName + " - $" + String.format("%.2f", itemPrice) + " (Quantity: " + itemQuantity + ")");
            pw.println("<button onclick=\"deleteItem('" + itemName + "')\" style=\"background: red; border: none; width: 80px; padding: 8px;\">Delete</button></li>");
        }
        double discount = totalCartPrice * 0.10;
        totalCartPrice -= discount;
        

        pw.println("</ul>");
        pw.println("<p>Total items: " + cart.size() + "</p>");
        pw.println("<p>Total price: $" + String.format("%.2f", totalCartPrice) + "</p>");


        pw.println("<div id='checkout-form'>");
        pw.println("<button onclick=\"showCheckoutForm()\" style=\"background: cadetblue; border: none; width: 120px; padding: 8px;font-size: larger;\">Checkout</button>" );
        pw.println("</div>");

        pw.println("<div id='checkout-details' style='display:none;'>");
        pw.println("<h3>Checkout</h3>");
        pw.println("<form id='checkoutForm'>");
        pw.println("<p> 50$ extra charges will be added for home delivery</p>");
        pw.println("<label for='customerName'>Name:</label>");
        pw.println("<input type='text' id='customerName' name='customerName' required><br><br>");
        pw.println("<label for='address'>Delivery Address:</label>");
        pw.println("<textarea id='address' name='address' required></textarea><br><br>");
        pw.println("<label for='zipcode'>Zipcode:</label>");
        pw.println("<input type='text' id='zipcode' name='zipcode' required><br><br>");
        pw.println("<label for='paymentMethod'>Payment Method:</label>");
        pw.println("<select id='paymentMethod' name='paymentMethod' required>");
        pw.println("<option value='credit'>Credit Card</option>");
        pw.println("<option value='paypal'>PayPal</option>");
        pw.println("</select><br><br>");
        pw.println("<label for='customerName1'>Card Number:</label>");
        pw.println("<input type='text' id='customerName1' name='customerName1' required><br><br>");
        pw.println("<label for='deliveryMethod'>Delivery Method:</label>");
        pw.println("<select id='deliveryMethod' name='deliveryMethod' required>");
        pw.println("<option value='storePickup'>Store Pickup</option>");
        pw.println("<option value='homeDelivery'>Home Delivery</option>");
        pw.println("</select><br><br>");

        pw.println("<div id='storeLocation'>");
        pw.println("<label for='storeLocation'>Select Store Location:</label>");
        pw.println("<select id='storeLocation' name='storeLocation' required>");
        pw.println("<option value='store0'>Select a Store</option>");
        pw.println("<option value='store1'>123 Main St, Chicago, IL, 60616</option>");
        pw.println("<option value='store2'>456 Oak St, Evanston, IL, 60201</option>");
        pw.println("<option value='store3'>789 Pine St, Naperville, IL, 60540</option>");
        pw.println("<option value='store4'>101 Maple Ave, Aurora, IL, 60505</option>");
        pw.println("<option value='store5'>202 Elm St, Schaumburg, IL, 60173</option>");
        pw.println("<option value='store6'>303 Cedar Rd, Joliet, IL, 60431</option>");
        pw.println("<option value='store7'>404 Walnut St, Peoria, IL, 61614</option>");
        pw.println("<option value='store8'>505 Birch Ln, Springfield, IL, 62701</option>");
        pw.println("<option value='store9'>606 Ash Ave, Rockford, IL, 61101</option>");
        pw.println("<option value='store10'>707 Chestnut Blvd, Champaign, IL, 61820</option>");
        pw.println("</select><br><br>");
        pw.println("</div>");

        pw.println("<button type='button' onclick=\"placeOrder()\" style=\"background: green; border: none; padding: 10px;font-size: larger;\">Place Order</button>");
        pw.println("</form>");
        pw.println("</div>");

        pw.println("<div id='order-confirmation' style='display:none;'>");
        pw.println("<h3 id='confirmation-message'></h3>");
        pw.println("</div>");

        // JavaScript functions
        pw.println("<script>");
        pw.println("function showCheckoutForm() { document.getElementById('checkout-details').style.display = 'block'; }");
        pw.println("function placeOrder() {");
        pw.println("  var form = document.getElementById('checkoutForm');");
        pw.println("  var customerName = form.customerName.value;");
        pw.println("  var address = form.address.value;");
        pw.println("  var zipcode = form.zipcode.value;");
        pw.println("  var paymentMethod = form.paymentMethod.value;");
        pw.println("  var cardNumber = form.customerName1.value;");
        pw.println("  var deliveryMethod = form.deliveryMethod.value;");
        pw.println("  var storeLocation = form.storeLocation.value;");
        pw.println("  window.location.href = 'order.java?customerName=' + encodeURIComponent(customerName) +");
        pw.println("    '&address=' + encodeURIComponent(address) +");
        pw.println("    '&zipcode=' + encodeURIComponent(zipcode) +");
        pw.println("    '&paymentMethod=' + encodeURIComponent(paymentMethod) +");
        pw.println("    '&cardNumber=' + encodeURIComponent(cardNumber) +");
        pw.println("    '&deliveryMethod=' + encodeURIComponent(deliveryMethod) +");
        pw.println("    '&storeLocation=' + encodeURIComponent(storeLocation);");

        //print cart totalprice
        pw.println("  var cartTotalPrice = " + totalCartPrice + ";");

        pw.println("}");
        pw.println("</script>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }
}
