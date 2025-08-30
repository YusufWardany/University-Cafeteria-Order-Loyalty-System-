package services;

import models.Order;
import models.Reward;
import models.Student;
import java.util.HashMap;
import java.util.Map;

public class LoyaltyProgram {
    private double pointsPerCurrency;
    private Map<String, Reward> rewards;

    public LoyaltyProgram() {
        this.pointsPerCurrency = 0.1; // 1 point for every EGP 10 spent
        this.rewards = new HashMap<>();
        initializeDefaultRewards();
    }

    private void initializeDefaultRewards() {
        rewards.put("FREE_COFFEE", new Reward("FREE_COFFEE", "Free Coffee", 100, "Get a free coffee"));
        rewards.put("DISCOUNT_10", new Reward("DISCOUNT_10", "EGP 10 Discount", 50, "Get EGP 10 off your order"));
        rewards.put("FREE_DESSERT", new Reward("FREE_DESSERT", "Free Dessert", 150, "Get a free dessert"));
    }

    public int calculatePoints(Order order) {
        double orderAmount = order.getOrderCalculator().calculateTotal();
        int points = (int) (orderAmount * pointsPerCurrency);
        System.out.println("Awarding " + points + " points for order " + order.getOrderId());
        return points;
    }

    public void awardPoints(Order order) {
        Student student = order.getStudent();
        int points = calculatePoints(order);
        student.addPoints(points);
        System.out.println("Awarded " + points + " points to student " + student.getName());
    }

    public boolean redeemReward(Student student, String rewardId) {
        Reward reward = rewards.get(rewardId);
        if (reward == null) {
            System.out.println("Reward not found: " + rewardId);
            return false;
        }

        return reward.applyReward(student);
    }

    public void addReward(Reward reward) {
        rewards.put(reward.getRewardId(), reward);
        System.out.println("Added new reward: " + reward.getName());
    }

    public void displayAvailableRewards() {
        System.out.println("\n=== Available Rewards ===");
        rewards.values().forEach(System.out::println);
    }

    // Getters
    public Map<String, Reward> getRewards() { return rewards; }
    public double getPointsPerCurrency() { return pointsPerCurrency; }

    public void setPointsPerCurrency(double pointsPerCurrency) {
        this.pointsPerCurrency = pointsPerCurrency;
    }
}