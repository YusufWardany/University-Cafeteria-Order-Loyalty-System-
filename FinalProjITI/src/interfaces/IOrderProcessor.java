package interfaces;

import enums.OrderStatus;
import models.Order;

public interface IOrderProcessor {
    boolean processOrder(Order order);
    boolean updateOrderStatus(String orderId, OrderStatus status);
}