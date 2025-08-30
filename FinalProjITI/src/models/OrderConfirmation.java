package models;

import java.util.Date;

public class OrderConfirmation {
    private Order order;
    private boolean isConfirmed;
    private Date confirmationTime;

    public OrderConfirmation(Order order) {
        this.order = order;
        this.isConfirmed = false;
    }

    public boolean confirmOrder() {
        if (order.getOrderItems().getItemCount() == 0) {
            System.out.println("Cannot confirm empty order");
            return false;
        }

        this.isConfirmed = true;
        this.confirmationTime = new Date();
        order.changeStatus(enums.OrderStatus.PENDING);
        System.out.println("Order " + order.getOrderId() + " confirmed at " + confirmationTime);
        return true;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public Date getConfirmationTime() {
        return confirmationTime;
    }
}