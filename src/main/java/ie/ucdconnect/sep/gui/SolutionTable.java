package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Project;
import ie.ucdconnect.sep.Solution;
import ie.ucdconnect.sep.Student;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

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

        studentColumn.setCellFactory(new Callback<TableColumn<Map.Entry<Project, Student>, String>, TableCell<Map.Entry<Project, Student>, String>>() {
            @Override
            public TableCell<Map.Entry<Project, Student>, String> call(TableColumn<Map.Entry<Project, Student>, String> param) {
                return new TableCell<Map.Entry<Project, Student>, String>(){
                    Student student;
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item, empty);
                        student = null;
                        this.setTextFill(Color.BLACK);

                        if(getIndex() < getTableView().getItems().size() && getIndex() != -1)
                             student = getTableView().getItems().get(getIndex()).getValue();
                        if(student!=null && !student.isGotPreference() && !empty ) {
                            this.setTextFill(Color.RED);
                        }



                        setText(item);
                    }
                };
            }

        });
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
