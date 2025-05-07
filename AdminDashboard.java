package com.flowerorder;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AdminDashboard {
    @FXML
    private AnchorPane dashboard_form;
    
    @FXML
    private Label daily_income;
    
    @FXML
    private Label total_income;
    
    @FXML
    private Label total_customers;
    
    @FXML
    private Label total_products;
    
    @FXML
    private BarChart<String, Number> revenue_chart;
    
    @FXML
    private AreaChart<String, Number> customers_chart;
    
    public void displayDailyIncome(double amount) {
        daily_income.setText(String.format("₱ %.2f", amount));
    }
    
    public void displayTotalIncome(double amount) {
        total_income.setText(String.format("₱ %.2f", amount));
    }
    
    public void displayTotalCustomers(int count) {
        total_customers.setText(String.valueOf(count));
    }
    
    public void displayTotalProducts(int count) {
        total_products.setText(String.valueOf(count));
    }
    
    public void updateRevenueChart() {
        // TODO: Implement revenue chart update logic
    }
    
    public void updateCustomersChart() {
        // TODO: Implement customers chart update logic
    }
} 