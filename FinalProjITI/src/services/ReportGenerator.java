package services;

import models.Order;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportGenerator {
    private OrderProcessor orderProcessor;

    public ReportGenerator() {
        // In a real application, this would be injected
        this.orderProcessor = new OrderProcessor(
                new services.MenuManager(), new StudentManager(),
                new PaymentProcessor(), new services.LoyaltyProgram()
        );
    }

    public ReportGenerator(OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    public String generateDailySalesReport(Date date) {
        List<Order> dailyOrders = orderProcessor.getCompletedOrders().stream()
                .filter(order -> isSameDay(order.getOrderDate(), date))
                .collect(Collectors.toList());

        double totalRevenue = dailyOrders.stream()
                .mapToDouble(order -> order.getOrderCalculator().calculateTotal())
                .sum();

        int totalOrders = dailyOrders.size();

        return "Daily Sales Report for " + date +
                "\nTotal Orders: " + totalOrders +
                "\nTotal Revenue: $" + String.format("%.2f", totalRevenue) +
                "\nAverage Order Value: $" + (totalOrders > 0 ? String.format("%.2f", totalRevenue / totalOrders) : "0.00");
    }

    public String generateWeeklySalesReport(Date startDate) {
        // Simplified implementation - in real app would calculate week range
        List<Order> weeklyOrders = orderProcessor.getCompletedOrders().stream()
                .filter(order -> !order.getOrderDate().before(startDate))
                .collect(Collectors.toList());

        double totalRevenue = weeklyOrders.stream()
                .mapToDouble(order -> order.getOrderCalculator().calculateTotal())
                .sum();

        int totalOrders = weeklyOrders.size();

        return "Weekly Sales Report starting from " + startDate +
                "\nTotal Orders: " + totalOrders +
                "\nTotal Revenue: $" + String.format("%.2f", totalRevenue) +
                "\nAverage Order Value: $" + (totalOrders > 0 ? String.format("%.2f", totalRevenue / totalOrders) : "0.00");
    }

    public String generateLoyaltyReport() {
        // This would normally access student data
        return "Loyalty Program Report\n" +
                "Total Points Awarded: [Would calculate from database]\n" +
                "Total Rewards Redeemed: [Would calculate from database]\n" +
                "Most Popular Reward: [Would calculate from database]";
    }

    public String generateCallv5siteReport(Date date) {
        return "Call v5 Site Report for " + date + "\n[Implementation details would go here]";
    }

    public String generateView5siteReport(Date date) {
        return "View 5 Site Report for " + date + "\n[Implementation details would go here]";
    }

    public String generateLoginReport() {
        return "Login Report\n[Would show login statistics and patterns]";
    }

    private boolean isSameDay(Date date1, Date date2) {
        // Simplified implementation - in real app would use proper date comparison
        return date1.getDate() == date2.getDate() &&
                date1.getMonth() == date2.getMonth() &&
                date1.getYear() == date2.getYear();
    }
}