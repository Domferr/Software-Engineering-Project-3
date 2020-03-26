package ie.ucdconnect.sep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void getInstance() {
        assertDoesNotThrow(Config::getInstance, "Config instance is null!");
    }

    @Test
    void save() {
        assertDoesNotThrow(() -> Config.getInstance().save("resources",
                        "testcases",
                        "names.txt",
                        "Miskatonic Staff Members.csv",
                        "prefixes.txt"), "Unable to save on Config file");
    }
}