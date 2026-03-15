package mainFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.Color;

import com.jmatio.types.MLDouble;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLUInt8;

import classes.ExperimentData;
import consts.Defs;

public class FileSystem {
    String dir;

    ExperimentFlow expFlow;
    //! TODO check it points on the same experiment
    ExperimentData expData;

    private String BEHAVIORAL_FILE = Defs.BEHAVIORAL_FILE_NAME;
    private String BEHAVIORAL_FILE_NO_TTL = "";
    private String SYNC_FILE = ""; // and date and so on
    private String MAT_OUTPUT_FILE = "";
    private String GRAPH_OUTPUT_FILE = "";
    private boolean running = true;
    private final BlockingQueue<String> bhvQueue = new LinkedBlockingQueue<>();
    //! to do, change the program to use ExecutorService (and to constructor) instead of threads
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final String SEPARATOR = Defs.SEPARATOR; // Separator for CSV files
    
    public FileSystem(ExperimentFlow expFlow, ExperimentData expData) {
        this.expFlow = expFlow;
        this.expData = expData;
        this.dir = expData.getDir();
        
        if (dir != "" && !dir.endsWith(File.separator)) {
            this.dir += File.separator;
        }

        this.BEHAVIORAL_FILE = dir + this.BEHAVIORAL_FILE;
    }

    public void start() {
        startLoggingBehavioral();
    }

