package consts;

public class Defs {
    public static int BLENDER_PORT = 65432;
    public static String BLENDER_IP = "localhost";
    public static String BLENDER_PATH = "C:\\Program Files\\Blender Foundation\\Blender\\blenderplayer.exe"; // TODO change in production

    public static int REWARDS_SPOTS_AMOUNT = 5;
    public static int MAX_LAPS_NUMBER = 1000;

    public static String BEHAVIORAL_FILE_NAME = "behavioralData.csv";
    public static String SYNC_FILE_NAME = "finalData.csv";
    public static String GRAPH_FILE_NAME = "graph.png";
    public static String MAT_OUTPUT_FILE_NAME = "data.m";

    public static String GRAPH_REWARDS_NAME = "Rewards";
    public static String GRAPH_LICK_NAME = "Licks";
    public static String GRAPH_DATA_NAME = "My Data";

    public static String MAT_LOCATIONS_NAME = "locations";
    public static String MAT_REWARDS_NAME = "rewards";
    public static String MAT_LAPS_NAME = "laps";
    public static String MAT_LICK_NAME = "licks";

    public static int THREAD_SLEEP_TIME = 50; // in ms, for the threads to sleep
    public static int UPDATE_TIME = 50; // in ms, for the update of the files
    public static int REWARD_SIGNAL_TIME = 10; // in ms

    // public static enum FileData {
    //     TIME,
    //     LOCATION,
    //     REWARD,
    //     LAP,
    //     TTL
    // };
    public static String BHVFILE_TIME_NAME = "Time";
    public static int BHVFILE_TIME_NUMBER = 0;
    public static String BHVFILE_LOCATION_NAME = "Location";
    public static int BHVFILE_LOCATION_NUMBER = 1;
    public static String BHVFILE_REWARD_NAME = "On Reward";
    public static int BHVFILE_REWARD_NUMBER = 2;
    public static String BHVFILE_LAP_NAME = "Lap Number";
    public static int BHVFILE_LAP_NUMBER = 3;
    public static String BHVFILE_TTL_NAME = "TTL Number";
    public static int BHVFILE_TTL_NUMBER = 4;
    public static String SEPARATOR = ", "; // separator for the files
    public static String BEHAVIORAL_FILE_HEADER = BHVFILE_TIME_NAME + SEPARATOR +
            BHVFILE_LOCATION_NAME + SEPARATOR +
            BHVFILE_REWARD_NAME + SEPARATOR +
            BHVFILE_LAP_NAME + SEPARATOR +
            BHVFILE_TTL_NAME; // Header for the behavioral file
}
