package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/** Class that manages students into a table view */
public class StudentsTable {

    private TableView<Student> studentsTableView;
    private TableColumn<Student, String> studentNumberColumn;
    private TableColumn<Student, String> studentNameColumn;

    public StudentsTable(TableView<Student> studentsTableView) {
        this.studentsTableView = studentsTableView;
        studentNumberColumn = new TableColumn<>("Student Number");
        studentNameColumn = new TableColumn<>("Name");
        setUp();
    }

    private void setUp() {
        studentNumberColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getStudentNumber()));
        studentNameColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getName()));
        studentsTableView.getColumns().setAll(studentNameColumn, studentNumberColumn);
        //Sort by first name
        studentsTableView.getSortOrder().add(studentNameColumn);
    }

    public void showStudents(List<Student> studentList) {
        studentsTableView.setItems(FXCollections.observableList(studentList));
    }
}
