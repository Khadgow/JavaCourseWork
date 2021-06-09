package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Scanner;

class Server {
    public static LinkedList<NewClient> hostList = new LinkedList<NewClient>();
    public static void main(String[] args) {
        try {
            System.out.println("Server is running");
            int port = 3001;
            ServerSocket ss = new ServerSocket(port);

            Thread acceptor = new Thread(() -> {
                while (true) {
                    try {
                        Socket s = ss.accept();
                        NewClient p = new NewClient(s);
                        p.start();
                        System.out.println("\n-- CONNECTED --");
                        System.out.print("server > ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            acceptor.setDaemon(false);
            acceptor.setName("Server accept");
            acceptor.start();

            Thread console = new Thread(() -> {
                while (true) {
                    System.out.print("server > ");
                    String cmd = new Scanner(System.in).nextLine();
                    if (cmd.equals("clients")) {
                        System.out.println("---------------- CLIENTS ----------------");
                        for (NewClient host : hostList) {
                            System.out.println(", id: " + host.getid());
                        }
                        System.out.println("-----------------------------------------");
                    } else {
                        System.out.println("Unknown command. Available: clients");
                    }
                }
            });
            console.setDaemon(true);
            console.setName("Server console");
            console.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class NewClient extends Thread {
    private static int COUNT = 0;
    private int id;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private DatabaseHandler dbHandler;
    private Socket sock;


    public NewClient(Socket s) {
        sock = s;
        id = COUNT++;
        try {
            inStream = new DataInputStream(sock.getInputStream());
            outStream = new DataOutputStream(sock.getOutputStream());
            dbHandler = new DatabaseHandler();
            outStream.writeInt(id);
            Server.hostList.addLast(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NewClient constructor error");
        }

    }

    public int getid() {
        return id;
    }


    public void sendData(int data) {
        try {
            outStream.writeInt(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendData(String data) {
        try {
            outStream.writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            boolean exit = false;
            while (!exit) {
                int action = inStream.readInt();
                switch (action) {
                    case 101:
                        String userType = "";
                        String loginData = inStream.readUTF();
                        String[] loginDataArr = loginData.split(" ");
                        ResultSet loginResult = dbHandler.login(loginDataArr[0], loginDataArr[1]);
                        while (loginResult.next()){
                            userType = loginResult.getString("type");
                        }
                        outStream.writeUTF(userType);

                        break;
                    case 102:
                        String registrationData = inStream.readUTF();
                        String[] registrationDataArr = registrationData.split(" ");
                        int registrationResult = dbHandler.registration(registrationDataArr[0], registrationDataArr[1], registrationDataArr[2]);
                        System.out.println(registrationData);
                        outStream.writeInt(registrationResult);
                        break;
                    case 103:
                        String videoTitle = inStream.readUTF();
                        String path = "C:\\Users\\Khadgow\\IdeaProjects\\VideoNews\\src\\server\\videos\\" + videoTitle + ".mp4";
                        int addVideoIntoDBResult = dbHandler.addVideo(path,videoTitle);
                        if(addVideoIntoDBResult == 1){
                            File saveFile = new File(path);
                            if(saveFile.createNewFile()){
                                FileOutputStream fileOut = new FileOutputStream(saveFile);
                                byte[] bytes = new byte[8192];
                                int count;
                                while ((count = inStream.read(bytes)) > 0) {
                                    fileOut.write(bytes, 0, count);
                                }
                                fileOut.close();
                            }
                        }
                        outStream.writeInt(addVideoIntoDBResult);
                        break;
                    case 104:
                        String allVideosJSON = "";
                        ResultSet allVideosResult = dbHandler.getAllVideos();
                        while (allVideosResult.next()){
                            if(allVideosJSON.equals("")){
                                allVideosJSON += allVideosResult.getString("title");
                            } else {
                                allVideosJSON += ";;" + allVideosResult.getString("title");
                            }

                            allVideosJSON += ";;" + allVideosResult.getString("path");
                        }
                        outStream.writeUTF(allVideosJSON);
                        break;
                    case 105:

                        String videoTitleGet = inStream.readUTF();
                        ResultSet videoGetResult = dbHandler.getVideoByTitle(videoTitleGet);
                        String pathToGetFile = "";
                        while (videoGetResult.next()){
                            pathToGetFile = videoGetResult.getString("path");
                        }
                        if(!pathToGetFile.equals("")){
                            File getFile = new File(pathToGetFile);
                            long length = getFile.length();
                            byte[] bytes = new byte[8192];
                            InputStream fileInput = new FileInputStream(getFile);
                            int count;
                            while ((count = fileInput.read(bytes)) > 0) {
                                outStream.write(bytes, 0, count);
                            }
                            System.out.println("Before close");
                            fileInput.close();
                            outStream.close();
                            System.out.println("After close");
                        }
                        break;
                    case 106:
                        String videoTitleToDelete = inStream.readUTF();
                        String pathToDelete = "C:\\Users\\Khadgow\\IdeaProjects\\VideoNews\\src\\server\\videos\\" + videoTitleToDelete + ".mp4";
                        dbHandler.deleteVideoByTitle(videoTitleToDelete);
                        File fileToDelete = new File(pathToDelete);
                        if (fileToDelete.delete()){
                            outStream.writeInt(1);
                        } else {
                            outStream.writeInt(0);
                        }
                        break;
                    case 2:
                        exit = true;
                        break;
                }
            }
            Server.hostList.remove(this);
            inStream.close();
            outStream.close();
            sock.close();
            System.out.println("\n-- DISCONNECTED --");
            System.out.print("server > ");

        } catch (Exception e) {
            System.out.println("\n-- FORCE DISCONNECTED --");
            System.out.print("server > ");
            Server.hostList.remove(this);
        }

    }
}
