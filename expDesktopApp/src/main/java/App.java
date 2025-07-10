import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystem;
import java.util.ArrayList;

import classes.RewardStationDef;
import consts.Defs;
import mainFlow.ArduinoConnection;
import mainFlow.BlenderConnection;
import mainFlow.ExperimentFlow;

public class App {
    public static void main(String[] args) throws Exception {
        ExperimentFlow exp = new ExperimentFlow(35, "");
        ArrayList<RewardStationDef> rewards = new ArrayList<>();
        rewards.add(new RewardStationDef(3, 2, 1, 0));
        exp.startExp(new File("C:\\Users\\Yonina\\OneDrive - Jerusalem College of Technology - Machon Lev\\Documents\\n" + //
                                            "euro_lab\\Blender mazes\\startAgain\\env _A_csv_random_rew_new_small_treadmill.blend.exe"), rewards);
    }
}
