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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
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
    private int setupCount = 0;
    private SimpleIntegerProperty SIZE_OF_CHART = new SimpleIntegerProperty(20);
    private SimpleIntegerProperty STEP_OF_CHART = new SimpleIntegerProperty(1);
    private static Double SCALE_OF_Y_AXIS;

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

    int x = 0;
    XYChart.Series<String, Integer> series = new XYChart.Series<>();

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
            y_axis.setUpperBound(SCALE_OF_Y_AXIS);
            ++setupCount;
        }
    }

    @FXML
    private void export(){
        WritableImage nodeshot = chart.snapshot(new SnapshotParameters(), null);
        File file = new File("chart.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
        } catch (IOException e) {
            textAreaChanger(LOGS.INTRP, "<----------Screenshot has not "
                    +"saved because of interruption------------->",
                    Integer.MIN_VALUE);
            System.err.println(e);
        }
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDImageXObject pdimage;
        PDPageContentStream content;
        try {
            pdimage = PDImageXObject.createFromFile("chart.png",doc);
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdimage, 0, 100);
            content.close();
            doc.addPage(page);
            doc.save("Record.pdf");
            doc.close();
        } catch (IOException ex) {
            textAreaChanger(LOGS.ERROR, "<-------------Screenshot has not saved------------->", Integer.MIN_VALUE);
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
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

    private void textAreaChanger(LOGS log, String x, int y){
        dialogPane.appendText("\n["+log.name()+"]\t\t\t\t\t "
                +"x : "+x
                +"\t\ty : "+y);
    }

    @FXML
    private void chartChanger(){
        if (!sizeOfChartTextArea.getText().isEmpty()
            && !x_axisStep.getText().isEmpty()) {
            SIZE_OF_CHART.set(new Integer(sizeOfChartTextArea.getText()));
            STEP_OF_CHART.set(new Integer(x_axisStep.getText()));
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
                    .getSystemPortName());
        }

    }

    @FXML
    private void setButton() {
        SerialPort serialPort = SerialPort.getCommPort(portNames.getValue());
        if(serialPort.openPort()) {
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0);
            Scanner portScanner = new Scanner(serialPort.getInputStream());
            XYChart.Series<String, Integer> series = new XYChart.Series<>();

            chart.getData().add(series);

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
                    textAreaChanger(LOGS.INFO, (String)data.getXValue(), (Integer) data.getYValue());
                    while (series.getData().size() > SIZE_OF_CHART.get())
                        series.getData().remove(0);
                });
            }, 0, STEP_OF_CHART.get(), TimeUnit.SECONDS);
        } else {
            textAreaChanger(LOGS.ERROR, "<--------------Port has not opened-------------->", Integer.MIN_VALUE);
        }








//        SerialPort serialPort = SerialPort.getCommPort(portNames.getValue());
//        if (serialPort.openPort()) {
//            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0);
//            System.out.println(serialPort.isOpen());
//            /**
//             * this thread is used to show real-time broadcast of serial port
//             *
//             */
//            if(serialPort.openPort()) {
//            Scanner portScanner = new Scanner(serialPort.getInputStream());
//             XYChart.Series<String, Integer> series = new XYChart.Series<>();
//
//             chart.getData().add(series);
//
//             final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//
//             // setup a scheduled executor to periodically put data into the chart
//             ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//
//             // put data onto graph per second
//             scheduledExecutorService.scheduleAtFixedRate(() -> {
//
//                 // Update the chart
//                 Platform.runLater(() -> {
//                     // get current time
//                     Date now = new Date();
//                     try {
//                        String newData = portScanner.nextLine();
//                         XYChart.Data data = new XYChart.Data<>(simpleDateFormat.format(now) + "", Integer
//                                 .parseInt(newData));
//                         series.getData().add(data);
//                         textAreaChanger(LOGS.INFO, (String) data.getXValue(), (Integer) data.getYValue());
//                         if (series.getData().size() > SIZE_OF_CHART.get())
//                             series.getData().remove(0);
//                     }catch (Exception e){
//                         textAreaChanger(LOGS.INTRP, simpleDateFormat.format(now), Integer.MIN_VALUE);
//                     }
//                 });
//             }, 0, STEP_OF_CHART.get(), TimeUnit.SECONDS);
//        } else {
//            System.out.print("<----------------[an Error occurred]---------------->");
//        }
//
//        }
    }
}