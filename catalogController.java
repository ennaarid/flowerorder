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
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class catalogController extends BaseController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(catalogController.class.getName());

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
    private Button view_cart_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button manageorders_btn;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (season_filter != null) {
            season_filter.getItems().addAll("All", "Wet", "Dry", "All Year Round");
            season_filter.setValue("All");
        }

        if (price_filter != null) {
            price_filter.getItems().addAll("All", "Under ₱100", "₱100 - ₱500", "₱500 - ₱1000", "Over ₱1000");
            price_filter.setValue("All");
        }

        loadCatalogData();

        if (search_field != null) {
            search_field.textProperty().addListener((observable, oldValue, newValue) -> {
                filterCatalog(null);
            });
        }

        if (filter_btn != null) {
            filter_btn.setOnAction(this::filterCatalog);
        }

        if (view_cart_btn != null) {
            view_cart_btn.setOnAction(this::openCart);
        }

        try {
            if (orders_btn != null) {
                orders_btn.setOnAction(this::openOrderHistory);
            }
        } catch (Exception e) {
            System.err.println("Warning: orders_btn could not be initialized: " + e.getMessage());
        }
    }

    private void loadCatalogData() {
        if (catalog_grid == null) {
            System.err.println("Warning: catalog_grid is null, cannot load catalog data");
            return;
        }

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
            int maxColumns = 3;

            while (rs.next()) {
                try {
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

                    controller.getActionButton().setOnAction(e -> addToCart(product_id, product_name, price));

                    catalog_grid.add(itemCard, column, row);
                    column++;
                    if (column >= maxColumns) {
                        column = 0;
                        row++;
                    }
                } catch (Exception e) {
                    System.err.println("Error processing flower item: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load catalog data: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred while loading catalog data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void filterCatalog(ActionEvent event) {
        String searchText = search_field.getText().toLowerCase();
        String selectedSeason = season_filter.getValue();
        String selectedPrice = price_filter.getValue();

        catalog_grid.getChildren().clear();

        String sql = "SELECT * FROM flower WHERE 1=1";
        if (!searchText.isEmpty()) {
            sql += " AND LOWER(name) LIKE ?";
        }
        if (!selectedSeason.equals("All")) {
            sql += " AND season = ?";
        }
        if (!selectedPrice.equals("All")) {
            sql += " AND price " + getPriceFilterCondition(selectedPrice);
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

            result = prepare.executeQuery();

            int column = 0;
            int row = 0;
            int maxColumns = 3;

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

                    controller.getActionButton().setOnAction(e -> addToCart(product_id, product_name, price));

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

    private void addToCart(String productId, String productName, double price) {
        try {
            cartService.addToCart(
                productId,
                productName,
                price,
                1,
                ""
            );

            showAlert("Success", productName + " added to cart!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add item to cart: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/cart.fxml"));
            Parent root = loader.load();

            Stage stage = null;
            if (event.getSource() instanceof javafx.scene.Node) {
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            } else if (main_form != null && main_form.getScene() != null) {
                stage = (Stage) main_form.getScene().getWindow();
            } else if (view_cart_btn != null && view_cart_btn.getScene() != null) {
                stage = (Stage) view_cart_btn.getScene().getWindow();
            }

            if (stage == null) {
                stage = new Stage();
                System.err.println("Warning: Creating new stage for cart because current stage could not be determined");
            }

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
    private void openOrderHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/orderHistory.fxml"));
            Parent root = loader.load();

            Stage stage = null;
            if (event.getSource() instanceof javafx.scene.Node) {
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            } else if (main_form != null && main_form.getScene() != null) {
                stage = (Stage) main_form.getScene().getWindow();
            } else if (orders_btn != null && orders_btn.getScene() != null) {
                stage = (Stage) orders_btn.getScene().getWindow();
            }

            if (stage == null) {
                stage = new Stage();
                System.err.println("Warning: Creating new stage for order history because current stage could not be determined");
            }

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
                fxmlFile = "/com/example/flowermanagementsystem/catalog.fxml";
                title = "Catalog";
            } else if (event.getSource() == profile_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/profile.fxml";
                title = "Profile";
            } else if (event.getSource() == settings_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/settings.fxml";
                title = "Settings";
            } else if (event.getSource() == dashboard_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/admindash.fxml";
                title = "Dashboard";
            } else if (event.getSource() == inventory_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/inventory.fxml";
                title = "Inventory";
            } else if (event.getSource() == manageorders_btn) {
                fxmlFile = "/com/example/flowermanagementsystem/manageOrders.fxml";
                title = "Manage Orders";
            }

            if (fxmlFile != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();

                Stage stage = null;
                if (event.getSource() instanceof javafx.scene.Node) {
                    stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                } else if (main_form != null && main_form.getScene() != null) {
                    stage = (Stage) main_form.getScene().getWindow();
                }

                if (stage == null) {
                    stage = new Stage();
                    System.err.println("Warning: Creating new stage for " + title + " because current stage could not be determined");
                }

                Scene scene = new Scene(root);
                stage.setTitle(title);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to the selected page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get() == ButtonType.OK) {
                Stage currentStage = null;

                if (event.getSource() instanceof javafx.scene.Node) {
                    currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                } else if (logout_btn != null && logout_btn.getScene() != null) {
                    currentStage = (Stage) logout_btn.getScene().getWindow();
                } else if (main_form != null && main_form.getScene() != null) {
                    currentStage = (Stage) main_form.getScene().getWindow();
                }

                if (currentStage != null) {
                    currentStage.hide();
                } else {
                    System.err.println("Warning: Could not find current stage to hide");
                }

                Parent root = FXMLLoader.load(getClass().getResource("/com/example/flowermanagementsystem/FlowerLogin.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setTitle("Flower Ordering System");
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Logout Error", "Failed to logout: " + e.getMessage(), Alert.AlertType.ERROR);
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
        return DatabaseConnector.connectDB();
    }
}
