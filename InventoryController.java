package com.example.final_flowerorderingsystem;

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

public class inventoryController implements Initializable {

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

    @FXML
    private Button profile_btn;

    @FXML
    private Button settings_btn;

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
    private TableColumn<InventoryData, String> inventory_col_status;

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

    // Profile Form Components
    @FXML
    private AnchorPane profile_form;

    @FXML
    private ImageView profile_image;

    @FXML
    private TextField profile_username;

    @FXML
    private TextField profile_email;

    @FXML
    private TextField profile_phone;

    @FXML
    private Button profile_importBtn;

    @FXML
    private Button profile_saveBtn;

    // Settings Form Components
    @FXML
    private AnchorPane settings_form;

    @FXML
    private TextField settings_currentPassword;

    @FXML
    private TextField settings_newPassword;

    @FXML
    private TextField settings_confirmPassword;

    @FXML
    private CheckBox settings_emailNotifications;

    @FXML
    private CheckBox settings_orderUpdates;

    @FXML
    private CheckBox settings_promotions;

    @FXML
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
        inventory_form.setVisible(true);
        profile_form.setVisible(false);
        settings_form.setVisible(false);
        
        inventory_season.setItems(FXCollections.observableArrayList(
            "Wet", "Dry", "All Year Round"
        ));
        
        inventory_status.setItems(FXCollections.observableArrayList(
            "Available", "Low Stock", "Out of Stock"
        ));

        inventory_col_productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        inventory_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        inventory_col_season.setCellValueFactory(new PropertyValueFactory<>("season"));
        inventory_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        inventory_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
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
        loadProfileData();
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
            InventoryData newItem = new InventoryData(
                inventory_productID.getText(),
                inventory_productName.getText(),
                inventory_season.getValue(),
                Double.parseDouble(inventory_price.getText()),
                inventory_status.getValue(),
                Integer.parseInt(inventory_stock.getText()),
                java.time.LocalDate.now().toString(),
                inventory_description.getText(),
                currentImagePath
            );
            inventoryList.add(newItem);
            clearFields();
            showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void inventoryUpdateBtn() {
        InventoryData selectedItem = inventory_tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && validateInput()) {
            selectedItem.setProductName(inventory_productName.getText());
            selectedItem.setSeason(inventory_season.getValue());
            selectedItem.setPrice(Double.parseDouble(inventory_price.getText()));
            selectedItem.setStatus(inventory_status.getValue());
            selectedItem.setStock(Integer.parseInt(inventory_stock.getText()));
            selectedItem.setDescription(inventory_description.getText());
            if (currentImagePath != null) {
                selectedItem.setImagePath(currentImagePath);
            }
            inventory_tableView.refresh();
            clearFields();
            showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Please select a product to update!", Alert.AlertType.ERROR);
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
            inventoryList.remove(selectedItem);
            clearFields();
            showAlert("Success", "Product deleted successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Please select a product to delete!", Alert.AlertType.ERROR);
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
            showAlert("Error", "Please fill in all required fields!", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Double.parseDouble(inventory_price.getText());
            Integer.parseInt(inventory_stock.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for price and stock!", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadInventoryData() {
        // TODO: Load data from database
        // For now, adding some sample data
        inventoryList.add(new InventoryData(
            "P001",
            "Rose",
            "Spring",
            25.99,
            "Available",
            100,
            "2024-03-20",
            "Beautiful red roses",
            null
        ));
    }

    // Profile Methods
    @FXML
    private void profileImportBtn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            currentProfileImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            profile_image.setImage(image);
        }
    }

    @FXML
    private void profileSaveBtn() {
        if (validateProfileInput()) {
            // TODO: Save profile data to database
            showAlert("Success", "Profile updated successfully!", Alert.AlertType.INFORMATION);
        }
    }

