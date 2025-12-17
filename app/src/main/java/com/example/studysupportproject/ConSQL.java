package com.example.studysupportproject;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConSQL {
    Connection con;
    @SuppressLint("NewApi")
    public Connection conclass()
    {
        String ip = "10.0.2.2", port = "1433", db = "Mobile_app", username="sa", password="password";

        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String ConnectURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename="+ db + ";user=" + username + ";password=" + password + ";";
            con = DriverManager.getConnection(ConnectURL);
        } catch (Exception e) {
            Log.e("Error is ", e.getMessage());
        }
        return con;
    }
}
