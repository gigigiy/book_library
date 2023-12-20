package com.example.book_library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection connection = DataBaseConnection.getConnection();

        if (connection != null) {
            try {
                String selectQuery = "SELECT * FROM books";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    double price = resultSet.getDouble("price");

                    System.out.println("Title: " + title + ", Price: " + price);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DataBaseConnection.closeConnection();
            }
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}

