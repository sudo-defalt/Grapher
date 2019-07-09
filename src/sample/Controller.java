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

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXML;


public class Controller {

    /**
     *
     * inner dataFields of class
     *
     */

    enum LOGS{ INFO, ERROR, INTRP}

    private Thread thread;
    private int setupCount = 0;
    private SerialPort serialPort;
    private SimpleIntegerProperty SIZE_OF_CHART = new SimpleIntegerProperty(20);
    private SimpleBooleanProperty loopCondition = new SimpleBooleanProperty(true);

    /**
     *
     *  outer dataFields of class which is imported from
     * sample.FXML
     *
     *
     */
    @FXML
    JFXTextField sizeOfChartTextArea;
    @FXML
    JFXComboBox<SerialPort> portNames;
    @FXML
    JFXComboBox<Integer> baudRate;
    @FXML
    JFXComboBox<Integer> dataBits;
    @FXML
    JFXComboBox<Integer> stopBit;
    @FXML
    JFXComboBox<String> parity;
    @FXML
    TextArea dialogPane;
    @FXML
    MenuBar menuBar;
    @FXML
    AnchorPane pane;
    @FXML
    Separator v_separator;
    @FXML
    LineChart<String, Integer> chart;

    /**
     *
     * this method return initialized serial port
     *  to read/write
     *
     * @return SerialPort
     *
     */


    /**
     *
     * inner Setup for choiceBoxes of
     * baud rate, data bits, stop bit and parity
     *
     */

    private void baudRateSetup(){
        baudRate.getItems().add(110);
        baudRate.getItems().add(300);
        baudRate.getItems().add(600);
        baudRate.getItems().add(1200);
        baudRate.getItems().add(4800);
        baudRate.getItems().add(9600);
        baudRate.getItems().add(14400);
        baudRate.getItems().add(19200);
        baudRate.getItems().add(38400);
        baudRate.getItems().add(57600);
        baudRate.getItems().add(115200);
        baudRate.getItems().add(128000);
        baudRate.getItems().add(256000);
    }
    
    private void dataBitsSetup(){
        dataBits.getItems().add(5);
        dataBits.getItems().add(6);
        dataBits.getItems().add(7);
        dataBits.getItems().add(8);
    }

    private void stopBitSetup(){
        stopBit.getItems().add(1);
        stopBit.getItems().add(1_5);
        stopBit.getItems().add(2);
    }

    private void paritySetup(){
        parity.getItems().add("Even");
        parity.getItems().add("Odd");
        parity.getItems().add("Mark");
        parity.getItems().add("Space");
        parity.getItems().add("None");
    }
/*
    private int paritySetter(){
        if (parity.getValue().equalsIgnoreCase("EVEN"))
            return ;
        if (parity.getValue().equalsIgnoreCase("ODD"))
            return ;
        if (parity.getValue().equalsIgnoreCase("MARK"))
            return SerialPort.PARITY_MARK;
        if (parity.getValue().equalsIgnoreCase("SPACE"))
            return SerialPort.PARITY_SPACE;
        if (parity.getValue().equalsIgnoreCase("NONE"))
            return SerialPort.PARITY_NONE;
        else return 0;
    }
    */
    
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

    double x = 0;

    XYChart.Series<String, Integer> series;
    @FXML
    private void setUp(){
        if (setupCount == 0){
            dialogPane.setWrapText(true);
            series = new XYChart.Series<>();
            chart.getData().add(series);
            v_separator.prefHeightProperty().bind(pane.heightProperty());
            menuBar.prefWidthProperty().bind(pane.widthProperty());
            refresh();
            baudRateSetup();
            dataBitsSetup();
            stopBitSetup();
            /*
            thread = new Thread(()->{
                while (loopCondition.get()){
                    Platform.runLater(()->{
                        while (series.getData().size()
                                >= SIZE_OF_CHART.intValue()){
                            int i = 0;
                            series.getData().remove(i);
                            ++i;
                        }
                        XYChart.Data data = new XYChart.Data<>(x+"",
                                (int)(Math.random()*200));
                        Rectangle rectangle = new Rectangle();
                        rectangle.setVisible(false);
                        data.setNode(rectangle);
                        series.getData().add(data);
                        textAreaChanger(new Integer((String) data.getXValue()),
                                (Integer)data.getYValue());
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++x;
                }
            });
            thread.start();
            paritySetup();
            */
            ++setupCount;
        }
    }

    /**
     *
     * this method detect available Comm Ports
     * and refresh port ChoiceBox
     *
     */


    private void textAreaChanger(int x, int y){
        dialogPane.appendText("\n["+LOGS.INFO+"]\t\t\t\t\t "
                +"x : "+x
                +"\t\ty : "+y);
    }

    @FXML
    private void EOO(){
        if (thread.isAlive()){
            System.out.println("thread is stopped");
            thread.stop();
        }
        else{
            System.out.println("thread is started");
            thread.resume();
        }
    }

    @FXML
    private void chartChanger(){
        SIZE_OF_CHART.set(new Integer(sizeOfChartTextArea.getText()));
    }

    @FXML
    private void refresh(){
        SerialPort[] list = SerialPort.getCommPorts();
        portNames = new JFXComboBox<>();
        for (int i = 0; i < list.length; i++){
            portNames.getItems().add(list[i]);
        }

    }

    @FXML
    private void setButton(){
        System.out.println("set button clicked!");
        serialPort = portNames.getValue();
        serialPort.setBaudRate(baudRate.getValue());
        serialPort.setNumDataBits(dataBits.getValue());
        serialPort.setNumStopBits(stopBit.getValue());
        byte[] buffer = new byte[2];
        thread = new Thread(()->{
            while (loopCondition.getValue()){
                Platform.runLater(()->{
                    while (series.getData().size()
                            >= SIZE_OF_CHART.intValue()){
                        int i = 0;
                        series.getData().remove(i);
                        ++i;
                    }
                    serialPort.readBytes(buffer, 2);
                    XYChart.Data data = new XYChart.Data<>(x+"",
                            buffer[0]+buffer[1]);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setVisible(false);
                    data.setNode(rectangle);
                    series.getData().add(data);
                    textAreaChanger(new Integer((String) data.getXValue()),
                            (Integer)data.getYValue());
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                x+= 0.5;
            }
        });
        thread.start();
    }

}