package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Project;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/** Class that manages projects into a table view */
public class ProjectsTable {

    private TableView<Project> projectsTableView;
    private TableColumn<Project, String> projectSupervisorColumn;
    private TableColumn<Project, String> projectTitleColumn;

    public ProjectsTable(TableView<Project> projectsTableView) {
        this.projectsTableView = projectsTableView;
        projectSupervisorColumn = new TableColumn<>("Supervisor");
        projectTitleColumn = new TableColumn<>("Title");
        setUp();
    }

    private void setUp() {
        projectSupervisorColumn.setCellValueFactory((p) -> new SimpleStringProperty(getSupervisor(p.getValue())));
        projectTitleColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getTitle()));
        projectsTableView.getColumns().setAll(projectSupervisorColumn, projectTitleColumn);
        //Sort by supervisor
        projectsTableView.getSortOrder().add(projectSupervisorColumn);
    }

    private String getSupervisor(Project project) {
        return project.getSupervisor() == null ? "N/A" : project.getSupervisor().getName();
    }

    public void showProjects(List<Project> projectList) {
        projectsTableView.setItems(FXCollections.observableList(projectList));
    }
}
