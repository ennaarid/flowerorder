package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try {
            revenue_chart.getData().clear();

            Connection connection = DatabaseConnector.connectDB();

            javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
            series.setName("Monthly Revenue");

            String query = "SELECT MONTH(order_date) as month, SUM(total_amount) as revenue " +
                          "FROM `order` " +
                          "WHERE YEAR(order_date) = YEAR(CURRENT_DATE()) " +
                          "GROUP BY MONTH(order_date) " +
                          "ORDER BY month";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                double revenue = resultSet.getDouble("revenue");

                String monthName = getMonthName(month);

                series.getData().add(new javafx.scene.chart.XYChart.Data<>(monthName, revenue));
            }

            revenue_chart.getData().add(series);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error updating revenue chart: " + e.getMessage());
            e.printStackTrace();

            addSampleRevenueData();
        }
    }

    private void addSampleRevenueData() {
        revenue_chart.getData().clear();

        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Monthly Revenue");

        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Jan", 5000));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Feb", 7500));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Mar", 10000));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Apr", 12500));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("May", 15000));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Jun", 17500));

        revenue_chart.getData().add(series);
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
            default: return "Unknown";
        }
    }

    public void updateCustomersChart() {
        try {
            customers_chart.getData().clear();

            Connection connection = DatabaseConnector.connectDB();

            javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
            series.setName("New Customers");

            String query = "SELECT MONTH(registration_date) as month, COUNT(*) as new_customers " +
                          "FROM Users " +
                          "WHERE YEAR(registration_date) = YEAR(CURRENT_DATE()) " +
                          "AND role = 'Customer' " +
                          "GROUP BY MONTH(registration_date) " +
                          "ORDER BY month";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int newCustomers = resultSet.getInt("new_customers");

                String monthName = getMonthName(month);

                series.getData().add(new javafx.scene.chart.XYChart.Data<>(monthName, newCustomers));
            }

            customers_chart.getData().add(series);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Error updating customers chart: " + e.getMessage());
            e.printStackTrace();

            addSampleCustomersData();
        }
    }

    private void addSampleCustomersData() {
        customers_chart.getData().clear();

        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("New Customers");

        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Jan", 10));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Feb", 15));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Mar", 20));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Apr", 25));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("May", 30));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Jun", 35));

        customers_chart.getData().add(series);
    }
}
