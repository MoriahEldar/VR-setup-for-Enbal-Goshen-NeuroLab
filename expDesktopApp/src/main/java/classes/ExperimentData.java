package classes;

public class ExperimentData {
    private String cageName = "";
    private String mouseName = "";
    private String dir = "";
    public ExperimentData(String cageName, String mouseName, String dir) {
        this.cageName = cageName.trim();
        this.mouseName = mouseName.trim();
        this.dir = dir.trim();
    }

    public String getCageName() {
        return cageName;
    }

    public String getMouseName() {
        return mouseName;
    }

    public String getDir() {
        return dir;
    }

}
