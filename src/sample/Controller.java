package sample;


/**
 *
 * this class is controller of UI
 * main operations of GUI will be setup and triggered here
 *
 * @auther Adnan
 * @auther Anahita
 *
 */

import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import jssc.*;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class Controller {

    /**
     *
     * inner dataFields of class
     *
     */

    private int setupCount = 0;
    private SerialPort serialPort;

    /**
     *
     *  outer dataFields of class which is imported from
     * sample.FXML
     *
     *
     */

    @FXML
    ChoiceBox<String> portNames;
    @FXML
    ChoiceBox<Integer> baudRate;
    @FXML
    ChoiceBox<Integer> dataBits;
    @FXML
    ChoiceBox<Integer> stopBit;
    @FXML
    ChoiceBox<String> parity;
    @FXML
    MenuBar menuBar;
    @FXML
    AnchorPane pane;
    @FXML
    Separator v_separator;

    /**
     *
     * this method return initialized serial port
     *  to read/write
     *
     * @return SerialPort
     * 
     */

    public SerialPort getPort(){
        return serialPort;
    }

    /**
     *
     * inner Setup for choiceBoxes of
     * baud rate, data bits, stop bit and parity
     *
     */

    private void baudRateSetup(){
        baudRate.getItems().add(SerialPort.BAUDRATE_110);
        baudRate.getItems().add(SerialPort.BAUDRATE_300);
        baudRate.getItems().add(SerialPort.BAUDRATE_600);
        baudRate.getItems().add(SerialPort.BAUDRATE_1200);
        baudRate.getItems().add(SerialPort.BAUDRATE_4800);
        baudRate.getItems().add(SerialPort.BAUDRATE_9600);
        baudRate.getItems().add(SerialPort.BAUDRATE_14400);
        baudRate.getItems().add(SerialPort.BAUDRATE_19200);
        baudRate.getItems().add(SerialPort.BAUDRATE_38400);
        baudRate.getItems().add(SerialPort.BAUDRATE_57600);
        baudRate.getItems().add(SerialPort.BAUDRATE_115200);
        baudRate.getItems().add(SerialPort.BAUDRATE_128000);
        baudRate.getItems().add(SerialPort.BAUDRATE_256000);
    }
    
    private void dataBitsSetup(){
        dataBits.getItems().add(SerialPort.DATABITS_5);
        dataBits.getItems().add(SerialPort.DATABITS_6);
        dataBits.getItems().add(SerialPort.DATABITS_7);
        dataBits.getItems().add(SerialPort.DATABITS_8);
    }

    private void stopBitSetup(){
        stopBit.getItems().add(SerialPort.STOPBITS_1);
        stopBit.getItems().add(SerialPort.STOPBITS_1_5);
        stopBit.getItems().add(SerialPort.STOPBITS_2);
    }

    private void paritySetup(){
        parity.getItems().add("Even");
        parity.getItems().add("Odd");
        parity.getItems().add("Mark");
        parity.getItems().add("Space");
        parity.getItems().add("None");
    }

    private int paritySetter(){
        if (parity.getValue().equalsIgnoreCase("EVEN"))
            return SerialPort.PARITY_EVEN;
        if (parity.getValue().equalsIgnoreCase("ODD"))
            return SerialPort.PARITY_ODD;
        if (parity.getValue().equalsIgnoreCase("MARK"))
            return SerialPort.PARITY_MARK;
        if (parity.getValue().equalsIgnoreCase("SPACE"))
            return SerialPort.PARITY_SPACE;
        if (parity.getValue().equalsIgnoreCase("NONE"))
            return SerialPort.PARITY_NONE;
        else return 0;
    }

    /**
     *
     * setup choiceBoxes and other elements
     * of GUI here
     *
     * this method is used to initialize
     * pane and GUI components
     *
     * please test GUI export in scene builder preview section
     * before commit and push in git
     *
     */

    @FXML
    private void setUp(){
        if (setupCount == 0){
            pane.setMinHeight(595);
            pane.setMinWidth(798);
            v_separator.prefHeightProperty().bind(pane.heightProperty());
            menuBar.prefWidthProperty().bind(pane.widthProperty());
            baudRateSetup();
            dataBitsSetup();
            stopBitSetup();
            paritySetup();
            ++setupCount;
        }
    }

    /**
     *
     * this method detect available Comm Ports
     * and refresh port ChoiceBox
     *
     */

    @FXML
    private void refresh(){
        String[] list = SerialPortList.getPortNames();
        portNames = new ChoiceBox<>();
        for (int i = 0; i < list.length; i++){
            portNames.getItems().add(list[i]);
        }

    }

    @FXML
    private void setButton(){
        System.out.println("set button clicked!");
        serialPort = new SerialPort(portNames.getValue());
        try {
            serialPort.setParams(baudRate.getValue(),
                    dataBits.getValue(),
                    stopBit.getValue(),
                    paritySetter());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
