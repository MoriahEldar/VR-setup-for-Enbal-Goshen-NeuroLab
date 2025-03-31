package mainFlow;

import com.fazecast.jSerialComm.*;

public class ArduinoConnection {
    SerialPort ardPort = null;
    public ArduinoConnection() {
        
    }

    public void connectArduino() throws Exception {
        this.ardPort = SerialPort.getCommPorts()[0];
        ardPort.setBaudRate(9600);       
        if(!ardPort.openPort()) {
            throw new Exception("failed to open port");
        }

        ardPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { 
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; 
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;

                byte[] newData = new byte[ardPort.bytesAvailable()];
                int numRead = ardPort.readBytes(newData, newData.length);
                String receivedData = new String(newData);
                System.out.println("Received: " + receivedData);
            }
        });
    }

    public void disconnectArduino() throws Exception {
        if (ardPort != null) {
            ardPort.closePort(); 
        }
    }

    public static void sendStartSignal() {
        // TODO send start signal to arduino and start listening
    }

    public static void getTtl() {
        // TODO make a file of all the ttls and times
    }

    public static void getEncoderData() {
        // TODO write to the file
    }

    public static void sendGotToReward() {
        // TODO send code to open lick
    }

    public static void lick() {
        // TODO write that licked
    }
}
