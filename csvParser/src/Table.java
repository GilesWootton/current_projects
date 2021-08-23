/***********************************************************
 * Table class File
 * Creates table objects containing csv file data
 *
 *  @author  Giles Wootton
 *  @version 1.0
 *  @since   2021-08-23
 * ********************************************************/
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class Table {
    private String fileName = "";
    private Vector<Vector<String>> data = new Vector<Vector<String>>();
    private Vector<String> headers = new Vector<String>();
    private ArrayList<DataTypes> dataTypeArray = new ArrayList<>();
    private String delim;

    /**********************************************************************
     * readFile
     * reads csv file into "data" vector inside object
     * @param path         file path for csv input file
     *********************************************************************/

    public void readFile(String path) {

        String line = "";
        Scanner sc = null;
        File inFile = null;
        int index = 0;

        try {
            inFile = new File(path);
            sc = new Scanner(inFile, findCharSet(inFile));
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            e.printStackTrace();
        }

        this.fileName = inFile.getName();
        this.fileName = this.fileName.replaceAll
                ("(?<!^)[.].*", ""); // remove extension for table name

        while (sc.hasNextLine())   //populate data
        {
            line = sc.nextLine();
            String[] strArr = line.split
                    (delim + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            // separating commas only
            strArr = removeSpacesNum(strArr);
            data.add(new Vector<>());
            data.get(index).addAll(Arrays.asList(strArr));
            index++;
        }

        this.populateDataTypes();
        this.populateHeaders();
    }


    /****************************************************************************
     * removeSpacesNums
     * removes spaces and " from numbers currently stored as a string
     * @param strArr      Array of string containing mixture of numbers and words
     * @return strArr     Array with spaces and " removed from numbers
     ****************************************************************************/
    public String[] removeSpacesNum(String[] strArr) {

        for (int x = 0; x < strArr.length; x++) {
            if ((!strArr[x].matches(".*[A-Za-z].*"))) // doesn't contain any letters .: probably number
            {
                strArr[x] = strArr[x].replaceAll("[^0-9.]", "");
            }
        }
        return strArr;
    }

    /**************************************************************************
     * findCharSet
     * Find encoding for csv file. I don't like this and will e replaced
     * ... but it works...
     * @param inFile                    open inFile
     * @return charSet                  string with encoding type
     **************************************************************************/
    public String findCharSet(File inFile) {

        Scanner sc = null;
        String[] charSetTypes = new String[]
                {"US-ASCII", "UTF-8", "CP1252", "UTF-16", "CP1252"};
        String charSet = "CP1252";

        for (int x = 0; x < charSetTypes.length; x++) {
            sc = findCharSetHelper(inFile, charSetTypes[x]);
            if (sc != null) {
                if (sc.hasNextLine()) {
                    charSet = charSetTypes[x];
                    System.out.println(charSet);
                    break;
                }
            }
        }

        return charSet;
    }

    /**************************************************************************
     * findCharSetHelper
     * To avoid try block
     * @param inFile                open file
     * @param charSet               charset encoding
     * @return string with encoding type
     **************************************************************************/
    Scanner findCharSetHelper(File inFile, String charSet) {

        Scanner sc = null;

        try {
            sc = new Scanner(inFile, charSet);
            sc.hasNextLine();
        } catch (FileNotFoundException e) {
            System.out.println("helper error");
            e.printStackTrace();
            return null;
        }
        return sc;
    }

    /*************************************************************
     * populateHeaders
     * populates header array. Substitutes header for A,D,C,D,...
     * if header is not present
     *************************************************************/
    public void populateHeaders() {

        char ch = 'A';

        if (this.checkForHeader()) {
            for (int x = 0; x < data.get(0).size(); x++) {
                data.get(0).set(x, data.get(0).get(x).replaceAll
                        ("[^0-9,a-z,A-Z$_]", "_"));
                // Only valid characters for column name.
                headers.add(data.get(0).get(x));
            }
            data.remove(0);
        } else {
            for (int x = 0; x < data.get(0).size(); x++) {
                headers.add(Character.toString(ch));
                ch++;
            }
        }
    }

    /********************************************************************
     * CheckForHeader
     * Checks if header is present. populateHeders helper method
     * @return header - boolean equals true if header is present
     *******************************************************************/
    public boolean checkForHeader() {

        boolean header = false;
        for (int x = 0; x < data.get(0).get(x).length(); x++) // compares data types of rows
        {
            if (calcDT(data.get(0).get(x)) != this.dataTypeArray.get(x)) {
                header = true;
                break;
            }
        }
        return header;

    }

    /********************************************************************
     * populateDataTypes
     * getsDataTypes and stores in object
     ********************************************************************/
    void populateDataTypes() {

        for (int x = 0; x < data.get(1).size(); x++) {
            this.dataTypeArray.add(calcDT(data.get(1).get(x)));
        }
    }


    /*****************************************************************
     * calcDT
     * calculates dataType of single cell. populateDataTypes helper
     * @param str
     * @return results - string representing dataType
     *****************************************************************/

    DataTypes calcDT(String str) {
        DataTypes result = DataTypes.STRING;

        if (str.matches("-?\\d+?")) // is int
        {
            result = DataTypes.INT;
        } else if (str.matches("-?\\d+(\\.\\d+)?")) // is double
        {
            result = DataTypes.DOUBLE;
        } else if (str.length() == 1) // char
        {
            result = DataTypes.STRING;
        } else //string
        {
            result = DataTypes.STRING;
        }

        return result;
    }

    /*********************************************************
     * printName
     * prints filename for testing purposes
     **********************************************************/
    public void printName() {

        System.out.println("print name :" + fileName);
    }

    /**********************************************************
     * printData
     * prints data for testing purposes
     **********************************************************/
    public void printData() {

        System.out.println(this.fileName);

        for (int y = 0; y < data.size(); y++) {
            for (int x = 0; x < data.get(y).size(); x++) {
                System.out.print(data.get(y).get(x) + " ");
            }
            System.out.println();
        }
    }

    /**************************************************************
     * Setters and Getters
     **************************************************************/

    void setDelim(String delim) {
        this.delim = delim;
    }

    public void setHeaders(Vector<String> headers) {
        this.headers = headers;
    }

    public void setData(Vector<Vector<String>> data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public Vector<Vector<String>> getData() {
        return data;
    }

    public Vector<String> getHeaders() {
        return headers;
    }

    public ArrayList<DataTypes> getDataTypeArray() {
        return dataTypeArray;
    }

    public String getDelim() {
        return delim;
    }
}