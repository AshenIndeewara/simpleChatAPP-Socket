package lk.ijse.simplechatapp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Server extends Application {

    @FXML
    private TextArea incommingMSG;

    @FXML
    private ImageView imgView;

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
                    if(messagee.equals("IMG")) {
                        incommingMSG.appendText("Image received\n");
                        long fileSize = dataInputStream.readLong();
                        byte[] imageBytes = new byte[(int) fileSize];
                        dataInputStream.readFully(imageBytes);
                        Image image = new Image(new ByteArrayInputStream(imageBytes));
                        imgView.setImage(image);
                    }else{
                        incommingMSG.appendText("Client: " +messagee + "\n");
                    }
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

    @FXML
    void sendImage(ActionEvent event) {
        Window window = ((Node) (event.getSource())).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        event.consume();
        if (file != null) {
            try {
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                dataOutputStream.writeUTF("IMG");
                dataOutputStream.writeLong(fileBytes.length);
                dataOutputStream.write(fileBytes);
                dataOutputStream.flush();
                incommingMSG.appendText("Image sent successfully.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            incommingMSG.appendText("File selection cancelled.\n");
        }
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/simplechatapp/server.fxml"))));
        stage.show();
    }
}
