package mainFlow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

import consts.Defs;

public class FileSystem {
    String dir;

    //! TODO check it points on the same experiment
    ExperimentFlow exp;

    private String BEHAVIORAL_FILE = Defs.BEHAVIORAL_FILE_NAME;
    private String SYNC_FILE = Defs.SYNC_FILE_NAME; // and date and so on
    private String MAT_OUTPUT_FILE = Defs.MAT_OUTPUT_FILE_NAME;
    private String GRAPH_OUTPUT_FILE = Defs.GRAPH_FILE_NAME;
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
        this.MAT_OUTPUT_FILE = dir + this.MAT_OUTPUT_FILE;
        this.GRAPH_OUTPUT_FILE = dir + this.GRAPH_OUTPUT_FILE;
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BEHAVIORAL_FILE, true))) { // TODO remove the append
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
        return exp.getMazeLocation() + SEPARATOR + exp.fileSystemIsOnReward() + SEPARATOR + exp.getLapNumber();
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
        System.out.println("syncing files");
        this.stopLogging();
        // This method can be used to ensure all data is written to the files
        // It will wait for the queues to be empty before proceeding
        try {
            while (!bhvQueue.isEmpty()) {
                Thread.sleep(Defs.THREAD_SLEEP_TIME); // Wait until both queues are empty
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!exp.isTtlOn()) {
            // If there is no TTL, just copy the behavioral file to the sync file
            try {
                Path source = Path.of(BEHAVIORAL_FILE);
                Path target = Path.of(SYNC_FILE);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // if there is a ttl, we need to group the data by ttl
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
                    String last = fields[Defs.BHVFILE_TTL_NUMBER].trim();

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
        System.out.println("made sync file, making graph");
    }

    private String getGroupData(List<String[]> list) {
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

        return time + SEPARATOR + location + SEPARATOR + onReward + SEPARATOR + lap + SEPARATOR + TTL; // return the data in the format: time, location, onReward, lap, ttlNumber
    }

    public void makeOutputData() {
        File syncFile = new File(SYNC_FILE);
        if (!syncFile.exists()) {
            syncFiles();
        }

        XYSeries dataXY = new XYSeries(Defs.GRAPH_DATA_NAME);
        XYSeries rewardsXY = new XYSeries(Defs.GRAPH_REWARDS_NAME);
        XYSeries lickXY = new XYSeries(Defs.GRAPH_LICK_NAME);
        List<Double> locations = new ArrayList<>();
        List<Boolean> licks = new ArrayList<>();
        List<Boolean> rewards = new ArrayList<>();
        List<Boolean> laps = new ArrayList<>();
        double roundLength = exp.getRadius() * 2 * Math.PI; // Calculate the round length based on the radius
        long firstTime = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(SYNC_FILE))) {
            String line;
            int lastLap = 0;
            while ((line = br.readLine()) != null) {
                // Assuming the file is CSV and SEPARATOR is the delimiter
                String[] row = line.split(SEPARATOR);
                
                if (firstTime == 0) {
                    try {
                        firstTime = Long.parseLong(row[Defs.BHVFILE_TIME_NUMBER]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid time data in row: " + Arrays.toString(row));
                        continue; // skip rows with invalid time data
                    }
                }

                double location = 0;
                try {
                    location = Double.parseDouble(row[Defs.BHVFILE_LOCATION_NUMBER]); // sum the locations
                    locations.add(location);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid location data in row: " + Arrays.toString(row));
                    continue; // skip rows with invalid location data
                }

                // boolean lick = Boolean.parseBoolean(row[Defs.BHVFILE_LICK_NUMBER]);
                // licks.add(lick);
                boolean reward = Boolean.parseBoolean(row[Defs.BHVFILE_REWARD_NUMBER]);
                rewards.add(reward);
                
                int lap = Integer.parseInt(row[Defs.BHVFILE_LAP_NUMBER]);
                if (lap != lastLap) {
                    laps.add(true);
                    lastLap = lap;
                }
                else {
                    laps.add(false);
                }

                double lineLocation = (location/360) * roundLength; // convert the location to a line location
                try {
                    long timeInMillis = Long.parseLong(row[Defs.BHVFILE_TIME_NUMBER]) - firstTime; // calculate the time in milliseconds
                    if (!Double.isNaN(lineLocation) && !Double.isInfinite(lineLocation)) {
                        dataXY.add(timeInMillis, lineLocation); // add the data to the series for graphing
                        if (reward) {
                            rewardsXY.add(timeInMillis, lineLocation); // add the point to the list of reward points
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid time data in row: " + row[Defs.BHVFILE_TTL_NUMBER]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        makeMatFile(locations, rewards, laps, licks);
        makeGraph(dataXY, rewardsXY);
    }

    private void makeGraph(XYSeries series, XYSeries rewards) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(rewards);
        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "laps: " + exp.getLapNumber() + "\n rewards: " + rewards.getItemCount(),
                "time (ms)",
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
                mlList.add(new MLDouble(Defs.MAT_LOCATIONS_NAME, locationArr, n));
                mlList.add(new MLUInt8(Defs.MAT_REWARDS_NAME, onRewardArr, n));
                mlList.add(new MLUInt8(Defs.MAT_LAPS_NAME, lapArr, n));
                mlList.add(new MLUInt8(Defs.MAT_LICK_NAME, licksArr, n));
                new com.jmatio.io.MatFileWriter(this.MAT_OUTPUT_FILE, mlList);
                System.out.println("MAT file created: " + this.MAT_OUTPUT_FILE);
            } catch (Exception e) {
                System.err.println("Could not write MAT file. Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop both logging operations safely
    public void stopLogging() {
        running = false;
        executor.shutdown();
    }
}
