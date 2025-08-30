package models;

import interfaces.IOrderItems;
import java.util.ArrayList;
import java.util.List;

public class OrderItems implements IOrderItems {
    private Order order;
    private List<MenuItem> items;

    public OrderItems(Order order) {
        this.order = order;
        this.items = new ArrayList<>();
    }

    @Override
    public void addItem(MenuItem item) {
        items.add(item);
        System.out.println("Added " + item.getName() + " to order " + order.getOrderId());
    }

    @Override
    public boolean removeItem(String itemId) {
        return items.removeIf(item -> item.getItemId().equals(itemId));
    }

    @Override
    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public boolean containsItem(String itemId) {
        return items.stream().anyMatch(item -> item.getItemId().equals(itemId));
    }

    public int getItemCount() {
        return items.size();
    }
}