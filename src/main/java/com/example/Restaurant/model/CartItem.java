package com.example.Restaurant.model;

    import jakarta.persistence.*;

    /**
     * The CartItem class represents an item in a shopping cart in the restaurant application.
     *
     * Main Responsibilities:
     * - Track the food item, quantity, and total price for each cart item.
     *
     * Component Relationships:
     * - Interacts with the Food entity to get food details and price.
     *
     * Dependencies:
     * - Food: Represents the food item associated with the cart item.
     *
     * Security Considerations:
     * - Ensure proper validation of quantity and total price.
     */
    @Entity
    public class CartItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        private Food food;

        private Integer quantity;

        private Double totalPrice;

        /**
         * Default constructor initializing quantity to 0.
         */
        public CartItem() {
            this.quantity = 0;
        }

        /**
         * Gets the ID of the cart item.
         *
         * @return the ID of the cart item
         */
        public Long getId() {
            return id;
        }

        /**
         * Sets the ID of the cart item.
         *
         * @param id the ID to set
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * Gets the food item associated with the cart item.
         *
         * @return the food item
         */
        public Food getFood() {
            return food;
        }

        /**
         * Sets the food item associated with the cart item.
         *
         * @param food the food item to set
         */
        public void setFood(Food food) {
            this.food = food;
        }

        /**
         * Gets the quantity of the food item in the cart.
         *
         * @return the quantity of the food item
         */
        public Integer getQuantity() {
            return quantity;
        }

        /**
         * Sets the quantity of the food item in the cart and updates the total price.
         *
         * @param quantity the quantity to set
         * @throws IllegalArgumentException if the quantity is negative
         */
        public void setQuantity(Integer quantity) {
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
            this.quantity = quantity;
            if (food != null) {
                // Update total price based on food price and quantity
                this.totalPrice = food.getPrice() * quantity;
            }
        }

        /**
         * Gets the total price of the cart item.
         *
         * @return the total price of the cart item
         */
        public Double getTotalPrice() {
            return totalPrice;
        }

        /**
         * Sets the total price of the cart item.
         *
         * @param totalPrice the total price to set
         */
        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }