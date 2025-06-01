package main.java.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import main.java.dao.*;
import main.java.model.*;

public class OrderHistoryView extends JFrame {
    
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JTable detailsTable;
    private DefaultTableModel detailsTableModel;
    private JComboBox<Customer> customerCombo;
    private JButton loadAllButton;
    private JLabel statusLabel;
    
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    
    public OrderHistoryView() {
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCustomers();
        loadAllOrders();
    }
    
    private void initializeComponents() {
        setTitle("Mini ERP - Order History");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        String[] orderColumns = {"Order ID", "Date", "Customer", "Net €", "Tax €", "Total €"};
        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setRowHeight(25);
        
        String[] detailColumns = {"Product", "Quantity", "Unit Price €", "Line Total €"};
        detailsTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        detailsTable = new JTable(detailsTableModel);
        detailsTable.setRowHeight(25);
        
        customerCombo = new JComboBox<>();
        customerCombo.setPreferredSize(new Dimension(300, 25));
        
        loadAllButton = new JButton("Load All Orders");
        loadAllButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loadAllButton.setBackground(new Color(70, 130, 180));
        
        statusLabel = new JLabel("Select a customer or load all orders");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.add(new JLabel("Customer:"));
        topPanel.add(customerCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(loadAllButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Orders"));
        ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        ordersPanel.setPreferredSize(new Dimension(1200, 350));
        
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));
        detailsPanel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);
        detailsPanel.setPreferredSize(new Dimension(1200, 250));
        
        mainPanel.add(ordersPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(statusLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        loadAllButton.addActionListener(e -> loadAllOrders());
        
        customerCombo.addActionListener(e -> {
            Customer selected = (Customer) customerCombo.getSelectedItem();
            if (selected != null) {
                loadCustomerOrders(selected.getCustomerId());
            }
        });
        
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadOrderDetails();
            }
        });
    }
    
    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        customerCombo.removeAllItems();
        customerCombo.addItem(null);
        for (Customer customer : customers) {
            customerCombo.addItem(customer);
        }
        statusLabel.setText("Loaded " + customers.size() + " customers");
    }
    
    private void loadAllOrders() {
        statusLabel.setText("Loading all orders...");
        statusLabel.setForeground(Color.BLUE);
        
        List<Order> orders = orderDAO.getAllOrders();
        updateOrdersTable(orders);
        customerCombo.setSelectedItem(null);
        
        statusLabel.setText("Loaded " + orders.size() + " orders");
        statusLabel.setForeground(new Color(0, 150, 0));
    }
    
    private void loadCustomerOrders(int customerId) {
        statusLabel.setText("Loading customer orders...");
        statusLabel.setForeground(Color.BLUE);
        
        List<Order> orders = orderDAO.getOrdersByCustomer(customerId);
        updateOrdersTable(orders);
        
        statusLabel.setText("Loaded " + orders.size() + " orders for customer");
        statusLabel.setForeground(new Color(0, 150, 0));
    }
    
    private void updateOrdersTable(List<Order> orders) {
        ordersTableModel.setRowCount(0);
        
        for (Order order : orders) {
            Object[] rowData = {
                order.getOrderId(),
                order.getOrderDate().toString(),
                order.getCustomerName(),
                String.format("%.2f", order.getNetAmount()),
                String.format("%.2f", order.getTax()),
                String.format("%.2f", order.getTotalAmount())
            };
            ordersTableModel.addRow(rowData);
        }
        
        detailsTableModel.setRowCount(0);
    }
    
    private void loadOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            detailsTableModel.setRowCount(0);
            return;
        }
        
        int orderId = (Integer) ordersTableModel.getValueAt(selectedRow, 0);
        Order order = orderDAO.getOrderWithDetails(orderId);
        
        if (order != null) {
            updateDetailsTable(order);
        }
    }
    
    private void updateDetailsTable(Order order) {
        detailsTableModel.setRowCount(0);
        
        for (OrderLine line : order.getOrderLines()) {
            Object[] rowData = {
                line.getProductTitle(),
                line.getQuantity(),
                String.format("%.2f", line.getProductPrice()),
                String.format("%.2f", line.getLineTotal())
            };
            detailsTableModel.addRow(rowData);
        }
    }
    
    static class CustomerComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Customer) {
                Customer customer = (Customer) value;
                setText(customer.getFirstname() + " " + customer.getLastname() + " - " + customer.getCity());
            } else if (value == null) {
                setText("Select a customer...");
            }
            return this;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            OrderHistoryView view = new OrderHistoryView();
            view.customerCombo.setRenderer(new CustomerComboRenderer());
            view.setVisible(true);
        });
    }
}