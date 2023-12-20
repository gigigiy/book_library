package com.example.book_library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.*;

public class HelloController {
    @FXML
    private Label bookNameLabel;

    @FXML
    private Label bookPriceLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Label bookNameLabel2;

    @FXML
    private Label bookPriceLabel2;

    @FXML
    private Button addButton2;

    @FXML
    private Button removeButton2;

    @FXML
    private Label bookNameLabel3;

    @FXML
    private Label bookPriceLabel3;

    @FXML
    private Button addButton3;

    @FXML
    private Button removeButton3;

    @FXML
    private ListView<String> cartListView;

    private ObservableList<String> cartItems = FXCollections.observableArrayList();

    private Connection connection;

    private int[] bookIds = {1, 2, 3}; // IDs of the three books in the controller

    @FXML
    private Label totalPriceLabel;

    private double totalPrice = 0.0;

    @FXML
    public void initialize() {
        connectToDatabase();
        loadBookDetailsByIds();
        calculateTotalPrice();
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql:book_library", "postgres", "123");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void calculateTotalPrice() {
        totalPrice = 0.0;
        for (String item : cartItems) {
            // Split the item to extract the price
            String[] parts = item.split(" - ");
            double price = Double.parseDouble(parts[1]);
            totalPrice += price;
        }
        totalPriceLabel.setText(String.format("Total Price: %.2f", totalPrice));
    }

    private void loadBookDetailsByIds() {
        try {
            for (int i = 0; i < 3; i++) {
                int bookId = bookIds[i];
                String query = "SELECT title, price FROM books WHERE book_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, bookId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String bookTitle = resultSet.getString("title");
                    double bookPrice = resultSet.getDouble("price");

                    switch (i) {
                        case 0:
                            bookNameLabel.setText(bookTitle);
                            bookPriceLabel.setText(String.valueOf(bookPrice));
                            break;
                        case 1:
                            bookNameLabel2.setText(bookTitle);
                            bookPriceLabel2.setText(String.valueOf(bookPrice));
                            break;
                        case 2:
                            bookNameLabel3.setText(bookTitle);
                            bookPriceLabel3.setText(String.valueOf(bookPrice));
                            break;
                    }
                }

                resultSet.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAddButtonClick(ActionEvent event) {
        addToCart(bookIds[0], bookNameLabel.getText(), Double.parseDouble(bookPriceLabel.getText()));
    }

    @FXML
    void onAddButtonClick2(ActionEvent event) {
        addToCart(bookIds[1], bookNameLabel2.getText(), Double.parseDouble(bookPriceLabel2.getText()));
    }

    @FXML
    void onAddButtonClick3(ActionEvent event) {
        addToCart(bookIds[2], bookNameLabel3.getText(), Double.parseDouble(bookPriceLabel3.getText()));
    }

    private void addToCart(int bookId, String bookName, double bookPrice) {
        try {
            String insertQuery = "INSERT INTO cart (book_id, book_name, price, quantity) VALUES (?, ?, ?, 1)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, bookId);
            insertStatement.setString(2, bookName);
            insertStatement.setDouble(3, bookPrice);
            insertStatement.executeUpdate();

            insertStatement.close();

            // Update UI ListView
            cartItems.add(bookName + " - " + bookPrice);
            cartListView.setItems(cartItems);

            calculateTotalPrice(); // Recalculate total price after adding a book
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onRemoveButtonClick(ActionEvent event) {
        removeFromCart();
    }

    @FXML
    void onRemoveButtonClick2(ActionEvent event) {
        removeFromCart();
    }

    @FXML
    void onRemoveButtonClick3(ActionEvent event) {
        removeFromCart();
    }

    private void removeFromCart() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedItem = cartItems.get(selectedIndex);
            cartItems.remove(selectedIndex);

            // Assuming you want to remove items from the database too
            try {
                String query = "DELETE FROM cart WHERE cart_id = ?"; // Assuming cart_id is the primary key for cart table
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, selectedIndex + 1); // Adjusting index since SQL starts from 1
                preparedStatement.executeUpdate();

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            calculateTotalPrice(); // Recalculate total price after removing a book
        }
    }

    // Other methods...

}