    // Start the continuous file logging in a background thread
    private void startLoggingBehavioral() {
        this.createFile(BEHAVIORAL_FILE);
        executor.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEHAVIORAL_FILE))) {
                while (running || !bhvQueue.isEmpty()) {
                    String data;
                    while ((data = bhvQueue.poll()) != null) {
                        writer.write(data);
                        writer.newLine();
                    }
                    writer.flush();
                    Thread.sleep(Defs.FILE_THREAD_SLEEP_TIME); // Small sleep to reduce CPU usage
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // Method to queue triggered updates
    public void updateFileOnTtl(String ttlNumber) {
       writeUpdatesToBhvFile(ttlNumber);
    }

    public void updateBehavioralFile() {
        this.updateFileOnTtl("0"); // add the ttl time
    }
   
    private void writeUpdatesToBhvFile(String ttlNumber) {
        long time = System.currentTimeMillis();
        bhvQueue.offer(logsFileRowFormat(time, expFlow.getMazeLocation(), expFlow.fileSystemIsOnReward(), expFlow.fileSystemLick(), expFlow.getLapNumber(), ttlNumber)); // add the ttl time

    }

    public void finishLoggingAndMakeOutputFiles() {
        stopLogging();
        makeOutputFiles();
    }

    private void makeOutputFiles() {
        updateFileNames();
        syncFiles();
        handleGraphAndMatFiles();

        //! TODO check if needed
        if(expFlow.isTtlOn()) {
            makeBaseBehavioralFile(this.BEHAVIORAL_FILE_NO_TTL);
        }
        
        if (new File(this.BEHAVIORAL_FILE).delete()) {
            System.out.println("bhv File deleted successfully.");
        } else {
            System.out.println("Failed to delete bhv file.");
        }
    }

    private void syncFiles() {
        if (SYNC_FILE.trim() == "") {
            this.updateFileNames();
        }

        System.out.println("syncing files");
        this.createFile(SYNC_FILE);

        if (!expFlow.isTtlOn()) {
            // If there is no TTL, just copy the behavioral file to the sync file
            makeBaseBehavioralFile(SYNC_FILE);
            System.out.println("made sync file, making graph");
            return;
        }

        // if there is a ttl, we need to group the data by ttls

        try (BufferedReader br = new BufferedReader(new FileReader(BEHAVIORAL_FILE))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SYNC_FILE))) {
                writer.write(Defs.BEHAVIORAL_FILE_HEADER); // Header for the CSV file
                writer.newLine();

                String line;
                // go until the first non-zero ttl, and just copy all the rows (including the first non-zero ttl row)
                while ((line = br.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();

                    String[] fields = line.split(SEPARATOR, -1);
                    String ttlNumber = fields[Defs.BHVFILE_TTL_NUMBER].trim();
                    if (!ttlNumber.equals("0")) {
                        break;
                    }
                }

                boolean breaked = false;
                List<String> group = new ArrayList<>();

                // go over the rest of the file, and handle the groups 
                while ((line = br.readLine()) != null) {
                    breaked = false;

                    // group together each continuous rows of ttl 0 and the first row of the next ttl (if there is one)
                    do {
                        group.add(line);
                        
                        String[] fields = line.split(SEPARATOR, -1);
                        String ttlNumber = fields[Defs.BHVFILE_TTL_NUMBER].trim();
                        if (!ttlNumber.equals("0")) {
                            breaked = true;
                            break;
                        }
                    }
                    while ((line = br.readLine()) != null);

                    if(!breaked) { // if we reached the end of the file and there is still a group to write
                        break;
                    }
                    
                    if (group.size() > Defs.MAX_TTL_ZERO_LINES) { // if ttl stopped in the middle and it's not a group
                        copyGroupToFile(group, writer);
                        continue;
                    }
                    else {
                        String data = getGroupData(group);
                        writer.write(data);
                        writer.newLine();
                    }

                    group.clear(); // clear the group for the next set of rows
                    // continue to the line after the line that the ttl is non-zero
                }

                if (group.size() > 0) { // if there is a group at the end of the file that we didn't write yet
                    copyGroupToFile(group, writer);
                }
                
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try (BufferedReader br = new BufferedReader(new FileReader(BEHAVIORAL_FILE))) {
        //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(SYNC_FILE))) {
        //         writer.write(Defs.BEHAVIORAL_FILE_HEADER); // Header for the CSV file
        //         writer.newLine();
        //         String line;
        //         String currentLabel = null;

        //         while ((line = br.readLine()) != null) {
        //             // split on seperetor
        //             String[] fields = line.split(SEPARATOR, -1);
        //             String last = fields[Defs.BHVFILE_TTL_NUMBER].trim();

        //             // if this row’s last column is non-zero, start a new group
        //             if (last.equals("0") && currentLabel != null && !currentLabel.equals(lastTtlNumber)) {
        //                 group.add(line); // add the row to the current group
        //             }
        //             if ((!last.isEmpty() && !last.equals("0")) || currentLabel == null || currentLabel.equals(lastTtlNumber)) {
        //                 if (currentLabel == null || currentLabel.equals(lastTtlNumber)) {
        //                     group = new ArrayList<>();
        //                     group.add(line); // initialize the group with the first row
        //                 }
        //                 else {
        //                     group.add(line);
        //                 }
        //                 String data = getGroupData(group);
        //                 writer.write(data);
        //                 writer.newLine();
        //                 if (!last.isEmpty() && !last.equals("0")) {
        //                     currentLabel = last;
        //                 } else {
        //                     currentLabel = null;
        //                 }
        //                 group.clear(); // clear the group for the next set of rows
        //             }
        //         }
                
        //         writer.flush();
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        System.out.println("made sync file, making graph");
    }

    private String getGroupData(List<String> list) {
        int size = list.size();
        if (size == 0) return ""; // No data to process

        double totalLocation = 0;
        double lastLocation = -100;
        int locationChange = 0;

        boolean onReward = false;
        boolean lick = false;

        for (String row : list) {
            String[] splitRow = row.split(SEPARATOR, -1);
            if(splitRow[Defs.BHVFILE_REWARD_NUMBER].equals("true")) { // check if on reward
                onReward = true;
            }
            if(splitRow[Defs.BHVFILE_LICK_NUMBER].equals("true")) { // check if on reward
                lick = true;
            }

            try {
                double location = Double.parseDouble(splitRow[Defs.BHVFILE_LOCATION_NUMBER]);
                if (lastLocation != -100 && Math.abs(lastLocation - location) > 300) {
                    int rowIndex = list.indexOf(row);
                    locationChange = rowIndex;
                    totalLocation = 0;
                }
                lastLocation = location;
                totalLocation += location; // sum the locations
            } catch (NumberFormatException e) {
                System.err.println("Invalid location data in row: " + Arrays.toString(splitRow));
                continue; // skip rows with invalid location data
            }
        }
        double averageLocation = totalLocation / (size - locationChange); // average the location

        String[] last = list.get(size - 1).split(SEPARATOR, -1); // get the last row
        String time = last[Defs.BHVFILE_TIME_NUMBER]; // get the last time
        String lap = last[Defs.BHVFILE_LAP_NUMBER]; // get the last lap
        String TTL = last[Defs.BHVFILE_TTL_NUMBER]; // get the last ttl

        return time + SEPARATOR + averageLocation + SEPARATOR + onReward + SEPARATOR + lick + SEPARATOR + lap + SEPARATOR + TTL; // return the data in the format: time, location, onReward, lick, lap, ttlNumber
    }

    public void handleGraphAndMatFiles() {
        XYSeries dataXY = new XYSeries(Defs.GRAPH_DATA_NAME);
        XYSeries rewardsXY = new XYSeries(Defs.GRAPH_REWARDS_NAME);
        XYSeries licksXY = new XYSeries(Defs.GRAPH_LICK_NAME);
        List<Double> locations = new ArrayList<>();
        List<Boolean> licks = new ArrayList<>();
        List<Boolean> rewards = new ArrayList<>();
        List<Boolean> laps = new ArrayList<>();
        double roundLength = expFlow.getRadius() * 2 * Math.PI; // Calculate the round length based on the radius
        long firstTime = 0;
        int lastLickSec = -100;
        double lastLocation = -100;
        double lastSec = 0;
        int lastLap = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(SYNC_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming the file is CSV and SEPARATOR is the delimiter
                String[] row = line.split(SEPARATOR);
                
                // check if ttl
                boolean ttl = false;
                try {
                    int ttlNumber = Integer.parseInt(row[Defs.BHVFILE_TTL_NUMBER]);
                    if (ttlNumber > 0) {
                        ttl = true;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid ttl data in row: " + Arrays.toString(row));
                    continue; // skip rows with invalid ttl data
                }
                
                // find first time for graph
                long time = 0;
                try {
                    time = Long.parseLong(row[Defs.BHVFILE_TIME_NUMBER]);
                    if (firstTime == 0) {
                        firstTime = time;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid time data in row: " + Arrays.toString(row));
                    continue; // skip rows with invalid time data
                }

                // mat data
                int addFictive = 0;
                double location = 0;
                try {
                    location = Double.parseDouble(row[Defs.BHVFILE_LOCATION_NUMBER]); // sum the locations
                    if (lastLocation != -100) {
                        if (lastLocation - location > 300) {
                            addFictive = 1;
                        }
                        else if(lastLocation - location < -300) {
                            addFictive = -1;
                        }
                    }
                    lastLocation = location;
                    if (ttl) {
                        locations.add(location);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid location data in row: " + Arrays.toString(row));
                    continue; // skip rows with invalid location data
                }

                boolean lick = Boolean.parseBoolean(row[Defs.BHVFILE_LICK_NUMBER]);
                if (ttl) {
                    licks.add(lick);
                }
                
                boolean reward = Boolean.parseBoolean(row[Defs.BHVFILE_REWARD_NUMBER]);
                if (ttl) {
                    rewards.add(reward);
                }
                
                int lap = Integer.parseInt(row[Defs.BHVFILE_LAP_NUMBER]);
                if (ttl) {
                    if (lap != lastLap) {
                        laps.add(true);
                        lastLap = lap;
                    }
                    else {
                        laps.add(false);
                    }
                }

                // graph data
                double sec = (time - firstTime) / 1000.0;
                int intSec = (int) sec;
                double lineLocation = (location/360) * roundLength; // convert the location to a line location
                if (!Double.isNaN(lineLocation) && !Double.isInfinite(lineLocation)) {
                    // add fictive points if there is a gap in the location data (like when the mouse passes the start point)
                    if (addFictive != 0) {
                        double gap = (lastSec - sec)/3.0;
                        if(addFictive == 1) {
                            dataXY.add(lastSec + gap, roundLength);
                            dataXY.add(lastSec + 2*gap, 0);
                        }
                        else {
                            dataXY.add(lastSec + gap, 0);
                            dataXY.add(lastSec + 2*gap, roundLength);
                        }
                    }
                    dataXY.add(sec, lineLocation); // add the data to the series for graphing
                    
                    if (reward) {
                        rewardsXY.add(sec, lineLocation); // add the point to the list of reward points
                    }
                    
                    if (lick) {
                        if (intSec > lastLickSec) { // add licks in different seconds only
                            licksXY.add(sec, lineLocation); // add the point to the list of lick points
                        }
                        lastLickSec = intSec;
                    }
                }
                lastSec = sec;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (expFlow.isTtlOn()) {
            makeMatFile(locations, rewards, laps, licks);
        }
        makeGraph(dataXY, rewardsXY, licksXY);
    }


    // ===============================================
    // technical graph functions
    // ===============================================
    private void makeGraph(XYSeries series, XYSeries rewards, XYSeries licks) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(rewards);
        dataset.addSeries(licks);

        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "laps: " + expFlow.getLapNumber() + "\n rewards: " + rewards.getItemCount() + "\n licks: " + licks.getItemCount(),
                "time (s)",
                "location (cm)",
                dataset
        );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Main line: show line, hide shapes
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesPaint(0, Color.BLUE);

        // Rewards: hide line, show shapes
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        // Licks: hide line, show shapes
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesShape(2, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));

        org.jfree.chart.plot.XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);

        // Show the chart in a window
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("experiment graph");
        frame.add(chartPanel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            ChartUtils.saveChartAsPNG(new File(this.GRAPH_OUTPUT_FILE), chart, 800, 600);
            System.out.println("made graph");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeMatFile(List<Double> locations, List<Boolean> rewards, List<Boolean> laps, List<Boolean> licks) {
        // This method creates a .mat file with arrays of the experiment data.
        // It assumes that behavioralData is a List<String> where each entry is: time,location,onReward,lap,ttlNumber (separated by SEPARATOR)
        // The arrays will be: location (double[]), onReward (int[]), lap (int[]), licks (int[])
        try {
            // Prepare arrays
            int n = locations.size();
            double[] locationArr = new double[n];
            byte[] onRewardArr = new byte[n];
            byte[] lapArr = new byte[n];
            byte[] licksArr = new byte[n];

            // fill the arrays
            int i = 0;
            for (double location : locations) {
                locationArr[i] = location;
                i++;
            }

            i = 0;
            for (boolean reward : rewards) {
                onRewardArr[i] = (byte) (reward ? 1 : 0);
                i++;
            }

            i = 0;
            for (boolean lap : laps) {
                lapArr[i] = (byte) (lap ? 1 : 0);
                i++;
            }

            i = 0;
            for (boolean lick : licks) {
                licksArr[i] = (byte) (lick ? 1 : 0);
                i++;
            }

            // write the arrays to the .mat file
            try {
                ArrayList<MLArray> mlList = new ArrayList<>();
                mlList.add(new MLDouble("location", locationArr, n));
                mlList.add(new MLUInt8("onReward", onRewardArr, n));
                mlList.add(new MLUInt8("lap", lapArr, n));
                mlList.add(new MLUInt8("lick", licksArr, n));
                new com.jmatio.io.MatFileWriter(this.MAT_OUTPUT_FILE, mlList);
                System.out.println("MAT file created: " + this.MAT_OUTPUT_FILE);
            } catch (Exception e) {
                System.err.println("Could not write MAT file. Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ===============================================
    // technical help functions
    // ===============================================
    private void updateFileNames() {
        String newName = this.expData.getCageName() + Defs.EXP_NAME_SEPARATOR + this.expData.getMouseName() + Defs.EXP_NAME_SEPARATOR;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");
        newName += myFormatObj.format(myDateObj);
        this.BEHAVIORAL_FILE_NO_TTL = dir + newName + Defs.BEHAVIORAL_FILE_NAME;
        this.SYNC_FILE = dir + newName + Defs.SYNC_FILE_NAME_ENDING;
        this.MAT_OUTPUT_FILE = dir + newName + Defs.MAT_OUTPUT_FILE_NAME_ENDING;
        this.GRAPH_OUTPUT_FILE = dir + newName + Defs.GRAPH_FILE_NAME_ENDING;
    }

    private void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to stop both logging operations safely and wait for the queues to be empty before shutting down the executor
    private void stopLogging() {
        running = false;
        try {
            while (!bhvQueue.isEmpty()) {
                Thread.sleep(Defs.FILE_THREAD_SLEEP_TIME); // Wait until both queues are empty
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private void makeBaseBehavioralFile(String fileName) {
        try {
            // Add header to the beginning of the target file, then copy the rest of the source file
            try (
                BufferedReader reader = new BufferedReader(new FileReader(BEHAVIORAL_FILE));
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))
            ) {
                writer.write(Defs.BEHAVIORAL_FILE_HEADER);
                writer.newLine();
                reader.transferTo(writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyGroupToFile(List<String> group, BufferedWriter writer) throws IOException {
        for (String row : group) {
            writer.write(row);
            writer.newLine();
        }
    }

    private String logsFileRowFormat(long time, String location, boolean onReward, boolean lick, int lap, String ttl) {
        return logsFileRowFormat(Long.toString(time), location, onReward ? "true" : "false", lick ? "true" : "false", Integer.toString(lap), ttl);
    }

    private String logsFileRowFormat(String time, String location, String onReward, String lick, String lap, String ttl) {
        return time + SEPARATOR + location + SEPARATOR + onReward + SEPARATOR + lick + SEPARATOR + lap + SEPARATOR + ttl;
    }
}
