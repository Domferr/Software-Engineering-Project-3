package ie.ucdconnect.sep.Controllers;

import ie.ucdconnect.sep.*;
import ie.ucdconnect.sep.generators.AsexualGeneticAlgorithm;
import ie.ucdconnect.sep.generators.GeneticAlgorithm;
import ie.ucdconnect.sep.generators.SimulatedAnnealing;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class Controller {

    private List<Project> projects;
    private List<Student> students;
    private Solution solution;
    private int test_size;
    private Alert alert;
    //Algorithm that will be run
    private SolutionGenerationStrategy generationStrategy;

    @FXML
    Slider gpaSlider;
    @FXML
    ChoiceBox<String> algorithmChoiceBox;

    @FXML
    public void initialize() {
        alert = new Alert(Alert.AlertType.INFORMATION);

        setUpSlider(0,1,0.5);
        setUpAlgorithmChoiceBox(FXCollections.observableArrayList(SimulatedAnnealing.DISPLAY_NAME, GeneticAlgorithm.DISPLAY_NAME, AsexualGeneticAlgorithm.DISPLAY_NAME));

        try {
            int [] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
            test_size = testSetsStudentsSize[1];
            List<StaffMember> staffMembers = Utils.readStaffMembers();
            projects = Utils.readProjects(staffMembers, test_size);
            students = Utils.readStudents(Utils.generateProjectsMap(projects), test_size);
        } catch (IOException e){
            e.printStackTrace();
        }
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
    public void generateSolution(){
        solution = generationStrategy.generate(projects, students);
        alert.setHeaderText(generationStrategy.getDisplayName()+" finished.");
        alert.setContentText("Make sure to save the results!");
        alert.showAndWait();
    }

    @FXML
    public void saveResults() throws IOException{
        if(solution == null){
            alert.setHeaderText("No Solution Found!");
            alert.setContentText("A solution must be generated before results can be saved");
            alert.showAndWait();
        }else{
            new SolutionGenerator().saveSolution(solution, test_size);
            alert.setHeaderText("Results saved!");
            alert.setContentText("You can view and download the results!");
            alert.showAndWait();
        }

    }

    @FXML
    public void load(){
        //TODO
    }

}

