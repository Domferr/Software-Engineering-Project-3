package ie.ucdconnect.sep.controllers;

import ie.ucdconnect.sep.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class StudentsTable {

    private TableView<Student> studentsTableView;
    private TableColumn<Student, String> studentNumberColumn;
    private TableColumn<Student, String> studentFirstnameColumn;
    private TableColumn<Student, String> studentLastnameColumn;

    public StudentsTable(TableView<Student> studentsTableView) {
        this.studentsTableView = studentsTableView;
        studentNumberColumn = new TableColumn<>("Student Number");
        studentFirstnameColumn = new TableColumn<>("First Name");
        studentLastnameColumn = new TableColumn<>("Last Name");
        setUp();
    }

    private void setUp() {
        studentNumberColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getStudentNumber()));
        studentFirstnameColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getFirstName()));
        studentLastnameColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getLastName()));
        studentsTableView.getColumns().setAll(studentFirstnameColumn, studentLastnameColumn, studentNumberColumn);
        //Sort by first name
        studentsTableView.getSortOrder().add(studentFirstnameColumn);
    }

    public void showStudents(List<Student> studentList) {
        studentsTableView.setItems(FXCollections.observableList(studentList));
    }
}
