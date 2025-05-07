package com.example.final_flowerorderingsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.IOException;

public class customerCatalogController implements Initializable {

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Button profile_btn;

    @FXML
    private Button settings_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private Button cart_btn;

    @FXML
    private Button order_history_btn;

    @FXML
    private TextField search_field;

    @FXML
    private ComboBox<String> season_filter;

    @FXML
    private ComboBox<String> price_filter;

    @FXML
    private Button filter_btn;

    @FXML
    private GridPane catalog_grid;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize season filter
        season_filter.getItems().addAll("All", "Wet", "Dry", "All Year Round");
        season_filter.setValue("All");

        // Initialize price filter
        price_filter.getItems().addAll("All", "Under ₱100", "₱100 - ₱500", "₱500 - ₱1000", "Over ₱1000");
        price_filter.setValue("All");

        // Load catalog data
        loadCatalogData();

        // Add search listener
        search_field.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCatalog(null);
        });
    }

    private void loadCatalogData() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM product";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 3;

            while (rs.next()) {
                String product_id = rs.getString("product_id");
                String product_name = rs.getString("product_name");
                String description = rs.getString("description");
                String season = rs.getString("season");
                double price = rs.getDouble("price");
                String image = rs.getString("image");

                // Load the item card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_flowerorderingsystem/itemCard.fxml"));
                VBox itemCard = loader.load();
                ItemCardController controller = loader.getController();

                // Set the data
                controller.setData(product_id, product_name, description, season, price, image, -1, "Add to Cart");

                // Set up the action button
                controller.getActionButton().setOnAction(e -> addToCart(product_id, product_name, price));

                // Add to grid
                catalog_grid.add(itemCard, column, row);
                column++;
                if (column >= maxColumns) {
                    column = 0;
                    row++;
                }
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load catalog data", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void filterCatalog(ActionEvent event) {
        String searchText = search_field.getText().toLowerCase();
        String selectedSeason = season_filter.getValue();
        String selectedPrice = price_filter.getValue();

        // Clear existing items
        catalog_grid.getChildren().clear();

        String sql = "SELECT * FROM product WHERE 1=1";
        if (!searchText.isEmpty()) {
            sql += " AND LOWER(product_name) LIKE ?";
        }
        if (!selectedSeason.equals("All")) {
            sql += " AND season = ?";
        }
        if (!selectedPrice.equals("All")) {
            sql += " AND price " + getPriceFilterCondition(selectedPrice);
        }

        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            int paramIndex = 1;

            if (!searchText.isEmpty()) {
                prepare.setString(paramIndex++, "%" + searchText + "%");
            }
            if (!selectedSeason.equals("All")) {
                prepare.setString(paramIndex++, selectedSeason);
            }

            result = prepare.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 3;

            while (result.next()) {
                ItemCard itemCard = new ItemCard(
                    result.getString("product_id"),
                    result.getString("product_name"),
                    result.getString("season"),
                    result.getDouble("price"),
                    result.getString("image"),
                    -1, // Stock not shown in catalog
                    "Add to Cart"
                );

                // Set up the action button
                itemCard.getActionButton().setOnAction(e -> addToCart(
                    itemCard.getProductId(),
                    itemCard.getProductName(),
                    itemCard.getPrice()
                ));

                catalog_grid.add(itemCard, column, row);
                column++;

                if (column == maxColumns) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private String getPriceFilterCondition(String priceRange) {
        switch (priceRange) {
            case "Under ₱100":
                return "<= 100";
            case "₱100 - ₱500":
                return "BETWEEN 100 AND 500";
            case "₱500 - ₱1000":
                return "BETWEEN 500 AND 1000";
            case "Over ₱1000":
                return ">= 1000";
            default:
                return "";
        }
    }

    private void addToCart(String productId, String productName, double price) {
        // TODO: Implement add to cart functionality
        showAlert("Success", productName + " added to cart!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void openCart() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("cart.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Shopping Cart");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openOrderHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("orderHistory.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Order History");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchForm(ActionEvent event) {
        try {
            if (event.getSource() == profile_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("profile.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Profile");
                stage.setScene(scene);
                stage.show();
                main_form.getScene().getWindow().hide();
            } else if (event.getSource() == settings_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Settings");
                stage.setScene(scene);
                stage.show();
                main_form.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logout() {
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get() == ButtonType.OK) {
                logout_btn.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Flower Ordering System");
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Connection getConnection() throws SQLException {
        return database.connectDB();
    }
} 
