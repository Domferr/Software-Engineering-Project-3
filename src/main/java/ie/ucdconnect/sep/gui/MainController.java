package ie.ucdconnect.sep.gui;

import com.opencsv.CSVParser;
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
import java.util.*;

public class MainController {

    private Solution solution;              //Generated solution
    private List<StaffMember> staffMembers; //Loaded staff members
    private List<Project> projects;         //Loaded projects
    private List<Student> students;         //Loaded students
    private SolutionGenerationStrategy generationStrategy;  //Algorithm used for generating solution
    // Tables to show data
    private StudentsTable studentsTable;
    private ProjectsTable projectsTable;
    private SolutionTable solutionTable;

    // File chooser and supported extensions
    private FileChooser fileChooser;
    private final FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV file", "*.csv");
    private final FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text file", "*.txt");
    private final FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("XLSX file", "*.xlsx");
    // Algorithm Settings
    @FXML
    Slider gpaSlider;
    @FXML
    ChoiceBox<String> algorithmChoiceBox;

    // Status
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

    // Buttons
    @FXML
    Button loadStudentsBtn;
    @FXML
    Button loadProjectsBtn;
    @FXML
    Button loadStaffBtn;
    @FXML
    Button saveSolutionBtn;

    @FXML
    public void initialize() {
        setStatusToReady();
        setUpSlider(0,1,0.5);
        setUpAlgorithmChoiceBox(FXCollections.observableArrayList(SimulatedAnnealing.DISPLAY_NAME, GeneticAlgorithm.DISPLAY_NAME, AsexualGeneticAlgorithm.DISPLAY_NAME, RandomGeneration.DISPLAY_NAME));
        setUpStudentsTable("Nothing to display.\n You can press the \"Load Students\" button on the left to load the students.");
        setUpProjectsTable("Nothing to display.\n You can press the \"Load Projects\" button on the left to load the projects.");
        setUpSolutionTable("Nothing to display.\n You can press the \"generate\" button on the left to generate a solution. Remember to select the algorithm and how much importance the student GPA has.");

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(csvFilter, txtFilter, xlsxFilter);

        // Projects will be enabled after staff members are loaded, and students after projects
        loadStudentsBtn.setDisable(true);
        loadProjectsBtn.setDisable(true);
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
        solutionTable = new SolutionTable(solutionTableView, saveSolutionBtn);
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

    /** Method invoked when the button for generating a solution is clicked.  */
    @FXML
    private void generateSolutionOnClick() {
        final String[] ORDINALS ={"st", "nd", "rd", "th"};

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
            StringBuilder alertText = new StringBuilder();
            setStatusToReady();
            solution = generatorTask.getValue();
            solutionTable.showSolution(solution);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Solution generation finished.");
            alertText.append("Fitness: ").append(solution.getFitness()).append(". Energy: ").append(solution.getEnergy()).append("\n");
            alertText.append("Report of Preferences of Students\n");

            for(int key : solution.getPreferenceResults().keySet()){
                if (key == -1){
                    alertText.append("No Preference: ").append(solution.getPreferenceResults().get(key)).append("\n");
                }
                else if(key >= 0 && key <= 3){
                    alertText.append(key + 1).append(ORDINALS[key]).append(" Preference: ").append(solution.getPreferenceResults().get(key)).append("\n");
                }else {
                    alertText.append(key + 1).append(ORDINALS[3]).append(" Preference: ").append(solution.getPreferenceResults().get(key)).append("\n");
                }
            }
            alert.setContentText(alertText.toString());
            alert.showAndWait();

        });
        generatorTask.setOnCancelled(this::onGenerationCancel);
        generatorTask.setOnFailed(this::onGenerationCancel);
        new Thread(generatorTask).start();
    }

    /** Method invoked is the generator task fails for some reason. */
    private void onGenerationCancel(WorkerStateEvent workerStateEvent) {
        if (workerStateEvent.getSource().getException() != null) {
            workerStateEvent.getSource().getException().printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Solution generation failed/canceled.");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }

    /** Method invoked when the button for saving the solution is clicked.  */
    @FXML
    public void saveSolutionOnClick() {
        if (solution == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No Solution Found!");
            alert.setContentText("A solution must be generated before results can be saved");
            alert.showAndWait();
        } else {
            //Show save file dialog
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                FileChooser.ExtensionFilter selectedExtension = fileChooser.getSelectedExtensionFilter();

                try {
                    //Save in CSV format for both .csv and .txt
                    if (csvFilter.equals(selectedExtension)) {
                        FileSaver.saveSolutionAsCSV(file, solution);
                    } else if (txtFilter.equals(selectedExtension)) {
                        FileSaver.saveSolutionAsTXT(file, solution);
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Results saved!");
                    alert.setContentText("You have saved this solution!");
                    alert.showAndWait();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    /** Method invoked when the button for loading projects is clicked.  */
    @FXML
    public void loadProjectsOnClick() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        fileChooser.setTitle("Choose project file");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            System.out.println("No file selected");
            return;
        }
        FileChooser.ExtensionFilter selectedExtension = fileChooser.getSelectedExtensionFilter();
        try {
            if (csvFilter.equals(selectedExtension)) {
                projects = FileLoader.loadProjectsFromCSV(file, staffMembers);
                if (students != null) {
                    students = FileLoader.loadStudentsFromCSV(file, Utils.generateProjectsMap(projects));
                    studentsTable.showStudents(students);
                }
            } else if (txtFilter.equals(selectedExtension)) {
                projects = FileLoader.loadProjectsFromTXT(file, staffMembers);
                if (students != null) {
                    students = FileLoader.loadStudentsFromTXT(file, Utils.generateProjectsMap(projects));
                    studentsTable.showStudents(students);
                }
            }
            projectsTable.showProjects(projects);
            loadStudentsBtn.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [name, focus, project]");
            alert.showAndWait();
        }
    }

    /** Method invoked when the button for loading students is clicked.  */
    @FXML
    public void loadStudentsOnClick() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        fileChooser.setTitle("Choose student file");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            System.out.println("No file selected");
            return;
        }
        FileChooser.ExtensionFilter selectedExtension = fileChooser.getSelectedExtensionFilter();
        try {
            if (csvFilter.equals(selectedExtension)) {
                students = FileLoader.loadStudentsFromCSV(file, Utils.generateProjectsMap(projects));
            } else if (txtFilter.equals(selectedExtension)) {
                students = FileLoader.loadStudentsFromTXT(file, Utils.generateProjectsMap(projects));
            }
            studentsTable.showStudents(students);
        } catch (IllegalArgumentException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [student no., first name, last name, gpa, stream, 10 project preferences each seperated by ,]");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Method invoked when the button for loading staff members is clicked.  */
    @FXML
    public void loadStaffMembersOnClick() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        fileChooser.setTitle("Choose staff members file");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            System.out.println("No file selected");
            return;
        }
        FileChooser.ExtensionFilter selectedExtension = fileChooser.getSelectedExtensionFilter();
        try {
            if (csvFilter.equals(selectedExtension)) {
                staffMembers = FileLoader.loadStaffMembersFromCSV(file);
            } else if (txtFilter.equals(selectedExtension)) {
                staffMembers = FileLoader.loadStaffMembersFromTXT(file);
            }
            loadProjectsBtn.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [name, research activity, research area, special focus]");
            alert.showAndWait();
        }
    }


    //TODO READ THE SAMPLE FILE PROVIDED. NOT FULLY WORKING/COMPLETE YET
    @FXML
    private void readContent() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        fileChooser.setTitle("Choose file");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            System.out.println("No file selected");
            return;
        }
        FileChooser.ExtensionFilter selectedExtension = fileChooser.getSelectedExtensionFilter();
        try {
                projects = fromCSVProjects(Utils.readFile(file.toPath()));
                students = fromCSVStudents(Utils.readFile(file.toPath()), Utils.generateProjectsMap(projects));
              //  projectsTable.showProjects(projects);
                studentsTable.showStudents(students);

        } /*catch (IllegalArgumentException e) {
            alert.setTitle("Error");
            alert.setHeaderText("Input File Error");
            alert.setContentText("Make sure the files are correctly formatted.\nCSV ROW: [student no., first name, last name, gpa, stream, 10 project preferences each seperated by ,]");
            alert.showAndWait();
        }*/ catch (IOException e){
            e.printStackTrace();
        }
    }

    public static List<Project> fromCSVProjects(String csvFile) {
        Vector<String> projectAdded = new Vector<>();
        List<Project> projects= new LinkedList<>();
        String[] rows = csvFile.split("\n");

        for (String row : rows) {
            try {
                String[] parts = new CSVParser().parseLine(row);
                for (int i = 4; i < parts.length&& parts[i] != null; i++) {
                    if (!projectAdded.contains(parts[i])) {
                        projectAdded.add(parts[i]);
                        Project project = new Project();
                        project.setTitle(parts[i]);
                        project.setSupervisor(new StaffMember());
                        projects.add(project);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
          //  throw new IllegalArgumentException("Could not parse: " + row);
        }

        return projects;
    }

    public static List<Student> fromCSVStudents(String csvFile, Map<String, Project> projects) {
        List<Student> students = new LinkedList<>();
        String[] rows = csvFile.split("\n");

        for (String row : rows) {
            System.out.println(row);
            try {
                String[] parts = new CSVParser().parseLine(row);
                List<Project> projectPreferences = new ArrayList<>();
                Student student = new Student();
                for (int i = 4; i < parts.length && parts[i] !=null; i++) {

                    projectPreferences.add(projects.get(parts[i]));
                }
                System.out.println(parts[2]);
                if(!parts[0].equals(""))
                    student.setFullName(parts[0], parts[0]);
                student.setPreferences(projectPreferences);
                if(!parts[2].equals(""))
                    student.setGpa(Double.parseDouble(parts[2]));
                if(!parts[1].equals(""))
                    student.setStudentNumber(parts[1]);
                students.add(student);

            } catch (IOException e) {
                e.printStackTrace();
            }
        //    throw new IllegalArgumentException("Could not parse: " + row);
        }
        return students;
    }
}

