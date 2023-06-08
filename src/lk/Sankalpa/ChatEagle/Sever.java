package lk.Sankalpa.ChatEagle;

import lk.Sankalpa.ChatEagle.Clients.modle.ClientManage;

import java.io.IOException;
import java.net.ServerSocket;

public class Sever {

    private ServerSocket socket;
    private ServerSocket socketImage;

    public Sever(ServerSocket socket,ServerSocket socketImage){

        this.socket=socket;
        this.socketImage=socketImage;

    }

    public void RunSever(){
        while (!socket.isClosed()){
            try {
                new Thread(new ClientManage(socket.accept(),socketImage.accept())).start();
            } catch (IOException e) {
                SeverClose();
                e.printStackTrace();
            }
        }
    }

    public void SeverClose(){

        while (socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        final int Port_1=5555;
        final int Port_2=5556;

        Sever sever = new Sever(new ServerSocket(Port_1), new ServerSocket(Port_2));
        System.out.println("Sever Runing");
        sever.RunSever();
    }


}
