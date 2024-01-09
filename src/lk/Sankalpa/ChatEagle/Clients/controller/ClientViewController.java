package lk.Sankalpa.ChatEagle.Clients.controller;
//jhgyfjyvhy d jtchtgmjhkyugmh gmygyukg
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.Sankalpa.ChatEagle.Clients.modle.ClientManage;

import java.io.*;
import java.net.Socket;

public class ClientViewController {
    public AnchorPane pane_main;
    public TextField txt_loger_name;
    public Pane pane_second;
    public Pane pane_section;
    public Label lbl_name;
    public VBox vBoxMsgPane;
    public ScrollPane ScrollPane;
    @FXML
    private JFXTextField txt_client_msg;

    @FXML
    private TextArea client_side_msg;

    @FXML
    private TextArea server_side_msg;


    Socket socket ;
    Socket socketImage ;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    DataOutputStream dataOutputStreamImage;
    DataInputStream dataInputStreamImage;

    String c_msg ="";

    public void initialize() throws IOException {
        pane_second.setVisible(true);
        pane_second.setOpacity(1);
        pane_section.setOpacity(0.6);


        lbl_name.setText(txt_loger_name.getText());

        socket=new Socket("localhost",5555);
        socketImage=new Socket("localhost",5556);

        dataOutputStream=new DataOutputStream(socket.getOutputStream());
        dataInputStream=new DataInputStream(socket.getInputStream());

        dataOutputStreamImage=new DataOutputStream(socketImage.getOutputStream());
        dataInputStreamImage=new DataInputStream(socketImage.getInputStream());

        dataOutputStream.writeUTF(txt_loger_name.getText());

            new Thread(()->{

                while (!c_msg.equals("Done")){

                    try {

                        c_msg=dataInputStream.readUTF();
                      final String[] namear = c_msg.split(" ", 2);
                        Text text = new Text(namear[0] + "From : ");
                        text.setStyle("-fx-fill: #70707a");
                        Text text1 = new Text(namear[1]);
                        text1.setWrappingWidth(200);
                        text1.setStyle("-fx-font-weight: bold");

                      final   VBox vBox1 = new VBox(text, text1);

                        TextFlow textFlow = new TextFlow(vBox1);
                        textFlow.setPadding(new Insets(5,0,5,10));

                       final HBox hBox = new HBox(textFlow);
                       hBox.setPadding(new Insets(0,0,10,0));

                       setMsgVbox(hBox);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }).start();

            new Thread(()->{

                while (true){

                    try {

                       final int readInt = dataInputStreamImage.readInt();
                       final byte[] bytes = new byte[readInt];

                       dataInputStreamImage.readFully(bytes);
                        Text text = new Text(dataInputStreamImage.readUTF() + " : ");
                        text.setStyle("-fx-fill: #70707a");

                        Image image = new Image(new ByteArrayInputStream(bytes));
                        ImageView imageView = new ImageView(image);
                        imageView.setStyle("-fx-background-color: rgb(222,222,222);" +
                                "-fx-background-radius: 20px;");

                        imageView.preserveRatioProperty().set(true);
                        if (image.getWidth()>50){
                            imageView.setFitWidth(250);
                            imageView.setFitHeight(250);
                        }

                        TextFlow textFlow = new TextFlow(new VBox(text, imageView));
                        textFlow.setPadding(new Insets(10,10,10,10));
                        HBox hBox = new HBox(textFlow);
                        hBox.setPadding(new Insets(0,0,10,0));
                        setMsgVbox(hBox);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }).start();

    }

    private void setMsgVbox(HBox hBox) {
        Platform.runLater(()->{
            vBoxMsgPane.getChildren().add(hBox);

        });
    }


    @FXML
    void btn_client_close_chat(MouseEvent event) {

        Platform.exit();

    }

    @FXML
    void sent_client_msg_On_Action(ActionEvent event) throws IOException {

        c_msg=txt_client_msg.getText();
        if (c_msg.isEmpty()) return;
        try {

            dataOutputStream.writeUTF(c_msg);
            dataOutputStream.flush();
            Text text = new Text(c_msg);
            text.setWrappingWidth(200);

            Text you = new Text("You : ");
            you.setStyle("-fx-fill: #0bdc0b");
            text.setStyle("-fx-font-weight: bold");

            TextFlow textFlow = new TextFlow(new VBox(you, text));
           final HBox hBox = new HBox(textFlow);

           hBox.setPadding(new Insets(0,0,10,0));
           hBox.setAlignment(Pos.CENTER_RIGHT);
           setMsgVbox(hBox);

           textFlow.setPadding(new Insets(5,-100,5,10));
           txt_client_msg.setText("");

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public void send_image_On_Action(MouseEvent mouseEvent) throws IOException {

      /* final FileChooser fileChooser = new FileChooser();
       final File file = fileChooser.showOpenDialog(vBoxMsgPane.getScene().getWindow());
        System.out.println(file);

        setAnImage(file.getAbsolutePath());

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStream.read(bytes);

        dataOutputStreamImage.writeInt((int) file.length());
        dataOutputStreamImage.write(bytes);*/

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 10, 5, 10));
            Image image = new Image("file:" + file.getAbsolutePath());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(250);
            hBox.getChildren().add(imageView);
            vBoxMsgPane.getChildren().add(hBox);
            clientManage.sendFileToServer(file, lbl_name.getText());
            System.out.println("send " + file.getName());
        }
    }
    private ClientManage clientManage;

    private void setAnImage(String path){

        HBox hBox = new HBox();

        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(0,0,10,0));

        Image image = new Image("file : " + path);

        ImageView imageView = new ImageView(image);
        imageView.preserveRatioProperty().set(true);

        if (image.getWidth()>50){
            imageView.setFitWidth(300);
            imageView.setFitHeight(300);
        }

        TextFlow textFlow = new TextFlow(imageView);
        textFlow.setStyle("-fx-background-color: rgb(222,222,222);" +
                "-fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(10,10,10,10));
        hBox.getChildren().add(textFlow);
        vBoxMsgPane.getChildren().add(hBox);
    }

    public void txt_loger_name_On_Action(ActionEvent actionEvent) {

        lbl_name.setText(txt_loger_name.getText());

        pane_section.setOpacity(1);
        pane_second.setVisible(false);

    }
}
