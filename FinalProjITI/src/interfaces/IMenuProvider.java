package interfaces;

import models.MenuItem;
import enums.MenuCategory;
import java.util.List;

public interface IMenuProvider {
    List<MenuItem> getMenuItems();
    boolean addMenuItem(MenuItem item);
    boolean updateMenuItem(String itemId, MenuItem newItem);
    boolean removeMenuItem(String itemId);
    MenuItem getMenuItemById(String itemId);
    // Add any other methods you need
}