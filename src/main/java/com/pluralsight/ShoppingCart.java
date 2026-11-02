package com.pluralsight;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<String, ItemsInCart> items = new LinkedHashMap<>();

    public void add(Product product) {
        ItemsInCart existing = items.get(product.getSku());

        if (existing == null) {
            items.put(product.getSku(), new ItemsInCart(product));
        } else {
            existing.addOne();
        }
    }

    public boolean remove(String sku) {
        return items.remove(sku.toUpperCase()) != null;
    }

    public Collection<ItemsInCart> getItems() {
        return items.values();
    }

    public double getTotal() {
        double total = 0.0;
        for ()
    }
}
