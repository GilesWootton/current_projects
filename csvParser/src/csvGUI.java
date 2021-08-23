/************************************************************
 * CSV reader/uploader program main/GUI
 * Opens local CSV file, diplays and uploads table to mySql
 * database. Also downloands and displays table from mysql sever
 *
 * @author Giles Wootton
 * @version 1.0
 * @since   2021-08-23
************************************************************/
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class csvGUI {

    private JButton Open;
    private JButton Download;
    private JButton Upload;
    private JTextField textField1;
    private JTextField TextField;
    private JButton setDelimeterButton;
    private JPanel guiPanel;
    private JButton selectButton;
    private JPanel printPanel;
    private JTextField textField2;
    private JComboBox comboBox1;
    private JButton downloadButton;
    private JButton updateButton;

    private Table tab;
    private Network net;

    public static void main(String[] args) {
        JFrame frame = new JFrame("csvGUI");
        frame.setContentPane(new csvGUI().guiPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public csvGUI() {

        tab = new Table();
        net = new Network();

        comboBox1.removeAllItems();
        net.populateAvailableTables();
        net = populateServerComboBox(net);

        Open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tab = new Table();
                tab.setDelim(TextField.getText());
                tab.readFile(textField1.getText());
                printTable(tab);
            }
        });

        Upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                net.uploadToServer(tab);
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(selectButton);

                File file = fc.getSelectedFile();
                String path = file.getPath();
                textField1.setText(path);
            }
        });
        setDelimeterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tab.setDelim(TextField.getText());
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox1.removeAllItems();
                net.populateAvailableTables();
                for (int x = 0; x < net.getDatabasesAvailable().size(); x++) {
                    comboBox1.addItem(net.getDatabasesAvailable().get(x));
                }
                comboBox1.revalidate();
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tab = new Table();
                tab.setDelim(TextField.getText());
                tab = net.downloadTable(comboBox1.getSelectedItem().toString());
                printTable(tab);
            }
        });
    }

    /***************************************************************
     * populateServerComboBox
     * Grabs names of tables, and displays in combobox
     * @param net        object with network settings
     * @return net       object with populated table names from server
     ***************************************************************/
    public Network populateServerComboBox(Network net) {

        for (int x = 0; x < net.getDatabasesAvailable().size(); x++) {
            comboBox1.addItem(net.getDatabasesAvailable().get(x));
        }
        comboBox1.revalidate();

        return net;
    }

    /***************************************************************
     * printTable
     * Adds current table object JTable to display
     * @param ent        object with table data
     ***************************************************************/
    public void printTable(Table ent) {

        JTable table = new JTable(tab.getData(), tab.getHeaders());
        table.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(table);

        this.printPanel.removeAll();
        this.printPanel.setLayout(new BoxLayout(this.printPanel, BoxLayout.PAGE_AXIS));
        this.printPanel.setAlignmentX(Component.LEFT_ALIGNMENT);//0.0)
        this.printPanel.add(sp);
        this.printPanel.revalidate();
    }
}