    private boolean validateProfileInput() {
        if (profile_username.getText().isEmpty() ||
            profile_email.getText().isEmpty() ||
            profile_phone.getText().isEmpty()) {
            showAlert("Error", "Please fill in all required fields!", Alert.AlertType.ERROR);
            return false;
        }

        if (!isValidEmail(profile_email.getText())) {
            showAlert("Error", "Please enter a valid email address!", Alert.AlertType.ERROR);
            return false;
        }

        if (!isValidPhone(profile_phone.getText())) {
            showAlert("Error", "Please enter a valid phone number!", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^\\d{10,11}$");
    }

    // Settings Methods
    @FXML
    private void settingsSaveBtn() {
        if (validateSettingsInput()) {
            // TODO: Save settings to database
            showAlert("Success", "Settings updated successfully!", Alert.AlertType.INFORMATION);
        }
    }

    private boolean validateSettingsInput() {
        if (!settings_newPassword.getText().equals(settings_confirmPassword.getText())) {
            showAlert("Error", "New passwords do not match!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void loadProfileData() {
        // TODO: Load profile data from database
        // For now, setting placeholder data
        profile_username.setText("Admin User");
        profile_email.setText("admin@example.com");
        profile_phone.setText("1234567890");
    }

    @FXML
    private void switchForm(ActionEvent event) {
        inventory_form.setVisible(false);
        profile_form.setVisible(false);
        settings_form.setVisible(false);

        if (event.getSource() == inventory_btn) {
            inventory_form.setVisible(true);
            loadInventoryData();
        } else if (event.getSource() == dashboard_btn) {
            // Placeholder for Dashboard form
            System.out.println("Switch to Dashboard form");
        } else if (event.getSource() == menu_btn) {
            // Placeholder for In Season form
            System.out.println("Switch to In Season form");
        } else if (event.getSource() == manageorders_btn) {
            // Placeholder for Manage Orders form
            System.out.println("Switch to Manage Orders form");
        } else if (event.getSource() == profile_btn) {
            profile_form.setVisible(true);
            loadProfileData();
        } else if (event.getSource() == settings_btn) {
            settings_form.setVisible(true);
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

    @FXML
    private void addInventory() {
        try {
            String sql = "INSERT INTO product (product_id, product_name, season, price, stock, image) VALUES (?, ?, ?, ?, ?, ?)";
            connect = database.connectDB();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, inventory_productID.getText());
            prepare.setString(2, inventory_productName.getText());
            prepare.setString(3, inventory_season.getValue());
            prepare.setDouble(4, Double.parseDouble(inventory_price.getText()));
            prepare.setInt(5, Integer.parseInt(inventory_stock.getText()));
            prepare.setString(6, inventory_image.getText());

            prepare.executeUpdate();
            showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
            clearInventoryFields();
            displayInventory();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add product. Please check your input.", Alert.AlertType.ERROR);
        }
    }

    private void displayInventory() {
        try {
            Connection conn = getConnection();
            String query = "SELECT * FROM product";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            inventory_grid.getChildren().clear();
            int column = 0;
            int row = 0;
            int maxColumns = 3;

            while (rs.next()) {
                String product_id = rs.getString("product_id");
                String product_name = rs.getString("product_name");
                String description = rs.getString("description");
                String season = rs.getString("season");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String image = rs.getString("image");

                // Load the item card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_flowerorderingsystem/itemCard.fxml"));
                VBox itemCard = loader.load();
                ItemCardController controller = loader.getController();

                // Set the data
                controller.setData(product_id, product_name, description, season, price, image, stock, "Update");

                // Set up the action button
                controller.getActionButton().setOnAction(e -> {
                    inventory_productID.setText(product_id);
                    inventory_productName.setText(product_name);
                    inventory_description.setText(description);
                    inventory_season.setValue(season);
                    inventory_price.setText(String.valueOf(price));
                    inventory_stock.setText(String.valueOf(stock));
                    inventory_image.setText(image);
                });

                // Add to grid
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
            showAlert("Error", "Failed to load inventory data", Alert.AlertType.ERROR);
        }
    }

    private Connection getConnection() throws SQLException {
        return database.connectDB();
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
}
