package main.java.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import main.java.dao.InventoryDAO;
import main.java.model.Inventory;

public class InventoryView extends JFrame {
    
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton loadAllButton;
    private JButton lowStockButton;
    private JButton restockButton;
    private JSpinner thresholdSpinner;
    private JSpinner restockSpinner;
    private JLabel statusLabel;
    private JLabel summaryLabel;
    
    private InventoryDAO inventoryDAO;
    
    public InventoryView() {
        inventoryDAO = new InventoryDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAllInventory();
    }
    
    private void initializeComponents() {
        setTitle("Mini ERP - Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        String[] columnNames = {"Product ID", "Product Title", "Category", "Price €", "In Stock", "Sales", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setRowHeight(25);
        
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        loadAllButton = new JButton("Load All Inventory");
        loadAllButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loadAllButton.setBackground(new Color(70, 130, 180));
        
        lowStockButton = new JButton("Show Low Stock");
        lowStockButton.setFont(new Font("Arial", Font.PLAIN, 12));
        lowStockButton.setBackground(new Color(220, 53, 69));
        lowStockButton.setForeground(Color.WHITE);
        
        restockButton = new JButton("Restock Selected");
        restockButton.setFont(new Font("Arial", Font.PLAIN, 12));
        restockButton.setBackground(new Color(40, 167, 69));
        restockButton.setForeground(Color.WHITE);
        
        thresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));
        thresholdSpinner.setPreferredSize(new Dimension(60, 25));
        
        restockSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 1000, 10));
        restockSpinner.setPreferredSize(new Dimension(60, 25));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
        
        summaryLabel = new JLabel("");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        summaryLabel.setForeground(new Color(0, 100, 0));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        topPanel.add(loadAllButton);
        topPanel.add(Box.createHorizontalStrut(20));
        
        topPanel.add(new JLabel("Low stock threshold:"));
        topPanel.add(thresholdSpinner);
        topPanel.add(lowStockButton);
        topPanel.add(Box.createHorizontalStrut(20));
        
        topPanel.add(new JLabel("Restock quantity:"));
        topPanel.add(restockSpinner);
        topPanel.add(restockButton);
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory"));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(summaryLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        loadAllButton.addActionListener(e -> loadAllInventory());
        lowStockButton.addActionListener(e -> loadLowStock());
        restockButton.addActionListener(e -> restockSelected());
    }
    
    private void loadAllInventory() {
        statusLabel.setText("Loading inventory...");
        statusLabel.setForeground(Color.BLUE);
        
        List<Inventory> inventories = inventoryDAO.getAllInventory();
        updateTable(inventories);
        
        long lowStockCount = inventories.stream().filter(inv -> inv.isLowStock(10)).count();
        long outOfStockCount = inventories.stream().filter(Inventory::isOutOfStock).count();
        
        statusLabel.setText("Inventory loaded");
        statusLabel.setForeground(new Color(0, 150, 0));
        
        summaryLabel.setText(String.format("Total: %d | Low Stock: %d | Out of Stock: %d", 
            inventories.size(), lowStockCount, outOfStockCount));
    }
    
    private void loadLowStock() {
        int threshold = (Integer) thresholdSpinner.getValue();
        
        statusLabel.setText("Loading low stock items...");
        statusLabel.setForeground(Color.BLUE);
        
        List<Inventory> lowStockItems = inventoryDAO.getLowStockProducts(threshold);
        updateTable(lowStockItems);
        
        long outOfStockCount = lowStockItems.stream().filter(Inventory::isOutOfStock).count();
        
        statusLabel.setText(String.format("Low stock items loaded (threshold: %d)", threshold));
        statusLabel.setForeground(Color.RED);
        
        summaryLabel.setText(String.format("Low Stock: %d | Out of Stock: %d", 
            lowStockItems.size(), outOfStockCount));
    }
    
    private void restockSelected() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to restock", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int prodId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String productTitle = (String) tableModel.getValueAt(selectedRow, 1);
        int currentStock = (Integer) tableModel.getValueAt(selectedRow, 4);
        int restockQuantity = (Integer) restockSpinner.getValue();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Restock product '%s' (ID: %d)\nCurrent stock: %d\nAdd: %d units\nNew stock: %d",
                productTitle, prodId, currentStock, restockQuantity, currentStock + restockQuantity),
            "Confirm Restock", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = inventoryDAO.restockProduct(prodId, restockQuantity);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Product restocked successfully!\n%s +%d units",
                            productTitle, restockQuantity),
                        "Restock Complete", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadAllInventory();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to restock product. Please try again.",
                        "Restock Failed", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error restocking product:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable(List<Inventory> inventories) {
        tableModel.setRowCount(0);
        
        for (Inventory inventory : inventories) {
            String status;
            if (inventory.isOutOfStock()) {
                status = "OUT OF STOCK";
            } else if (inventory.isLowStock(10)) {
                status = "LOW STOCK";
            } else {
                status = "OK";
            }
            
            Object[] rowData = {
                inventory.getProdId(),
                inventory.getProductTitle(),
                inventory.getCategoryName(),
                String.format("%.2f", inventory.getProductPrice()),
                inventory.getQuantityInStock(),
                inventory.getSales(),
                status
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
        
        SwingUtilities.invokeLater(() -> new InventoryView().setVisible(true));
    }
}