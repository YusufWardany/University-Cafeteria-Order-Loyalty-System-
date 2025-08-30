package models;

public class Reward {
    private String rewardId;
    private String name;
    private int pointCost;
    private String description;

    public Reward(String rewardId, String name, int pointCost, String description) {
        this.rewardId = rewardId;
        this.name = name;
        this.pointCost = pointCost;
        this.description = description;
    }

    public boolean applyReward(Student student) {
        if (student.viewPointsBalance() >= pointCost) {
            student.deductPoints(pointCost);
            System.out.println("Reward '" + name + "' applied for student " + student.getName());
            return true;
        }
        System.out.println("Insufficient points for reward '" + name + "'");
        return false;
    }

    // Getters
    public String getRewardId() { return rewardId; }
    public String getName() { return name; }
    public int getPointCost() { return pointCost; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " - " + description + " (" + pointCost + " points)";
    }
}