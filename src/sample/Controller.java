package sample;


/**
 *
 * this class is the controller of UI
 * main operations of GUI will be set up and triggered here
 *
 * @author Adnan
 * @author Anahita
 *
 */

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

    enum LOGS {INFO, ERROR, INTRP}


    private int setupCount = 0;
    private SimpleIntegerProperty SIZE_OF_CHART = new SimpleIntegerProperty(20);
    private SimpleIntegerProperty STEP_OF_CHART = new SimpleIntegerProperty(1);
    private static Double SCALE_OF_Y_AXIS;
    private static String pdfName;
    private static String pngName;
    private static String txtName;

    /**
     *
     * outer dataFields of class which is imported from
     * sample.FXML
     *
     */
    @FXML
    Pane innerPane;
    @FXML
    JFXTextField sizeOfChartTextArea, x_axisStep,
            pdfNameTextField, pngNameTextField, txtNameTextField;
    @FXML
    JFXComboBox<String> portNames;
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
    private void setUp() {
        if (setupCount == 0) {
            x_axisStep.setOnKeyPressed((keyEvent) -> {
                if (keyEvent.getCode() == KeyCode.ENTER)
                    chartChanger();
            });
            sizeOfChartTextArea.setOnKeyPressed((keyEvent)->{
                if (keyEvent.getCode() == KeyCode.ENTER)
                    chartChanger();
            });
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
            y_axis.setUpperBound(SCALE_OF_Y_AXIS);
            ++setupCount;
        }
    }

    private String nameSetter(String name, String extension){
        if (new File(name+extension).exists()){
            int i = 1;
            while (i<100000){
                if (!(new File(name+"("+i+")"+extension).exists()))
                    return (name+"("+i+")");
                ++i;
            }
            return (name+"("+100000+")");
        }else return name;
    }

    private void nameConfigure(){
       if (pdfNameTextField.getText().isEmpty()){
            pdfName = nameSetter("pdfDocument", ".pdf");
       }else {
            pdfName = nameSetter(pdfNameTextField.getText(), ".pdf");
       }

       if (pngNameTextField.getText().isEmpty()){
            pngName = nameSetter("pngDocument", ".png");
       }else {
            pngName = nameSetter(pngNameTextField.getText(), ".png");
       }

       if (txtNameTextField.getText().isEmpty()){
            txtName = nameSetter("txtDocument", ".txt");
       }else {
            txtName = nameSetter(txtNameTextField.getText(), ".txt");
       }
    }

    /**
     * to get the image and the pdf of our chart
     */
    @FXML
    private void export() {
        nameConfigure();
        WritableImage nodeshot = chart.snapshot(new SnapshotParameters(), null);
        File file = new File(pngName+".png");
        File txtFile = new File(txtName+".txt");
        try {
            FileOutputStream writeToFile = new FileOutputStream(txtFile);
            DataOutputStream fileOutPut = new DataOutputStream(writeToFile);
            try {
                fileOutPut.writeUTF(dialogPane.getText());
            } catch (IOException e) {
                textAreaChanger(LOGS.INTRP, "Could not write to txt file", Integer.MIN_VALUE);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            textAreaChanger(LOGS.INTRP, "Could not open text file", Integer.MIN_VALUE);
            e.printStackTrace();
        }
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
        } catch (IOException e) {
            textAreaChanger(LOGS.INTRP, "<----------Screenshot has not "
                            + "saved because of interruption------------->",
                    Integer.MIN_VALUE);
            System.err.println(e);
        }
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDImageXObject pdimage;
        PDPageContentStream content;
        try {
            pdimage = PDImageXObject.createFromFile(pngName+".png",doc);
            content = new PDPageContentStream(doc, page);
            content.drawImage(pdimage, 0, 100);
            content.close();
            doc.addPage(page);
            doc.save(pdfName+".pdf");
            doc.close();
        } catch (IOException ex) {
            textAreaChanger(LOGS.ERROR, "<-------------Screenshot has not saved------------->", Integer.MIN_VALUE);
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @FXML
    private void torsion() {
        SCALE_OF_Y_AXIS = 20.0;
        Main.changeToMain();
    }
    @FXML
    private void tension(){
        SCALE_OF_Y_AXIS = 3000.0;
        Main.changeToMain();
    }

    private void textAreaChanger(LOGS log, String x, int y) {
        dialogPane.appendText("\n[" + log.name() + "]\t\t\t\t\t "
                + "x : " + x
                + "\t\ty : " + y);
    }

    /**
     * changes the range of x and y axis
     */
    @FXML
    private void chartChanger() {
        if (!sizeOfChartTextArea.getText().isEmpty()
                && !x_axisStep.getText().isEmpty()) {
            SIZE_OF_CHART.set(new Integer(sizeOfChartTextArea.getText()));
            STEP_OF_CHART.set(new Integer(x_axisStep.getText()));
        } else {
            if (sizeOfChartTextArea.getText().isEmpty()){
                sizeOfChartTextArea.requestFocus();
                sizeOfChartTextArea.setFocusColor(Color.RED);
            } else {
                x_axisStep.requestFocus();
                x_axisStep.setFocusColor(Color.RED);
            }
        }
    }

    /**
     *
     * detects available Comm Ports
     * and refreshes port ChoiceBox
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

    private ScheduledExecutorService scheduledExecutorService;
    @FXML
    private void setButton() {
        try {
            SerialPort serialPort = SerialPort.getCommPort(portNames.getValue());
            if(serialPort.openPort()) {
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0);
                Scanner portScanner = new Scanner(serialPort.getInputStream());
                XYChart.Series<String, Integer> series = new XYChart.Series<>();

                chart.getData().add(series);

                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

                // setup a scheduled executor to periodically put data into the chart
                scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

                // put data onto graph per second
                scheduledExecutorService.scheduleAtFixedRate(() -> {
                    if(serialPort.openPort() && iteration % 2 != 0) {
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
                    }
                }, 0, STEP_OF_CHART.get(), TimeUnit.SECONDS);
            } else {
                textAreaChanger(LOGS.ERROR, "<--------------Port has not opened-------------->", Integer.MIN_VALUE);
            }
        } catch (Exception e) {
            dialogPane.appendText("\n[" + LOGS.ERROR+ "]\t\t\t\t\t "+"");
        }
    }

    /**
     * to control main thread
     */
    private int iteration = 0;
    @FXML
    private void start_stop() {
        //if the user wants to start while no port is selected!
        if(portNames.getValue() == null) {
            dialogPane.appendText("Please select a port first!");
        }else
        ++iteration;
    }
}