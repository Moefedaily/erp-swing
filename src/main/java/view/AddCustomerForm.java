package main.java.view;

import javax.swing.*;

import main.java.util.DatabaseManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCustomerForm extends JDialog {
    
    private JTextField firstnameField;
    private JTextField lastnameField;
    private JTextField address1Field;
    private JTextField address2Field;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField zipField;
    private JTextField countryField;
    private JTextField regionField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField creditcardtypeField;
    private JTextField creditcardField;
    private JTextField creditcardexpirationField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField ageField;
    private JTextField incomeField;
    private JComboBox<String> genderCombo;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Runnable onSuccessCallback;
    
    public AddCustomerForm(JFrame parent, Runnable onSuccessCallback) {
        super(parent, "Ajouter un nouveau client", true);
        this.onSuccessCallback = onSuccessCallback;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        setSize(500, 700);
        setLocationRelativeTo(parent);
    }
    
  
    private void initializeComponents() {
        firstnameField = new JTextField(20);
        lastnameField = new JTextField(20);
        address1Field = new JTextField(20);
        address2Field = new JTextField(20);
        cityField = new JTextField(20);
        stateField = new JTextField(20);
        zipField = new JTextField(20);
        countryField = new JTextField(20);
        regionField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        creditcardtypeField = new JTextField(20);
        creditcardField = new JTextField(20);
        creditcardexpirationField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        ageField = new JTextField(20);
        incomeField = new JTextField(20);
        
        genderCombo = new JComboBox<>(new String[]{"M", "F"});
        
        countryField.setText("US");
        regionField.setText("1");
        creditcardtypeField.setText("1");
        creditcardexpirationField.setText("2025/12");
        
        saveButton = new JButton("Enregistrer");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        addFormField(formPanel, gbc, row++, "Prénom *:", firstnameField);
        addFormField(formPanel, gbc, row++, "Nom *:", lastnameField);
        addFormField(formPanel, gbc, row++, "Email *:", emailField);
        addFormField(formPanel, gbc, row++, "Username *:", usernameField);
        addFormField(formPanel, gbc, row++, "Password *:", passwordField);
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        
        addFormField(formPanel, gbc, row++, "Adresse 1 *:", address1Field);
        addFormField(formPanel, gbc, row++, "Adresse 2:", address2Field);
        addFormField(formPanel, gbc, row++, "Ville *:", cityField);
        addFormField(formPanel, gbc, row++, "État:", stateField);
        addFormField(formPanel, gbc, row++, "Code postal:", zipField);
        addFormField(formPanel, gbc, row++, "Pays *:", countryField);
        addFormField(formPanel, gbc, row++, "Région *:", regionField);

        addFormField(formPanel, gbc, row++, "Téléphone:", phoneField);        
        addFormField(formPanel, gbc, row++, "Type carte *:", creditcardtypeField);
        addFormField(formPanel, gbc, row++, "N° carte *:", creditcardField);
        addFormField(formPanel, gbc, row++, "Expiration *:", creditcardexpirationField);
        
        addFormField(formPanel, gbc, row++, "Âge:", ageField);
        addFormField(formPanel, gbc, row++, "Revenus:", incomeField);
        addFormField(formPanel, gbc, row++, "Genre:", genderCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
   
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    
    private void setupEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    
    private boolean validateFields() {
        String[] requiredFields = {
            firstnameField.getText().trim(),
            lastnameField.getText().trim(),
            emailField.getText().trim(),
            usernameField.getText().trim(),
            new String(passwordField.getPassword()).trim(),
            address1Field.getText().trim(),
            cityField.getText().trim(),
            countryField.getText().trim(),
            regionField.getText().trim(),
            creditcardtypeField.getText().trim(),
            creditcardField.getText().trim(),
            creditcardexpirationField.getText().trim()
        };
        
        for (String field : requiredFields) {
            if (field.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez remplir tous les champs obligatoires (marqués par *).",
                    "Champs manquants", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
    
    private void saveCustomer() {
        if (!validateFields()) {
            return;
        }
        
        saveButton.setEnabled(false);
        saveButton.setText("Enregistrement...");
        
        try {
            Connection conn = DatabaseManager.getConnection();
            
            String sql = "{ ? = call new_customer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
            CallableStatement stmt = conn.prepareCall(sql);
            
            stmt.registerOutParameter(1, java.sql.Types.INTEGER);
            
            stmt.setString(2, firstnameField.getText().trim());
            stmt.setString(3, lastnameField.getText().trim());
            stmt.setString(4, address1Field.getText().trim());
            stmt.setString(5, address2Field.getText().trim());
            stmt.setString(6, cityField.getText().trim());
            stmt.setString(7, stateField.getText().trim());
            stmt.setInt(8, parseIntSafe(zipField.getText()));
            stmt.setString(9, countryField.getText().trim());
            stmt.setInt(10, parseIntSafe(regionField.getText()));
            stmt.setString(11, emailField.getText().trim());
            stmt.setString(12, phoneField.getText().trim());
            stmt.setInt(13, parseIntSafe(creditcardtypeField.getText()));
            stmt.setString(14, creditcardField.getText().trim());
            stmt.setString(15, creditcardexpirationField.getText().trim());
            stmt.setString(16, usernameField.getText().trim());
            stmt.setString(17, new String(passwordField.getPassword()).trim());
            stmt.setInt(18, parseIntSafe(ageField.getText()));
            stmt.setInt(19, parseIntSafe(incomeField.getText()));
            stmt.setString(20, (String) genderCombo.getSelectedItem());
            
            stmt.execute();
            
            int customerId = stmt.getInt(1);
            
            if (customerId > 0) {
                JOptionPane.showMessageDialog(this,
                    "Client ajouté avec succès !\nID du nouveau client : " + customerId,
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Le nom d'utilisateur existe déjà. Veuillez en choisir un autre.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout du client :\n" + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            saveButton.setEnabled(true);
            saveButton.setText("Enregistrer");
        }
    }
    
  
    private int parseIntSafe(String value) {
        try {
            return value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}