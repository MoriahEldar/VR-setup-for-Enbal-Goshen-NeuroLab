import java.io.File;
import java.util.ArrayList;

import classes.ExperimentData;
import classes.RewardStationDef;
import mainFlow.ExperimentFlow;

public class App {
    public static void main(String[] args) throws Exception {
        ExperimentData expData = new ExperimentData("A1", "B1", "");
        ExperimentFlow exp = new ExperimentFlow(35, expData);
        ArrayList<RewardStationDef> rewards = new ArrayList<>();
        rewards.add(new RewardStationDef(3, 2, 1, 0));
        exp.startExp(new File("C:\\Users\\Yonina\\OneDrive - Jerusalem College of Technology - Machon Lev\\Documents\\neuro_lab\\Blender mazes\\training\\training_csv_random_rew_new_small_treadmill.blend.exe"), rewards);
    }
}
