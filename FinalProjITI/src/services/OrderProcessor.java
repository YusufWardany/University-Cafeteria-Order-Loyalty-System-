package services;

import interfaces.IOrderProcessor;
import interfaces.IMenuProvider;
import interfaces.IStudentRepository;
import interfaces.IPaymentProcessor;
import models.Order;
import models.MenuItem;
import models.Student;
import enums.OrderStatus;
import java.util.*;
import java.util.stream.Collectors;

public class OrderProcessor implements IOrderProcessor {
    private List<Order> currentOrders;
    private IMenuProvider menuProvider;
    private IStudentRepository studentRepository;
    private IPaymentProcessor paymentProcessor;
    private services.LoyaltyProgram loyaltyProgram;
    private NotificationService notificationService;
    private QLearningRecommender recommender;

    public OrderProcessor(IMenuProvider menuProvider, IStudentRepository studentRepository,
                          IPaymentProcessor paymentProcessor, services.LoyaltyProgram loyaltyProgram) {
        this.currentOrders = new ArrayList<>();
        this.menuProvider = menuProvider;
        this.studentRepository = studentRepository;
        this.paymentProcessor = paymentProcessor;
        this.loyaltyProgram = loyaltyProgram;
        this.notificationService = new NotificationService();
        this.recommender = new QLearningRecommender();
    }

    public OrderProcessor(services.MenuManager menuManager, StudentManager studentManager,
                          PaymentProcessor paymentProcessor, services.LoyaltyProgram loyaltyProgram) {
        this((IMenuProvider) menuManager, (IStudentRepository) studentManager,
                (IPaymentProcessor) paymentProcessor, loyaltyProgram);
    }

    @Override
    public boolean processOrder(Order order) {
        if (order.confirmOrder()) {
            currentOrders.add(order);
            System.out.println("Order " + order.getOrderId() + " processed successfully");

            // Update reinforcement learning model
            updateRecommendationModel(order);

            // Award loyalty points
            loyaltyProgram.awardPoints(order);

            // Notify student
            notificationService.sendNotification(order.getStudent(),
                    "Your order #" + order.getOrderId() + " has been received and is being processed.");

            return true;
        }

        System.out.println("Failed to process order " + order.getOrderId());
        return false;
    }

    private void updateRecommendationModel(Order order) {
        order.getOrderItems().getItems().forEach(item -> {
            // Calculate reward based on item price and quantity
            double reward = recommender.calculateReward(
                    order.getStudent(),
                    item,
                    1, // Assuming quantity 1 for simplicity
                    order.getOrderCalculator().calculateTotal()
            );

            // Update Q-values
            recommender.updateQValue(order.getStudent(), item, reward);
        });

        // Save Q-table periodically
        if (currentOrders.size() % 10 == 0) {
            recommender.saveQTable();
        }
    }

    // Get personalized recommendations for a student
    public List<MenuItem> getPersonalizedRecommendations(Student student, int maxRecommendations) {
        return recommender.getRecommendations(student, menuProvider.getMenuItems(), maxRecommendations);
    }

    // Get recommendation scores for monitoring
    public Map<String, Double> getRecommendationScores(Student student) {
        return recommender.getStudentQValues(student.getStudentId());
    }

    // Reset recommendation data for a student
    public void resetStudentRecommendations(String studentId) {
        recommender.resetStudentQValues(studentId);
    }

    @Override
    public boolean updateOrderStatus(String orderId, OrderStatus status) {
        Optional<Order> orderOpt = currentOrders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst();

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.changeStatus(status);

            // Notify student when order is ready
            if (status == OrderStatus.READY) {
                notificationService.sendNotification(order.getStudent(),
                        "Your order #" + orderId + " is ready for pickup!");
            }

            return true;
        }

        System.out.println("Order with ID " + orderId + " not found");
        return false;
    }

    public List<Order> getPendingOrders() {
        List<Order> pendingOrders = new ArrayList<>();
        for (Order order : currentOrders) {
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING) {
                pendingOrders.add(order);
            }
        }
        return pendingOrders;
    }

    public List<Order> getCompletedOrders() {
        List<Order> completedOrders = new ArrayList<>();
        for (Order order : currentOrders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                completedOrders.add(order);
            }
        }
        return completedOrders;
    }

    public Order getOrderById(String orderId) {
        return currentOrders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    // Getters for dependencies
    public IMenuProvider getMenuProvider() { return menuProvider; }
    public IStudentRepository getStudentRepository() { return studentRepository; }
    public services.LoyaltyProgram getLoyaltyProgram() { return loyaltyProgram; }
    public QLearningRecommender getRecommender() { return recommender; }
}