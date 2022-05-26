module com.example.hw1_zagorcic_amar {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.PacMan to javafx.fxml;
    exports com.example.PacMan;
}