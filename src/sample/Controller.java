package sample;


/**
 *
 * this class is controller of UI
 * main operations of GUI will be setup and triggered here
 *
 * @author Adnan
 * @author Anahita
 *
 */

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Controller {

    /**
     *
     * inner dataFields of class
     *
     */

    enum LOGS{ INFO, ERROR, INTRP}

    private Thread thread;
    private int setupCount = 0;
    private SimpleIntegerProperty SIZE_OF_CHART = new SimpleIntegerProperty(60);
    private SimpleDoubleProperty STEP_OF_CHART = new SimpleDoubleProperty(1.0);
    private static Double SCALE_OF_Y_AXIS;
    private SimpleBooleanProperty loopCondition = new SimpleBooleanProperty(true);
    final int WINDOW_SIZE = 15;

    /**
     *
     *  outer dataFields of class which is imported from
     * sample.FXML
     *
     *
     */
    @FXML
    Pane innerPane;
    @FXML
    JFXTextField sizeOfChartTextArea, x_axisStep;
    @FXML
    JFXComboBox<String> portNames;
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
    @FXML
    NumberAxis y_axis;
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
        baudRate.setValue(110);
    }
    
    private void dataBitsSetup(){
        dataBits.getItems().add(5);
        dataBits.getItems().add(6);
        dataBits.getItems().add(7);
        dataBits.getItems().add(8);
        dataBits.setValue(5);
    }

    private void stopBitSetup(){
        stopBit.getItems().add(1);
        stopBit.getItems().add(2);
        stopBit.setValue(1);
    }

    private void paritySetup(){
        parity.getItems().add("Even");
        parity.getItems().add("Odd");
        parity.getItems().add("Mark");
        parity.getItems().add("Space");
        parity.getItems().add("None");
        parity.setValue("None");
    }

    private int paritySetter(){
        if (parity.getValue().equalsIgnoreCase("EVEN"))
            return 2;
        if (parity.getValue().equalsIgnoreCase("ODD"))
            return 1;
        if (parity.getValue().equalsIgnoreCase("MARK"))
            return 3;
        if (parity.getValue().equalsIgnoreCase("SPACE"))
            return 4;
        if (parity.getValue().equalsIgnoreCase("NONE"))
            return 0;
        else return -1;
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

    double x = 0;
    double y;
    XYChart.Series<String, Integer> series;

    @FXML
    private void setUp(){
        if (setupCount == 0){
            dialogPane.setWrapText(true);
            chart.prefWidthProperty().bind(innerPane
                    .widthProperty());
            chart.prefHeightProperty().bind(innerPane
                    .heightProperty().subtract(265.0));
            series = new XYChart.Series<>();
            innerPane.prefHeightProperty().bind(Main
                    .scene.heightProperty());
            innerPane.prefWidthProperty().bind(Main
                    .scene.widthProperty().subtract(215));
            chart.getData().add(series);
            v_separator.prefHeightProperty().bind(pane.heightProperty());
            menuBar.prefWidthProperty().bind(pane.widthProperty());
            refresh();
            baudRateSetup();
            dataBitsSetup();
            stopBitSetup();
            paritySetup();
            if (SCALE_OF_Y_AXIS == 20.0){
                y_axis.setUpperBound(20.0);
            }else {
                y_axis.setUpperBound(3000.0);
            }
            /**
             * this thread is a simple meaningless exp
             */
//            thread = new Thread(()->{
//                while (loopCondition.get()){
//                    Platform.runLater(()->{
//                        while (series.getData().size()
//                                >= SIZE_OF_CHART.intValue()){
//                            int i = 0;
//                            series.getData().remove(i);
//                            ++i;
//                        }
//                        XYChart.Data data = new XYChart.Data<>(x+"",
//                                (int)(Math.random()*SCALE_OF_Y_AXIS));
//                        series.getData().add(data);
//                        textAreaChanger(LOGS.INFO, new Double((String) data.getXValue()),
//                                (Integer)data.getYValue());
//                    });
//                    try {
//                        Thread.sleep(STEP_OF_CHART.intValue()*1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    x+= STEP_OF_CHART.doubleValue();
//                }
//            });
//            thread.start();
            ++setupCount;
        }
    }


    @FXML
    private void torsion(){
        SCALE_OF_Y_AXIS = 20.0;
        Main.changeToMain();
    }
    @FXML
    private void tension(){
        SCALE_OF_Y_AXIS = 3000.0;
        Main.changeToMain();
    }

    private void textAreaChanger(LOGS log, double x, int y){
        dialogPane.appendText("\n["+log.name()+"]\t\t\t\t\t "
                +"x : "+x
                +"\t\ty : "+y);
    }

    @FXML
    private void chartChanger(){
        if (!sizeOfChartTextArea.getText().isEmpty()
            && !x_axisStep.getText().isEmpty()) {
            SIZE_OF_CHART.set(new Integer(sizeOfChartTextArea.getText()));
            STEP_OF_CHART.set(new Double(x_axisStep.getText()));
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
        SerialPort[] list = SerialPort.getCommPorts();
        portNames.getItems().clear();
        for (int i = 0; i < list.length; i++){
            portNames.getItems().add(list[i]
                    .getDescriptivePortName());
        }

    }

    @FXML
    private void setButton(){
        /**
         * setting parameters of serial port
         */
//        if (portNames.getValue() != null
//                && baudRate.getValue() != null
//                && dataBits.getValue() != null
//                && stopBit.getValue() != null
//                && paritySetter() != -1) {
//
//            serialPort.setBaudRate(baudRate.getValue());
//            serialPort.setNumDataBits(dataBits.getValue());
//            serialPort.setNumStopBits(stopBit.getValue());
//            serialPort.setParity(paritySetter());
//        }

        SerialPort serialPort = SerialPort.getCommPort(portNames.getValue());
        /**
         * this thread is used to show real-time broadcast of serial port
         *
         */


        if(serialPort.openPort()) {
            Scanner portScanner = new Scanner(serialPort.getInputStream());
            //defining a series to display data
            XYChart.Series<String, Integer> series = new XYChart.Series<>();


            // add series to chart
            chart.getData().add(series);


            // this is used to display time in HH:mm:ss format
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

            // setup a scheduled executor to periodically put data into the chart
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

            // put data onto graph per second
            scheduledExecutorService.scheduleAtFixedRate(() -> {

                // Update the chart
                Platform.runLater(() -> {
                    // get current time
                    Date now = new Date();
                    String newData = portScanner.nextLine();
                    XYChart.Data data = new XYChart.Data<>(simpleDateFormat.format(now) + "", Integer.parseInt(newData));
                    series.getData().add(data);

                    if (series.getData().size() > WINDOW_SIZE)
                        series.getData().remove(0);
                });
            }, 0, 1, TimeUnit.SECONDS);
        } else {
            System.out.print("boogh");
        }
//        thread = new Thread(()->{
//            while (input.hasNextLine()){
//                /**
//                 * platform which set data of chart
//                 */
//                Platform.runLater(()->{
//                    /**
//                     * this loop will keep size of charts X-axis fixed
//                     */
//                    while (series.getData().size()
//                            >= SIZE_OF_CHART.intValue()){
//                        int i = 0;
//                        series.getData().remove(i);
//                        ++i;
//                    }
//                    /**
//                     * set data of series here -> y will be the data which been read from
//                     * serial port , x will be set automatically in line 288
//                     * use global variables x and y
//                     *
//                     * please use textAreaChanger method to update dialogPane
//                     *
//                     */
//                    XYChart.Data data = new XYChart.Data<>(x+"",
//                            new Integer(input.nextLine()));
//                    series.getData().add(data);
//                    textAreaChanger(LOGS.INFO, new Double((String) data.getXValue()),
//                            (Integer)data.getYValue());
//                });
//                try {
//                    Thread.sleep(STEP_OF_CHART.intValue()*1000);
//                } catch (InterruptedException e) {
//                    textAreaChanger(LOGS.INTRP, Double.NaN, Integer.MIN_VALUE);
//                    e.printStackTrace();
//                }
//                x+= STEP_OF_CHART.doubleValue();
//            }
//            input.close();
//        });
//        thread.start();
    }
}

