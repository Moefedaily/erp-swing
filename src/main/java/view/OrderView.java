package main.java.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import main.java.dao.*;
import main.java.model.*;

public class OrderView extends JFrame {
    
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    
    private JComboBox<Customer> customerCombo;
    private JButton refreshCustomersButton;
    
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JComboBox<Category> categoryCombo;
    private JTextField searchField;
    private JButton searchButton;
    
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JButton addToCartButton;
    private JButton removeFromCartButton;
    private JButton clearCartButton;
    private JSpinner quantitySpinner;
    
    private JLabel netAmountLabel;
    private JLabel taxLabel;
    private JLabel totalAmountLabel;
    private JButton createOrderButton;
    
    private JLabel statusLabel;
    
    private Order currentOrder;
    
    public OrderView() {
        customerDAO = new CustomerDAO();
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        currentOrder = new Order();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadInitialData();
    }
    
    private void initializeComponents() {
        setTitle("Mini ERP - Create New Order");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        customerCombo = new JComboBox<>();
        customerCombo.setPreferredSize(new Dimension(200, 25));
        refreshCustomersButton = new JButton("🔄");
        refreshCustomersButton.setPreferredSize(new Dimension(30, 25));
        
        String[] productColumns = {"ID", "Title", "Actor", "Price €", "Category"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        categoryCombo = new JComboBox<>();
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        
        String[] cartColumns = {"Product", "Price €", "Quantity", "Total €"};
        cartTableModel = new DefaultTableModel(cartColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        addToCartButton = new JButton("Add to Cart");
        removeFromCartButton = new JButton("Remove Item");
        clearCartButton = new JButton("Clear Cart");
        
        netAmountLabel = new JLabel("Net: 0.00€");
        taxLabel = new JLabel("Tax: 0.00€");
        totalAmountLabel = new JLabel("Total: 0.00€");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        createOrderButton = new JButton("CREATE ORDER");
        createOrderButton.setBackground(new Color(34, 139, 34));
        createOrderButton.setForeground(Color.WHITE);
        createOrderButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        statusLabel = new JLabel("Select a customer and add products to cart");
        statusLabel.setForeground(Color.GRAY);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.setBorder(BorderFactory.createTitledBorder("1. Select Customer"));
        customerPanel.add(new JLabel("Customer:"));
        customerPanel.add(customerCombo);
        customerPanel.add(refreshCustomersButton);
        
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("2. Select Products"));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        
        productPanel.add(filterPanel, BorderLayout.NORTH);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.add(new JLabel("Quantity:"));
        addPanel.add(quantitySpinner);
        addPanel.add(addToCartButton);
        productPanel.add(addPanel, BorderLayout.SOUTH);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("3. Shopping Cart"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        JPanel cartButtonPanel = new JPanel(new FlowLayout());
        cartButtonPanel.add(removeFromCartButton);
        cartButtonPanel.add(clearCartButton);
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("4. Order Summary"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(netAmountLabel, gbc);
        gbc.gridy = 1;
        summaryPanel.add(taxLabel, gbc);
        gbc.gridy = 2;
        summaryPanel.add(totalAmountLabel, gbc);
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        summaryPanel.add(createOrderButton, gbc);
        
        rightPanel.add(cartPanel, BorderLayout.CENTER);
        rightPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(productPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(customerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        productPanel.setPreferredSize(new Dimension(600, 600));
        rightPanel.setPreferredSize(new Dimension(400, 600));
    }
    
    private void setupEventListeners() {
        refreshCustomersButton.addActionListener(e -> loadCustomers());
        
        customerCombo.addActionListener(e -> {
            Customer selected = (Customer) customerCombo.getSelectedItem();
            if (selected != null) {
                currentOrder.setCustomerId(selected.getCustomerId());
                currentOrder.setCustomerName(selected.getFirstname() + " " + selected.getLastname());
                updateStatus("Customer selected: " + currentOrder.getCustomerName());
            }
        });
        
        categoryCombo.addActionListener(e -> {
            Category selected = (Category) categoryCombo.getSelectedItem();
            if (selected != null && selected.getCategoryId() != -1) {
                loadProductsByCategory(selected.getCategoryId());
            } else {
                loadAllProducts();
            }
        });
        
        searchButton.addActionListener(e -> searchProducts());
        searchField.addActionListener(e -> searchProducts());
        
        addToCartButton.addActionListener(e -> addToCart());
        removeFromCartButton.addActionListener(e -> removeFromCart());
        clearCartButton.addActionListener(e -> clearCart());
        
        createOrderButton.addActionListener(e -> createOrder());
    }
    
    private void loadInitialData() {
        loadCustomers();
        loadCategories();
        loadAllProducts();
    }
    
    private void loadCustomers() {
        SwingWorker<List<Customer>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Customer> doInBackground() {
                return customerDAO.getAllCustomers();
            }
            
            @Override
            protected void done() {
                try {
                    List<Customer> customers = get();
                    customerCombo.removeAllItems();
                    customerCombo.addItem(null); 
                    for (Customer customer : customers) {
                        customerCombo.addItem(customer);
                    }
                    updateStatus("Loaded " + customers.size() + " customers");
                } catch (Exception e) {
                    updateStatus("Error loading customers: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadCategories() {
        List<Category> categories = productDAO.getAllCategories();
        categoryCombo.removeAllItems();
        categoryCombo.addItem(new Category(-1, "All Categories"));
        for (Category category : categories) {
            categoryCombo.addItem(category);
        }
    }
    
    private void loadAllProducts() {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() {
                return productDAO.getAllProducts();
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateProductTable(products);
                } catch (Exception e) {
                    updateStatus("Error loading products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadProductsByCategory(int categoryId) {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() {
                return productDAO.getProductsByCategory(categoryId);
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateProductTable(products);
                } catch (Exception e) {
                    updateStatus("Error loading products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void searchProducts() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllProducts();
            return;
        }
        
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() {
                return productDAO.searchProductsByTitle(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateProductTable(products);
                    updateStatus("Found " + products.size() + " products for '" + searchTerm + "'");
                } catch (Exception e) {
                    updateStatus("Error searching products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void updateProductTable(List<Product> products) {
        productTableModel.setRowCount(0);
        for (Product product : products) {
            Object[] row = {
                product.getProdId(),
                product.getTitle(),
                product.getActor(),
                String.format("%.2f", product.getPrice()),
                product.getCategoryName()
            };
            productTableModel.addRow(row);
        }
    }
    
    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int prodId = (Integer) productTableModel.getValueAt(selectedRow, 0);
        String title = (String) productTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) productTableModel.getValueAt(selectedRow, 3);
        double price = Double.parseDouble(priceStr);
        int quantity = (Integer) quantitySpinner.getValue();
        
        OrderLine orderLine = new OrderLine(prodId, title, quantity, price);
        currentOrder.addOrderLine(orderLine);
        
        updateCartTable();
        updateOrderSummary();
        
        updateStatus("Added " + quantity + "x " + title + " to cart");
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        OrderLine removedLine = currentOrder.getOrderLines().get(selectedRow);
        currentOrder.removeOrderLine(removedLine);
        
        updateCartTable();
        updateOrderSummary();
        
        updateStatus("Removed " + removedLine.getProductTitle() + " from cart");
    }
    
    private void clearCart() {
        currentOrder.clearOrderLines();
        updateCartTable();
        updateOrderSummary();
        updateStatus("Cart cleared");
    }
    
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        for (OrderLine line : currentOrder.getOrderLines()) {
            Object[] row = {
                line.getProductTitle(),
                String.format("%.2f", line.getProductPrice()),
                line.getQuantity(),
                String.format("%.2f", line.getLineTotal())
            };
            cartTableModel.addRow(row);
        }
    }
    
    private void updateOrderSummary() {
        netAmountLabel.setText(String.format("Net: %.2f€", currentOrder.getNetAmount()));
        taxLabel.setText(String.format("Tax: %.2f€", currentOrder.getTax()));
        totalAmountLabel.setText(String.format("Total: %.2f€", currentOrder.getTotalAmount()));
        
        boolean canCreateOrder = currentOrder.getCustomerId() > 0 && !currentOrder.getOrderLines().isEmpty();
        createOrderButton.setEnabled(canCreateOrder);
    }
    
    private void createOrder() {
        if (currentOrder.getCustomerId() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer first", "No Customer", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentOrder.getOrderLines().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add products to cart first", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentOrder.ensureOrderDate();
        
        createOrderButton.setEnabled(false);
        createOrderButton.setText("Creating...");
        
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return orderDAO.createOrder(currentOrder);
            }
            
            @Override
            protected void done() {
                try {
                    int orderId = get();
                    JOptionPane.showMessageDialog(OrderView.this,
                        "Order created successfully!\nOrder ID: " + orderId +
                        "\nTotal: " + String.format("%.2f€", currentOrder.getTotalAmount()),
                        "Order Created", JOptionPane.INFORMATION_MESSAGE);
                    
                    currentOrder = new Order();
                    customerCombo.setSelectedIndex(0);
                    clearCart();
                    updateStatus("Order #" + orderId + " created successfully!");
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(OrderView.this,
                        "Error creating order:\n" + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    updateStatus("Error creating order: " + e.getMessage());
                    e.printStackTrace(); 
                } finally {
                    createOrderButton.setEnabled(true);
                    createOrderButton.setText("CREATE ORDER");
                }
            }
        };
        worker.execute();
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(message.toLowerCase().contains("error") ? Color.RED : Color.BLUE);
    }
    
    static class CustomerComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Customer) {
                Customer customer = (Customer) value;
                setText(customer.getFirstname() + " " + customer.getLastname() + " (" + customer.getEmail() + ")");
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
            OrderView view = new OrderView();
            view.customerCombo.setRenderer(new CustomerComboRenderer());
            view.setVisible(true);
        });
    }
}