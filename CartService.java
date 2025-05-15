package com.example.flowermanagementsystem;

import java.util.List;

public interface CartService {

    CartItem addToCart(String productId, String productName, double price, int quantity, String imagePath);
    List<CartItem> getCartItems();

    boolean removeFromCart(String productId);

    /**
     * Clears all items from the cart.
     */
    void clearCart();

    /**
     * Gets the total price of all items in the cart.
     * 
     * @return The total price
     * @throws CartServiceImpl.CartTotalException if there is an error calculating the total
     */
    double getCartTotal() throws CartServiceImpl.CartTotalException;
}
