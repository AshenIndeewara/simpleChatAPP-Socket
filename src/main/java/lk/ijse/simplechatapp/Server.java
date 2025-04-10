package lk.ijse.simplechatapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {

    @FXML
    private TextArea incommingMSG;

    @FXML
    private TextField message;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String messagee="";

    public void initialize(){
        new Thread(()->{
            try {
                serverSocket = new ServerSocket(4000);
                incommingMSG.appendText("Server started.\n");
                socket = serverSocket.accept();
                incommingMSG.appendText("Client connected\n");
                dataInputStream = new DataInputStream(socket.getInputStream());
                while (!message.equals("exit")){
                    messagee = dataInputStream.readUTF();
                    incommingMSG.appendText("Client: " +messagee + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    void sendMSG(ActionEvent event) throws IOException {
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        messagee = message.getText();
        dataOutputStream.writeUTF(messagee);
        dataOutputStream.flush();
        incommingMSG.appendText("Server: " + messagee + "\n");
        message.setText("");
        if (messagee.equals("exit")) {
            socket.close();
            serverSocket.close();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/simplechatapp/server.fxml"))));
        stage.show();
    }
}
