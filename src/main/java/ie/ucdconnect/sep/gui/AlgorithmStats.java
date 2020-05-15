package ie.ucdconnect.sep.gui;

import ie.ucdconnect.sep.Solution;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

/** Class that manages the chart that shows the algorithm performance */
public class AlgorithmStats {

    private BarChart<String, Number> energyAndFitness;
    private XYChart.Series<String, Number> seriesEnergy;
    private XYChart.Series<String, Number> seriesFitness;

    public AlgorithmStats(BarChart<String, Number> energyAndFitnessChart) {
        this.energyAndFitness = energyAndFitnessChart;
        this.energyAndFitness.setAnimated(false);
        this.seriesEnergy = new XYChart.Series<>();
        this.seriesFitness = new XYChart.Series<>();
        seriesEnergy.setName("Energy");
        seriesFitness.setName("Fitness");
        this.energyAndFitness.getData().addAll(seriesEnergy, seriesFitness);
    }

    public void showStats(Solution solution) {
        Platform.runLater(() -> {
            //Clear previous data
            if (!seriesFitness.getData().isEmpty())
                seriesFitness.getData().clear();
            if (!seriesEnergy.getData().isEmpty())
                seriesEnergy.getData().clear();
            //Show new stats
            seriesEnergy.getData().add(new XYChart.Data<>("", solution.getEnergy()));
            seriesFitness.getData().add(new XYChart.Data<>("", solution.getFitness()));
        });
    }
}
