package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Koneksi {
    Connection conn;
    Statement stat;
    public void config(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost/toko_db", "root", "");
            stat= conn.createStatement();
        } catch (ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Koneksi gagal"+e.getMessage());
        }        
    }
}

