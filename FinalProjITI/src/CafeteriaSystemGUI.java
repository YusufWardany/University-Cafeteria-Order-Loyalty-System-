import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;
import models.MenuItem;
import services.*;
import enums.*;
import services.LoyaltyProgram;
import services.MenuManager;

import java.util.*;

public class CafeteriaSystemGUI extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    private StudentManager studentManager;
    private MenuManager menuManager;
    private LoyaltyProgram loyaltyProgram;
    private OrderProcessor orderProcessor;
    private Student currentStudent;
    private Staff currentStaff;

    private ObservableList<Order> allOrders;
    private ObservableList<MenuItem> menuItems;
    private ObservableList<Student> allStudents;
    private TabPane contentTabs;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("University Cafeteria System");

        initializeServices();
        initRootLayout();
        showLoginScreen();
    }

    private void initializeServices() {
        studentManager = new StudentManager();
        menuManager = new MenuManager();
        loyaltyProgram = new LoyaltyProgram();
        PaymentProcessor paymentProcessor = new PaymentProcessor();
        orderProcessor = new OrderProcessor(menuManager, studentManager, paymentProcessor, loyaltyProgram);

        allOrders = FXCollections.observableArrayList();
        menuItems = FXCollections.observableArrayList(menuManager.getMenuItems());
        allStudents = FXCollections.observableArrayList();

        // Load sample data
        loadSampleData();
    }

    private void loadSampleData() {
        // Load sample orders
        allOrders.add(new Order("ORD1001", studentManager.getStudent("S1001")));
        allOrders.add(new Order("ORD1002", studentManager.getStudent("S1002")));
        allOrders.add(new Order("ORD1003", studentManager.getStudent("S1003")));

        // Load sample students
        allStudents.addAll(studentManager.getAllStudents());
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        Scene scene = new Scene(rootLayout, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showLoginScreen() {
        StackPane loginContainer = new StackPane();
        loginContainer.getStyleClass().add("login-container");

        VBox loginCard = new VBox(30);
        loginCard.getStyleClass().add("login-card");
        loginCard.setMaxWidth(400);
        loginCard.setAlignment(Pos.CENTER);

        // Header
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("UNIVERSITY CAFETERIA");
        titleLabel.getStyleClass().add("title");
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label("Order & Loyalty System");
        subtitleLabel.getStyleClass().add("subtitle");
        subtitleLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.8);");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // Login Form
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("form-field");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");
        passwordField.setMaxWidth(300);

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Staff", "Admin");
        roleComboBox.setPromptText("Select Role");
        roleComboBox.getStyleClass().add("form-field");
        roleComboBox.setMaxWidth(300);

        Button loginButton = new Button("SIGN IN");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(300);

        Hyperlink registerLink = new Hyperlink("Create New Student Account");
        registerLink.setStyle("-fx-text-fill: white; -fx-underline: true;");

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), roleComboBox.getValue()));
        registerLink.setOnAction(e -> showRegistrationScreen());

        formBox.getChildren().addAll(usernameField, passwordField, roleComboBox, loginButton, registerLink);
        loginCard.getChildren().addAll(headerBox, formBox);

        loginContainer.getChildren().add(loginCard);
        rootLayout.setCenter(loginContainer);
    }

    private void handleLogin(String username, String password, String role) {
        if (authenticateUser(username, password, role)) {
            if ("Student".equals(role)) {
                showStudentDashboard();
            } else if ("Staff".equals(role)) {
                showStaffDashboard();
            } else if ("Admin".equals(role)) {
                showAdminDashboard();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed",
                    "Invalid credentials. Demo credentials:\n" +
                            "Student: john_doe / password123\n" +
                            "Staff: staff / password\n" +
                            "Admin: admin / admin123");
        }
    }

    private boolean authenticateUser(String username, String password, String role) {
        if ("Student".equals(role)) {
            currentStudent = studentManager.login(username, password);
            return currentStudent != null;
        } else if ("Staff".equals(role)) {
            if ("staff".equals(username) && "password".equals(password)) {
                currentStaff = new Staff("U2001", "staff", "password", "STF1001", "Cafeteria Staff");
                return true;
            }
        } else if ("Admin".equals(role)) {
            if ("admin".equals(username) && "admin123".equals(password)) {
                currentStaff = new Staff("U2002", "admin", "admin123", "ADM1001", "System Administrator");
                return true;
            }
        }
        return false;
    }

    private void showStudentDashboard() {
        BorderPane dashboard = new BorderPane();
        dashboard.getStyleClass().add("main-container");

        // Sidebar
        VBox sidebar = createStudentSidebar();
        dashboard.setLeft(sidebar);

        // Content
        contentTabs = new TabPane();
        contentTabs.getStyleClass().add("content-area");

        Tab dashboardTab = new Tab("Dashboard", createStudentDashboardContent());
        Tab menuTab = new Tab("Menu", createMenuView());
        Tab cartTab = new Tab("Cart (" + currentStudent.getCartItemCount() + ")", createCartView());
        Tab ordersTab = new Tab("My Orders", createOrderHistoryView());
        Tab rewardsTab = new Tab("Rewards", createRewardsView());
        Tab profileTab = new Tab("Profile", createProfileView());

        contentTabs.getTabs().addAll(dashboardTab, menuTab, cartTab, ordersTab, rewardsTab, profileTab);
        dashboard.setCenter(contentTabs);

        rootLayout.setCenter(dashboard);
    }

    private VBox createStudentSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(30, 20, 30, 20));

        // User Profile
        VBox profileBox = new VBox(15);
        profileBox.setAlignment(Pos.CENTER);

        ImageView avatar = new ImageView(new Image("file:user.png"));
        avatar.setFitWidth(60);
        avatar.setFitHeight(60);

        Label nameLabel = new Label(currentStudent.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label pointsLabel = new Label(currentStudent.viewPointsBalance() + " Loyalty Points");
        pointsLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

        Label cartLabel = new Label(currentStudent.getCartItemCount() + " items in cart");
        cartLabel.setStyle("-fx-text-fill: #27ae60;");

        profileBox.getChildren().addAll(avatar, nameLabel, pointsLabel, cartLabel);

        // Navigation
        VBox navBox = new VBox(5);

        String[] navItems = {"Dashboard", "Menu", "Cart", "My Orders", "Rewards", "Profile"};
        for (String item : navItems) {
            Button navBtn = new Button(item);
            navBtn.getStyleClass().add("nav-button");
            navBtn.setMaxWidth(Double.MAX_VALUE);
            navBtn.setOnAction(e -> navigateToTab(item));
            navBox.getChildren().add(navBtn);
        }

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> showLoginScreen());

        sidebar.getChildren().addAll(profileBox, navBox, logoutBtn);
        return sidebar;
    }

    private void navigateToTab(String tabName) {
        for (Tab tab : contentTabs.getTabs()) {
            if (tab.getText().startsWith(tabName)) {
                contentTabs.getSelectionModel().select(tab);
                break;
            }
        }
    }

    private ScrollPane createStudentDashboardContent() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));

        // Welcome Section
        VBox welcomeCard = new VBox(15);
        welcomeCard.getStyleClass().add("card");
        welcomeCard.setPadding(new Insets(25));

        Label welcomeTitle = new Label("Welcome back, " + currentStudent.getName() + "!");
        welcomeTitle.getStyleClass().add("section-title");

        Label welcomeText = new Label("Ready to place your next order? Check out our daily specials and personalized recommendations.");
        welcomeText.setWrapText(true);
        welcomeText.getStyleClass().add("text-muted");

        Button orderBtn = new Button("Start Ordering");
        orderBtn.getStyleClass().add("primary-button");
        orderBtn.setOnAction(e -> navigateToTab("Menu"));

        welcomeCard.getChildren().addAll(welcomeTitle, welcomeText, orderBtn);

        // Stats Section
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        VBox pointsCard = createStatCard("Loyalty Points", String.valueOf(currentStudent.viewPointsBalance()), "#3498db");
        VBox ordersCard = createStatCard("Total Orders", String.valueOf(currentStudent.getOrderHistory().size()), "#27ae60");
        VBox cartCard = createStatCard("Cart Items", String.valueOf(currentStudent.getCartItemCount()), "#e74c3c");

        statsGrid.add(pointsCard, 0, 0);
        statsGrid.add(ordersCard, 1, 0);
        statsGrid.add(cartCard, 2, 0);

        content.getChildren().addAll(welcomeCard, statsGrid);
        return new ScrollPane(content);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stats-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("text-muted");

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private ScrollPane createMenuView() {
        VBox menuContent = new VBox(20);
        menuContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Cafeteria Menu");
        titleLabel.getStyleClass().add("section-title");

        // Menu items grid
        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(20);
        menuGrid.setVgap(20);
        menuGrid.setPadding(new Insets(20, 0, 0, 0));

        int col = 0;
        int row = 0;

        // Use a Set to track displayed items and avoid duplicates
        Set<String> displayedItems = new HashSet<>();

        for (MenuItem item : menuManager.getMenuItems()) {
            // Check if we've already displayed this item
            if (!displayedItems.contains(item.getItemId())) {
                VBox itemCard = createMenuItemCard(item);
                menuGrid.add(itemCard, col, row);
                displayedItems.add(item.getItemId());

                col++;
                if (col > 2) {
                    col = 0;
                    row++;
                }
            }
        }

        menuContent.getChildren().addAll(titleLabel, menuGrid);
        return new ScrollPane(menuContent);
    }

    private VBox createMenuItemCard(MenuItem item) {
        VBox card = new VBox(15);
        card.getStyleClass().add("menu-item-card");
        card.setPadding(new Insets(20));

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("menu-item-title");

        Label descLabel = new Label(item.getDescription());
        descLabel.getStyleClass().add("text-muted");
        descLabel.setWrapText(true);

        Label priceLabel = new Label("$" + item.getPrice());
        priceLabel.getStyleClass().add("menu-item-price");

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        quantitySpinner.setPrefWidth(80);

        Button addButton = new Button("Add to Cart");
        addButton.getStyleClass().add("success-button");
        addButton.setOnAction(e -> {
            currentStudent.addToCart(item, quantitySpinner.getValue());
            updateCartTab();
            showAlert(Alert.AlertType.INFORMATION, "Added to Cart",
                    quantitySpinner.getValue() + " x " + item.getName() + " added to cart!");
            quantitySpinner.getValueFactory().setValue(1);
        });

        actionBox.getChildren().addAll(new Label("Qty:"), quantitySpinner, addButton);
        card.getChildren().addAll(nameLabel, descLabel, priceLabel, actionBox);
        return card;
    }
    private ScrollPane createCartView() {
        VBox cartContent = new VBox(20);
        cartContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Shopping Cart");
        titleLabel.getStyleClass().add("section-title");

        if (currentStudent.getCartItems().isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty. Start adding items from the Menu!");
            emptyLabel.getStyleClass().add("text-muted");
            cartContent.getChildren().addAll(titleLabel, emptyLabel);
        } else {
            VBox cartItemsBox = new VBox(15);

            for (CartItem cartItem : currentStudent.getCartItems()) {
                HBox itemBox = new HBox(15);
                itemBox.setAlignment(Pos.CENTER_LEFT);
                itemBox.getStyleClass().add("card");
                itemBox.setPadding(new Insets(15));

                Label nameLabel = new Label(cartItem.getItem().getName());
                nameLabel.setStyle("-fx-font-weight: bold;");

                Label priceLabel = new Label("$" + cartItem.getItem().getPrice());

                Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, cartItem.getQuantity());
                quantitySpinner.setPrefWidth(60);
                quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    currentStudent.updateCartQuantity(cartItem.getItem().getItemId(), newVal);
                    updateCartTab();
                });

                Label totalLabel = new Label("$" + String.format("%.2f", cartItem.getTotalPrice()));
                totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

                Button removeButton = new Button("Remove");
                removeButton.getStyleClass().add("danger-button");
                removeButton.setOnAction(e -> {
                    currentStudent.removeFromCart(cartItem.getItem().getItemId());
                    updateCartTab();
                });

                itemBox.getChildren().addAll(nameLabel, priceLabel,
                        new Label("Qty:"), quantitySpinner, totalLabel, removeButton);
                cartItemsBox.getChildren().add(itemBox);
            }

            // Cart summary
            VBox summaryBox = new VBox(10);
            summaryBox.getStyleClass().add("card");
            summaryBox.setPadding(new Insets(20));

            double cartTotal = currentStudent.getCartTotal();

            Label totalLabel = new Label("Total: $" + String.format("%.2f", cartTotal));
            totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Button checkoutButton = new Button("Proceed to Checkout");
            checkoutButton.getStyleClass().add("primary-button");
            checkoutButton.setOnAction(e -> processOrder());

            Button clearButton = new Button("Clear Cart");
            clearButton.getStyleClass().add("secondary-button");
            clearButton.setOnAction(e -> {
                currentStudent.clearCart();
                updateCartTab();
            });

            HBox buttonBox = new HBox(10, checkoutButton, clearButton);
            summaryBox.getChildren().addAll(totalLabel, buttonBox);

            cartContent.getChildren().addAll(titleLabel, cartItemsBox, summaryBox);
        }

        return new ScrollPane(cartContent);
    }


    private void processOrder() {
        if (currentStudent.getCartItems().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Your cart is empty. Add items before checkout.");
            return;
        }

        Order order = new Order("ORD" + System.currentTimeMillis(), currentStudent);

        // Add all cart items to order
        for (CartItem cartItem : currentStudent.getCartItems()) {
            for (int i = 0; i < cartItem.getQuantity(); i++) {
                order.addItem(cartItem.getItem());
            }
        }

        // Process payment
        Payment payment = new Payment("PAY" + System.currentTimeMillis(), order, PaymentMethod.CREDIT_CARD);
        if (payment.processPayment()) {
            if (orderProcessor.processOrder(order)) {
                currentStudent.addOrderToHistory(order.getOrderId());
                currentStudent.clearCart();
                updateCartTab();

                // Add to all orders list for staff/admin view
                allOrders.add(order);

                showAlert(Alert.AlertType.INFORMATION, "Order Successful",
                        "Order placed successfully! Order ID: " + order.getOrderId() +
                                "\nTotal: $" + order.getOrderCalculator().calculateTotal());

                // Refresh orders tab
                contentTabs.getTabs().get(3).setContent(createOrderHistoryView());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Failed", "Payment processing failed. Please try again.");
        }
    }

    private void updateCartTab() {
        // Update cart tab title with item count
        for (Tab tab : contentTabs.getTabs()) {
            if (tab.getText().startsWith("Cart")) {
                tab.setText("Cart (" + currentStudent.getCartItemCount() + ")");
                break;
            }
        }

        // Always refresh cart view to ensure it's up to date
        Tab cartTab = contentTabs.getTabs().stream()
                .filter(tab -> tab.getText().startsWith("Cart"))
                .findFirst()
                .orElse(null);

        if (cartTab != null) {
            cartTab.setContent(createCartView());
        }

    }
    private ScrollPane createOrderHistoryView() {
        VBox historyContent = new VBox(20);
        historyContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Order History");
        titleLabel.getStyleClass().add("section-title");

        if (currentStudent.getOrderHistory().isEmpty()) {
            Label emptyLabel = new Label("No orders yet. Place your first order from the Menu!");
            emptyLabel.getStyleClass().add("text-muted");
            historyContent.getChildren().addAll(titleLabel, emptyLabel);
        } else {
            VBox ordersBox = new VBox(15);

            for (String orderId : currentStudent.getOrderHistory()) {
                Order order = orderProcessor.getOrderById(orderId);
                if (order != null) {
                    VBox orderCard = new VBox(10);
                    orderCard.getStyleClass().add("card");
                    orderCard.setPadding(new Insets(15));

                    HBox headerBox = new HBox(10);
                    headerBox.setAlignment(Pos.CENTER_LEFT);

                    Label orderIdLabel = new Label("Order #" + order.getOrderId());
                    orderIdLabel.setStyle("-fx-font-weight: bold;");

                    Label dateLabel = new Label(order.getOrderDate().toString());
                    dateLabel.getStyleClass().add("text-muted");

                    Label statusLabel = new Label(order.getStatus().toString());
                    statusLabel.getStyleClass().add("status-" + order.getStatus().toString().toLowerCase());

                    Label totalLabel = new Label("Total: $" + order.getOrderCalculator().calculateTotal());
                    totalLabel.setStyle("-fx-font-weight: bold;");

                    headerBox.getChildren().addAll(orderIdLabel, dateLabel, statusLabel, totalLabel);

                    // Order items
                    VBox itemsBox = new VBox(5);
                    for (MenuItem item : order.getOrderItems().getItems()) {
                        Label itemLabel = new Label("• " + item.getName() + " - $" + item.getPrice());
                        itemsBox.getChildren().add(itemLabel);
                    }

                    orderCard.getChildren().addAll(headerBox, itemsBox);
                    ordersBox.getChildren().add(orderCard);
                }
            }

            historyContent.getChildren().addAll(titleLabel, ordersBox);
        }

        return new ScrollPane(historyContent);
    }

    private ScrollPane createRewardsView() {
        VBox rewardsContent = new VBox(20);
        rewardsContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Loyalty Rewards");
        titleLabel.getStyleClass().add("section-title");

        Label pointsLabel = new Label("Your Points: " + currentStudent.viewPointsBalance());
        pointsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        GridPane rewardsGrid = new GridPane();
        rewardsGrid.setHgap(20);
        rewardsGrid.setVgap(20);

        int col = 0;
        int row = 0;
        for (Map.Entry<String, Reward> entry : loyaltyProgram.getRewards().entrySet()) {
            Reward reward = entry.getValue();
            VBox rewardCard = createRewardCard(reward);
            rewardsGrid.add(rewardCard, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        rewardsContent.getChildren().addAll(titleLabel, pointsLabel, rewardsGrid);
        return new ScrollPane(rewardsContent);
    }

    private VBox createRewardCard(Reward reward) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label nameLabel = new Label(reward.getName());
        nameLabel.getStyleClass().add("section-title");

        Label descLabel = new Label(reward.getDescription());
        descLabel.setWrapText(true);
        descLabel.getStyleClass().add("text-muted");

        Label pointsLabel = new Label(reward.getPointCost() + " points");
        pointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Button redeemBtn = new Button("Redeem Now");
        redeemBtn.getStyleClass().add("primary-button");
        redeemBtn.setDisable(currentStudent.viewPointsBalance() < reward.getPointCost());
        redeemBtn.setOnAction(e -> redeemReward(reward));

        card.getChildren().addAll(nameLabel, descLabel, pointsLabel, redeemBtn);
        return card;
    }

    private void redeemReward(Reward reward) {
        boolean success = loyaltyProgram.redeemReward(currentStudent, reward.getRewardId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Reward Redeemed",
                    "You've successfully redeemed: " + reward.getName());
            // Refresh rewards view and profile
            contentTabs.getTabs().get(4).setContent(createRewardsView());
            contentTabs.getTabs().get(5).setContent(createProfileView());
        } else {
            showAlert(Alert.AlertType.ERROR, "Redemption Failed",
                    "Failed to redeem reward. Please check your points balance.");
        }
    }

    private ScrollPane createProfileView() {
        VBox profileContent = new VBox(20);
        profileContent.setPadding(new Insets(30));

        Label titleLabel = new Label("Student Profile");
        titleLabel.getStyleClass().add("section-title");

        GridPane profileGrid = new GridPane();
        profileGrid.setHgap(20);
        profileGrid.setVgap(15);
        profileGrid.setPadding(new Insets(20, 0, 20, 0));

        // Profile information
        profileGrid.add(new Label("Student ID:"), 0, 0);
        profileGrid.add(new Label(currentStudent.getStudentId()), 1, 0);

        profileGrid.add(new Label("Name:"), 0, 1);
        profileGrid.add(new Label(currentStudent.getName()), 1, 1);

        profileGrid.add(new Label("Email:"), 0, 2);
        profileGrid.add(new Label(currentStudent.getEmail()), 1, 2);

        profileGrid.add(new Label("Username:"), 0, 3);
        profileGrid.add(new Label(currentStudent.getUsername()), 1, 3);

        profileGrid.add(new Label("Loyalty Points:"), 0, 4);
        profileGrid.add(new Label(String.valueOf(currentStudent.viewPointsBalance())), 1, 4);

        profileGrid.add(new Label("Total Orders:"), 0, 5);
        profileGrid.add(new Label(String.valueOf(currentStudent.getOrderHistory().size())), 1, 5);

        profileGrid.add(new Label("Cart Items:"), 0, 6);
        profileGrid.add(new Label(String.valueOf(currentStudent.getCartItemCount())), 1, 6);

        VBox statsBox = new VBox(15);
        statsBox.getStyleClass().add("card");
        statsBox.setPadding(new Insets(20));
        statsBox.getChildren().addAll(
                new Label("Profile Information"),
                profileGrid
        );

        profileContent.getChildren().addAll(titleLabel, statsBox);
        return new ScrollPane(profileContent);
    }

    private void showStaffDashboard() {
        BorderPane dashboard = new BorderPane();
        dashboard.getStyleClass().add("main-container");

        // Sidebar
        VBox sidebar = createStaffSidebar();
        dashboard.setLeft(sidebar);

        // Content
        contentTabs = new TabPane();
        contentTabs.getStyleClass().add("content-area");

        Tab dashboardTab = new Tab("Dashboard", createStaffDashboardContent());
        Tab ordersTab = new Tab("Order Management", createOrderManagementView());
        Tab menuTab = new Tab("Menu Management", createMenuManagementView());
        Tab reportsTab = new Tab("Reports", createReportsView());

        contentTabs.getTabs().addAll(dashboardTab, ordersTab, menuTab, reportsTab);
        dashboard.setCenter(contentTabs);

        rootLayout.setCenter(dashboard);
    }

    private void showAdminDashboard() {
        BorderPane dashboard = new BorderPane();
        dashboard.getStyleClass().add("main-container");

        // Sidebar
        VBox sidebar = createAdminSidebar();
        dashboard.setLeft(sidebar);

        // Content
        contentTabs = new TabPane();
        contentTabs.getStyleClass().add("content-area");

        Tab dashboardTab = new Tab("Dashboard", createAdminDashboardContent());
        Tab ordersTab = new Tab("Order Management", createOrderManagementView());
        Tab menuTab = new Tab("Menu Management", createMenuManagementView());
        Tab studentsTab = new Tab("Student Management", createStudentManagementView());
        Tab reportsTab = new Tab("Reports", createAdminReportsView());
        Tab systemTab = new Tab("System Settings", createSystemSettingsView());

        contentTabs.getTabs().addAll(dashboardTab, ordersTab, menuTab, studentsTab, reportsTab, systemTab);
        dashboard.setCenter(contentTabs);

        rootLayout.setCenter(dashboard);
    }

    private VBox createStaffSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(30, 20, 30, 20));

        Label welcomeLabel = new Label("Staff Portal");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        VBox navBox = new VBox(5);
        String[] navItems = {"Dashboard", "Order Management", "Menu Management", "Reports"};

        for (String item : navItems) {
            Button navBtn = new Button(item);
            navBtn.getStyleClass().add("nav-button");
            navBtn.setMaxWidth(Double.MAX_VALUE);
            navBtn.setOnAction(e -> navigateToTab(item));
            navBox.getChildren().add(navBtn);
        }

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> showLoginScreen());

        sidebar.getChildren().addAll(welcomeLabel, navBox, logoutBtn);
        return sidebar;
    }

    private VBox createAdminSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(30, 20, 30, 20));

        Label welcomeLabel = new Label("Admin Portal");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        VBox navBox = new VBox(5);
        String[] navItems = {"Dashboard", "Order Management", "Menu Management", "Student Management", "Reports", "System Settings"};

        for (String item : navItems) {
            Button navBtn = new Button(item);
            navBtn.getStyleClass().add("nav-button");
            navBtn.setMaxWidth(Double.MAX_VALUE);
            navBtn.setOnAction(e -> navigateToTab(item));
            navBox.getChildren().add(navBtn);
        }

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> showLoginScreen());

        sidebar.getChildren().addAll(welcomeLabel, navBox, logoutBtn);
        return sidebar;
    }

    private ScrollPane createStaffDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Staff Dashboard");
        titleLabel.getStyleClass().add("section-title");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        // Add staff statistics cards
        long pendingOrders = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING)
                .count();

        VBox pendingOrdersCard = createStatCard("Pending Orders", String.valueOf(pendingOrders), "#f39c12");
        VBox totalOrdersCard = createStatCard("Total Orders", String.valueOf(allOrders.size()), "#3498db");
        VBox revenueCard = createStatCard("Today's Revenue", "$" + calculateDailyRevenue(), "#27ae60");

        statsGrid.add(pendingOrdersCard, 0, 0);
        statsGrid.add(totalOrdersCard, 1, 0);
        statsGrid.add(revenueCard, 2, 0);

        // Quick actions
        VBox actionsBox = new VBox(15);
        actionsBox.getStyleClass().add("card");
        actionsBox.setPadding(new Insets(20));

        Label actionsLabel = new Label("Quick Actions");
        actionsLabel.getStyleClass().add("section-title");

        Button viewOrdersBtn = new Button("View All Orders");
        Button manageMenuBtn = new Button("Manage Menu Items");
        Button generateReportBtn = new Button("Generate Daily Report");

        viewOrdersBtn.getStyleClass().add("primary-button");
        manageMenuBtn.getStyleClass().add("primary-button");
        generateReportBtn.getStyleClass().add("primary-button");

        viewOrdersBtn.setOnAction(e -> navigateToTab("Order Management"));
        manageMenuBtn.setOnAction(e -> navigateToTab("Menu Management"));
        generateReportBtn.setOnAction(e -> generateDailyReport());

        actionsBox.getChildren().addAll(actionsLabel, viewOrdersBtn, manageMenuBtn, generateReportBtn);

        content.getChildren().addAll(titleLabel, statsGrid, actionsBox);
        return new ScrollPane(content);
    }

    private ScrollPane createAdminDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.getStyleClass().add("section-title");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        // Add admin statistics cards
        long pendingOrders = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING)
                .count();

        VBox pendingOrdersCard = createStatCard("Pending Orders", String.valueOf(pendingOrders), "#f39c12");
        VBox totalOrdersCard = createStatCard("Total Orders", String.valueOf(allOrders.size()), "#3498db");
        VBox totalStudentsCard = createStatCard("Total Students", String.valueOf(allStudents.size()), "#27ae60");
        VBox totalRevenueCard = createStatCard("Total Revenue", "$" + calculateTotalRevenue(), "#e74c3c");

        statsGrid.add(pendingOrdersCard, 0, 0);
        statsGrid.add(totalOrdersCard, 1, 0);
        statsGrid.add(totalStudentsCard, 2, 0);
        statsGrid.add(totalRevenueCard, 0, 1);

        // System overview
        VBox systemBox = new VBox(15);
        systemBox.getStyleClass().add("card");
        systemBox.setPadding(new Insets(20));

        Label systemLabel = new Label("System Overview");
        systemLabel.getStyleClass().add("section-title");

        Label systemStatus = new Label("🟢 System Status: Online");
        Label databaseStatus = new Label("🟢 Database: Connected");
        Label usersOnline = new Label("👥 Active Users: 12");

        systemBox.getChildren().addAll(systemLabel, systemStatus, databaseStatus, usersOnline);

        content.getChildren().addAll(titleLabel, statsGrid, systemBox);
        return new ScrollPane(content);
    }

    private double calculateDailyRevenue() {
        return allOrders.stream()
                .filter(order -> isToday(order.getOrderDate()))
                .mapToDouble(order -> order.getOrderCalculator().calculateTotal())
                .sum();
    }

    private double calculateTotalRevenue() {
        return allOrders.stream()
                .mapToDouble(order -> order.getOrderCalculator().calculateTotal())
                .sum();
    }

    private boolean isToday(Date date) {
        Date today = new Date();
        return date.getDate() == today.getDate() &&
                date.getMonth() == today.getMonth() &&
                date.getYear() == today.getYear();
    }

    private ScrollPane createOrderManagementView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Order Management");
        titleLabel.getStyleClass().add("section-title");

        // Order filter
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Preparing", "Ready", "Completed", "Cancelled");
        statusFilter.setValue("All");

        Button filterBtn = new Button("Apply Filter");
        filterBtn.getStyleClass().add("secondary-button");

        filterBox.getChildren().addAll(new Label("Filter by Status:"), statusFilter, filterBtn);

        // Orders table
        TableView<Order> ordersTable = new TableView<>();
        ordersTable.getStyleClass().add("table-view");
        ordersTable.setPrefHeight(600);

        TableColumn<Order, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrderId()));

        TableColumn<Order, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudent().getName()));

        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrderDate().toString()));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getOrderCalculator().calculateTotal())));

        ordersTable.getColumns().addAll(orderIdCol, studentCol, dateCol, statusCol, totalCol);
        ordersTable.getItems().addAll(allOrders);

        // Order actions
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<OrderStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(OrderStatus.values());

        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.getStyleClass().add("primary-button");
        updateStatusBtn.setOnAction(e -> {
            Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
            if (selectedOrder != null && statusCombo.getValue() != null) {
                selectedOrder.changeStatus(statusCombo.getValue());
                ordersTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Status Updated",
                        "Order status updated to: " + statusCombo.getValue());
            }
        });

        actionBox.getChildren().addAll(new Label("Update Status:"), statusCombo, updateStatusBtn);

        content.getChildren().addAll(titleLabel, filterBox, ordersTable, actionBox);
        return new ScrollPane(content);
    }

    private ScrollPane createMenuManagementView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Menu Management");
        titleLabel.getStyleClass().add("section-title");

        // Menu items table
        TableView<MenuItem> menuTable = new TableView<>();
        menuTable.getStyleClass().add("table-view");
        menuTable.setPrefHeight(400);

        TableColumn<MenuItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getItemId()));

        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<MenuItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%.2f", data.getValue().getPrice())));

        TableColumn<MenuItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCategory().toString()));

        menuTable.getColumns().addAll(idCol, nameCol, priceCol, categoryCol);
        menuTable.getItems().addAll(menuItems);

        // Menu actions
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button addItemBtn = new Button("Add New Item");
        Button editItemBtn = new Button("Edit Item");
        Button deleteItemBtn = new Button("Delete Item");

        addItemBtn.getStyleClass().add("primary-button");
        editItemBtn.getStyleClass().add("secondary-button");
        deleteItemBtn.getStyleClass().add("danger-button");

        addItemBtn.setOnAction(e -> showAddMenuItemDialog());
        editItemBtn.setOnAction(e -> {
            MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditMenuItemDialog(selected);
            }
        });
        deleteItemBtn.setOnAction(e -> {
            MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteMenuItem(selected);
            }
        });

        actionBox.getChildren().addAll(addItemBtn, editItemBtn, deleteItemBtn);

        content.getChildren().addAll(titleLabel, menuTable, actionBox);
        return new ScrollPane(content);
    }

    private void showAddMenuItemDialog() {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Add New Menu Item");
        dialog.setHeaderText("Enter menu item details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Item ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        ComboBox<MenuCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(MenuCategory.values());

        grid.add(new Label("Item ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a menu item
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return new MenuItem(
                            idField.getText(),
                            nameField.getText(),
                            descField.getText(),
                            Double.parseDouble(priceField.getText()),
                            categoryCombo.getValue()
                    );
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid price.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(menuItem -> {
            menuManager.addMenuItem(menuItem);
            menuItems.add(menuItem);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Menu item added successfully!");
        });
    }

    private void showEditMenuItemDialog(MenuItem item) {
        Dialog<MenuItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Menu Item");
        dialog.setHeaderText("Edit menu item details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(item.getName());
        TextField descField = new TextField(item.getDescription());
        TextField priceField = new TextField(String.valueOf(item.getPrice()));
        ComboBox<MenuCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(MenuCategory.values());
        categoryCombo.setValue(item.getCategory());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new MenuItem(
                            item.getItemId(),
                            nameField.getText(),
                            descField.getText(),
                            Double.parseDouble(priceField.getText()),
                            categoryCombo.getValue()
                    );
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid price.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedItem -> {
            menuManager.updateMenuItem(item.getItemId(), updatedItem);
            menuItems.set(menuItems.indexOf(item), updatedItem);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Menu item updated successfully!");
        });
    }

    private void deleteMenuItem(MenuItem item) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Menu Item");
        confirmation.setContentText("Are you sure you want to delete " + item.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                menuManager.removeMenuItem(item.getItemId());
                menuItems.remove(item);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Menu item deleted successfully!");
            }
        });
    }

    private ScrollPane createStudentManagementView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Student Management");
        titleLabel.getStyleClass().add("section-title");

        // Students table
        TableView<Student> studentsTable = new TableView<>();
        studentsTable.getStyleClass().add("table-view");
        studentsTable.setPrefHeight(500);

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudentId()));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Student, String> pointsCol = new TableColumn<>("Points");
        pointsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().viewPointsBalance())));

        TableColumn<Student, String> ordersCol = new TableColumn<>("Total Orders");
        ordersCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getOrderHistory().size())));

        studentsTable.getColumns().addAll(idCol, nameCol, emailCol, pointsCol, ordersCol);
        studentsTable.getItems().addAll(allStudents);

        // Student actions
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button addPointsBtn = new Button("Add Points");
        Button resetPasswordBtn = new Button("Reset Password");
        Button viewDetailsBtn = new Button("View Details");

        addPointsBtn.getStyleClass().add("primary-button");
        resetPasswordBtn.getStyleClass().add("secondary-button");
        viewDetailsBtn.getStyleClass().add("secondary-button");

        actionBox.getChildren().addAll(addPointsBtn, resetPasswordBtn, viewDetailsBtn);

        content.getChildren().addAll(titleLabel, studentsTable, actionBox);
        return new ScrollPane(content);
    }

    private ScrollPane createReportsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Reports");
        titleLabel.getStyleClass().add("section-title");

        VBox reportsBox = new VBox(15);
        reportsBox.getStyleClass().add("card");
        reportsBox.setPadding(new Insets(20));

        Button dailyReportBtn = new Button("Generate Daily Report");
        Button weeklyReportBtn = new Button("Generate Weekly Report");
        Button salesReportBtn = new Button("Generate Sales Report");
        Button loyaltyReportBtn = new Button("Generate Loyalty Report");

        dailyReportBtn.getStyleClass().add("primary-button");
        weeklyReportBtn.getStyleClass().add("primary-button");
        salesReportBtn.getStyleClass().add("primary-button");
        loyaltyReportBtn.getStyleClass().add("primary-button");

        dailyReportBtn.setOnAction(e -> generateDailyReport());
        weeklyReportBtn.setOnAction(e -> generateWeeklyReport());
        salesReportBtn.setOnAction(e -> generateSalesReport());
        loyaltyReportBtn.setOnAction(e -> generateLoyaltyReport());

        reportsBox.getChildren().addAll(dailyReportBtn, weeklyReportBtn, salesReportBtn, loyaltyReportBtn);
        content.getChildren().addAll(titleLabel, reportsBox);

        return new ScrollPane(content);
    }

    private ScrollPane createAdminReportsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("Advanced Reports");
        titleLabel.getStyleClass().add("section-title");

        GridPane reportsGrid = new GridPane();
        reportsGrid.setHgap(20);
        reportsGrid.setVgap(20);

        // Report cards
        VBox financialReportCard = createReportCard("Financial Report", "Revenue analysis and financial metrics", "#27ae60");
        VBox studentReportCard = createReportCard("Student Activity", "Student ordering patterns and behavior", "#3498db");
        VBox inventoryReportCard = createReportCard("Inventory Analysis", "Menu item performance and inventory", "#f39c12");
        VBox systemReportCard = createReportCard("System Performance", "System usage and performance metrics", "#e74c3c");

        reportsGrid.add(financialReportCard, 0, 0);
        reportsGrid.add(studentReportCard, 1, 0);
        reportsGrid.add(inventoryReportCard, 0, 1);
        reportsGrid.add(systemReportCard, 1, 1);

        content.getChildren().addAll(titleLabel, reportsGrid);
        return new ScrollPane(content);
    }

    private VBox createReportCard(String title, String description, String color) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + color + ";");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("text-muted");
        descLabel.setWrapText(true);

        Button generateBtn = new Button("Generate Report");
        generateBtn.getStyleClass().add("primary-button");

        card.getChildren().addAll(titleLabel, descLabel, generateBtn);
        return card;
    }

    private ScrollPane createSystemSettingsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label titleLabel = new Label("System Settings");
        titleLabel.getStyleClass().add("section-title");

        VBox settingsBox = new VBox(15);
        settingsBox.getStyleClass().add("card");
        settingsBox.setPadding(new Insets(20));

        // System settings
        Label systemLabel = new Label("System Configuration");
        systemLabel.getStyleClass().add("section-title");

        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton lightTheme = new RadioButton("Light Theme");
        RadioButton darkTheme = new RadioButton("Dark Theme");
        lightTheme.setToggleGroup(themeGroup);
        darkTheme.setToggleGroup(themeGroup);
        lightTheme.setSelected(true);

        CheckBox notifications = new CheckBox("Enable Notifications");
        notifications.setSelected(true);

        CheckBox autoBackup = new CheckBox("Enable Auto Backup");
        autoBackup.setSelected(true);

        // Loyalty program settings
        Label loyaltyLabel = new Label("Loyalty Program Settings");
        loyaltyLabel.getStyleClass().add("section-title");

        Spinner<Double> pointsRate = new Spinner<>(0.01, 1.0, 0.1, 0.01);
        pointsRate.setPrefWidth(100);

        Spinner<Integer> minRedeem = new Spinner<>(10, 1000, 50, 10);
        minRedeem.setPrefWidth(100);

        Button saveSettingsBtn = new Button("Save Settings");
        saveSettingsBtn.getStyleClass().add("primary-button");

        settingsBox.getChildren().addAll(
                systemLabel, lightTheme, darkTheme, notifications, autoBackup,
                new Separator(),
                loyaltyLabel,
                new HBox(10, new Label("Points per dollar:"), pointsRate),
                new HBox(10, new Label("Minimum redeem points:"), minRedeem),
                new Separator(),
                saveSettingsBtn
        );

        content.getChildren().addAll(titleLabel, settingsBox);
        return new ScrollPane(content);
    }

    private void generateDailyReport() {
        String report = "=== DAILY SALES REPORT ===\n" +
                "Date: " + new Date() + "\n" +
                "Total Orders: " + allOrders.stream().filter(order -> isToday(order.getOrderDate())).count() + "\n" +
                "Total Revenue: $" + calculateDailyRevenue() + "\n" +
                "Average Order Value: $" + (calculateDailyRevenue() / Math.max(1, allOrders.stream().filter(order -> isToday(order.getOrderDate())).count()));

        showTextDialog("Daily Sales Report", report);
    }

    private void generateWeeklyReport() {
        String report = "=== WEEKLY SALES REPORT ===\n" +
                "Week Ending: " + new Date() + "\n" +
                "Total Orders: " + allOrders.size() + "\n" +
                "Total Revenue: $" + calculateTotalRevenue() + "\n" +
                "Most Popular Item: " + getMostPopularItem();

        showTextDialog("Weekly Sales Report", report);
    }

    private void generateSalesReport() {
        String report = "=== SALES ANALYSIS REPORT ===\n" +
                "Total Orders: " + allOrders.size() + "\n" +
                "Total Revenue: $" + calculateTotalRevenue() + "\n" +
                "Average Order Value: $" + (calculateTotalRevenue() / Math.max(1, allOrders.size())) + "\n" +
                "Busiest Day: Monday\n" +
                "Top Selling Category: " + getTopCategory();

        showTextDialog("Sales Analysis Report", report);
    }

    private void generateLoyaltyReport() {
        String report = "=== LOYALTY PROGRAM REPORT ===\n" +
                "Total Students: " + allStudents.size() + "\n" +
                "Total Points Distributed: " + calculateTotalPoints() + "\n" +
                "Average Points per Student: " + (calculateTotalPoints() / Math.max(1, allStudents.size())) + "\n" +
                "Rewards Redeemed: 15\n" +
                "Most Popular Reward: Free Coffee";

        showTextDialog("Loyalty Program Report", report);
    }

    private String getMostPopularItem() {
        return "Cheeseburger";
    }

    private String getTopCategory() {
        return "Main Course";
    }

    private int calculateTotalPoints() {
        return allStudents.stream().mapToInt(Student::viewPointsBalance).sum();
    }

    private void showTextDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setPrefSize(600, 400);

        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
    }

    private void showRegistrationScreen() {
        VBox registerContent = new VBox(25);
        registerContent.setPadding(new Insets(30));
        registerContent.getStyleClass().add("form-pane");

        Label titleLabel = new Label("Student Registration");
        titleLabel.getStyleClass().add("section-title");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20, 0, 20, 0));

        // Form fields
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");
        studentIdField.getStyleClass().add("form-field");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.getStyleClass().add("form-field");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.getStyleClass().add("form-field");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("form-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("form-field");

        // Add to grid
        formGrid.add(new Label("Student ID:"), 0, 0);
        formGrid.add(studentIdField, 1, 0);
        formGrid.add(new Label("Full Name:"), 0, 1);
        formGrid.add(nameField, 1, 1);
        formGrid.add(new Label("Email:"), 0, 2);
        formGrid.add(emailField, 1, 2);
        formGrid.add(new Label("Username:"), 0, 3);
        formGrid.add(usernameField, 1, 3);
        formGrid.add(new Label("Password:"), 0, 4);
        formGrid.add(passwordField, 1, 4);
        formGrid.add(new Label("Confirm Password:"), 0, 5);
        formGrid.add(confirmPasswordField, 1, 5);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("primary-button");

        Button backBtn = new Button("Back to Login");
        backBtn.getStyleClass().add("secondary-button");

        registerBtn.setOnAction(e -> handleRegistration(
                studentIdField.getText(), nameField.getText(), emailField.getText(),
                usernameField.getText(), passwordField.getText(), confirmPasswordField.getText()
        ));

        backBtn.setOnAction(e -> showLoginScreen());

        buttonBox.getChildren().addAll(registerBtn, backBtn);
        registerContent.getChildren().addAll(titleLabel, formGrid, buttonBox);

        ScrollPane scrollPane = new ScrollPane(registerContent);
        scrollPane.setFitToWidth(true);
        rootLayout.setCenter(scrollPane);
    }

    private void handleRegistration(String studentId, String name, String email,
                                    String username, String password, String confirmPassword) {

        if (validateRegistration(studentId, name, email, username, password, confirmPassword)) {
            boolean success = studentManager.registerStudent(studentId, username, password, name, email);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                        "Account created successfully! You can now login.");
                showLoginScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Could not create account. Student ID or username may already exist.");
            }
        }
    }

    private boolean validateRegistration(String studentId, String name, String email,
                                         String username, String password, String confirmPassword) {

        if (studentId.isEmpty() || name.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return false;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password should be at least 6 characters.");
            return false;
        }

        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}