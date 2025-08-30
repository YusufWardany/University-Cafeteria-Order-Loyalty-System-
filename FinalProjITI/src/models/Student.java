package models;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String studentId;
    private List<String> orderHistory;
    private List<CartItem> cartItems;
    private int loyaltyPoints;

    public Student(String userId, String username, String password, String name, String email, String studentId) {
        super(userId, username, password, name, email);
        this.studentId = studentId;
        this.orderHistory = new ArrayList<>();
        this.cartItems = new ArrayList<>();
        this.loyaltyPoints = 0;
    }

    public String getStudentId() {
        return studentId;
    }

    public List<String> getOrderHistory() {
        return orderHistory;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int viewPointsBalance() {
        return loyaltyPoints;
    }

    public void addPoints(int points) {
        this.loyaltyPoints += points;
    }

    public void deductPoints(int points) {
        this.loyaltyPoints = Math.max(0, this.loyaltyPoints - points);
    }

    public void addOrderToHistory(String orderId) {
        orderHistory.add(orderId);
    }

    public void addToCart(MenuItem item, int quantity) {
        // Check if item already exists in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getItemId().equals(item.getItemId())) {
                // Update quantity if item already exists
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                return;
            }
        }

        // Add new item if it doesn't exist
        cartItems.add(new CartItem(item, quantity));
    }

    public void updateCartQuantity(String itemId, int newQuantity) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getItemId().equals(itemId)) {
                if (newQuantity <= 0) {
                    removeFromCart(itemId);
                } else {
                    cartItem.setQuantity(newQuantity);
                }
                return;
            }
        }
    }

    public void removeFromCart(String itemId) {
        cartItems.removeIf(cartItem -> cartItem.getItem().getItemId().equals(itemId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getCartItemCount() {
        int total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getQuantity();
        }
        return total;
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getTotalPrice();
        }
        return total;
    }
}