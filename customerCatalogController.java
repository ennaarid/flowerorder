package com.example.flowermanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.IOException;

public class customerCatalogController extends BaseController implements Initializable {

    @FXML
    private GridPane catalog_grid;

    @FXML
    private Button filter_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Button orders_btn;

    @FXML
    private ComboBox<String> price_filter;

    @FXML
    private Button profile_btn;

    @FXML
    private TextField search_field;

    @FXML
    private ComboBox<String> season_filter;

    @FXML
    private Button settings_btn;

    @FXML
    private Button admin_btn;

    @FXML
    private Button view_cart_btn;


    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize season filter
        season_filter.getItems().addAll("All", "Wet", "Dry", "All Year Round");
        season_filter.setValue("All");

        // Add season filter listener
        season_filter.setOnAction(e -> filterCatalog(e));

        // Initialize price filter
        price_filter.getItems().addAll("All", "Under ₱100", "₱100 - ₱500", "₱500 - ₱1000", "Over ₱1000");
        price_filter.setValue("All");

        // Add price filter listener
        price_filter.setOnAction(e -> filterCatalog(e));

        // Load catalog data
        loadCatalogData();

        // Add search listener
        search_field.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCatalog(null);
        });

        // Connect orders button to order history
        if (orders_btn != null) {
            orders_btn.setOnAction(e -> openOrderHistory());
        }

        // Show admin button only for users with admin privileges
        if (admin_btn != null) {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null && "Administrator".equals(currentUser.getRole())) {
                admin_btn.setVisible(true);
            } else {
                admin_btn.setVisible(false);
            }
        }
    }


    @FXML
    void logout(ActionEvent event) {
        logout();
    }

    @FXML
    void openCart(ActionEvent event) {
        openCart();
    }

    @FXML
    void openOrderHistory(ActionEvent event) {
        openOrderHistory();
    }

    private void loadCatalogData() {
        try {
            Connection conn = getConnection();
            if (conn == null) {
                showAlert("Database Error", "Could not connect to the database. Please check your database connection.", Alert.AlertType.ERROR);
                return;
            }
            String query = "SELECT * FROM flower";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 4;

            while (rs.next()) {
                String product_id = rs.getString("flowerId");
                String product_name = rs.getString("name");
                String description = rs.getString("description");
                String season = rs.getString("season");
                double price = rs.getDouble("price");
                String image = rs.getString("image");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/itemcard.fxml"));
                VBox itemCard = loader.load();
                ItemCardController controller = loader.getController();

               
                controller.setData(product_id, product_name, description, season, price, image, -1, "Add to Cart");
                controller.getActionButton().setOnAction(e -> addToCart(product_id, product_name, price, image, controller));

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

        String sql = "SELECT * FROM flower WHERE 1=1";
        if (!searchText.isEmpty()) {
            sql += " AND LOWER(name) LIKE ?";
        }
        if (!selectedSeason.equals("All")) {
            sql += " AND season = ?";
        }
        if (!selectedPrice.equals("All")) {
            switch (selectedPrice) {
                case "Under ₱100":
                    sql += " AND price <= ?";
                    break;
                case "₱100 - ₱500":
                    sql += " AND price BETWEEN ? AND ?";
                    break;
                case "₱500 - ₱1000":
                    sql += " AND price BETWEEN ? AND ?";
                    break;
                case "Over ₱1000":
                    sql += " AND price >= ?";
                    break;
            }
        }

        connect = DatabaseConnector.connectDB();
        if (connect == null) {
            showAlert("Database Error", "Could not connect to the database. Please check your database connection.", Alert.AlertType.ERROR);
            return;
        }

        try {
            prepare = connect.prepareStatement(sql);
            int paramIndex = 1;

            if (!searchText.isEmpty()) {
                prepare.setString(paramIndex++, "%" + searchText + "%");
            }
            if (!selectedSeason.equals("All")) {
                prepare.setString(paramIndex++, selectedSeason);
            }
            if (!selectedPrice.equals("All")) {
                switch (selectedPrice) {
                    case "Under ₱100":
                        prepare.setDouble(paramIndex++, 100);
                        break;
                    case "₱100 - ₱500":
                        prepare.setDouble(paramIndex++, 100);
                        prepare.setDouble(paramIndex++, 500);
                        break;
                    case "₱500 - ₱1000":
                        prepare.setDouble(paramIndex++, 500);
                        prepare.setDouble(paramIndex++, 1000);
                        break;
                    case "Over ₱1000":
                        prepare.setDouble(paramIndex++, 1000);
                        break;
                }
            }

            result = prepare.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 4;

            while (result.next()) {
                String product_id = result.getString("flowerId");
                String product_name = result.getString("name");
                String description = result.getString("description");
                String season = result.getString("season");
                double price = result.getDouble("price");
                String image = result.getString("image");
                
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/itemcard.fxml"));
                    VBox itemCard = loader.load();
                    ItemCardController controller = loader.getController();


                    controller.setData(product_id, product_name, description, season, price, image, -1, "Add to Cart");

                    controller.getActionButton().setOnAction(e -> addToCart(product_id, product_name, price, image, controller));

                
                    catalog_grid.add(itemCard, column, row);
                    column++;

                    if (column == maxColumns) {
                        column = 0;
                        row++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to load item card", Alert.AlertType.ERROR);
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

    private CartService cartService = CartServiceImpl.getInstance();

    private void addToCart(String productId, String productName, double price, String imagePath, ItemCardController controller) {
        try {
            int quantity = controller.getQuantity();
            cartService.addToCart(
                productId,
                productName,
                price,
                quantity, 
                imagePath 
            );

            showAlert("Success", quantity + " " + productName + " added to cart!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add item to cart: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openCart() {
        try {
            String fxmlFile = "/com/example/flowermanagementsystem/cart.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) main_form.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Shopping Cart");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not open cart: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openOrderHistory() {
        try {
            String fxmlFile = "/com/example/flowermanagementsystem/orderHistory.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) main_form.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Order History");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not open order history: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void switchForm(ActionEvent event) {
        try {
            String fxmlFile = null;
            String title = null;

            if (event.getSource() == menu_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/customerCatalog.fxml";
                title = "Catalog";
            } else if (event.getSource() == profile_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/profile.fxml";
                title = "Profile";
            } else if (event.getSource() == settings_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/settings.fxml";
                title = "Settings";
            }

            if (fxmlFile != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                    Parent root = loader.load();
                    Stage stage = (Stage) main_form.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setTitle(title);
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Navigation Error", "Failed to load " + fxmlFile + ": " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to the selected page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void logout() {
        logout(main_form);
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
        return DatabaseConnector.connectDB();
    }

    @Override
    protected void clearInputFields() {
        if (search_field != null) {
            search_field.clear();
        }
        
        if (season_filter != null) {
            season_filter.setValue("All");
        }

        if (price_filter != null) {
            price_filter.setValue("All");
        }
    }

    @FXML
    public void switchToAdminView(ActionEvent event) {
        switchToAdminView(main_form);
    }
} 
