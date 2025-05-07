package com.flowerorder;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AdminDashboardController {
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
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    
    public void initialize() {
        displayDailyIncome();
        displayTotalIncome();
        displayTotalCustomers();
        displayTotalProducts();
        updateRevenueChart();
        updateCustomersChart();
    }
    
    public void displayDailyIncome() {
        String sql = "SELECT SUM(total) FROM orders WHERE DATE(order_date) = CURDATE()";
        connect = Database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                double amount = result.getDouble("SUM(total)");
                daily_income.setText(String.format("₱ %.2f", amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayTotalIncome() {
        String sql = "SELECT SUM(total) FROM orders";
        connect = Database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                double amount = result.getDouble("SUM(total)");
                total_income.setText(String.format("₱ %.2f", amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayTotalCustomers() {
        String sql = "SELECT COUNT(*) FROM customers";
        connect = Database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                int count = result.getInt("COUNT(*)");
                total_customers.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayTotalProducts() {
        String sql = "SELECT COUNT(*) FROM inventory";
        connect = Database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            if (result.next()) {
                int count = result.getInt("COUNT(*)");
                total_products.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateRevenueChart() {
        revenue_chart.getData().clear();
        
        String sql = "SELECT DATE(order_date) as date, SUM(total) as total FROM orders GROUP BY DATE(order_date) ORDER BY date DESC LIMIT 7";
        connect = Database.connectDB();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                series.getData().add(new XYChart.Data<>(result.getString("date"), result.getDouble("total")));
            }
            
            revenue_chart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCustomersChart() {
        customers_chart.getData().clear();
        
        String sql = "SELECT DATE(order_date) as date, COUNT(DISTINCT customer_id) as count FROM orders GROUP BY DATE(order_date) ORDER BY date DESC LIMIT 7";
        connect = Database.connectDB();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            while (result.next()) {
                series.getData().add(new XYChart.Data<>(result.getString("date"), result.getInt("count")));
            }
            
            customers_chart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 