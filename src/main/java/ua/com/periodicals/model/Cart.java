package ua.com.periodicals.model;

import ua.com.periodicals.entity.Periodical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cart {

    private Map<Long, Periodical> cartItems = new HashMap<>();

    private int totalCount = 0;
    private long totalCost = 0;

    public Set<Periodical> getCartItems() {
        Set<Periodical> periodicalsSet = new HashSet(cartItems.values());
        return periodicalsSet;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void addItem(Periodical periodical) {
        cartItems.put(periodical.getId(), periodical);
        refreshStatistics();
    }

    public void removeItem(Long itemId) {
        cartItems.remove(itemId);
        refreshStatistics();
    }

    private void refreshStatistics() {
        totalCount = cartItems.size();
        totalCost = 0;

        for (Map.Entry<Long, Periodical> entry : cartItems.entrySet()) {
            totalCost += entry.getValue().getMonthlyPrice();
        }
    }

    @Override
    public String toString() {
        return "Cart{" +
            "cartItems=" + cartItems +
            ", totalCount=" + totalCount +
            ", totalCost=" + totalCost +
            '}';
    }
}
