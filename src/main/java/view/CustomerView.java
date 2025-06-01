package main.java.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.java.dao.CustomerDAO;
import main.java.model.Customer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerView extends JFrame {
    
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JButton loadButton;
    private JButton refreshButton;
    private JButton addButton; 
    private JLabel statusLabel;
    
    private CustomerDAO customerDAO;
    
    public CustomerView() {
        customerDAO = new CustomerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
   
    private void initializeComponents() {
        setTitle("Mini ERP - Gestion des Clients");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        String[] columnNames = {"ID", "Prénom", "Nom", "Email", "Ville", "Adresse"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setRowHeight(25);
        
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);  
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(100); 
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(100); 
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        loadButton = new JButton("Charger clients");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loadButton.setBackground(new Color(70, 130, 180));
        
        refreshButton = new JButton("Actualiser");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(70, 130, 180));

        
        addButton = new JButton("Ajouter client");
        addButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addButton.setBackground(new Color(70, 130, 180)); 

        statusLabel = new JLabel("Cliquez sur 'Charger clients' pour afficher les données");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
    }
    
   
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(loadButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(refreshButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(addButton); 
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des clients"));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
   
    private void setupEventListeners() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomers();
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCustomers();
            }
        });
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddCustomerForm();
            }
        });
    }
    
 
    private void openAddCustomerForm() {
        AddCustomerForm form = new AddCustomerForm(this, new Runnable() {
            @Override
            public void run() {
                loadCustomers();
            }
        });
        form.setVisible(true);
    }
    
    
    private void loadCustomers() {
        statusLabel.setText("Chargement des clients en cours...");
        statusLabel.setForeground(Color.BLUE);
        
        loadButton.setEnabled(false);
        refreshButton.setEnabled(false);
        addButton.setEnabled(false); 
        
        SwingWorker<List<Customer>, Void> worker = new SwingWorker<List<Customer>, Void>() {
            @Override
            protected List<Customer> doInBackground() throws Exception {
                return customerDAO.getAllCustomers();
            }
            
            @Override
            protected void done() {
                try {
                    List<Customer> customers = get();
                    updateTable(customers);
                    
                    statusLabel.setText(customers.size() + " clients chargés avec succès");
                    statusLabel.setForeground(new Color(0, 150, 0));
                    
                } catch (Exception e) {
                    statusLabel.setText("Erreur lors du chargement: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    
                    JOptionPane.showMessageDialog(CustomerView.this, 
                        "Erreur lors du chargement des clients:\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loadButton.setEnabled(true);
                    refreshButton.setEnabled(true);
                    addButton.setEnabled(true); 
                }
            }
        };
        
        worker.execute();
    }
    
   
    private void updateTable(List<Customer> customers) {
        tableModel.setRowCount(0);
        
        for (Customer customer : customers) {
            Object[] rowData = {
                customer.getCustomerId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getCity(),
                customer.getAddress1()
            };
            tableModel.addRow(rowData);
        }
    }
    
   
    public static void main(String[] args) {
        try {
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CustomerView().setVisible(true);
            }
        });
    }
}