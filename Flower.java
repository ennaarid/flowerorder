package com.example.cart;

public class Flower {
    private String name;
    private double price;

    public Flower(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flower flower = (Flower) obj;
        return name.equals(flower.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}