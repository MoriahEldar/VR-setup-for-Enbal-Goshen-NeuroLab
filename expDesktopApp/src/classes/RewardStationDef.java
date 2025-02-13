package classes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

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
    // num of laps for this defenition
    private int numLaps;
    // probability of reward
    private int probability;
    // for the sake of changing the rewards. From -1 - 4, where -1 is random.
    private int placeInZone;
}
