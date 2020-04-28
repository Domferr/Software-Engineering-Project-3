package ie.ucdconnect.sep.controllers;

import com.google.common.collect.ImmutableCollection;
import ie.ucdconnect.sep.*;
import ie.ucdconnect.sep.generators.AsexualGeneticAlgorithm;
import ie.ucdconnect.sep.generators.GeneticAlgorithm;
import ie.ucdconnect.sep.generators.RandomGeneration;
import ie.ucdconnect.sep.generators.SimulatedAnnealing;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main2Controller {

    public Solution solution;   //Generated solution
    private List<Project> projects; //Loaded projects
    private List<Student> students; //Loaded students
    private int test_size;
    private SolutionGenerationStrategy generationStrategy;
    private StudentsTable studentsTable;
    private ProjectsTable projectsTable;

    @FXML
    Slider gpaSlider;
    @FXML
    ChoiceBox<String> algorithmChoiceBox;
    @FXML
    ProgressIndicator progressIndicator;
    @FXML
    Label statusLabel;
    @FXML
    Label bottomBarStatusLabel;
    @FXML
    TableView<Map.Entry<Project, Student>> solutionTableView;
    @FXML
    TableColumn<Map.Entry<Project, Student>, String> solutionStudentColumn;
    @FXML
    TableColumn<Map.Entry<Project, Student>, String> solutionProjectColumn;
    @FXML
    TableView<Student> studentsTableView;
    @FXML
    TableView<Project> projectsTableView;

    @FXML
    public void initialize() {
        setStatusToReady();
        setUpSlider(0,1,0.5);
        setUpAlgorithmChoiceBox(FXCollections.observableArrayList(SimulatedAnnealing.DISPLAY_NAME, GeneticAlgorithm.DISPLAY_NAME, AsexualGeneticAlgorithm.DISPLAY_NAME, RandomGeneration.DISPLAY_NAME));
        setUpStudentsTable("Nothing to display.\n You can press the \"Load Students\" button on the left to load the students.");
        setUpProjectsTable("Nothing to display.\n You can press the \"Load Projects\" button on the left to load the projects.");

        setUpSolutionTable("Nothing to display.\n You can press the \"generate\" button on the left to generate a solution. Remember to select the algorithm and how much importance the student GPA has.");
        try {
            int [] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
            test_size = testSetsStudentsSize[1];
            List<StaffMember> staffMembers = Utils.readStaffMembers();
            projects = Utils.readProjects(staffMembers, test_size);
            students = Utils.readStudents(Utils.generateProjectsMap(projects), test_size);
        } catch (IOException e){
            e.printStackTrace();
        }
        studentsTable.showStudents(students);
        projectsTable.showProjects(projects);
    }

    private void setStatusToBusy(String text) {
        progressIndicator.setPadding(new Insets(0,0,0,0));
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        statusLabel.setText(text);
        bottomBarStatusLabel.setText(text);
    }

    private void setStatusToReady() {
        progressIndicator.setPadding(new Insets(0,0,-24,0));
        progressIndicator.setProgress(1);
        statusLabel.setText("Ready");
        bottomBarStatusLabel.setText("Ready");
    }

    private Label getTableViewPlaceholder(String placeholderText) {
        Label placeholder = new Label(placeholderText);
        placeholder.setWrapText(true);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        placeholder.setPadding(new Insets(48));
        return placeholder;
    }

    private void setUpProjectsTable(String placeholderText) {
        projectsTableView.setPlaceholder(getTableViewPlaceholder(placeholderText));
        projectsTable = new ProjectsTable(projectsTableView);
    }

    private void setUpStudentsTable(String placeholderText) {
        studentsTableView.setPlaceholder(getTableViewPlaceholder(placeholderText));
        studentsTable = new StudentsTable(studentsTableView);
    }

    private void setUpSolutionTable(String placeholderText) {
        solutionTableView.setPlaceholder(getTableViewPlaceholder(placeholderText));
        //TODO set up the solution table and update it each time a new solution is generated
        solutionStudentColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getValue().getFullName()));
        solutionProjectColumn.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getKey().getTitle()));
    }

    private void setUpAlgorithmChoiceBox(ObservableList<String> algorithmsName) {
        algorithmChoiceBox.setItems(algorithmsName);
        algorithmChoiceBox.setValue(SimulatedAnnealing.DISPLAY_NAME);
        generationStrategy = new SimulatedAnnealing();
        algorithmChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newIndex) {
                switch (algorithmChoiceBox.getItems().get((Integer) newIndex)) {
                    case SimulatedAnnealing.DISPLAY_NAME:
                        generationStrategy = new SimulatedAnnealing();
                        break;
                    case GeneticAlgorithm.DISPLAY_NAME:
                        generationStrategy = new GeneticAlgorithm();
                        break;
                    case AsexualGeneticAlgorithm.DISPLAY_NAME:
                        generationStrategy = new AsexualGeneticAlgorithm();
                        break;
                    case RandomGeneration.DISPLAY_NAME:
                        generationStrategy = new RandomGeneration();
                        break;
                }
                System.out.println("Algorithm selected: "+generationStrategy.getDisplayName());
            }
        });
    }

    private void setUpSlider(double sliderMin, double sliderMax, double sliderValue) {
        gpaSlider.setMax(sliderMax);
        gpaSlider.setMin(sliderMin);
        gpaSlider.setValue(sliderValue);
        gpaSlider.setShowTickMarks(false);
        gpaSlider.setShowTickLabels(true);
        gpaSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!gpaSlider.isValueChanging() || newValue.doubleValue() == sliderMax || newValue.doubleValue() == sliderMin) {
                Solution.GPA_IMPORTANCE = newValue.doubleValue() / sliderMax;
                System.out.println("Updated GPA_IMPORTANCE: " + Solution.GPA_IMPORTANCE);
            }
        });
        gpaSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n == sliderMin) return "Low";
                else if (n == sliderMax) return "High";
                return n.toString();
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Low":
                        return sliderMin;
                    case "High":
                        return sliderMax;
                    default:
                        return Double.parseDouble(s);
                }
            }
        });
    }

    @FXML
    private void generateSolution() {
        setStatusToBusy("Running "+generationStrategy.getDisplayName());
        GeneratorTask generatorTask = new GeneratorTask(generationStrategy, projects, students);
        generatorTask.setOnSucceeded(e -> {
            setStatusToReady();
            solution = generatorTask.getValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Solution generation finished.");
            alert.setContentText("Fitness: " + solution.getFitness() + ". Energy: " + solution.getEnergy());
            alert.showAndWait();

        });
        new Thread(generatorTask).start();
    }

    @FXML
    public void saveResults() throws IOException {
        if(solution == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No Solution Found!");
            alert.setContentText("A solution must be generated before results can be saved");
            alert.showAndWait();
        }else{
            SolutionGenerator.saveSolution(solution, test_size);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Results saved!");
            alert.setContentText("You can view and download the results!");
            alert.showAndWait();
        }

    }

    @FXML
    public void loadProjects(){
        //TODO
        //After loading call projectsTable.showProjects(projects);
    }

    @FXML
    public void loadStudents(){
        //TODO
        //After loading call studentsTable.showStudents(students);
    }
}

