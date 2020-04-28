package ie.ucdconnect.sep.Controllers;

import ie.ucdconnect.sep.*;
import ie.ucdconnect.sep.generators.GeneticAlgorithm;
import ie.ucdconnect.sep.generators.SimulatedAnnealing;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Slider;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Controller {

    private List<Project> projects;
    private List<Student> students;
    private Solution solution;
    private int test_size;
    private Alert alert;

    @FXML
    Slider gpaSlider;

    @FXML
    public void initialize(){
        alert = new Alert(Alert.AlertType.INFORMATION);
        gpaSlider.setMax(100);
        gpaSlider.setMin(0);
        gpaSlider.valueProperty().addListener((observableValue, number, t1) -> Solution.GPA_IMPORTANCE = gpaSlider.getValue() / 100);
        try{
            int [] testSetsStudentsSize = Config.getInstance().getTestSetsStudentsSize();
            test_size = testSetsStudentsSize[1];
            List<StaffMember> staffMembers = Utils.readStaffMembers();
            projects = Utils.readProjects(staffMembers, test_size);
            students = Utils.readStudents(Utils.generateProjectsMap(projects), test_size);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void doSA(){
        solution = new SimulatedAnnealing().generate(projects, students);
        alert.setHeaderText("Simulated Annealing finished.");
        alert.setContentText("Make sure to save the results!");
        alert.showAndWait();
    }

    @FXML
    public void doGA(){
        solution = new GeneticAlgorithm().generate(projects, students);
        alert.setHeaderText("Simulated Annealing finished.");
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

