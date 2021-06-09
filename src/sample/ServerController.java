package sample;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;


public class ServerController {

    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private int appId;
    private String host;
    private int port;

    public ServerController(String host, int port) {
        try {
            socket = new Socket(host, port);
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new DataInputStream(socket.getInputStream());
            this.port = port;
            this.host = host;
            appId = inStream.readInt();
        } catch (IOException e) {
            System.exit(-5);
        }
    }
    private void reconnect() throws IOException {
        disconnect();
        outStream.close();
        inStream.close();
        socket.close();
        socket = new Socket(host, port);
        outStream = new DataOutputStream(socket.getOutputStream());
        inStream = new DataInputStream(socket.getInputStream());
        appId = inStream.readInt();
    }

    public String authorization(String username, String password) throws IOException {
        outStream.writeInt(101);
        outStream.writeUTF(username + " " + password);
        return inStream.readUTF();
    }

    public int registration(String username, String password, String email) throws IOException {
        outStream.writeInt(102);
        outStream.writeUTF(username + " " + password + " " + email);
        return inStream.readInt();
    }

    public void sendFile(File file, String title) throws IOException {
        outStream.writeInt(103);
        outStream.writeUTF(title);
//        File file = new File(path);
        long length = file.length();
        byte[] bytes = new byte[8192];
        InputStream fileInput = new FileInputStream(file);
        int count;
        while ((count = fileInput.read(bytes)) > 0) {
            outStream.write(bytes, 0, count);

        }
        fileInput.close();
        reconnect();
    }



    public String[] getAllVideos() throws IOException {
        outStream.writeInt(104);
        String result = inStream.readUTF();
        String[] resultArr = result.split(";;");
        return resultArr;
    }



    public String getFile(String title) throws IOException {
        String path = "C:\\Users\\Khadgow\\IdeaProjects\\VideoNews\\src\\sample\\cache\\" + title + ".mp4";
        File saveFile = new File(path);

        if(saveFile.createNewFile()){
            outStream.writeInt(105);
            outStream.writeUTF(title);
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            byte[] bytes = new byte[8192];
            int count;
            while ((count = inStream.read(bytes)) > 0) {
                fileOut.write(bytes, 0, count);
            }
            fileOut.close();
            reconnect();
        }
        return path;
    }

    public int deleteVideo(String title) throws IOException {
        outStream.writeInt(106);
        outStream.writeUTF(title);
        return inStream.readInt();
    }

    public void disconnect() throws IOException {
        outStream.writeInt(2);
    }

}
