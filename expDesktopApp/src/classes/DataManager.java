package classes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;

public class DataManager {
    // all the mice in the project, defined by name
    private HashMap<Integer, Mouse> miceList;
    // all the experiments in the project, defined by name
    private HashMap<Integer, Experiment> experimentsList;
    //? check
    //private List<Log> logs; // get logs by mice and logs by exp?
        
    private DataManager() {
        this.miceList = new HashMap<>();
        this.experimentsList = new HashMap<>();
    }

    // singleton access
    private static class SingletonHelper {
        private static DataManager instance = new DataManager();
    }

    /**
     * singelton access
     * @return the singelton instance
     */
    public static DataManager getDataManager() {
        return SingletonHelper.instance;
    }
   
    protected Object readResolve() {
        return getDataManager();
    }

    /**
     * loads a project from a file
     * @param path path of file
     * @return a DataManager for the project
     */
    public static DataManager loadDataFromFile(Path path) {
        try (InputStream in = new FileInputStream(path.toFile()); 
        ObjectInputStream ois = new ObjectInputStream(in)) {
            SingletonHelper.instance = ((DataManager) ois.readObject());
        }
        catch (IOException e) {
            System.err.println("###################################################");
            System.err.println("Error related to serialization while loading cage");
            System.err.println("###################################################");
            e.printStackTrace();
        } 
        catch (ClassNotFoundException y) {
            System.err.println("File does not contain DataManager object");
        }
        return SingletonHelper.instance;       
    }

    /**
     * saves the data to a file
     * @param path path of file
     * @return a DataManager for the project
     */
    public boolean loadDataToFile(String path) {
        try(OutputStream out = new FileOutputStream(path); 
        ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(this);
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("###################################################");
            System.err.println("Error related to serialization save");
            System.err.println("###################################################");
            return false;
        }
    }

    /**
     * @return the DS of all the mice in the DB
     */
    public HashMap<Integer, Mouse> getMiceList() {
        return (HashMap<Integer, Mouse>) this.miceList.clone();
    }

    /**
     * @return the DS of all the experiments in the DB
     */
    public HashMap<String, Mouse> getExperimentsList() {
        return (HashMap<String, Mouse>) experimentsList.clone();
    }

    /**
     * get a specific mouse
     * @param id mouse id
     * @return mouse
     */
    public Mouse getMouse(int id) {
        //! check if by refrence or not and so on...
        return miceList.get(id);
    }

    /**
     * get a specific experiment
     * @param id experiment name
     * @return experiment
     */
    public Experiment getExperiment(int id) {
        return experimentsList.get(id);
    }

    /**
     * creates an experiment
     * @return a new experiment with a valid system id
     */
    public Experiment createExperiment() {
        Experiment newExp = new Experiment(this.experimentsList.size());
        this.experimentsList.put(newExp.getId(), newExp);
        return newExp;
    }

    /**
     * creates a mouse
     * @return a new mouse with a valid system id
     */
    public Mouse createMouse() {
        Mouse newMouse = new Mouse(this.miceList.size());
        this.miceList.put(newMouse.getId(), newMouse);
        return newMouse;
    }
}
