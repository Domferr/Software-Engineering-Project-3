package ie.ucdconnect.sep.controllers;

import ie.ucdconnect.sep.Project;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ProjectsTable {

    private TableView<Project> projectsTableView;
    private TableColumn<Project, String> projectSupervisorColumn;
    private TableColumn<Project, String> projectTitleColumn;
    private TableColumn<Project, String> projectTypeColumn;

    public ProjectsTable(TableView<Project> projectsTableView) {
        this.projectsTableView = projectsTableView;
        projectSupervisorColumn = new TableColumn<>("Supervisor");
        projectTitleColumn = new TableColumn<>("Title");
        projectTypeColumn = new TableColumn<>("Type");
        setUp();
    }

    private void setUp() {
        projectSupervisorColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getSupervisor().getName()));
        projectTitleColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getTitle()));
        projectTypeColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getType().toString()));
        projectsTableView.getColumns().setAll(projectSupervisorColumn, projectTitleColumn, projectTypeColumn);
        //Sort by supervisor
        projectsTableView.getSortOrder().add(projectSupervisorColumn);
    }

    public void showProjects(List<Project> projectList) {
        projectsTableView.setItems(FXCollections.observableList(projectList));
    }
}