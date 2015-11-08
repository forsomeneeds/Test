package com.ayanson;

import java.util.ArrayList;
import java.util.Random;
import java.sql.*;

public class Sql {

    public static boolean fakeMode = true;
    private Random random = new Random();

    private Connection getConnection() {
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
        } catch (Exception e) {
            Main.logger.error("ERROR: failed to load HSQLDB JDBC driver.");
        }
        return  c;
    }

    public ArrayList<Item> getItems() {
        ArrayList<Item> result = new ArrayList<Item>();
        if (fakeMode) {
            //генерируем от 10 до 20 записей
            for (int i=0;i<random.nextInt(10)+11;i++) {
                result.add(new Item(i+1, random.nextInt(6)+1 ));
            }
        }
        else {
            Connection conn = null;
            Statement statement = null;

            try {
                conn = getConnection();
                statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select Item_Id,Group_Id from Queue");

                while(rs.next())
                    result.add(new Item(rs.getInt("Item_Id"), rs.getInt("Group_Id")));

                rs.close();
                conn.close();
            }
            catch (Exception ex) {
                Main.logger.error(ex.getMessage());

                try{
                    if(statement!=null)
                        conn.close();
                }catch(SQLException se){
                    Main.logger.error(se.getMessage());
                }

                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    Main.logger.error(se.getMessage());
                }
            }
        }

        return result;
    }

    public void  deleteItem(int itemId) {
        if (!fakeMode) {
            Connection conn = null;
            PreparedStatement statement = null;

            try {
                conn = getConnection();
                statement = conn.prepareStatement("delete from Queue where Item_Id=?");
                statement.setInt(1, itemId);

                statement.execute();

                conn.close();
            }
            catch (Exception ex) {
                Main.logger.error(ex.getMessage());

                try{
                    if(statement!=null)
                        conn.close();
                }catch(SQLException se){
                    Main.logger.error(se.getMessage());
                }

                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException se) {
                    Main.logger.error(se.getMessage());
                }
            }
        }
    }

}
