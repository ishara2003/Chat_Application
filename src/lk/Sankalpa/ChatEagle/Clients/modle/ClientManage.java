package lk.Sankalpa.ChatEagle.Clients.modle;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManage implements Runnable{

    private static List<ClientManage> clientManageList=new ArrayList<>();
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private DataInputStream dataInputStreamImage;
    private DataOutputStream dataOutputStreamImage;

    private Socket socket;
    private Socket socketImage;

    private String userName;

    public ClientManage(Socket socket,Socket socketImage){
        try{

            this.socket=socket;
            this.socketImage=socketImage;
            this.dataOutputStream=new DataOutputStream(socket.getOutputStream());
            this.dataInputStream=new DataInputStream(socket.getInputStream());
            this.dataOutputStreamImage=new DataOutputStream(socketImage.getOutputStream());
            this.dataInputStreamImage=new DataInputStream(socketImage.getInputStream());

            userName=dataInputStream.readUTF();
            setMsgToClient(this,"Joined..!");
            clientManageList.add(this);

        }catch (Exception e){}


    }

    private void setMsgToClient(ClientManage client,String msg){

        for (ClientManage clientManage: clientManageList){

            if (client!=clientManage){
                try {
                    clientManage.dataOutputStream.writeUTF(userName+" "+msg);
                } catch (IOException e) {
                    closeConnection(socket,dataOutputStream,dataInputStream,dataOutputStreamImage,dataInputStreamImage);
                    e.printStackTrace();
                }
            }

        }

    }

    private void setImage(){
        new Thread(()->{
            while (socket.isConnected()){
                try {
                    final int readInt=dataInputStreamImage.readInt();
                    final byte[]bytes=new byte[readInt];

                    dataInputStreamImage.readFully(bytes,0,readInt);
                    sendImageToClient(this,readInt,bytes);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void sendImageToClient(ClientManage clientManage,int readInt,byte[] bytes){

        for (ClientManage clientManager: clientManageList) {

            if (clientManage!=clientManager){
                try {

                    clientManager.dataOutputStreamImage.writeInt(readInt);
                    clientManager.dataOutputStreamImage.write(bytes);
                    clientManager.dataOutputStreamImage.writeUTF(userName);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    private void sendMsg(){
        new Thread(()->{

            while (socket.isConnected()){
                try {
                    setMsgToClient(this,dataInputStream.readUTF());
                } catch (IOException e) {
                    closeConnection(socket,dataOutputStream,dataInputStream,dataOutputStreamImage,dataInputStreamImage);

                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void cientLeft(){
        clientManageList.remove(this);
        setMsgToClient(this,"Left..!");
    }

    private void closeConnection(Socket socket,DataOutputStream dataOutputStream
    ,DataInputStream dataInputStream,DataOutputStream dataOutputStreamImage,DataInputStream dataInputStreamImage){

        cientLeft();

        try {
            if(dataInputStream!=null)dataInputStream.close();
            if (dataOutputStream!=null)dataOutputStream.close();
            if (dataOutputStreamImage!=null)dataOutputStreamImage.close();
            if (dataInputStreamImage!=null)dataInputStreamImage.close();
            if(socket!=null){
                socket.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendFileToServer(File file,String userName){

        try {

            PrintWriter printWriter = new PrintWriter(socketImage.getOutputStream());
            printWriter.println("File"+userName);
            printWriter.flush();

            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataOutputStream dataOutputStream1 = new DataOutputStream(socketImage.getOutputStream());

            String name = file.getName();
            byte[] bytes = name.getBytes();
            byte[] bytes1 = new byte[(int) file.length()];
            fileInputStream.read(bytes1);

            dataOutputStreamImage.writeInt(bytes.length);
            dataOutputStreamImage.write(bytes);
            dataOutputStreamImage.writeInt(bytes1.length);
            dataOutputStreamImage.write(bytes1);
            dataOutputStreamImage.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        sendMsg();
        setImage();
    }
}
