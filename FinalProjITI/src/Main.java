import javafx.application.Application;
import models.*;
import services.*;
import enums.*;
import services.LoyaltyProgram;
import services.MenuManager;

import java.util.Date;
import java.util.Scanner;
import javafx.application.Application;


public class Main {
    private static StudentManager studentManager;
    private static MenuManager menuManager;
    private static LoyaltyProgram loyaltyProgram;
    private static OrderProcessor orderProcessor;
    private static PaymentProcessor paymentProcessor;
    private static ReportGenerator reportGenerator;
    private static Student currentStudent;
    private static Staff currentStaff;
    private static Scanner scanner;

    public static void main(String[] args) {

        Application.launch(CafeteriaSystemGUI.class, args);
        initializeSystem();
        scanner = new Scanner(System.in);

        System.out.println("=== University Cafeteria Order & Loyalty System ===");

        boolean running = true;
        while (running) {
            if (currentStudent == null && currentStaff == null) {
                running = showMainMenu();
            } else if (currentStudent != null) {
                running = showStudentMenu();
            } else if (currentStaff != null) {
                running = showStaffMenu();
            }
        }

        scanner.close();
        System.out.println("Thank you for using the University Cafeteria System!");
    }

    private static void initializeSystem() {
        studentManager = new StudentManager();
        menuManager = new MenuManager();
        loyaltyProgram = new LoyaltyProgram();
        paymentProcessor = new PaymentProcessor();
        orderProcessor = new OrderProcessor(menuManager, studentManager, paymentProcessor, loyaltyProgram);
        reportGenerator = new ReportGenerator(orderProcessor);
    }

    private static boolean showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Student Login");
        System.out.println("2. Staff Login");
        System.out.println("3. Student Registration");
        System.out.println("4. Exit");
        System.out.print("Please choose an option: ");

        int choice = getIntInput(1, 4);

        switch (choice) {
            case 1:
                studentLogin();
                break;
            case 2:
                staffLogin();
                break;
            case 3:
                studentRegistration();
                break;
            case 4:
                return false;
        }

