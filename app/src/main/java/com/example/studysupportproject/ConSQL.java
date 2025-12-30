package com.example.studysupportproject;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConSQL {
    private static final String TAG = "ConSQL";
    Connection con;
    @SuppressLint("NewApi")
    public Connection conclass()
    {
        String ip = "10.0.2.2", port = "1433", db = "Mobile_app", username="sa", password="password";

        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String ConnectURL = null;
        int maxRetries = 2;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                // Add connection timeout to avoid long waits
                ConnectURL = "jdbc:jtds:sqlserver://" + ip + ":" + port +
                        ";databasename=" + db +
                        ";user=" + username +
                        ";password=" + password +
                        ";loginTimeout=10;socketTimeout=10000";
                        
                con = DriverManager.getConnection(ConnectURL);

                if (con != null && !con.isClosed()) {
                    Log.i(TAG, "CONNECTION SUCCESSFUL");
                    Log.i(TAG, "Connected to: " + db);
                    return con;
                } else {
                    Log.e(TAG, "CONNECTION RETURNED NULL or CLOSED");
                    con = null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Connection attempt " + (retryCount + 1) + " failed: " + e.getMessage());
                con = null;
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(500); // Wait 500ms before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        Log.e(TAG, "Failed to establish connection after " + maxRetries + " attempts");
        return null;
    }

    /**
     * Execute SELECT query and return results as a List of Maps
     * @param query SQL SELECT query string
     * @return List of Maps where each Map represents a row with column names as keys
     */
    public List<Map<String, String>> executeQuery(String query) {
        List<Map<String, String>> resultList = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = conclass();
            if (con != null) {
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);

                // Get column metadata
                int columnCount = rs.getMetaData().getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = rs.getMetaData().getColumnName(i + 1);
                }

                // Process result set
                while (rs.next()) {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 0; i < columnCount; i++) {
                        String value = rs.getString(i + 1);
                        row.put(columnNames[i], value != null ? value : "");
                    }
                    resultList.add(row);
                }

                Log.i(TAG, "Query executed successfully. Rows returned: " + resultList.size());
            } else {
                Log.e(TAG, "Connection is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing query: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing resources: " + e.getMessage());
            }
        }

        return resultList;
    }

    /**
     * Execute INSERT, UPDATE, or DELETE query
     * @param query SQL INSERT/UPDATE/DELETE query string
     * @return true if query executed successfully, false otherwise
     */
    public boolean executeUpdate(String query) {
        Statement stmt = null;

        try {
            con = conclass();
            if (con != null) {
                stmt = con.createStatement();
                int rowsAffected = stmt.executeUpdate(query);
                Log.i(TAG, "Update executed successfully. Rows affected: " + rowsAffected);
                return rowsAffected > 0;
            } else {
                Log.e(TAG, "Connection is null");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error executing update: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error closing resources: " + e.getMessage());
            }
        }
    }
}
