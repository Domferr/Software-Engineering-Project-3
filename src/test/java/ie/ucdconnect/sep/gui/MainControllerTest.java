package ie.ucdconnect.sep.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest extends ApplicationTest {

    @Override
    public void start (Stage stage) throws Exception {
        Parent mainNode = FXMLLoader.load(Main.class.getResource("/main.fxml"));
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() throws Exception{
        FxToolkit.hideStage();
    }
    @Test
    public void testButtonText () {
        FxAssert.verifyThat("#loadData", LabeledMatchers.hasText("Load Data"));
        FxAssert.verifyThat("#generateSolution", LabeledMatchers.hasText("Generate"));
        FxAssert.verifyThat("#saveSolutionBtn", LabeledMatchers.hasText("Save"));

    }
    @Test
    public void testClick(){
        clickOn("#generateSolution");
        FxAssert.verifyThat("OK", NodeMatchers.isVisible());
        FxAssert.verifyThat("Project data has not been loaded.", NodeMatchers.isVisible());

        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn("#loadData");
        press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);

        drag("#gpaSlider").moveBy(100,0).moveBy(-200,0);

        FxAssert.verifyThat("Simulated Annealing", NodeMatchers.isVisible());
        clickOn("#algorithmChoiceBox");

    }
}