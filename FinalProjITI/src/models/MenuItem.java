package models;

import enums.MenuCategory;

public class MenuItem {
    private String itemId;
    private String name;
    private String description;
    private double price;
    private MenuCategory category;

    public MenuItem(String itemId, String name, String description,
                    double price, MenuCategory category) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public void updateDetails(String name, String description, double price, MenuCategory category) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price >= 0) {
            this.price = price;
        }
        if (category != null) {
            this.category = category;
        }
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public MenuCategory getCategory() { return category; }

    @Override
    public String toString() {
        return name + " - " + description + " ($" + price + ")";
    }
}