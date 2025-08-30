package services;

import interfaces.IMenuProvider;
import models.MenuItem;
import enums.MenuCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

public class MenuManager implements IMenuProvider {
    private Map<String, MenuItem> menuItemsMap; // Use Map to prevent duplicates by ID
    private List<MenuItem> menuItemsList; // Maintain a list for ordered access

    public MenuManager() {
        this.menuItemsMap = new HashMap<>();
        this.menuItemsList = new ArrayList<>();
        initializeDefaultMenu();
    }

    private void initializeDefaultMenu() {
        // Use helper method to ensure no duplicates
        addMenuItem(new MenuItem("M1", "Cheeseburger", "Delicious beef cheeseburger", 8.99, MenuCategory.MAIN_COURSE));
        addMenuItem(new MenuItem("M2", "Chicken Sandwich", "Grilled chicken sandwich", 7.99, MenuCategory.MAIN_COURSE));
        addMenuItem(new MenuItem("M3", "Vegetable Wrap", "Fresh vegetable wrap", 6.99, MenuCategory.MAIN_COURSE));

        addMenuItem(new MenuItem("S1", "French Fries", "Crispy golden fries", 3.99, MenuCategory.SNACK));
        addMenuItem(new MenuItem("S2", "Onion Rings", "Crispy onion rings", 4.99, MenuCategory.SNACK));

        addMenuItem(new MenuItem("D1", "Coffee", "Fresh brewed coffee", 2.99, MenuCategory.DRINK));
        addMenuItem(new MenuItem("D2", "Soda", "Carbonated beverage", 1.99, MenuCategory.DRINK));
        addMenuItem(new MenuItem("D3", "Fresh Juice", "Freshly squeezed juice", 3.99, MenuCategory.DRINK));

        addMenuItem(new MenuItem("DS1", "Chocolate Cake", "Rich chocolate cake", 4.99, MenuCategory.DESSERT));
        addMenuItem(new MenuItem("DS2", "Ice Cream", "Vanilla ice cream", 3.99, MenuCategory.DESSERT));
    }

    @Override
    public List<MenuItem> getMenuItems() {
        // Return a new list to prevent external modification
        return new ArrayList<>(menuItemsList);
    }

    @Override
    public boolean addMenuItem(MenuItem item) {
        if (item == null || item.getItemId() == null) {
            System.out.println("Invalid menu item");
            return false;
        }

        if (menuItemsMap.containsKey(item.getItemId())) {
            System.out.println("Menu item with ID " + item.getItemId() + " already exists");
            return false;
        }

        menuItemsMap.put(item.getItemId(), item);
        menuItemsList.add(item);
        System.out.println("Added new menu item: " + item.getName());
        return true;
    }

    @Override
    public boolean updateMenuItem(String itemId, MenuItem newItem) {
        if (!menuItemsMap.containsKey(itemId)) {
            System.out.println("Menu item with ID " + itemId + " not found");
            return false;
        }

        if (newItem == null) {
            System.out.println("Invalid update data");
            return false;
        }

        // Update both map and list
        MenuItem existingItem = menuItemsMap.get(itemId);
        existingItem.updateDetails(newItem.getName(), newItem.getDescription(), newItem.getPrice(), newItem.getCategory());

        // Also update the item in the list
        for (int i = 0; i < menuItemsList.size(); i++) {
            if (menuItemsList.get(i).getItemId().equals(itemId)) {
                menuItemsList.set(i, existingItem);
                break;
            }
        }

        System.out.println("Updated menu item: " + existingItem.getName());
        return true;
    }

    @Override
    public boolean removeMenuItem(String itemId) {
        if (!menuItemsMap.containsKey(itemId)) {
            System.out.println("Menu item with ID " + itemId + " not found");
            return false;
        }

        // Remove from both map and list
        MenuItem removedItem = menuItemsMap.remove(itemId);
        menuItemsList.removeIf(item -> item.getItemId().equals(itemId));

        System.out.println("Removed menu item: " + removedItem.getName());
        return true;
    }

    public void displayMenu() {
        System.out.println("\n=== Cafeteria Menu ===");

        for (MenuCategory category : MenuCategory.values()) {
            System.out.println("\n--- " + category + " ---");
            menuItemsList.stream()
                    .filter(item -> item.getCategory() == category)
                    .forEach(item -> System.out.println(item.getItemId() + ": " + item.getName() + " - $" + item.getPrice()));
        }
    }

    public MenuItem getMenuItemById(String itemId) {
        return menuItemsMap.get(itemId);
    }

    // Additional method to get items by category
    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem item : menuItemsList) {
            if (item.getCategory() == category) {
                result.add(item);
            }
        }
        return result;
    }

    // Method to check if menu item exists
    public boolean containsMenuItem(String itemId) {
        return menuItemsMap.containsKey(itemId);
    }

    // Method to get total number of menu items
    public int getMenuSize() {
        return menuItemsList.size();
    }

    // Method to clear all menu items
    public void clearMenu() {
        menuItemsMap.clear();
        menuItemsList.clear();
        System.out.println("Menu cleared");
    }
}