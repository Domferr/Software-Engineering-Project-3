package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Solution;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

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
            seriesEnergy.getData().add(new XYChart.Data<>("", solution.getEnergy()));
            seriesFitness.getData().add(new XYChart.Data<>("", solution.getFitness()));
            for (int key : solution.getPreferenceResults().keySet()) {
                if (key == -1) {
                    seriresPreferences.getData().add(new XYChart.Data<>("No preference", solution.getPreferenceResults().get(key)));
                }
                else if (key >= 0 && key <= 3) {
                    String label = (key + 1) + ORDINALS[key] + " Preference";
                    seriresPreferences.getData().add(new XYChart.Data<>(label, solution.getPreferenceResults().get(key)));
                } else {
                    String label = (key + 1) + ORDINALS[3] + " Preference";
                    seriresPreferences.getData().add(new XYChart.Data<>(label, solution.getPreferenceResults().get(key)));
                }
            }
        });
    }
}
