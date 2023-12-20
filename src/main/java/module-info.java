module com.example.book_library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.book_library to javafx.fxml;
    exports com.example.book_library;
}