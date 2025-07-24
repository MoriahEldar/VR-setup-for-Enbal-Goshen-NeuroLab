package mainFlow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import classes.RewardStationDef;
import consts.Defs;

public class ExperimentFlow {
    private BlenderConnection blender;
    private ArduinoConnection arduino;
    private FileSystem fileSystem;
    private boolean onReward = false;
    private int sentOnReward = 0; // on Reward changes fast. Helps to sync in.
    private int lapNumber = 1;
    private String mazeLocation = "0";
    private int ttlNumber = 0;
    private long lastUpdateTime = 0;
    
    public ExperimentFlow(float radius, String dir) {
        this.blender = new BlenderConnection(this, radius);
        this.arduino = new ArduinoConnection(this);
        this.fileSystem = new FileSystem(this, dir);
        this.fileSystem.makeOutputData();
    }

    static void uploadEnv() {
        // TODO: upload the maze
        // TODO: 
    }

    public void startExp(File maze, ArrayList<RewardStationDef> rewards) {
        try {
            blender.startGame(maze, rewards);
            arduino.connectArduino();
            fileSystem.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateMazeArgs(int lapNumber, boolean onReward, String mazeLocation) {
        int lastLapNumber = this.lapNumber;

        this.lapNumber = lapNumber;
        this.mazeLocation = mazeLocation;

        if(this.lapNumber != lastLapNumber) {
            System.out.println("Lap number: " + this.lapNumber);
        }

        if (onReward) { //! check if before was not reward?
            triggerGotToReward();
            this.sentOnReward += 1;
            this.onReward = true;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > Defs.UPDATE_TIME) {
            fileSystem.updateBehavioralFile();
            lastUpdateTime = currentTime;
        }
    }

    public void triggerGotToReward() {
        try {
            arduino.sendGotToReward();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handleArduinoNumber(int number) {
        if (number == 0) {
            this.ttlNumber += 1;
            fileSystem.updateFileOnTtl(Integer.toString(this.ttlNumber));
        }
        else {
            blender.move(number);
        }
    }

    public void finishRun() {
        try {
            arduino.disconnectArduino();
            blender.closeSocketConnection();
            fileSystem.stopLogging();
            fileSystem.makeOutputData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileSystemIsOnReward() {
        if (onReward) {
            sentOnReward -= 1;    
            if (sentOnReward == 0) {
                onReward = false;
            }
            return true;
        }

        return false;
    }

    public int getLapNumber() {
        return this.lapNumber;
    }

    public String getMazeLocation() {
        return this.mazeLocation;
    }

    public float getRadius() {
        return this.blender.getRadius();
    }

    public boolean isTtlOn() {
        return this.ttlNumber > 0;
    }
}
