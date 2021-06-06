package server;

import java.sql.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" +dbPort + "/" + dbName;

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public int registration(String username, String password, String email) {

        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERNAME + "=? OR " + Const.EMAIL + "=?";
        int counter = 0;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, username);
            prSt.setString(2, email);
            resSet = prSt.executeQuery();
            while (resSet.next()){
                counter++;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(counter>=1){
            return 0;
        }


        String insert = "INSERT INTO " + Const.USERS_TABLE + "(" + Const.USERNAME + "," + Const.PASSWORD + "," + Const.EMAIL
             + "," + Const.TYPE + ")" + "VALUES(?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, username);
            prSt.setString(2, password);
            prSt.setString(3, email);
            prSt.setString(4, "user");
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

        public ResultSet login(String username, String password) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USERS_TABLE + " WHERE " + Const.USERNAME + "=? AND " + Const.PASSWORD + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, username);
            prSt.setString(2, password);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resSet;
    }

    public ResultSet getAllVideos() {

        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.VIDEOS_TABLE;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resSet;
    }

    public ResultSet getVideoByTitle(String title) {

        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.VIDEOS_TABLE + " WHERE " + Const.VIDEOS_TITLE + "=?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, title);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resSet;
    }

    public int addVideo(String path, String title) {

        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.VIDEOS_TABLE + " WHERE " + Const.VIDEOS_TITLE + "=?";
        int counter = 0;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, title);
            resSet = prSt.executeQuery();
            while (resSet.next()) {
                counter++;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (counter >= 1) {
            return 0;
        }

        String insert = "INSERT INTO " + Const.VIDEOS_TABLE + "(" + Const.VIDEOS_PATH + "," + Const.VIDEOS_TITLE + ")" + "VALUES(?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, path);
            prSt.setString(2, title);
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

        public void deleteVideoByTitle(String title) {
        String delete = "DELETE FROM " + Const.VIDEOS_TABLE + " WHERE " +Const.VIDEOS_TITLE + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(delete);
            prSt.setString(1, title);
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//
//    public ResultSet getAllRabbits() {
//        ResultSet resSet = null;
//        String select = "SELECT * FROM " + Const.RABBITS_TABLE;
//
//
//        try {
//            PreparedStatement prSt = getDbConnection().prepareStatement(select);
//
//            resSet = prSt.executeQuery();
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return resSet;
//    }
//
//    public ResultSet getRabbitsByType(String type) {
//        ResultSet resSet = null;
//        String select = "SELECT * FROM " + Const.RABBITS_TABLE + " WHERE " + Const.TYPE + "=?";
//
//        try {
//            PreparedStatement prSt = getDbConnection().prepareStatement(select);
//            prSt.setString(1, type);
//            resSet = prSt.executeQuery();
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return resSet;
//    }
//    public void deleteAllRabbits() {
//        String delete = "DELETE FROM " + Const.RABBITS_TABLE;
//
//        try {
//            PreparedStatement prSt = getDbConnection().prepareStatement(delete);
//            prSt.executeUpdate();
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void deleteRabbitsByType(String type) {
//        String delete = "DELETE FROM " + Const.RABBITS_TABLE + " WHERE " +Const.TYPE + "=?";
//
//        try {
//            PreparedStatement prSt = getDbConnection().prepareStatement(delete);
//            prSt.setString(1, type);
//            prSt.executeUpdate();
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}