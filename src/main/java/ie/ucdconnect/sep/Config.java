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
    private static Config instance = null;  //Instance reference

    private static final String CONFIG_FILENAME = "config.txt"; //Name of the file with config data

    private static String FILES_DIR_NAME;   //Name of the directory that contains the files
    private static String TESTCASE_DIR_NAME;//Name of the directory with generated testcases
    private static File NAMES_FILE;         //File with student names
    private static File STAFF_MEMBERS_FILE; //File csv with supervisors
    private static File PREFIXES_FILE;      //File with custom project prefixes
    private static final int NUMBER_OF_ROWS = 5;

    private Config() {
        try {
            load();
        } catch (IOException ex) {
            //TODO what to do when loading fails?
            System.err.println("config file reading failed");
        }
    }

    /** Singleton implementation */
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    /** Load from the configuration file */
    private void load() throws IOException {
        File configFile = new File(CONFIG_FILENAME);
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

    /** Parse and save data read */
    private void parseDataRead(List<String> fileRows) {
        FILES_DIR_NAME = fileRows.get(0);
        String filesDirPath = "./"+FILES_DIR_NAME+"/";
        NAMES_FILE = new File(filesDirPath+fileRows.get(1));
        STAFF_MEMBERS_FILE = new File(filesDirPath+fileRows.get(2));
        PREFIXES_FILE = new File(filesDirPath+fileRows.get(3));
        TESTCASE_DIR_NAME = "./"+filesDirPath+fileRows.get(4)+"/";
    }

    /** Overwrite field strings and then write into config file */
    public void save(String filesDirName, String testCaseDir, String namesFileName, String staffMembersFileName, String prefixesFileName) throws IOException {
        String filesDirPath = "./"+filesDirName+"/";
        FILES_DIR_NAME = filesDirName;
        NAMES_FILE = new File(filesDirPath + namesFileName);
        STAFF_MEMBERS_FILE = new File(filesDirPath + staffMembersFileName);
        PREFIXES_FILE = new File(filesDirPath + prefixesFileName);
        TESTCASE_DIR_NAME = "./"+testCaseDir+"/";

        BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILENAME));
        writer.append(filesDirName+"\n");
        writer.append(namesFileName+"\n");
        writer.append(staffMembersFileName+"\n");
        writer.append(prefixesFileName+"\n");
        writer.append(testCaseDir+"\n");
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
}