        return true;
    }

    private static boolean showStudentMenu() {
        System.out.println("\n=== Student Menu ===");
        System.out.println("Welcome, " + currentStudent.getName() + "!");
        System.out.println("Loyalty Points: " + currentStudent.viewPointsBalance());
        System.out.println("1. View Menu");
        System.out.println("2. Place Order");
        System.out.println("3. View Loyalty Rewards");
        System.out.println("4. Redeem Reward");
        System.out.println("5. Order History");
        System.out.println("6. Logout");
        System.out.print("Please choose an option: ");

        int choice = getIntInput(1, 6);

        switch (choice) {
            case 1:
                menuManager.displayMenu();
                break;
            case 2:
                placeOrder();
                break;
            case 3:
                loyaltyProgram.displayAvailableRewards();
                break;
            case 4:
                redeemReward();
                break;
            case 5:
                viewOrderHistory();
                break;
            case 6:
                System.out.println("Logging out...");
                currentStudent = null;
                break;
        }

        return true;
    }

    private static boolean showStaffMenu() {
        System.out.println("\n=== Staff Menu ===");
        System.out.println("Welcome, " + currentStaff.getName() + "!");
        System.out.println("1. View Pending Orders");
        System.out.println("2. Update Order Status");
        System.out.println("3. Manage Menu");
        System.out.println("4. Generate Reports");
        System.out.println("5. View All Students");
        System.out.println("6. Logout");
        System.out.print("Please choose an option: ");

        int choice = getIntInput(1, 6);

        switch (choice) {
            case 1:
                viewPendingOrders();
                break;
            case 2:
                updateOrderStatus();
                break;
            case 3:
                manageMenu();
                break;
            case 4:
                generateReports();
                break;
            case 5:
                studentManager.displayAllStudents();
                break;
            case 6:
                System.out.println("Logging out...");
                currentStaff = null;
                break;
        }

        return true;
    }

    private static void studentLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        currentStudent = studentManager.login(username, password);

        if (currentStudent != null) {
            System.out.println("Login successful! Welcome, " + currentStudent.getName());
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void staffLogin() {
        // Simplified staff login - in real app would have proper authentication
        System.out.print("Staff Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Hardcoded staff credentials for demo
        if ("staff".equals(username) && "password".equals(password)) {
            currentStaff = new Staff("U2001", "staff", "password", "STF1001", "Cafeteria Staff");
            System.out.println("Staff login successful!");
        } else {
            System.out.println("Invalid staff credentials.");
        }
    }

    private static void studentRegistration() {
        System.out.print("Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        boolean success = studentManager.registerStudent(studentId, username, password, name, email);
        if (success) {
            System.out.println("Registration successful! You can now login.");
        }
    }

    private static void placeOrder() {
        menuManager.displayMenu();
        Order order = new Order("ORD" + System.currentTimeMillis(), currentStudent);

        boolean addingItems = true;
        while (addingItems) {
            System.out.print("Enter item ID to add to order (or 'done' to finish): ");
            String itemId = scanner.nextLine();

            if ("done".equalsIgnoreCase(itemId)) {
                addingItems = false;
            } else {
                MenuItem item = menuManager.getMenuItemById(itemId);
                if (item != null) {
                    order.addItem(item);
                    System.out.println("Current total: $" + order.getOrderCalculator().calculateTotal());
                } else {
                    System.out.println("Item not found. Please try again.");
                }
            }
        }

        if (order.getOrderItems().getItemCount() > 0) {
            System.out.println("Order summary:");
            order.getOrderItems().getItems().forEach(item ->
                    System.out.println("  " + item.getName() + " - $" + item.getPrice()));
            System.out.println("Total: $" + order.getOrderCalculator().calculateTotal());

            System.out.print("Confirm order? (yes/no): ");
            String confirm = scanner.nextLine();

            if ("yes".equalsIgnoreCase(confirm)) {
                // Process payment
                Payment payment = new Payment("PAY" + System.currentTimeMillis(), order, PaymentMethod.CREDIT_CARD);
                if (payment.processPayment()) {
                    orderProcessor.processOrder(order);
                    currentStudent.addOrderToHistory(order.getOrderId());
                    System.out.println("Order placed successfully! Your order ID is: " + order.getOrderId());
                } else {
                    System.out.println("Payment failed. Order not placed.");
                }
            } else {
                System.out.println("Order cancelled.");
            }
        } else {
            System.out.println("No items in order. Order cancelled.");
        }
    }

    private static void redeemReward() {
        loyaltyProgram.displayAvailableRewards();
        System.out.print("Enter reward ID to redeem: ");
        String rewardId = scanner.nextLine();

        boolean success = loyaltyProgram.redeemReward(currentStudent, rewardId);
        if (success) {
            System.out.println("Reward redeemed successfully!");
        } else {
            System.out.println("Failed to redeem reward.");
        }
    }

    private static void viewOrderHistory() {
        System.out.println("\n=== Order History ===");
        if (currentStudent.getOrderHistory().isEmpty()) {
            System.out.println("No order history found.");
        } else {
            for (String orderId : currentStudent.getOrderHistory()) {
                Order order = orderProcessor.getOrderById(orderId);
                if (order != null) {
                    System.out.println(order);
                }
            }
        }
    }

    private static void viewPendingOrders() {
        System.out.println("\n=== Pending Orders ===");
        var pendingOrders = orderProcessor.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders.");
        } else {
            pendingOrders.forEach(System.out::println);
        }
    }

    private static void updateOrderStatus() {
        viewPendingOrders();
        System.out.print("Enter order ID to update: ");
        String orderId = scanner.nextLine();

        Order order = orderProcessor.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        System.out.println("Current status: " + order.getStatus());
        System.out.println("Select new status:");
        System.out.println("1. PREPARING");
        System.out.println("2. READY");
        System.out.println("3. COMPLETED");
        System.out.println("4. CANCELLED");
        System.out.print("Choose option: ");

        int choice = getIntInput(1, 4);
        OrderStatus newStatus = null;

        switch (choice) {
            case 1: newStatus = OrderStatus.PREPARING; break;
            case 2: newStatus = OrderStatus.READY; break;
            case 3: newStatus = OrderStatus.COMPLETED; break;
            case 4: newStatus = OrderStatus.CANCELLED; break;
        }

        if (newStatus != null) {
            orderProcessor.updateOrderStatus(orderId, newStatus);
            System.out.println("Order status updated successfully.");
        }
    }

    private static void manageMenu() {
        System.out.println("\n=== Menu Management ===");
        System.out.println("1. Add Menu Item");
        System.out.println("2. Update Menu Item");
        System.out.println("3. Remove Menu Item");
        System.out.println("4. View Menu");
        System.out.print("Choose option: ");

        int choice = getIntInput(1, 4);

        switch (choice) {
            case 1:
                addMenuItem();
                break;
            case 2:
                updateMenuItem();
                break;
            case 3:
                removeMenuItem();
                break;
            case 4:
                menuManager.displayMenu();
                break;
        }
    }

    private static void addMenuItem() {
        System.out.print("Item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Price: ");
        double price = getDoubleInput();

        System.out.println("Category:");
        System.out.println("1. MAIN_COURSE");
        System.out.println("2. SNACK");
        System.out.println("3. DRINK");
        System.out.println("4. DESSERT");
        System.out.print("Choose category: ");

        int categoryChoice = getIntInput(1, 4);
        MenuCategory category = null;

        switch (categoryChoice) {
            case 1: category = MenuCategory.MAIN_COURSE; break;
            case 2: category = MenuCategory.SNACK; break;
            case 3: category = MenuCategory.DRINK; break;
            case 4: category = MenuCategory.DESSERT; break;
        }

        MenuItem newItem = new MenuItem(itemId, name, description, price, category);
        menuManager.addMenuItem(newItem);
    }

    private static void updateMenuItem() {
        menuManager.displayMenu();
        System.out.print("Enter item ID to update: ");
        String itemId = scanner.nextLine();

        MenuItem existingItem = menuManager.getMenuItemById(itemId);
        if (existingItem == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.print("New name (" + existingItem.getName() + "): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = existingItem.getName();

        System.out.print("New description (" + existingItem.getDescription() + "): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = existingItem.getDescription();

        System.out.print("New price (" + existingItem.getPrice() + "): ");
        String priceInput = scanner.nextLine();
        double price = priceInput.isEmpty() ? existingItem.getPrice() : Double.parseDouble(priceInput);

        MenuItem updatedItem = new MenuItem(
                existingItem.getItemId(),
                name,
                description,
                price,
                existingItem.getCategory()
        );

        menuManager.updateMenuItem(itemId, updatedItem);
    }

    private static void removeMenuItem() {
        menuManager.displayMenu();
        System.out.print("Enter item ID to remove: ");
        String itemId = scanner.nextLine();

        menuManager.removeMenuItem(itemId);
    }

    private static void generateReports() {
        System.out.println("\n=== Report Generation ===");
        System.out.println("1. Daily Sales Report");
        System.out.println("2. Weekly Sales Report");
        System.out.println("3. Loyalty Report");
        System.out.println("4. Call v5 Site Report");
        System.out.println("5. View 5 Site Report");
        System.out.println("6. Login Report");
        System.out.print("Choose option: ");

        int choice = getIntInput(1, 6);
        Date today = new Date();

        switch (choice) {
            case 1:
                System.out.println(reportGenerator.generateDailySalesReport(today));
                break;
            case 2:
                System.out.println(reportGenerator.generateWeeklySalesReport(today));
                break;
            case 3:
                System.out.println(reportGenerator.generateLoyaltyReport());
                break;
            case 4:
                System.out.println(reportGenerator.generateCallv5siteReport(today));
                break;
            case 5:
                System.out.println(reportGenerator.generateView5siteReport(today));
                break;
            case 6:
                System.out.println(reportGenerator.generateLoginReport());
                break;
        }
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.print("Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}