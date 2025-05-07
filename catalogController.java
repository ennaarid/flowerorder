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

public class catalogController implements Initializable {

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button manageorders_btn;

    @FXML
    private Button profile_btn;

    @FXML
    private Button settings_btn;

    @FXML
    private Button logout_btn;

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
        String sql = "SELECT * FROM product";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 3; // Number of items per row

            while (result.next()) {
                VBox itemBox = createCatalogItem(
                    result.getString("product_id"),
                    result.getString("product_name"),
                    result.getString("season"),
                    result.getDouble("price"),
                    result.getString("image")
                );

                catalog_grid.add(itemBox, column, row);
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

    private VBox createCatalogItem(String id, String name, String season, double price, String imagePath) {
        VBox itemBox = new VBox(10);
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/" + imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            // Load default image if the specified image is not found
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/final_flowerorderingsystem/images/default_flower.png"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        // Name
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Montserrat Bold", 16));
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        // Season
        Label seasonLabel = new Label("Season: " + season);
        seasonLabel.setFont(Font.font("Montserrat Regular", 12));

        // Price
        Label priceLabel = new Label(String.format("₱%.2f", price));
        priceLabel.setFont(Font.font("Montserrat Bold", 14));
        priceLabel.setStyle("-fx-text-fill: #89ac46;");

        // Add to Cart button
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #89ac46; -fx-text-fill: white; -fx-font-family: 'Montserrat Bold'; -fx-font-size: 12;");
        addToCartBtn.setOnAction(e -> addToCart(id, name, price));

        itemBox.getChildren().addAll(imageView, nameLabel, seasonLabel, priceLabel, addToCartBtn);
        return itemBox;
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
                VBox itemBox = createCatalogItem(
                    result.getString("product_id"),
                    result.getString("product_name"),
                    result.getString("season"),
                    result.getDouble("price"),
                    result.getString("image")
                );

                catalog_grid.add(itemBox, column, row);
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
    public void switchForm(ActionEvent event) {
        try {
            if (event.getSource() == dashboard_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("admindash.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Dashboard");
                stage.setScene(scene);
                stage.show();
                main_form.getScene().getWindow().hide();
            } else if (event.getSource() == inventory_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("inventory.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Inventory");
                stage.setScene(scene);
                stage.show();
                main_form.getScene().getWindow().hide();
            } else if (event.getSource() == manageorders_btn) {
                Parent root = FXMLLoader.load(getClass().getResource("manageOrders.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Manage Orders");
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
} 