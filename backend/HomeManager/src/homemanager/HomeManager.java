/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homemanager;

import java.sql.*;
import java.util.Date;

/**
 *
 * @author Robin
 */
public class HomeManager {

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "asdfghjkl";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        // Variables tasks table
        String modus = null;
        Date nextExe = new Date();
        String name = null;
        
        // Variable schedule table
        
        int day = 5;
        Date time = new Date();
        
        Connection connection = null;
        Statement stmt = null;
        try {
            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            connection = DriverManager.getConnection("jdbc:mariadb://192.168.178.44/HomeManager", "root", "asdfghjkl");
            System.out.println("Connected database successfully...");
            
            //STEP 4: Execute a query
            System.out.println("Add values...");
            stmt = connection.createStatement();
            
            String tasks = "INSERT tasks "
                    + "(modus, nextExe, name) "
                    + "VALUES ('"+modus+"', '"+nextExe+"', '"+name+"')";
            
            String schedule = "INSERT schedule "
                    + "(day, time) "
                    + "VALUES ('"+day+"', '"+time+"')";
            
            stmt.executeUpdate(tasks);
            stmt.executeUpdate(schedule);
            
            System.out.println("Updated Values..");
            
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    connection.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try


    }
    
}
