/***********************************************************
 * network class File
 * Handles network settings, upload/downloads for
 * csv reader program
 *
 *  @author  Giles Wootton
 *  @version 1.0
 *  @since   2021-08-23
 * ********************************************************/
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class Network {
    private String databaseURL;
    private ArrayList<String> databasesAvailable;
    private String username;
    private String password;

    /*******************************************************
     * Network Constructor
     * populates object with default values
     *******************************************************/
    public Network() {
        databaseURL = "jdbc:mysql://localhost:3306/mydb";
        username = "root";
        password = "";
        databasesAvailable = new ArrayList<String>();
    }


    /**************************************************************
     * populateAvailableTables
     * Gets available tables stored on server for GUI comboBox
     *
     **************************************************************/
    public void populateAvailableTables() {

        Connection connection = null;
        databasesAvailable.clear();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(databaseURL, username, password);
            connection.setAutoCommit(false);

            String[] types = {"TABLE"};
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Show tables");
            while (rs.next())                 // populating table names
            {
                databasesAvailable.add(rs.getString(1));
            }

            connection.commit();
            connection.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    /************************************************************
     * getSQLInsertString
     * creates mysql "insert" string in format shown below.
     * "INSERT INTO username
     *         (Username, _Identifier, First_name,
     *         Last_name) VALUES (?,?,?,?);");
     * @return
     ************************************************************/
    public String getSQLInsertString(Table tab) {

        StringBuilder strB = new StringBuilder();

        strB.append("INSERT INTO " + tab.getFileName() + " (`");
        for (int x = 0; x < tab.getData().get(0).size(); x++) {
            strB.append(tab.getHeaders().get(x));
            if (tab.getData().get(0).size() != x + 1) {
                strB.append("` , `");
            }
        }

        strB.append("` ) VALUES (");
        for (int x = 0; x < tab.getData().get(0).size(); x++) {
            strB.append("?");
            if (tab.getData().get(0).size() != x + 1) {
                strB.append(", ");
            }
        }
        strB.append(");\n");

        return strB.toString();
    }

    /************************************************************
     * getSQLCreateString
     * creates mysql "create" string in format shown below.
     * strB.append("CREATE TABLE username(Username VARCHAR(255),
     *         _Identifier VARCHAR(255), First_name VARCHAR(255),
     *         Last_name VARCHAR(255));\n");
     * @return
     **********************************************************/
    public String getSQLCreateString(Table tab) {

        StringBuilder strB = new StringBuilder();

        strB.append("CREATE TABLE `" + tab.getFileName() + "` (");
        for (int x = 0; x < tab.getData().get(0).size(); x++) {
            strB.append("`" + tab.getHeaders().get(x) + "` " +
                    tab.getDataTypeArray().get(x).dataTypeName);
            // " ' " added to ignore reserved words
            if (tab.getData().get(0).size() != x + 1) {
                strB.append(", ");
            } else {
                strB.append(");\n");
            }
        }
        return strB.toString();
    }


    /*************************************************************
     * uploadToServer
     * uploads csv as table in mySql database. Messy...needs work
     *
     ************************************************************/
    public void uploadToServer(Table tab) {

        String url = "jdbc:mysql://localhost:3306/mydb";
        String username = "root";
        String password = "SPIC3Ysausage";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        Statement createStatement = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);

            createStatement = connection.createStatement();
            createStatement.executeUpdate(getSQLCreateString(tab));
            for (int y = 0; y < tab.getData().size(); y++)       //insert data into mySql prepared statement.
            {
                insertStatement = connection.prepareStatement(getSQLInsertString(tab));
                for (int x = 0; x < tab.getData().get(y).size(); x++) {
                    try// need to fix formatting.
                    {
                        switch (tab.getDataTypeArray().get(x))      // convert to correct dataType
                        {
                            case INT:
                                insertStatement.setInt
                                        (x + 1, Integer.parseInt(tab.getData().get(y).get(x)));
                                break;
                            case DOUBLE:
                                insertStatement.setDouble
                                        (x + 1, Double.parseDouble(tab.getData().get(y).get(x)));
                                break;
                            case STRING:
                                insertStatement.setString
                                        (x + 1, tab.getData().get(y).get(x));
                                break;
                        }
                    } catch (Exception e) {
                        insertStatement.setNull(x + 1, Types.NULL);
                    }

                }
                insertStatement.addBatch();
                insertStatement.executeBatch();
            }

            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /****************************************************************
     * downloadTable
     * downloads table data and creates table object
     * @param tableName        name of table to be downloaded
     * @return Table object from downloded data
     ****************************************************************/

    public Table downloadTable(String tableName) {

        Table tab = new Table();
        ResultSet resultSet = null;
        Connection connection = null;
        Statement statement = null;
        ResultSetMetaData meta = null;
        Vector<Vector<String>> tempData = null;
        Vector<String> tempHeaders = null;
        int index = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(databaseURL, username, password);
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            meta = resultSet.getMetaData();

            tempHeaders = new Vector<>();
            for (int x = 0; x < meta.getColumnCount(); x++) //get headers
            {
                tempHeaders.add(meta.getColumnName(x + 1));
            }
            tab.setHeaders(tempHeaders);

            tempData = new Vector<>();
            while (resultSet.next())  // get data
            {
                Vector<String> vString = new Vector<String>();
                vString.addElement(resultSet.getString(tab.getHeaders().get(index)));
                tempData.add(vString);
                index++;
            }
            tab.setData(tempData);

            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tab;
    }

    /*****************************************************
     *Getters and Setters
     *****************************************************/

    public void setDatabaseURL(String url) {
        databaseURL = url;
    }

    public void setUsername(String name) {
        username = name;
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public ArrayList<String> getDatabasesAvailable() {
        return databasesAvailable;
    }

}




