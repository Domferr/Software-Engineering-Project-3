package ie.ucdconnect.sep;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Config file that contains important things needed for running TestCaseGenerator.
 *  Configuration file is written with the following order:
 *  1) Name of the directory that contains resource files
 *  2) Name of the file with student names
 *  3) Name of the supervisor .csv file
 *  4) Name of the file with project prefixes
 *  5) Name of the directory that contains generated test cases
 *
 *  N.B: Each element is written in one row.
 * */
public class Config {
    private static final int[] TEST_SETS_STUDENTS_SIZE = {60, 120, 240, 500};
    private static final char DIVIDER = '=';
    private static Config instance = null;  //Instance reference

    private static String CONFIG_FILENAME = "config.txt"; //Name of the file with config data

    private static String FILES_DIR_NAME;   //Name of the directory that contains the files
    private static String TESTCASE_DIR_NAME;//Name of the directory with generated testcases
    private static File NAMES_FILE;         //File with student names
    private static File STAFF_MEMBERS_FILE; //File csv with supervisors
    private static File PREFIXES_FILE;      //File with custom project prefixes
    private static final int NUMBER_OF_ROWS = 5;    //How many rows the config.txt file should have

    private Config() throws IOException {
        File configFile = new File(CONFIG_FILENAME);
        if (!configFile.exists()) {
            if (configFile.createNewFile())
                load(configFile);
            else
                throw new IOException("Unable to create config file");
        } else {
            load(configFile);
        }
    }

    /** Singleton implementation */
    public static Config getInstance() throws IOException {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    /** Load from the configuration file */
    private void load(File configFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(configFile));
        ArrayList<String> fileRows = new ArrayList<>(NUMBER_OF_ROWS);
        String line = "";
        //Reading from file
        while((line = reader.readLine()) != null) {
            fileRows.add(line);
        }
        reader.close();
        //If data is not missing then parse it
        if (fileRows.size() == NUMBER_OF_ROWS) {
            parseDataRead(fileRows);
        }
    }

    /** Given a row, returns its value and ignores its description and the divider.
     *  If the divider is missing nothing is ignored
     * */
    private String parseRow(String row) {
        int dividerIndex = row.lastIndexOf(DIVIDER)+1;
        return row.substring(dividerIndex);
    }

    /** Parse and save data read */
    private void parseDataRead(List<String> fileRows) {
        FILES_DIR_NAME = parseRow(fileRows.get(0));
        String filesDirPath = "./"+FILES_DIR_NAME+"/";
        NAMES_FILE = new File(filesDirPath+parseRow(fileRows.get(1)));
        STAFF_MEMBERS_FILE = new File(filesDirPath+parseRow(fileRows.get(2)));
        PREFIXES_FILE = new File(filesDirPath+parseRow(fileRows.get(3)));
        TESTCASE_DIR_NAME = filesDirPath+parseRow(fileRows.get(4))+"/";
    }

    /** Overwrite field strings and then write into config file */
    public void save(String filesDirName, String testCaseDir, String namesFileName, String staffMembersFileName, String prefixesFileName) throws IOException {
        String filesDirPath = "./"+filesDirName+"/";
        FILES_DIR_NAME = filesDirName;
        NAMES_FILE = new File(filesDirPath + namesFileName);
        STAFF_MEMBERS_FILE = new File(filesDirPath + staffMembersFileName);
        PREFIXES_FILE = new File(filesDirPath + prefixesFileName);
        TESTCASE_DIR_NAME = filesDirPath+testCaseDir+"/";

        BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILENAME));
        writer.append("RESOURCES_DIR"+DIVIDER+filesDirName);
        writer.newLine();
        writer.append("NAMES_FILE"+DIVIDER+namesFileName);
        writer.newLine();
        writer.append("STAFF_MEMBERS_FILE"+DIVIDER+staffMembersFileName);
        writer.newLine();
        writer.append("PREFIXES_FILE"+DIVIDER+prefixesFileName);
        writer.newLine();
        writer.append("TESTCASES_DIR"+DIVIDER+testCaseDir);
        writer.newLine();
        writer.close();
    }

    public File getNamesFile() {
        return NAMES_FILE;
    }

    public File getStaffMembersFile() {
        return STAFF_MEMBERS_FILE;
    }

    public File getPrefixesFile() {
        return PREFIXES_FILE;
    }

    public String getTestcaseDirName() {
        return TESTCASE_DIR_NAME;
    }

    public static File getConfigFile() {
        return new File(CONFIG_FILENAME);
    }

    public String getFilesDirName() {
        return FILES_DIR_NAME;
    }

    public static void setConfigFilename(String filename) {
        if (filename != null)
            CONFIG_FILENAME = filename;
    }

    public int[] getTestSetsStudentsSize() {
        return TEST_SETS_STUDENTS_SIZE;
    }
}
