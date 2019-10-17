package servlet;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://192.168.104.78:3306/inframind";

   // Database credentials
   static final String USER = "root";
   static final String PASS = "Dare2@hack";

   public ArrayList<ArrayList<String>> getData() {

      ArrayList<String> Continent = new ArrayList<String>();
      ArrayList<String> Region = new ArrayList<String>();
      ArrayList<String> LocalName = new ArrayList<String>();
      ArrayList<ArrayList<String>> DataCountry = new ArrayList<ArrayList<String>>();

      Connection conn = null;
      Statement stmt = null;
      try {
         // STEP 2: Register JDBC driver
         // Class.forName("com.mysql.jdbc.Driver");

         // STEP 3: Open a connection
         System.out.println("Connecting to database...");
         conn = DriverManager.getConnection(DB_URL, USER, PASS);

         // STEP 4: Execute a query
         System.out.println("Creating statement...");
         stmt = conn.createStatement();
         String sql;
         // sql = "SELECT id, first, last, age FROM Employees";
         sql = "SELECT * FROM country";
         ResultSet rs = stmt.executeQuery(sql);

         // STEP 5: Extract data from result set
         while (rs.next()) {

            String continent = rs.getString("continent");
            String region = rs.getString("region");
            String local_name = rs.getString("local_name");

            Continent.add(continent);
            Region.add(region);
            LocalName.add(local_name);

            // System.out.print("continent: " + continent + " ");
            // System.out.print("region: " + region + " ");
            // System.out.print("localo_name: " + local_name + " ");
            // System.out.println();
         }
         // STEP 6: Clean-up environment
         rs.close();
         stmt.close();
         conn.close();
      } catch (SQLException se) {
         // Handle errors for JDBC
         se.printStackTrace();
      } catch (Exception e) {
         // Handle errors for Class.forName
         e.printStackTrace();
      } finally {
         // finally block used to close resources
         try {
            if (stmt != null)
               stmt.close();
         } catch (SQLException se2) {
         } // nothing we can do
         try {
            if (conn != null)
               conn.close();
         } catch (SQLException se) {
            se.printStackTrace();
         } // end finally try
      } // end try
      System.out.println("Goodbye!");
      DataCountry.add(Continent);
      DataCountry.add(Region);
      DataCountry.add(LocalName);
      return DataCountry;

   }// end main
}// end FirstExample
