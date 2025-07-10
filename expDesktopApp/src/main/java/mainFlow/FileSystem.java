package mainFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

import consts.Defs;

public class FileSystem {
    String dir;

    //! TODO check it points on the same experiment
    ExperimentFlow exp;

    private String BEHAVIORAL_FILE = Defs.BEHAVIORAL_FILE_NAME;
    private String SYNC_FILE = Defs.SYNC_FILE_NAME; // and date and so on
    private boolean running = true;
    private final BlockingQueue<String> bhvQueue = new LinkedBlockingQueue<>();
    //! to do, change the program to use ExecutorService (and to constructor) instead of threads
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final String SEPARATOR = Defs.SEPARATOR; // Separator for CSV files

    
    public FileSystem(ExperimentFlow exp, String dir) {
        this.exp = exp;
        this.dir = dir.trim();
        
        if (dir != "" && !dir.endsWith(File.separator)) {
            this.dir += File.separator;
        }

        this.BEHAVIORAL_FILE = dir + this.BEHAVIORAL_FILE;
        this.SYNC_FILE = dir + this.SYNC_FILE;
    }

    public void start() {
        createBehavioralFile();
        startLoggingBehavioral();
    }

    private void createBehavioralFile() {
        // try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEHAVIORAL_FILE, true))) {
        //     writer.write(Defs.BEHAVIORAL_FILE_HEADER); // Header for the CSV file
        //     writer.newLine();
        //     writer.flush();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    // Start the continuous file logging in a background thread
    private void startLoggingBehavioral() {
        executor.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEHAVIORAL_FILE, true))) {
                while (running || !bhvQueue.isEmpty()) {
                    String data;
                    while ((data = bhvQueue.poll()) != null) {
                        writer.write(data);
                        writer.newLine();
                    }
                    writer.flush();
                    Thread.sleep(Defs.THREAD_SLEEP_TIME); // Small sleep to reduce CPU usage
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private String getBhvStringData() {
        return exp.getMazeLocation() + SEPARATOR + exp.isOnReward() + SEPARATOR + exp.getLapNumber();
    } 

    // Method to queue triggered updates
    public void updateFileOnTtl(String ttlNumber) {
        long time = System.currentTimeMillis();
        bhvQueue.offer(time + SEPARATOR + getBhvStringData() + SEPARATOR + ttlNumber); // add the ttl time
    }

    public void updateBehavioralFile() {
        bhvQueue.offer(System.currentTimeMillis() + SEPARATOR + getBhvStringData() + SEPARATOR + '0'); // add the ttl time
    }

    public void syncFiles() {
        // This method can be used to ensure all data is written to the files
        // It will wait for the queues to be empty before proceeding
        try {
            while (!bhvQueue.isEmpty()) {
                Thread.sleep(Defs.THREAD_SLEEP_TIME); // Wait until both queues are empty
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //XYSeries series = new XYSeries("My Data");
        //! TODO what happens if i don't have a ttl? 
        // Map from non-zero label to the list of rows under that group
        List<String[]> group = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(BEHAVIORAL_FILE))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SYNC_FILE))) {
                writer.write(Defs.BEHAVIORAL_FILE_HEADER); // Header for the CSV file
                writer.newLine();
                String line;
                String currentLabel = null; // TODO crop data before 1

                while ((line = br.readLine()) != null) {
                    // split on seperetor
                    String[] fields = line.split(SEPARATOR, -1);
                    String last = fields[fields.length - 1].trim();

                    // if this row’s last column is non-zero, start a new group
                    if (last.equals("0") && currentLabel != null) {
                        group.add(fields); // add the row to the current group
                    }
                    if (!last.isEmpty() && !last.equals("0")) {
                        if (currentLabel == null) {
                            group = new ArrayList<>();
                            group.add(fields); // initialize the group with the first row
                        }
                        if (currentLabel != null) {
                            group.add(fields);
                        }
                        String data = getGroupData(group);
                        writer.write(data);
                        writer.newLine();
                        currentLabel = last;
                        group.clear(); // clear the group for the next set of rows
                    }
                }
                
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getGroupData(List<String[]> list/*, XYSeries series*/) {
        int size = list.size();
        if (size == 0) return ""; // No data to process
        String[] last = list.get(size - 1); // get the last row
        String time = last[Defs.BHVFILE_TIME_NUMBER]; // get the last time
        String lap = last[Defs.BHVFILE_LAP_NUMBER]; // get the last lap
        String TTL = last[Defs.BHVFILE_TTL_NUMBER]; // get the last ttl
        double location = 0;
        boolean onReward = false;

        for (String[] row : list) {
            if(row[Defs.BHVFILE_REWARD_NUMBER].equals("true")) { // check if on reward
                onReward = true;
            }

            try {
                location += Double.parseDouble(row[Defs.BHVFILE_LOCATION_NUMBER]); // sum the locations
            } catch (NumberFormatException e) {
                System.err.println("Invalid location data in row: " + Arrays.toString(row));
                continue; // skip rows with invalid location data
            }
        }

        location /= size; // average the location

        //series.addValue(location, time); // add the data to the series for graphing
        return time + SEPARATOR + location + SEPARATOR + onReward + SEPARATOR + lap + SEPARATOR + TTL; // return the data in the format: time, location, onReward, lap, ttlNumber
    }

    // Method to stop both logging operations safely
    public void stop() {
        running = false;
        executor.shutdown();
    }
}
