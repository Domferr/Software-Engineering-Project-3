package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Project;
import ie.ucdconnect.sep.Solution;
import ie.ucdconnect.sep.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.*;

/** Class that manages the solution into a table view */
public class SolutionTable {

    TableView<Map.Entry<Project, Student>> solutionTableView;
    TableColumn<Map.Entry<Project, Student>, String> studentColumn;
    TableColumn<Map.Entry<Project, Student>, String> assignedProjectColumn;
    Button saveSolutionBtn;

    public SolutionTable(TableView<Map.Entry<Project, Student>> solutionTableView, Button saveSolutionBtn) {
        this.solutionTableView = solutionTableView;
        this.saveSolutionBtn = saveSolutionBtn;
        hideSaveBtn();
        studentColumn = new TableColumn<>("Student");
        assignedProjectColumn = new TableColumn<>("Assigned Project");
        setUp();
    }

    private void setUp() {
        studentColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getValue().getName()));
        assignedProjectColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getKey().getTitle()));
        solutionTableView.getColumns().setAll(studentColumn, assignedProjectColumn);
        //Sort by students
        solutionTableView.getSortOrder().add(studentColumn);
    }

    public void showSolution(Solution solution) {
        if (solution != null) {
            //This list is needed because the columns should be sorted by the user
            List<Map.Entry<Project, Student>> entriesList = new ArrayList<>(solution.getEntries());
            solutionTableView.setItems(FXCollections.observableList(entriesList));
            showSaveBtn();
        }
    }

    public void hideSaveBtn() {
        saveSolutionBtn.setVisible(false);
    }

    public void showSaveBtn() {
        saveSolutionBtn.setVisible(true);
    }
}
