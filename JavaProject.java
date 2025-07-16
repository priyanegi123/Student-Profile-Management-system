import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class Frame3 extends JFrame {
    JTextField t1, t2, t3, t4, t5, t6, t7;
    JButton b2, b3;
    JTextArea detailsArea;
    String mysqlUser;
    String mysqlPassword;

    public Frame3(String mysqlUser, String mysqlPassword) {
        super("STUDENT DETAILS");
        this.mysqlUser = mysqlUser;
        this.mysqlPassword = mysqlPassword;

        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(new Color(200, 200, 250));
        inputPanel.setBackground(new Color(200, 200, 250));
        //buttonPanel.setBackground(new Color(200, 200, 250));

        JLabel l1 = new JLabel("ENTER FIRST NAME ");
        inputPanel.add(l1);
        t1 = new JTextField();
        inputPanel.add(t1);

        JLabel l2 = new JLabel("ENTER LAST NAME ");
        inputPanel.add(l2);
        t2 = new JTextField();
        inputPanel.add(t2);

        JLabel l3 = new JLabel("SELECT GENDER ");
        inputPanel.add(l3);
        t5 = new JTextField();
        inputPanel.add(t5);

        JLabel l4 = new JLabel("ENTER BRANCH ");
        inputPanel.add(l4);
        t3 = new JTextField();
        inputPanel.add(t3);

        JLabel l5 = new JLabel("ENTER ADDRESS ");
        inputPanel.add(l5);
        t4 = new JTextField();
        inputPanel.add(t4);

        JLabel l6 = new JLabel("ENTER SERIAL NUMBER ");
        inputPanel.add(l6);
        t6 = new JTextField();
        inputPanel.add(t6);

        JLabel l7 = new JLabel("ENTER ID ");
        inputPanel.add(l7);
        t7 = new JTextField();
        inputPanel.add(t7);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        b2 = new JButton("SUBMIT DETAILS");
        buttonPanel.add(b2);
        b3 = new JButton("DELETE ENTRY");
        buttonPanel.add(b3);
        add(buttonPanel, BorderLayout.SOUTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        add(scrollPane, BorderLayout.EAST);

        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String firstName = t1.getText();
                String lastName = t2.getText();
                String gender = t5.getText();
                String branch = t3.getText();
                String address = t4.getText();
                String serialStr = t6.getText();
                String idStr = t7.getText();
                int serialNumber = serialStr.isEmpty() ? -1 : Integer.parseInt(serialStr);
                int id = idStr.isEmpty() ? -1 : Integer.parseInt(idStr);

                if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() || branch.isEmpty() || address.isEmpty() || serialNumber < 0 || id < 0) {
                    JOptionPane.showMessageDialog(Frame3.this, "Please Enter All Fields Correctly", "ALERT", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    displayDetails(firstName, lastName, gender, branch, address, serialNumber, id);
                    saveToDatabase(firstName, lastName, gender, branch, address, serialNumber, id);
                }
            }
        });

        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String serialStr = t6.getText();
                if (serialStr.isEmpty()) {
                    JOptionPane.showMessageDialog(Frame3.this, "Please Enter Serial Number to Delete", "ALERT", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int serialNumber = Integer.parseInt(serialStr);
                deleteFromDatabase(serialNumber);
            }
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displayDetails(String firstName, String lastName, String gender, String branch, String address, int serialNumber, int id) {
        String detailsMessage = "Entered Details:\n" +
                "First Name: " + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Gender: " + gender + "\n" +
                "Branch: " + branch + "\n" +
                "Address: " + address + "\n" +
                "Serial Number: " + serialNumber + "\n" +
                "ID: " + id;

        detailsArea.setText(detailsMessage);
    }

    private void saveToDatabase(String firstName, String lastName, String gender, String branch, String address, int serialNumber, int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentsdb", mysqlUser, mysqlPassword);

            String updateSerialsSQL = "UPDATE students SET serial_number = serial_number + 1 WHERE serial_number >= ?";
            PreparedStatement updateSerialsStmt = conn.prepareStatement(updateSerialsSQL);
            updateSerialsStmt.setInt(1, serialNumber);
            updateSerialsStmt.executeUpdate();

            String sql = "INSERT INTO students (serial_number, id, first_name, last_name, gender, branch, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, serialNumber);
            pstmt.setInt(2, id);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, gender);
            pstmt.setString(6, branch);
            pstmt.setString(7, address);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Details Submitted Successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Saving Details to Database: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFromDatabase(int serialNumber) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentsdb", mysqlUser, mysqlPassword);

            String deleteSQL = "DELETE FROM students WHERE serial_number = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
            deleteStmt.setInt(1, serialNumber);
            deleteStmt.executeUpdate();

            String updateSerialsSQL = "UPDATE students SET serial_number = serial_number - 1 WHERE serial_number > ?";
            PreparedStatement updateSerialsStmt = conn.prepareStatement(updateSerialsSQL);
            updateSerialsStmt.setInt(1, serialNumber);
            updateSerialsStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Entry Deleted Successfully!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error Deleting Entry from Database: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
class LoginFrame extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton b1;

    public LoginFrame() {
        super("LOGIN PAGE");
        setSize(800, 550);
        setResizable(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(200, 200, 250));

        ImageIcon icon = new ImageIcon("C:\\Users\\mlata\\OneDrive\\Documents\\Student Profile Mangement\\Student_Profile_Management\\Student_Profile_Management\\gehu-modified.png");
        Image image = icon.getImage();
        Image newimg = image.getScaledInstance(300, 280, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        imagePanel.add(imageLabel);
        imagePanel.setPreferredSize(new Dimension(800, 300));
        imagePanel.setBackground(new Color(200, 200, 250));

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(200, 200, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        loginPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(1);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        loginPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        loginPanel.add(passwordField, gbc);

        b1 = new JButton("Login");
        b1.setPreferredSize(new Dimension(300, 80));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(b1);
        buttonPanel.setPreferredSize(new Dimension(200, 100));
        buttonPanel.setBackground(new Color(200, 200, 250));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(imagePanel, BorderLayout.NORTH);
        centerPanel.add(loginPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.setBackground(new Color(200, 200, 250));

        add(centerPanel, BorderLayout.CENTER);

        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String mysqlUser = usernameField.getText();
                String mysqlPassword = new String(passwordField.getPassword());
                if (mysqlUser.isEmpty() || mysqlPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Please enter both username and password", "ALERT", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String dbURL = "jdbc:mysql://localhost:3306/studentsdb";
                    Connection conn = DriverManager.getConnection(dbURL, mysqlUser, mysqlPassword);
                    conn.close();


                    LoginFrame.this.setVisible(false);
                    new Frame3(mysqlUser, mysqlPassword);

                } catch (ClassNotFoundException | SQLException ex) {

                    JOptionPane.showMessageDialog(LoginFrame.this, "Error: Incorrect username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}