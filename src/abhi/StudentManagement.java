package abhi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentManagement {
    static Connection con;

    public static void main(String[] args) {
        connectDB();
        showLogin();
    }

    // ---------------- DB Connection ----------------
    static void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdb1", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DB Error: " + e.getMessage());
        }
    }

    // ---------------- Login UI ----------------
    static void showLogin() {
        JFrame f = new JFrame("Login");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 300);
        f.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(100, 50, 100, 30);

        JTextField userField = new JTextField();
        userField.setBounds(200, 50, 180, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(100, 100, 100, 30);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(200, 100, 180, 30);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 170, 80, 30);

        JButton signupBtn = new JButton("Signup");
        signupBtn.setBounds(250, 170, 80, 30);

        f.add(userLabel);
        f.add(userField);
        f.add(passLabel);
        f.add(passField);
        f.add(loginBtn);
        f.add(signupBtn);

        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setVisible(true);

        loginBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users1 WHERE username=? AND password=?"
                );
                ps.setString(1, userField.getText());
                ps.setString(2, new String(passField.getPassword()));
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    f.dispose();
                    showMenu();
                } else {
                    JOptionPane.showMessageDialog(f, "Invalid login");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });

        signupBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users1(username, password) VALUES(?, ?)"
                );
                ps.setString(1, userField.getText());
                ps.setString(2, new String(passField.getPassword()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(f, "Signup Successful");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });
    }


    // ---------------- Main Menu ----------------
    static void showMenu() {
        JFrame f = new JFrame("Dashboard");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] actions = {"Add", "View", "Search", "Update", "Delete", "Exit"};
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60)); // top, left, bottom, right

        for (String act : actions) {
            JButton b = new JButton(act + " Student");
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(200, 35)); // button size
            b.setFocusable(false);
            p.add(b);
            p.add(Box.createRigidArea(new Dimension(0, 15))); // vertical spacing

            switch (act) {
                case "Add"    -> b.addActionListener(e -> showAdd());
                case "View"   -> b.addActionListener(e -> showView());
                case "Search" -> b.addActionListener(e -> showSearch());
                case "Update" -> b.addActionListener(e -> showUpdate());
                case "Delete" -> b.addActionListener(e -> showDelete());
                case "Exit"   -> b.addActionListener(e -> System.exit(0));
            }
        }

        f.setContentPane(p);
        f.setSize(400, 350); // MEDIUM screen
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    // ---------------- Add Student ----------------
    static void showAdd() {
        JFrame f = new JFrame("Add Student");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField roll = new JTextField();
        JTextField name = new JTextField();
        JTextField course = new JTextField();
        JTextField sem = new JTextField();
        JTextField dob = new JTextField();
        JTextField fees = new JTextField();
        JButton save = new JButton("Save");

        JPanel p = new JPanel(new GridLayout(7, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addFields(p,
            "Roll No:", roll,
            "Name:", name,
            "Course:", course,
            "Semester:", sem,
            "DOB:", dob,
            "Fees:", fees
        );

        p.add(new JLabel());
        p.add(save);

        f.add(p);
        f.setSize(450, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        save.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO students1 VALUES(?,?,?,?,?,?)");
                ps.setInt(1, Integer.parseInt(roll.getText()));
                ps.setString(2, name.getText());
                ps.setString(3, course.getText());
                ps.setInt(4, Integer.parseInt(sem.getText()));
                ps.setString(5, dob.getText());
                ps.setDouble(6, Double.parseDouble(fees.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(f, "Student Added");
                f.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });
    }

    // ---------------- View Students ----------------
    static void showView() {
        JFrame f = new JFrame("All Students");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Roll No", "Name", "Course", "Semester", "DOB", "Fees"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM students1");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("roll_no"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getInt("semester"),
                    rs.getString("dob"),
                    rs.getDouble("fees")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(f, ex.getMessage());
        }

        f.add(new JScrollPane(table));
        f.setSize(700, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // ---------------- Search Student ----------------
    static void showSearch() {
        JFrame f = new JFrame("Search Student");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField roll = new JTextField();
        JTextArea result = new JTextArea();
        result.setEditable(false);
        JButton search = new JButton("Search");

        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        p.add(new JLabel("Roll No:")); 
        p.add(roll);
        p.add(new JLabel()); 
        p.add(search);

        f.add(p, BorderLayout.NORTH);
        f.add(new JScrollPane(result), BorderLayout.CENTER);
        f.setSize(450, 250);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        search.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM students1 WHERE roll_no=?");
                ps.setInt(1, Integer.parseInt(roll.getText()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    result.setText(
                        "Name: " + rs.getString("name") +
                        "\nCourse: " + rs.getString("course") +
                        "\nSemester: " + rs.getInt("semester") +
                        "\nDOB: " + rs.getString("dob") +
                        "\nFees: " + rs.getDouble("fees")
                    );
                } else {
                    result.setText("Student Not Found");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });
    }

    // ---------------- Update Student ----------------
    static void showUpdate() {
        JFrame f = new JFrame("Update Student");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(450, 350);
        f.setLocationRelativeTo(null);
        f.setLayout(null);

        JLabel rollLabel = new JLabel("Roll No:");
        JTextField rollField = new JTextField();
        JButton fetchBtn = new JButton("Fetch");

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel courseLabel = new JLabel("Course:");
        JTextField courseField = new JTextField();

        JLabel semLabel = new JLabel("Semester:");
        JTextField semField = new JTextField();

        JLabel dobLabel = new JLabel("DOB:");
        JTextField dobField = new JTextField();

        JLabel feesLabel = new JLabel("Fees:");
        JTextField feesField = new JTextField();

        JButton updateBtn = new JButton("Update");
        updateBtn.setEnabled(false); // Disable until data is fetched

        // Positioning
        int labelWidth = 80, fieldWidth = 200, height = 25, x1 = 30, x2 = 130;

        rollLabel.setBounds(x1, 20, labelWidth, height);
        rollField.setBounds(x2, 20, fieldWidth, height);
        fetchBtn.setBounds(x2 + fieldWidth + 10, 20, 80, height);

        nameLabel.setBounds(x1, 60, labelWidth, height);
        nameField.setBounds(x2, 60, fieldWidth, height);

        courseLabel.setBounds(x1, 100, labelWidth, height);
        courseField.setBounds(x2, 100, fieldWidth, height);

        semLabel.setBounds(x1, 140, labelWidth, height);
        semField.setBounds(x2, 140, fieldWidth, height);

        dobLabel.setBounds(x1, 180, labelWidth, height);
        dobField.setBounds(x2, 180, fieldWidth, height);

        feesLabel.setBounds(x1, 220, labelWidth, height);
        feesField.setBounds(x2, 220, fieldWidth, height);

        updateBtn.setBounds(170, 270, 100, 30);

        // Add components
        f.add(rollLabel); f.add(rollField); f.add(fetchBtn);
        f.add(nameLabel); f.add(nameField);
        f.add(courseLabel); f.add(courseField);
        f.add(semLabel); f.add(semField);
        f.add(dobLabel); f.add(dobField);
        f.add(feesLabel); f.add(feesField);
        f.add(updateBtn);

        // Disable all fields initially
        JTextField[] fields = {nameField, courseField, semField, dobField, feesField};
        for (JTextField field : fields) field.setEnabled(false);

        f.setVisible(true);

        // Fetch data when roll number is entered
        fetchBtn.addActionListener(e -> {
            try {
                int rollNo = Integer.parseInt(rollField.getText());
                PreparedStatement ps = con.prepareStatement("SELECT * FROM students1 WHERE roll_no = ?");
                ps.setInt(1, rollNo);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    courseField.setText(rs.getString("course"));
                    semField.setText(String.valueOf(rs.getInt("semester")));
                    dobField.setText(rs.getString("dob"));
                    feesField.setText(String.valueOf(rs.getDouble("fees")));

                    for (JTextField field : fields) field.setEnabled(true);
                    updateBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(f, "Student not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // Update logic
        updateBtn.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE students1 SET name=?, course=?, semester=?, dob=?, fees=? WHERE roll_no=?"
                );
                ps.setString(1, nameField.getText());
                ps.setString(2, courseField.getText());
                ps.setInt(3, Integer.parseInt(semField.getText()));
                ps.setString(4, dobField.getText());
                ps.setDouble(5, Double.parseDouble(feesField.getText()));
                ps.setInt(6, Integer.parseInt(rollField.getText()));

                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(f, rows > 0 ? "Student updated successfully." : "Update failed.");
                if (rows > 0) f.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });
    }

    // ---------------- Delete Student ----------------
    static void showDelete() {
        JFrame f = new JFrame("Delete Student");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField roll = new JTextField();
        JButton delete = new JButton("Delete");

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        p.add(new JLabel("Roll No:")); 
        p.add(roll);
        p.add(new JLabel()); 
        p.add(delete);

        f.add(p);
        f.setSize(400, 150);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        delete.addActionListener(e -> {
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM students1 WHERE roll_no=?");
                ps.setInt(1, Integer.parseInt(roll.getText()));
                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(f, rows > 0 ? "Deleted" : "Student Not Found");
                f.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, ex.getMessage());
            }
        });
    }


    // ---------------- Helper: Add Fields ----------------
    static void addFields(JPanel panel, Object... components) {
        for (int i = 0; i < components.length; i += 2) {
            panel.add(new JLabel((String) components[i]));
            panel.add((JComponent) components[i + 1]);
        }
    }
}