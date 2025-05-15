package com.example.flowermanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryController extends BaseController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(InventoryController.class.getName());
    private InventoryService inventoryService;

    @FXML
    private AnchorPane main_form;

    @FXML
    private AnchorPane inventory_form;

    @FXML
    private Button menu_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button inventory_btn;

    @FXML
    private Button manageorders_btn;

    // Profile and Settings buttons removed from admin interface
    // @FXML
    // private Button profile_btn;

    // @FXML
    // private Button settings_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private TableView<InventoryData> inventory_tableView;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_productID;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_productName;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_season;

    @FXML
    private TableColumn<InventoryData, Double> inventory_col_price;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_statusText;

    @FXML
    private TableColumn<InventoryData, Integer> inventory_col_stock;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_date;

    @FXML
    private TableColumn<InventoryData, String> inventory_col_description;

    @FXML
    private ImageView inventory_imageView;

    @FXML
    private Button inventory_importBtn;

    @FXML
    private Button inventory_addBtn;

    @FXML
    private Button inventory_updateBtn;

    @FXML
    private Button inventory_clearBtn;

    @FXML
    private Button inventory_deleteBtn;

    @FXML
    private TextField inventory_productID;

    @FXML
    private TextField inventory_productName;

    @FXML
    private ComboBox<String> inventory_season;

    @FXML
    private TextField inventory_price;

    @FXML
    private ComboBox<String> inventory_status;

    @FXML
    private TextField inventory_stock;

    @FXML
    private TextField inventory_description;

    @FXML
    private TextField inventory_search;

    @FXML
    private TextField inventory_image;

    // Profile and Settings Form Components removed from admin interface
    // @FXML
    private AnchorPane profile_form;

    // @FXML
    private ImageView profile_image;

    // @FXML
    private TextField profile_username;

    // @FXML
    private TextField profile_email;

    // @FXML
    private TextField profile_phone;

    // @FXML
    private Button profile_importBtn;

    // @FXML
    private Button profile_saveBtn;

    // Settings Form Components removed from admin interface
    // @FXML
    private AnchorPane settings_form;

    // @FXML
    private TextField settings_currentPassword;

    // @FXML
    private TextField settings_newPassword;

    // @FXML
    private TextField settings_confirmPassword;

    // @FXML
    private CheckBox settings_emailNotifications;

    // @FXML
    private CheckBox settings_orderUpdates;

    // @FXML
    private CheckBox settings_promotions;

    // @FXML
    private Button settings_saveBtn;

    @FXML
    private GridPane inventory_grid;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private Alert alert;
    private Image image;
    private ObservableList<InventoryData> inventoryList;
    private String currentImagePath;
    private String currentProfileImagePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the inventory service
        inventoryService = InventoryServiceImpl.getInstance();

        inventory_form.setVisible(true);

        // Check if profile_form and settings_form are not null before setting visibility
        if (profile_form != null) {
            profile_form.setVisible(false);
        }

        if (settings_form != null) {
            settings_form.setVisible(false);
        }

        inventory_season.setItems(FXCollections.observableArrayList(
            "Wet", "Dry", "All Year Round"
        ));


        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_season.setCellValueFactory(new PropertyValueFactory<>("season"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_stock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        inventory_col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        inventory_col_description.setCellValueFactory(new PropertyValueFactory<>("description"));

        inventoryList = FXCollections.observableArrayList();
        inventory_tableView.setItems(inventoryList);

        FilteredList<InventoryData> filteredData = new FilteredList<>(inventoryList, b -> true);
        inventory_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(inventory -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return inventory.getProductName().toLowerCase().contains(lowerCaseFilter) ||
                       inventory.getProductID().toLowerCase().contains(lowerCaseFilter) ||
                       inventory.getSeason().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<InventoryData> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inventory_tableView.comparatorProperty());
        inventory_tableView.setItems(sortedData);

        loadInventoryData();
        // Profile data loading removed from admin interface
        // loadProfileData();
    }

    @FXML
    private void inventoryImportBtn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            currentImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            inventory_imageView.setImage(image);
        }
    }

    @FXML
    private void inventoryAddBtn() {
        if (validateInput()) {
            // Update the image path field with the current image path
            if (currentImagePath != null) {
                inventory_image.setText(currentImagePath);
            }

            // Add to the database
            addInventory();

            // Also add to the in-memory list for immediate display
            InventoryData newItem = new InventoryData(
                inventory_productID.getText(),
                inventory_productName.getText(),
                inventory_season.getValue(),
                Double.parseDouble(inventory_price.getText()),
                null, // Status is no longer used
                Integer.parseInt(inventory_stock.getText()),
                java.time.LocalDate.now().toString(),
                inventory_description.getText(),
                currentImagePath != null ? currentImagePath : inventory_image.getText()
            );
            inventoryList.add(newItem);
            clearFields();
        }
    }

    @FXML
    private void inventoryUpdateBtn() {
        InventoryData selectedItem = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && validateInput()) {
            if (currentImagePath != null) {
                inventory_image.setText(currentImagePath);
                selectedItem.setImagePath(currentImagePath);
            }

            updateInventory();

            selectedItem.setProductName(inventory_productName.getText());
            selectedItem.setSeason(inventory_season.getValue());
            selectedItem.setPrice(Double.parseDouble(inventory_price.getText()));
            selectedItem.setStatus(inventory_status.getValue());
            selectedItem.setStock(Integer.parseInt(inventory_stock.getText()));
            selectedItem.setDescription(inventory_description.getText());

            inventory_tableView.refresh();
            clearFields();
        } else {
            showErrorAlert("Error", "Please select a product to update!");
        }
    }

    private void updateInventory() {
        try {
            String productId = inventory_productID.getText();
            String productName = inventory_productName.getText();
            String season = inventory_season.getValue();
            double price = Double.parseDouble(inventory_price.getText());
            int stock = Integer.parseInt(inventory_stock.getText());
            String imagePath = currentImagePath != null ? currentImagePath : inventory_image.getText();
            String description = inventory_description.getText();

            boolean success = inventoryService.updateInventory(
                productId, productName, season, price, stock, imagePath, description);

            if (success) {
                showInfoAlert("Success", "Product updated successfully!");
                loadInventoryData(); // Refresh the inventory display
            } else {
                showErrorAlert("Error", "Failed to update product. Please check your input.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format in input fields", e);
            showErrorAlert("Input Error", "Please enter valid numbers for price and stock.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating inventory item", e);
            showErrorAlert("Error", "Failed to update product: " + e.getMessage());
        }
    }

    @FXML
    private void inventoryClearBtn() {
        clearFields();
    }

    @FXML
    private void inventoryDeleteBtn() {
        InventoryData selectedItem = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete this product?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete from database
                deleteInventory(selectedItem.getProductID());

                // Remove from in-memory list
                inventoryList.remove(selectedItem);
                clearFields();
            }
        } else {
            showErrorAlert("Error", "Please select a product to delete!");
        }
    }

    private void deleteInventory(String productId) {
        try {
            boolean success = inventoryService.deleteInventory(productId);

            if (success) {
                showInfoAlert("Success", "Product deleted successfully!");
                loadInventoryData(); // Refresh the inventory display
            } else {
                showErrorAlert("Error", "Failed to delete product. The product may not exist.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting inventory item", e);
            showErrorAlert("Error", "Failed to delete product: " + e.getMessage());
        }
    }

    private void clearFields() {
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_season.setValue(null);
        inventory_price.clear();
        inventory_status.setValue(null);
        inventory_stock.clear();
        inventory_description.clear();
        inventory_imageView.setImage(null);
        currentImagePath = null;
    }

    private boolean validateInput() {
        if (inventory_productID.getText().isEmpty() ||
            inventory_productName.getText().isEmpty() ||
            inventory_season.getValue() == null ||
            inventory_price.getText().isEmpty() ||
            inventory_status.getValue() == null ||
            inventory_stock.getText().isEmpty()) {
            showErrorAlert("Error", "Please fill in all required fields!");
            return false;
        }

        try {
            Double.parseDouble(inventory_price.getText());
            Integer.parseInt(inventory_stock.getText());
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Please enter valid numbers for price and stock!");
            return false;
        }

        return true;
    }

    // Using BaseController's showInfoAlert and showErrorAlert methods instead

    private void loadInventoryData() {
        try {
            // Clear existing data
            inventoryList.clear();

            // Use the inventory service to load data
            List<InventoryData> items = inventoryService.loadInventoryData();

            // Add all items to the observable list
            inventoryList.addAll(items);

            // If no data was loaded, add a sample item
            if (inventoryList.isEmpty()) {
                inventoryList.add(new InventoryData(
                    "P001",
                    "Rose",
                    "Spring",
                    25.99,
                    null, // Status is no longer used
                    100,
                    "2024-03-20",
                    "Beautiful red roses",
                    null
                ));
                LOGGER.log(Level.WARNING, "No inventory data found, added sample item");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load inventory data", e);
            showInfoAlert("Database Error", "Failed to load inventory data: " + e.getMessage());

            // Add sample data if database load fails
            inventoryList.add(new InventoryData(
                "P001",
                "Rose",
                "Spring",
                25.99,
                null, 
                100,
                "2024-03-20",
                "Beautiful red roses",
                null
            ));
        }
    }

    @FXML
    private void switchForm(ActionEvent event) {
        inventory_form.setVisible(false);

        if (profile_form != null) {
            profile_form.setVisible(false);
        }

        if (settings_form != null) {
            settings_form.setVisible(false);
        }

        if (event.getSource() == inventory_btn) {
            inventory_form.setVisible(true);
            loadInventoryData();
        } else if (event.getSource() == dashboard_btn) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/flowermanagementsystem/admindash.fxml"));
                Stage stage = (Stage) dashboard_btn.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Dashboard");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Navigation Error", "Could not navigate to Dashboard: " + e.getMessage());
            }
        } else if (event.getSource() == menu_btn) {
            System.out.println("Switch to In Season form");
        } else if (event.getSource() == manageorders_btn) {
            System.out.println("Switch to Manage Orders form");
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
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/flowermanagementsystem/FlowerLogin.fxml"));
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

    @FXML
    private void addInventory() {
        try {
            String productId = inventory_productID.getText();
            String productName = inventory_productName.getText();
            String season = inventory_season.getValue();
            double price = Double.parseDouble(inventory_price.getText());
            int stock = Integer.parseInt(inventory_stock.getText());
            String imagePath = currentImagePath != null ? currentImagePath : inventory_image.getText();
            String description = inventory_description.getText();

            boolean success = inventoryService.addInventory(
                productId, productName, season, price, stock, imagePath, description);

            if (success) {
                showInfoAlert("Success", "Product added successfully!");
                clearInventoryFields();
                loadInventoryData(); // Refresh the inventory display
            } else {
                showErrorAlert("Error", "Failed to add product. Please check your input.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format in input fields", e);
            showErrorAlert("Input Error", "Please enter valid numbers for price and stock.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding inventory item", e);
            showErrorAlert("Error", "Failed to add product: " + e.getMessage());
        }
    }

    private void displayInventory() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM flower";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            inventory_grid.getChildren().clear();
            int column = 0;
            int row = 0;
            int maxColumns = 3;

            while (rs.next()) {
                String product_id = rs.getString("flowerId");
                String product_name = rs.getString("name");
                String description = rs.getString("description");
                String season = rs.getString("season");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String image = rs.getString("image");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/flowermanagementsystem/itemcard.fxml"));
                VBox itemCard = loader.load();
                ItemCardController controller = loader.getController();

                controller.setData(product_id, product_name, description, season, price, image, stock, "Update");

                controller.getActionButton().setOnAction(e -> {
                    inventory_productID.setText(product_id);
                    inventory_productName.setText(product_name);
                    inventory_description.setText(description);
                    inventory_season.setValue(season);
                    inventory_price.setText(String.valueOf(price));
                    inventory_stock.setText(String.valueOf(stock));
                    inventory_image.setText(image);
                });

                inventory_grid.add(itemCard, column, row);
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
            showErrorAlert("Error", "Failed to load inventory data");
        }
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnector.connectDB();
    }

    private void closeResources() {
        try {
            if (result != null) {
                result.close();
            }
            if (prepare != null) {
                prepare.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearInventoryFields() {
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_season.setValue(null);
        inventory_price.clear();
        inventory_stock.clear();
        inventory_image.clear();
    }

    @Override
    protected void clearInputFields() {
        inventory_productID.clear();
        inventory_productName.clear();
        inventory_season.setValue(null);
        inventory_price.clear();
        if (inventory_status != null) {
            inventory_status.setValue(null);
        }
        inventory_stock.clear();
        inventory_description.clear();
        inventory_image.clear();
        if (inventory_imageView != null) {
            inventory_imageView.setImage(null);
        }
        if (inventory_search != null) {
            inventory_search.clear();
        }
    }
}
