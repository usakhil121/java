import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class PhoneRecordApp implements ActionListener {

    JFrame frame;
    JTextField nameField, phoneField;
    JTextArea searchField, resultField;
    JButton saveButton, clrButton, showAllButton;
    JPanel panel;

    Font myFont = new Font("Helvetica", Font.BOLD, 20);
    Font resultFont = new Font("Helvetica", Font.PLAIN, 18);
    String directoryPath = System.getProperty("user.home") + "/Downloads/empmg";
    String filePath = directoryPath + "/contacts.csv";

    public void initUI() {
        frame = new JFrame("Phone Record Saver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(null);
        frame.setResizable(false);

        nameField = new JTextField();
        nameField.setBounds(18, 18, 350, 40);
        nameField.setFont(myFont);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createTitledBorder("Name"));

        phoneField = new JTextField();
        phoneField.setBounds(18, 70, 350, 40);
        phoneField.setFont(myFont);
        phoneField.setHorizontalAlignment(JTextField.CENTER);
        phoneField.setBorder(BorderFactory.createTitledBorder("Phone Number"));

        saveButton = new JButton("Save");
        saveButton.setBounds(18, 130, 160, 50);
        saveButton.setFont(myFont);
        saveButton.setFocusable(false);
        saveButton.addActionListener(this);
        saveButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        saveButton.setBorderPainted(false);
        saveButton.setContentAreaFilled(false);

        clrButton = new JButton("Clear");
        clrButton.setBounds(208, 130, 160, 50);
        clrButton.setFont(myFont);
        clrButton.setFocusable(false);
        clrButton.addActionListener(this);
        clrButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        clrButton.setBorderPainted(false);
        clrButton.setContentAreaFilled(false);

        searchField = new JTextArea();
        searchField.setBounds(18, 200, 360, 100);
        searchField.setFont(myFont);
        searchField.setLineWrap(true);
        searchField.setWrapStyleWord(true);
        searchField.setBorder(BorderFactory.createTitledBorder("Search by Name"));

        JScrollPane searchScrollPane = new JScrollPane(searchField);
        searchScrollPane.setBounds(18, 200, 360, 100);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            public void performSearch() {
                String name = searchField.getText();
                if (!name.isEmpty()) {
                    ArrayList<String> results = searchRecords(name);
                    displaySearchResults(results);
                } else {
                    resultField.setText("");
                }
            }
        });

        resultField = new JTextArea();
        resultField.setBounds(18, 310, 360, 100);
        resultField.setFont(resultFont);
        resultField.setLineWrap(true);
        resultField.setWrapStyleWord(true);
        resultField.setEditable(false);
        resultField.setBorder(BorderFactory.createTitledBorder("Search Result"));

        JScrollPane resultScrollPane = new JScrollPane(resultField);
        resultScrollPane.setBounds(18, 310, 360, 100);

        showAllButton = new JButton("Show All Contacts");
        showAllButton.setBounds(18, 420, 360, 50);
        showAllButton.setFont(myFont);
        showAllButton.setFocusable(false);
        showAllButton.addActionListener(this);
        showAllButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        showAllButton.setBorderPainted(false);
        showAllButton.setContentAreaFilled(false);

        frame.add(nameField);
        frame.add(phoneField);
        frame.add(saveButton);
        frame.add(clrButton);
        frame.add(searchScrollPane);
        frame.add(resultScrollPane);
        frame.add(showAllButton);
        frame.setVisible(true);

        createDirectoryAndFile();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            if (!name.isEmpty() && !phone.isEmpty()) {
                saveRecord(name, phone);
                nameField.setText("");
                phoneField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter both name and phone number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getSource() == clrButton) {
            nameField.setText("");
            phoneField.setText("");
            searchField.setText("");
            resultField.setText("");
        }
        if (e.getSource() == showAllButton) {
            displayAllContacts();
        }
    }

    private void createDirectoryAndFile() {
        Path dirPath = Paths.get(directoryPath);
        Path file = Paths.get(filePath);

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error creating directory or file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRecord(String name, String phone) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(name + "," + phone);
            writer.newLine();
            JOptionPane.showMessageDialog(frame, "Record saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving record", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ArrayList<String> searchRecords(String name) {
        ArrayList<String> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].toLowerCase().contains(name.toLowerCase())) {
                    results.add("Name: " + details[0] + ", Phone: " + details[1]);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error searching records", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (results.isEmpty()) {
            results.add("No record found");
        }
        return results;
    }

    private void displaySearchResults(ArrayList<String> results) {
        StringBuilder resultText = new StringBuilder();
        for (String result : results) {
            resultText.append(result).append("\n");
        }
        resultField.setText(resultText.toString());
    }

    private void displayAllContacts() {
        JFrame allContactsFrame = new JFrame("All Contacts");
        allContactsFrame.setSize(400, 500);
        allContactsFrame.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(resultFont);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder allContacts = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                allContacts.append(line).append("\n");
            }
            textArea.setText(allContacts.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error reading contacts", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        allContactsFrame.add(scrollPane, BorderLayout.CENTER);

        allContactsFrame.setVisible(true);
    }
}
