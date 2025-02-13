package classes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Ido Lazer
 * for G-Lab, Hebrew University of Jerusalem
 * contact at: ido.lazer@mail.huji.ac.il
 * version:
 * <p>
 * these are AirStaions in experiments
 * created on 11/07/2019
 */

public class AirStation implements Serializable {
    private static final long serialVersionUID = 1L;
    // name of station
    private String name;
    // id given to station
    private int id = 0;
    // location of station in space [x,y,z]
    private float[] location;
    // number of sequence this air station belongs to
    private int instance;
    // number of laps this air station appears in
    private int laps;
    // delimiter for config file
    public static final String DEL = ",";


    /**
     * copy constructor
     * @param air an air station to copy
     */
    public AirStation(AirStation air)
    {
        this.id = air.getId();
        this.name = air.getName();
        this.location = new float[3];
        this.location[0] = air.getX();
        this.location[1] = air.getY();
        this.location[2] = air.getZ();
    }

    /**
     * Constructor
     * @param name name of station
     * @param id id of station
     */
    public AirStation(String name, int id) {
        location = new float[3];
        this.id = id;
        this.name = name;
    }

    /**
     * @return return the name of the station
     */
    public String getName() {
        return name;
    }

    /**
     * set a location to the station
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setLocation(float x, float y, float z)
    {
        location[0] = x;
        location[1] = y;
        location[2] = z;
    }

    /**
     * @return station's id
     */
    public int getId() {
        return id;
    }

    /**
     * set stations id
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the number of sequence in which this air-puff appears
     */
    public int getInstance() {
        return instance;
    }

    /**
     * @return the number of laps in which this air-puff appears
     */
    public int getLaps() {
        return laps;
    }

    /**
     * Set the number of sequence in which this air-puff appears
     * @param instance an integer indicating in which sequence this air-puff appears
     */
    public void setInstance(int instance) {
        this.instance = instance;
    }

    /**
     * Set the number of laps in which this air-puff appears
     * @param laps an integer
     */
    public void setLaps(int laps) {
        this.laps = laps;
    }

    /**
     * save air station settings to config file
     * @param conf config file path
     */
    public void saveToConfig(String conf)
    {
        try
        {
            FileWriter fw = new FileWriter(conf, true);
            System.out.println(toConf());
            fw.write(toConf());
            fw.write(System.getProperty( "line.separator" ));
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    /**
     * @return a string representing station's configuration
     */
    private String toConf() {
        String str = id + DEL;
        str += location[0] + DEL + location[1] + DEL + location[2] + DEL;
        str+= "sequence:" + instance + DEL + "laps:" + (laps > 0 ? laps : "inf");
        return str;
    }

    @Override
    public String toString() {
        return name + " " + id + " { " + toConf() + " }";
    }

    /**
     * @return x location
     */
    public float getX() {
        return location[0];
    }

    /**
     * @return y location
     */
    public float getY() {
        return location[1];
    }

    /**
     * @return z location
     */
    public float getZ() {
        return location[2];
    }

    /**
     * set x location
     * @param x float
     */
    public void setX(float x) {
        this.location[0] = x;
    }

    /**
     * set x location
     * @param y float
     */
    public void setY(float y) {
        this.location[1] = y;
    }

    /**
     * set x location
     * @param z float
     */
    public void setZ(float z) {
        this.location[2] = z;
    }

    /**
     * constructor
     * @param x location
     * @param y location
     */
    public AirStation(float x, float y) {
        location = new float[3];
        this.location[0] = x;
        this.location[1] = y;
    }
}

