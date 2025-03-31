package mainFlow;

public class ExperimentFlow { // static class for eperiment and shit
    int lap = 0;
    BlenderConnection blenderConn;
    ArduinoConnection arduinoConn;
    
    public ExperimentFlow(float radius) {
        this.blenderConn = new BlenderConnection(radius);
        this.arduinoConn = new ArduinoConnection();
    }

    static void uploadEnv() {
        // TODO: upload the maze
        // TODO: 
    }

    static void startExp() {
        
    }
}
