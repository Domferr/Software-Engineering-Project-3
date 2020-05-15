package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Solution;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/** Class that manages the chart that shows the algorithm performance */
public class AlgorithmStats {

    private final String[] ORDINALS = {"st", "nd", "rd", "th"};
    private BarChart<String, Number> energyAndFitness;
    private BarChart<String, Number> reportStudentsPreference;
    private XYChart.Series<String, Number> seriesEnergy;
    private XYChart.Series<String, Number> seriesFitness;
    private XYChart.Series<String, Number> seriresPreferences;

    public AlgorithmStats(BarChart<String, Number> energyAndFitnessChart, BarChart<String, Number> reportStudentsPreference) {
        this.energyAndFitness = energyAndFitnessChart;
        this.reportStudentsPreference = reportStudentsPreference;
        this.reportStudentsPreference.setAnimated(false);
        this.energyAndFitness.setAnimated(false);
        this.seriesEnergy = new XYChart.Series<>();
        this.seriesFitness = new XYChart.Series<>();
        this.seriresPreferences = new XYChart.Series<>();
        seriesEnergy.setName("Energy");
        seriesFitness.setName("Fitness");
        this.energyAndFitness.getData().addAll(seriesEnergy, seriesFitness);
        this.reportStudentsPreference.getData().addAll(seriresPreferences);
    }

    public void showStats(Solution solution) {
        Platform.runLater(() -> {
            //Clear previous data
            seriesFitness.getData().clear();
            seriesEnergy.getData().clear();
            seriresPreferences.getData().clear();

            //Show new stats
            seriesEnergy.getData().add(createXYData("", solution.getEnergy()));
            seriesFitness.getData().add(createXYData("", solution.getFitness()));

            for (int key : solution.getPreferenceResults().keySet()) {
                String label;
                if (key == -1) {
                    label = "No preference";
                } else if (key >= 0 && key <= 3) {
                    label = (key + 1) + ORDINALS[key] + " Preference";
                } else {
                    label = (key + 1) + ORDINALS[3] + " Preference";
                }
                seriresPreferences.getData().add(createXYData(label, solution.getPreferenceResults().get(key)));

            }
        });
    }

    private XYChart.Data<String, Number> createXYData(String str, double value) {
        StackPane stackPane = new StackPane();
        Group main = new Group(new Label(((int)value)+""));
        StackPane.setAlignment(main, Pos.CENTER);
        StackPane.setMargin(main, new Insets(0, 0, 16, 0));
        stackPane.getChildren().add(main);

        XYChart.Data<String, Number> data =  new XYChart.Data<>(str, value);
        data.setNode(stackPane);
        return data;
    }
}
