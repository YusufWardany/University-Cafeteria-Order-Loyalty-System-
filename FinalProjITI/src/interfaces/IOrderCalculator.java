package interfaces;

public interface IOrderCalculator {
    double calculateTotal();
    double calculateSubtotal();
    double calculateTax();
    double applyDiscount(double discountPercentage);
}