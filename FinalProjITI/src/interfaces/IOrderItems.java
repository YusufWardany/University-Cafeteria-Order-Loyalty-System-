package interfaces;

import models.MenuItem;
import java.util.List;

public interface IOrderItems {
    void addItem(MenuItem item);
    boolean removeItem(String itemId);
    List<MenuItem> getItems();
    boolean containsItem(String itemId);
}