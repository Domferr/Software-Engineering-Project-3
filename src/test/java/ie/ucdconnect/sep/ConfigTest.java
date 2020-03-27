package ie.ucdconnect.sep;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    private Executable savingExecutable;

    @BeforeEach
    void setUp() {
        Config.setConfigFilename("configtest.txt");
        savingExecutable = () -> Config.getInstance().save("resources",
                "testcases",
                "names.txt",
                "Miskatonic Staff Members.csv",
                "prefixes.txt");
    }

    @Test
    void getInstance() {
        assertDoesNotThrow(Config::getInstance, "Config instance is null!");
    }

    @Test
    void save() {
        assertDoesNotThrow(savingExecutable, "Unable to save on Config file");
    }

    @Test
    void saveWithoutFileOnDisk() {
        File configFile = Config.getConfigFile();
        if (configFile.exists())
            configFile.delete();
        assertDoesNotThrow(savingExecutable, "Unable to save on Config file");
    }

    @AfterEach
    void tearDown() {
        File configFile = Config.getConfigFile();
        configFile.delete();
    }
}