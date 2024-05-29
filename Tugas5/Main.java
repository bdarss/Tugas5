import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/example_db";
    private static final String USER = "root";
    private static final String PASS = "Billy";
    private static JTextField nameField;
    private static JTextField ageField;
    private static JTextField emailField;
    private static JTextField cityField;
    private static DefaultTableModel tableModel;
    private static JTable table;

    public static void main(String[] args) {
        // Create table in database if not exists
        createTable();

        // Create main frame
        JFrame frame = new JFrame("Tabel Data Member");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(null);

        // Create input form with null layout
        JLabel nameLabel = new JLabel("Nama:");
        nameLabel.setBounds(20, 20, 80, 25);
        frame.add(nameLabel);
        
        nameField = new JTextField();
        nameField.setBounds(100, 20, 165, 25);
        frame.add(nameField);
        
        JLabel ageLabel = new JLabel("Umur:");
        ageLabel.setBounds(20, 50, 80, 25);
        frame.add(ageLabel);
        
        ageField = new JTextField();
        ageField.setBounds(100, 50, 165, 25);
        frame.add(ageField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 80, 80, 25);
        frame.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(100, 80, 165, 25);
        frame.add(emailField);

        JLabel cityLabel = new JLabel("Kota Asal:");
        cityLabel.setBounds(20, 110, 80, 25);
        frame.add(cityLabel);
        
        cityField = new JTextField();
        cityField.setBounds(100, 110, 165, 25);
        frame.add(cityField);

        JButton saveButton = new JButton("Simpan");
        saveButton.setBounds(20, 140, 80, 25);
        saveButton.addActionListener(new SaveButtonListener());
        frame.add(saveButton);

        JButton updateButton = new JButton("Ubah");
        updateButton.setBounds(110, 140, 80, 25);
        updateButton.addActionListener(new UpdateButtonListener());
        frame.add(updateButton);

        JButton deleteButton = new JButton("Hapus");
        deleteButton.setBounds(200, 140, 80, 25);
        deleteButton.addActionListener(new DeleteButtonListener());
        frame.add(deleteButton);

        // Create table to display data
        String[] columnNames = {"ID", "Nama", "Umur", "Email", "Kota Asal"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 180, 740, 250);
        frame.add(scrollPane);

        // Display data in table
        displayData();

        frame.setVisible(true);
    }

    private static void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(100) NOT NULL, " +
                         "age INT NOT NULL, " +
                         "email VARCHAR(100) NOT NULL, " +
                         "city VARCHAR(100) NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveData(String name, int age, String email, String city) {
        String sql = "INSERT INTO users (name, age, email, city) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, email);
            pstmt.setString(4, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateData(int id, String name, int age, String email, String city) {
        String sql = "UPDATE users SET name = ?, age = ?, email = ?, city = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, email);
            pstmt.setString(4, city);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteData(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayData() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String email = rs.getString("email");
                String city = rs.getString("city");
                tableModel.addRow(new Object[]{id, name, age, email, city});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String ageText = ageField.getText();
            String email = emailField.getText();
            String city = cityField.getText();

            if (name.isEmpty() || ageText.isEmpty() || email.isEmpty() || city.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua kolom harus diisi.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Masukkan umur yang valid.");
                return;
            }

            saveData(name, age, email, city);
            tableModel.setRowCount(0);
            displayData();
        }
    }

    static class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = nameField.getText();
                String ageText = ageField.getText();
                String email = emailField.getText();
                String city = cityField.getText();

                if (name.isEmpty() || ageText.isEmpty() || email.isEmpty() || city.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Semua kolom harus diisi.");
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Masukkan umur yang valid.");
                    return;
                }

                updateData(id, name, age, email, city);
                tableModel.setRowCount(0); // Clear existing data
                displayData(); // Reload data
            } else {
                JOptionPane.showMessageDialog(null, "Pilih data yang ingin diubah");
            }
        }
    }

    static class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                deleteData(id);
                tableModel.setRowCount(0); // Clear existing data
                displayData(); // Reload data
            } else {
                JOptionPane.showMessageDialog(null, "Pilih data yang ingin dihapus");
            }
        }
    }
}
