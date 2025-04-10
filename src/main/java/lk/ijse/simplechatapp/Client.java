package lk.ijse.simplechatapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class Client extends Application {
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String messagee="";
    Socket socket;

    @FXML
    private TextField message;

    @FXML
    private TextArea incommingMSG;

    @FXML
    void imageUpload(ActionEvent event) {
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        event.consume();
        if (file != null) {
            String filePath = file.getAbsolutePath();
            incommingMSG.appendText("File selected: " + filePath + "\n");
        } else {
            incommingMSG.appendText("File selection cancelled.\n");
        }
    }
    @FXML
    void sendMSG(ActionEvent event) throws IOException {
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        messagee = message.getText();
        dataOutputStream.writeUTF(messagee);
        dataOutputStream.flush();
        incommingMSG.appendText("Client: " + messagee + "\n");
        message.setText("");
    }

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost",4000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                while (!message.equals("exit")) {
                    messagee=dataInputStream.readUTF();
                    incommingMSG.appendText("Server: " + messagee+"\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/simplechatapp/client.fxml"))));
        stage.show();
    }
}
