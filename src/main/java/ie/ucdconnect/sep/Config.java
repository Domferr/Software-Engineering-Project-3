package ie.ucdconnect.sep;

import java.io.*;

/** Config file that contains important things needed for running TestCaseGenerator.
 *  Configuration file is written with the following order:
 *  1) Name of the directory that contains resource files
 *  2) Name of the file with student names
 *  3) Name of the supervisor .csv file
 *  4) Name of the file with project prefixes
 *
 *  N.B: Each element is written in one row.
 * */
public class Config {
    private static Config instance = null;  //Instance reference

    private static final String CONFIG_FILENAME = "config.txt"; //Name of the file with config stuffs

    private static String FILES_DIR_NAME;   //Name of the directory that contains the files
    private static File NAMES_FILE;         //File with student names
    private static File STAFF_MEMBERS_FILE; //File csv with supervisors
    private static File PREFIXES_FILE;      //File with custom project prefixes

    private Config() {
        try {
            load();
        } catch (IOException ex) {
            //TODO what to do when loading fails?
            System.err.println("config file reading failed");
        }
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    /** Load from the configuration file */
    private void load() throws IOException {
        File configFile = new File(CONFIG_FILENAME);
        BufferedReader reader = new BufferedReader(new FileReader(configFile));

        String line = "";
        int lineIndex = 0;
        String filesDirPath = "";
        while((line = reader.readLine()) != null) {
            switch (lineIndex) {
                case 0:
                    FILES_DIR_NAME = line;
                    filesDirPath = "./"+FILES_DIR_NAME+"/";
                    break;
                case 1: NAMES_FILE = new File(filesDirPath+line);
                    break;
                case 2: STAFF_MEMBERS_FILE = new File(filesDirPath+line);
                    break;
                case 3: PREFIXES_FILE = new File(filesDirPath+line);
                    break;
            }
            lineIndex++;
        }

        reader.close();
    }

    /** Overwrite field strings and then write into config file */
    public void save(String filesDirName, String namesFileName, String staffMembersFileName, String prefixesFileName) throws IOException {
        String filesDirPath = "./"+filesDirName+"/";
        FILES_DIR_NAME = filesDirName;
        NAMES_FILE = new File(filesDirPath + namesFileName);
        STAFF_MEMBERS_FILE = new File(filesDirPath + staffMembersFileName);
        PREFIXES_FILE = new File(filesDirPath + prefixesFileName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILENAME));
        writer.append(filesDirName+"\n");
        writer.append(namesFileName+"\n");
        writer.append(staffMembersFileName+"\n");
        writer.append(prefixesFileName+"\n");
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
}
