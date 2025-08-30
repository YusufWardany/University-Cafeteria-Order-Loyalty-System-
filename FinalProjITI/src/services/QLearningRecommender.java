package services;

import models.MenuItem;
import models.Student;
import enums.MenuCategory;
import java.util.*;
import java.util.stream.Collectors;

public class QLearningRecommender {
    private Map<String, Map<String, Double>> qTable; // studentId -> (itemId -> Q-value)
    private double learningRate = 0.1;
    private double discountFactor = 0.9;
    private double explorationRate = 0.3;
    private Random random;

    public QLearningRecommender() {
        this.qTable = new HashMap<>();
        this.random = new Random();
    }

    // Update Q-value when a student orders an item
    public void updateQValue(Student student, MenuItem item, double reward) {
        String studentId = student.getStudentId();
        String itemId = item.getItemId();

        // Initialize Q-table for student if not exists
        qTable.putIfAbsent(studentId, new HashMap<>());
        Map<String, Double> studentQValues = qTable.get(studentId);

        // Get current Q-value or initialize to 0
        double currentQValue = studentQValues.getOrDefault(itemId, 0.0);

        // Q-learning formula: Q(s,a) = Q(s,a) + α[r + γ*maxQ(s',a') - Q(s,a)]
        double maxFutureQ = getMaxFutureQ(studentId, item.getCategory());
        double newQValue = currentQValue + learningRate * (reward + discountFactor * maxFutureQ - currentQValue);

        studentQValues.put(itemId, newQValue);

        System.out.println("Updated Q-value for student " + student.getName() +
                ", item " + item.getName() + ": " + newQValue);
    }

    private double getMaxFutureQ(String studentId, MenuCategory category) {
        if (!qTable.containsKey(studentId)) {
            return 0.0;
        }

        // Get maximum Q-value for items in the same category
        return qTable.get(studentId).entrySet().stream()
                .filter(entry -> {
                    // This would require a way to get menu item by ID
                    // For simplicity, we'll assume we can check category from item ID pattern
                    return entry.getKey().startsWith(getCategoryPrefix(category));
                })
                .mapToDouble(Map.Entry::getValue)
                .max()
                .orElse(0.0);
    }

    private String getCategoryPrefix(MenuCategory category) {
        switch (category) {
            case MAIN_COURSE: return "M";
            case SNACK: return "S";
            case DRINK: return "D";
            case DESSERT: return "DS";
            default: return "";
        }
    }

    // Get recommended items for a student
    public List<MenuItem> getRecommendations(Student student, List<MenuItem> allMenuItems, int maxRecommendations) {
        String studentId = student.getStudentId();

        if (!qTable.containsKey(studentId) || random.nextDouble() < explorationRate) {
            // Exploration: return random items
            return getRandomRecommendations(allMenuItems, maxRecommendations);
        }

        // Exploitation: return items with highest Q-values
        Map<String, Double> studentQValues = qTable.get(studentId);

        return allMenuItems.stream()
                .sorted((a, b) -> {
                    double qA = studentQValues.getOrDefault(a.getItemId(), 0.0);
                    double qB = studentQValues.getOrDefault(b.getItemId(), 0.0);
                    return Double.compare(qB, qA); // Descending order
                })
                .limit(maxRecommendations)
                .collect(Collectors.toList());
    }

    private List<MenuItem> getRandomRecommendations(List<MenuItem> allMenuItems, int maxRecommendations) {
        List<MenuItem> shuffled = new ArrayList<>(allMenuItems);
        Collections.shuffle(shuffled);
        return shuffled.stream()
                .limit(maxRecommendations)
                .collect(Collectors.toList());
    }

    // Calculate reward based on student behavior
    public double calculateReward(Student student, MenuItem item, int quantity, double orderTotal) {
        double baseReward = 1.0;

        // Higher reward for expensive items (more profit)
        double priceReward = item.getPrice() / 10.0;

        // Higher reward for multiple quantities
        double quantityReward = quantity * 0.5;

        // Higher reward if student doesn't usually order this category
        double diversityReward = calculateDiversityReward(student, item.getCategory());

        return baseReward + priceReward + quantityReward + diversityReward;
    }

    private double calculateDiversityReward(Student student, MenuCategory category) {
        String studentId = student.getStudentId();
        if (!qTable.containsKey(studentId)) {
            return 1.0; // Encourage trying new categories
        }

        // Count how many times student ordered from this category
        long categoryCount = qTable.get(studentId).keySet().stream()
                .filter(itemId -> itemId.startsWith(getCategoryPrefix(category)))
                .count();

        // Higher reward for less ordered categories
        return 1.0 / (categoryCount + 1);
    }

    // Get Q-values for monitoring and debugging
    public Map<String, Double> getStudentQValues(String studentId) {
        return qTable.getOrDefault(studentId, new HashMap<>());
    }

    // Reset Q-values for a student (if needed)
    public void resetStudentQValues(String studentId) {
        qTable.remove(studentId);
    }

    // Save and load Q-table (for persistence)
    public void saveQTable() {
        // Would implement database persistence here
        System.out.println("Q-table saved with " + qTable.size() + " students");
    }

    public void loadQTable() {
        // Would implement database loading here
        System.out.println("Q-table loaded");
    }
}