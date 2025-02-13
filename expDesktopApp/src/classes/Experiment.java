package classes;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import consts.Formats;

public class Experiment implements Serializable {
    private static final long serialVersionUID = 2L;
    // should be unique
    private int id;
    // experiment name
    private String name;
    // list of experiments in reward
    private Vector<RewardStationDef> rewardList;
    // !Not in use!
    // list of air stations in reward
    // private Vector<AirStation> airStationList;
    // an exe blender maze file
    private File maze;
    // a file with the location of the experiment data output directory
    private File location;
    // the mice for this experiment
    private String mouse;

    public Experiment(int id) {
        this.id = id;
        this.name = "exp" + id; // TODO change default name
        this.rewardList = new Vector<>();
        this.maze = null;
        this.location = null;
        this.mouse = null;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
