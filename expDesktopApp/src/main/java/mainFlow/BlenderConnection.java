package mainFlow;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import org.json.JSONObject;

import classes.RewardStationDef;
import consts.Defs;

public class BlenderConnection {
    private float radius;
    private Socket socket;
    private boolean onReward = false;
    private int lapNumber = 1;
    private String mazeLocation = "";

    private static Properties properties = new Properties();

    static {
        try (InputStream input = BlenderConnection.class.getClassLoader().getResourceAsStream("blenderConnection.properties")) {
            if (input == null) {
                throw new IOException("Sorry, unable to find blenderConnection.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public BlenderConnection(float radius) {
        this.radius = radius;
    }

    public void loadMaze(File maze, ArrayList<RewardStationDef> rewards) {
        try {
            // Start Blender game with parameters
            String rewardList = calculateRewardList(rewards).toString();
            ProcessBuilder processBuilder = new ProcessBuilder(
                Defs.BLENDER_PATH,
                "-p", "0", "0", "1280", "720", // Set window position and size
                "-g", "noaudio", "1",          // Disable audio
                "-g", "show_framerate", "1",   // Show framerate
                "-g", "show_profile", "1",     // Show performance stats
                "-g", "fixedtime", "1",        // Ensure stable physics
                maze.getPath(),                // Load the game
                "--",                          // Separator for custom arguments
                rewardList                     // Rewards
            );
            processBuilder.start();

            // Wait for Blender server to be ready
            Thread.sleep(5000);  // TODO Adjust the sleep time as needed

            // Connect to Blender server
            this.socket = new Socket(Defs.BLENDER_IP, Defs.BLENDER_PORT);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Move on the blender file
     * @param movment = -1/1/0
     */
    public void move(int movement) {
        // send to blender the movment that should have
        if(movement != 0) {
            try (
                    DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                ) {
                    out.writeUTF(String.valueOf(movement)); // Send message to server
                    // TODO need to wait?
                    String response = in.readUTF(); // Read response from server
                    JSONObject receivedJson = new JSONObject(response);
                    this.lapNumber = receivedJson.getInt(properties.getProperty("recieve.data.laps.name"));
                    this.onReward = receivedJson.getBoolean(properties.getProperty("recieve.data.reward.name"));
                    this.mazeLocation = receivedJson.get(properties.getProperty("recieve.data.location.name")).toString();
                }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public LinkedList<LinkedList<Double[]>> calculateRewardList(ArrayList<RewardStationDef> rewardsDefs) {
        // calculate the rewards list
        LinkedList<LinkedList<Double[]>> rewardsLists = new LinkedList<>();
        for (RewardStationDef rewardDef : rewardsDefs) {
            double blendMazeCircumference = Math.PI * 2;
            for (int i = 0; i < rewardDef.getNumLaps(); i++) { // TODO short this when prob = 1 and not random
                // divide the maze into equal sections for the rewards to be in
                LinkedList<Double[]> rewards = new LinkedList<>();
                double sectionLength = blendMazeCircumference / rewardDef.getNumInLap();
                for (int j = 0; j < rewardDef.getNumInLap(); j++) {
                    if (Math.random() < rewardDef.getProbability()) {
                        // calculate the reward position
                        double rewardPosition = sectionLength * j;
                        int placeInZone = rewardDef.getPlaceInZone();
                        if (placeInZone == -1) {
                            rewardPosition += Math.random() * sectionLength;
                        } 
                        else {
                            if (placeInZone == 0) {
                                rewardPosition += sectionLength / 2;
                            } 
                            else {
                                double sectionZones = sectionLength / Defs.REWARDS_SPOTS_AMOUNT;
                                rewardPosition += sectionZones * (placeInZone - 1) + sectionZones / 2;
                            }
                        }

                        // calculate the reward position in the maze
                        double x = Math.cos(rewardPosition) * this.radius;
                        double y = Math.sin(rewardPosition) * this.radius;

                        // add the reward to the list of rewards for this lap
                        rewards.add(new Double[] { x, y });
                    }
                }
                // add the list of rewards for this lap to the list of rewards for all laps
                rewardsLists.add(rewards);
            }
        }
        return rewardsLists;
    }

    public void close() {
        // TODO how to get a closing notion?
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
