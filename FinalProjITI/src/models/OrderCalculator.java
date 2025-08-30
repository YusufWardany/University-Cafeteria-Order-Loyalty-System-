package models;

import interfaces.IOrderCalculator;

public class OrderCalculator implements IOrderCalculator {
    private Order order;
    private OrderItems orderItems;
    private static final double TAX_RATE = 0.08; // 8% tax

    public OrderCalculator(Order order, OrderItems orderItems) {
        this.order = order;
        this.orderItems = orderItems;
    }

    @Override
    public double calculateSubtotal() {
        return orderItems.getItems().stream()
                .mapToDouble(MenuItem::getPrice)
                .sum();
    }

    @Override
    public double calculateTax() {
        return calculateSubtotal() * TAX_RATE;
    }

    @Override
    public double calculateTotal() {
        return calculateSubtotal() + calculateTax();
    }

    @Override
    public double applyDiscount(double discountPercentage) {
        double discount = calculateSubtotal() * (discountPercentage / 100);
        return calculateTotal() - discount;
    }
}