package com.baselukasz.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private Connection con;

    // Element wzorca Singleton
    private static DBConnection instance;

    /**
     * Prywatny konstruktor domysly (Singleton)
     */
    private DBConnection() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("db.properties"));
            String dburl = properties.getProperty("dburl");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            con = DriverManager.getConnection(dburl, user, password);

        } catch (SQLException e) {
            System.out.println("Problem z logowaniem\nProsze sprawdzic dane do bazy danych lub adres IP serwera");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

        } catch (FileNotFoundException e) {
            System.out.println("Problem z wczytaniem pliku z danymi");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Problem z wczytaniem pliku z danumi");
            e.printStackTrace();
        }
    }

    /**
     *
     * @return obiekt klasy DBConnection (Singleton)
     */
    public static DBConnection getInstance(){
        if( instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Metoda do pobierania danych z bazy
     *
     * @param query zapytanie SQL
     * @return wynik zapytania
     */
    public ResultSet load(String query){
        ResultSet rs = null;
        Statement st = null;

        try {
            st = con.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    /**
     * Metoda do zmiany wartosci w bazie danych
     *
     * @param update nowa wartosc
     * @return
     */
    public boolean update(String update){
        int rs = 0;
        Statement st = null;

        try {
            st = con.createStatement();
            rs = st.executeUpdate(update);
            if (rs > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
       return false;
    }

    /**
     * Metoda niszczaca polaczenie z baza danych
     *
     * @param bool
     * @param rs
     */
    public void disconnect(boolean bool, ResultSet rs){
        if(bool == true) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metoda niszczaca obiekt klasy ResultSet
     * @param rs
     */
    public void destroyRS(ResultSet rs){
        disconnect(false, rs);
    }
}
