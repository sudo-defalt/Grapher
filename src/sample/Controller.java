package sample;

import javafx.collections.ObservableList;
import jssc.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class Controller {
    @FXML
    ChoiceBox<String> portNames;
    ChoiceBox<String> baudRate;
    ChoiceBox<String> bitRate;
    ChoiceBox<String> stopBit;
    ChoiceBox<String> parity;
    ChoiceBox<String> rts;
    ChoiceBox<String> dtr;
    @FXML
    private void refresh(){
        String[] list = SerialPortList.getPortNames();
        portNames = new ChoiceBox<>();
        for (int i = 0; i < list.length; i++){
            portNames.setValue(list[i]);
        }
    }
}
