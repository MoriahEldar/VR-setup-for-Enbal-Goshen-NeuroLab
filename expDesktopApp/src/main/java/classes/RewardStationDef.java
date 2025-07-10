package classes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import consts.Defs;

/**
 * Created by Moriah Eldar, based on Netai Benaim
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: netai.benaim@mail.huji.ac.il
 * version:
 * <p>
 * this is RewardStation in experiments
 * created on 29/01/2025
 */

public class RewardStationDef implements Serializable {
    // number of rewards in a lap
    private int numInLap;
    // num of laps for this defenition. If -1 then it's infinite
    private int numLaps;
    // probability of reward
    private float probability;
    // for the sake of changing the rewards. From -1 - REWARDS_SPOTS_OPTIONS, where -1 is random, 0 is in the middle.
    private int placeInZone;

    public RewardStationDef(int numRewardsInTheLap, int numLaps, float probability, int placeInZone) {
        this.numInLap = numRewardsInTheLap;
        this.numLaps = numLaps;
        this.probability = probability;
        this.placeInZone = placeInZone;
    }

    // Get and set to all the fields
    public int getNumInLap() {
        if (numInLap <= 0) {
            throw new IllegalArgumentException("numInLap must be positive");
        }
        return numInLap;
    }

    public void setNumInLap(int numInLap) {
        this.numInLap = numInLap;
    }

    public int getNumLaps() {
        return numLaps;
    }

    public void setNumLaps(int numLaps) {
        if (numLaps < -1 || numLaps == 0) {
            throw new IllegalArgumentException("numLaps must be between positive or -1 for infinite");
        }
        this.numLaps = numLaps;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        if(probability <= 0 || probability > 1){
            throw new IllegalArgumentException("probability must be between 0 and 1");
        }
        this.probability = probability;
    }

    public int getPlaceInZone() {
        return placeInZone;
    }

    public void setPlaceInZone(int placeInZone) {
        if(placeInZone < -1 || placeInZone > Defs.REWARDS_SPOTS_AMOUNT){
            throw new IllegalArgumentException("placeInZone must be between -1 and 4");
        }
        this.placeInZone = placeInZone;
    }
}
