
package com.example.flowermanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class PaymentController extends BaseController {
    @FXML private TextField cardNumber;
    @FXML private TextField expiryDate;
    @FXML private TextField cvv;
    @FXML private Button confirmPaymentBtn;

    @FXML
    public void initialize() {
        confirmPaymentBtn.setOnAction(event -> processPayment());
    }

    private void processPayment() {
        if (validatePaymentDetails()) {
            // Process payment logic here
            showInfoAlert("Success", "Payment processed successfully!");
            loadView("OrderReceipt", confirmPaymentBtn);
        }
    }

    private boolean validatePaymentDetails() {
        if (cardNumber.getText().isEmpty() || expiryDate.getText().isEmpty() || cvv.getText().isEmpty()) {
            showErrorAlert("Validation Error", "Please fill in all payment details");
            return false;
        }
        return true;
    }

    @Override
    protected void clearInputFields() {
        cardNumber.clear();
        expiryDate.clear();
        cvv.clear();
    }
}
