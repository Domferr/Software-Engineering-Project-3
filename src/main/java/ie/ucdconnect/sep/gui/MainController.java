package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.*;
import ie.ucdconnect.sep.generators.AsexualGeneticAlgorithm;
import ie.ucdconnect.sep.generators.GeneticAlgorithm;
import ie.ucdconnect.sep.generators.RandomGeneration;
import ie.ucdconnect.sep.generators.SimulatedAnnealing;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainController {

    public Solution solution;   //Generated solution
    private List<StaffMember> staffMembers;
    private List<Project> projects; //Loaded projects
    private List<Student> students; //Loaded students
    private int test_size;
    private SolutionGenerationStrategy generationStrategy;
    private StudentsTable studentsTable;
    private ProjectsTable projectsTable;
    private SolutionTable solutionTable;

    private FileChooser fileChooser;

    //Settings
    @FXML
    Slider gpaSlider;
    @FXML
    ChoiceBox<String> algorithmChoiceBox;

    //Status
    @FXML
    ProgressIndicator progressIndicator;
    @FXML
    Label statusLabel;
    @FXML
    Label bottomBarStatusLabel;

    // Table views
    @FXML
    TableView<Map.Entry<Project, Student>> solutionTableView;
    @FXML
    TableView<Student> studentsTableView;
    @FXML
    TableView<Project> projectsTableView;

    @FXML
    Button loadStudentsBtn;
    @FXML
    Button loadProjectsBtn;
    @FXML
    Button loadStaffBtn;

    @FXML
    public void initialize() {
        setStatusToReady();
        setUpSlider(0,1,0.5);
        setUpAlgorithmChoiceBox(FXCollections.observableArrayList(SimulatedAnnealing.DISPLAY_NAME, GeneticAlgorithm.DISPLAY_NAME, AsexualGeneticAlgorithm.DISPLAY_NAME, RandomGeneration.DISPLAY_NAME));
        setUpStudentsTable("Nothing to display.\n You can press the \"Load Students\" button on the left to load the students.");
        setUpProjectsTable("Nothing to display.\n You can press the \"Load Projects\" button on the left to load the projects.");
        setUpSolutionTable("Nothing to display.\n You can press the \"generate\" button on the left to generate a solution. Remember to select the algorithm and how much importance the student GPA has.");

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // Projects will be enabled after staff are loaded, and students after projects
        loadStudentsBtn.setDisable(true);
        loadProjectsBtn.setDisable(true);
        try {
            int [] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
            test_size = testSetsStudentsSize[1];
        } catch (IOException e){
            e.printStackTrace();
        }
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
        solutionTable = new SolutionTable(solutionTableView);
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
            Solution.GPA_IMPORTANCE = newValue.doubleValue() / sliderMax;
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
        if (projects == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Project data has not been loaded.");
            alert.setContentText("Please load project data before generating a solution.");
            alert.showAndWait();
            return;
        }
        if (students == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Student data has not been loaded.");
            alert.setContentText("Please load student data before generating a solution.");
            alert.showAndWait();
            return;
        }
        setStatusToBusy("Running "+generationStrategy.getDisplayName());
        GeneratorTask generatorTask = new GeneratorTask(generationStrategy, projects, students);
        generatorTask.setOnSucceeded(e -> {
            setStatusToReady();
            solution = generatorTask.getValue();
            solutionTable.showSolution(solution);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Solution generation finished.");
            alert.setContentText("Fitness: " + solution.getFitness() + ". Energy: " + solution.getEnergy());
            alert.showAndWait();

        });
        generatorTask.setOnCancelled(this::onTaskCancel);
        generatorTask.setOnFailed(this::onTaskCancel);
        new Thread(generatorTask).start();
    }

    private void onTaskCancel(WorkerStateEvent workerStateEvent) {
        if (workerStateEvent.getSource().getException() != null) {
            workerStateEvent.getSource().getException().printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Solution generation failed/canceled.");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }

    @FXML
    public void saveResults() throws IOException {
        if(solution == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No Solution Found!");
            alert.setContentText("A solution must be generated before results can be saved");
            alert.showAndWait();
        }else{
            // TODO: We should use a different method to save as a custom name, use a file chooser.
            SolutionGenerator.saveSolution(solution, solution.getEntries().size());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Results saved!");
            alert.setContentText("You can view and download the results!");
            alert.showAndWait();
        }

    }


    @FXML
    public void loadProjects(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        fileChooser.setTitle("Choose project file");
        File file = fileChooser.showOpenDialog(new Stage());


        try{
            String fileContent = Utils.readFile(file.toPath());
            projects = Project.fromCSV(fileContent, staffMembers);
            projectsTable.showProjects(projects);
            loadStudentsBtn.setDisable(false);
            if (students != null) {
                students = Student.fromCSV(fileContent, Utils.generateProjectsMap(projects));
                studentsTable.showStudents(students);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [name, focus, project]");
            alert.showAndWait();
        }
    }

    @FXML
    public void loadStudents(){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        fileChooser.setTitle("Choose student file");
        File file = fileChooser.showOpenDialog(new Stage());

        try{
            String fileContent = Utils.readFile(file.toPath());
            students = Student.fromCSV(fileContent, Utils.generateProjectsMap(projects));
            studentsTable.showStudents(students);
        }catch (IOException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [student no., first name, last name, gpa, stream, 10 project preferences each seperated by ,]");
            alert.showAndWait();
        }
    }
    @FXML
    public void loadStaffMembers(){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        fileChooser.setTitle("Choose Staff file");
        File file = fileChooser.showOpenDialog(new Stage());

        try{
            String fileContent = Utils.readFile(file.toPath());
            staffMembers = StaffMember.fromCSV(fileContent);
            loadProjectsBtn.setDisable(false);
        }catch (IOException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [name, research activity, research area, special focus]");
            alert.showAndWait();
        }
    }
}

