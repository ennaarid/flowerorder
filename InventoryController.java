package com.flowerorder;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class InventoryController {
    @FXML
    private AnchorPane inventory_form;
    
    @FXML
    private TableView<Inventory> inventory_tableView;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_productID;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_productName;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_season;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_price;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_status;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_stock;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_date;
    
    @FXML
    private TableColumn<Inventory, String> inventory_col_description;
    
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
    
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    
    private Image image;
    private String imagePath;
    
    public void initialize() {
        inventorySeasonList();
        inventoryStatusList();
        inventoryShowData();
    }
    
    public void inventorySeasonList() {
        ObservableList<String> list = FXCollections.observableArrayList(
            "Spring", "Summer", "Fall", "Winter", "All Year"
        );
        inventory_season.setItems(list);
    }
    
    public void inventoryStatusList() {
        ObservableList<String> list = FXCollections.observableArrayList(
            "Available", "Low Stock", "Out of Stock"
        );
        inventory_status.setItems(list);
    }
    
    public void inventoryImportBtn() {
        FileChooser open = new FileChooser();
        open.setTitle("Open Image File");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*jpg", "*png"));
        
        File file = open.showOpenDialog(inventory_form.getScene().getWindow());
        
        if (file != null) {
            imagePath = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 160, 140, false, true);
            inventory_imageView.setImage(image);
        }
    }
    
    public void inventoryAddBtn() {
        String sql = "INSERT INTO inventory (product_id, product_name, season, price, status, stock, date, description, image) VALUES (?,?,?,?,?,?,?,?,?)";
        connect = Database.connectDB();
        
        try {
            Alert alert;
            
            if (inventory_productID.getText().isEmpty() || 
                inventory_productName.getText().isEmpty() || 
                inventory_season.getSelectionModel().getSelectedItem() == null || 
                inventory_price.getText().isEmpty() || 
                inventory_status.getSelectionModel().getSelectedItem() == null || 
                inventory_stock.getText().isEmpty() || 
                inventory_description.getText().isEmpty() || 
                imagePath == null) {
                
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, inventory_productID.getText());
                prepare.setString(2, inventory_productName.getText());
                prepare.setString(3, inventory_season.getSelectionModel().getSelectedItem());
                prepare.setDouble(4, Double.parseDouble(inventory_price.getText()));
                prepare.setString(5, inventory_status.getSelectionModel().getSelectedItem());
                prepare.setInt(6, Integer.parseInt(inventory_stock.getText()));
                prepare.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
                prepare.setString(8, inventory_description.getText());
                prepare.setString(9, imagePath);
                
                prepare.executeUpdate();
                
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Added!");
                alert.showAndWait();
                
                inventoryShowData();
                inventoryClearBtn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void inventoryUpdateBtn() {
        String sql = "UPDATE inventory SET product_name = ?, season = ?, price = ?, status = ?, stock = ?, description = ?, image = ? WHERE product_id = ?";
        connect = Database.connectDB();
        
        try {
            Alert alert;
            
            if (inventory_productID.getText().isEmpty() || 
                inventory_productName.getText().isEmpty() || 
                inventory_season.getSelectionModel().getSelectedItem() == null || 
                inventory_price.getText().isEmpty() || 
                inventory_status.getSelectionModel().getSelectedItem() == null || 
                inventory_stock.getText().isEmpty() || 
                inventory_description.getText().isEmpty() || 
                imagePath == null) {
                
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE Product ID: " + inventory_productID.getText() + "?");
                
                Optional<ButtonType> option = alert.showAndWait();
                
                if (option.get().equals(ButtonType.OK)) {
                    prepare = connect.prepareStatement(sql);
                    prepare.setString(1, inventory_productName.getText());
                    prepare.setString(2, inventory_season.getSelectionModel().getSelectedItem());
                    prepare.setDouble(3, Double.parseDouble(inventory_price.getText()));
                    prepare.setString(4, inventory_status.getSelectionModel().getSelectedItem());
                    prepare.setInt(5, Integer.parseInt(inventory_stock.getText()));
                    prepare.setString(6, inventory_description.getText());
                    prepare.setString(7, imagePath);
                    prepare.setString(8, inventory_productID.getText());
                    
                    prepare.executeUpdate();
                    
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Updated!");
                    alert.showAndWait();
                    
                    inventoryShowData();
                    inventoryClearBtn();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void inventoryDeleteBtn() {
        String sql = "DELETE FROM inventory WHERE product_id = ?";
        connect = Database.connectDB();
        
        try {
            Alert alert;
            
            if (inventory_productID.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill the product ID");
                alert.showAndWait();
            } else {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to DELETE Product ID: " + inventory_productID.getText() + "?");
                
                Optional<ButtonType> option = alert.showAndWait();
                
                if (option.get().equals(ButtonType.OK)) {
                    prepare = connect.prepareStatement(sql);
                    prepare.setString(1, inventory_productID.getText());
                    
                    prepare.executeUpdate();
                    
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Deleted!");
                    alert.showAndWait();
                    
                    inventoryShowData();
                    inventoryClearBtn();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void inventoryClearBtn() {
        inventory_productID.setText("");
        inventory_productName.setText("");
        inventory_season.getSelectionModel().clearSelection();
        inventory_price.setText("");
        inventory_status.getSelectionModel().clearSelection();
        inventory_stock.setText("");
        inventory_description.setText("");
        inventory_imageView.setImage(null);
        imagePath = null;
    }
    
    public ObservableList<Inventory> inventoryDataList() {
        ObservableList<Inventory> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM inventory";
        connect = Database.connectDB();
        
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            Inventory inventory;
            
            while (result.next()) {
                inventory = new Inventory(
                    result.getInt("product_id"),
                    result.getString("product_name"),
                    result.getString("season"),
                    result.getDouble("price"),
                    result.getString("status"),
                    result.getInt("stock"),
                    result.getDate("date").toLocalDate(),
                    result.getString("description"),
                    result.getString("image")
                );
                listData.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    private ObservableList<Inventory> inventoryListData;
    
    public void inventoryShowData() {
        inventoryListData = inventoryDataList();
        
        inventory_col_productID.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProductId())));
        inventory_col_productName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        inventory_col_season.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSeason()));
        inventory_col_price.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
        inventory_col_status.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        inventory_col_stock.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStock())));
        inventory_col_date.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDate())));
        inventory_col_description.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        
        inventory_tableView.setItems(inventoryListData);
    }
    
    public void inventorySelectData() {
        Inventory inventory = inventory_tableView.getSelectionModel().getSelectedItem();
        int num = inventory_tableView.getSelectionModel().getSelectedIndex();
        
        if ((num - 1) < -1) {
            return;
        }
        
        inventory_productID.setText(String.valueOf(inventory.getProductId()));
        inventory_productName.setText(inventory.getProductName());
        inventory_season.setValue(inventory.getSeason());
        inventory_price.setText(String.valueOf(inventory.getPrice()));
        inventory_status.setValue(inventory.getStatus());
        inventory_stock.setText(String.valueOf(inventory.getStock()));
        inventory_description.setText(inventory.getDescription());
        
        imagePath = inventory.getImagePath();
        image = new Image(imagePath, 160, 140, false, true);
        inventory_imageView.setImage(image);
    }
} 