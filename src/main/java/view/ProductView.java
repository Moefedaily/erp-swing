package main.java.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.java.dao.ProductDAO;
import main.java.model.Product;
import main.java.model.Category;
import java.awt.*;
import java.util.List;

public class ProductView extends JFrame {
    
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JComboBox<Category> categoryComboBox;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton clearFilterButton;
    private JLabel statusLabel;
    private JLabel totalLabel;
    
    private ProductDAO productDAO;
    private List<Category> categories;
    
    public ProductView() {
        productDAO = new ProductDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadCategories();
    }
    
   
    private void initializeComponents() {
        setTitle("Mini ERP - Catalogue des Produits");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        String[] columnNames = {"ID", "Titre", "Acteur", "Prix (€)", "Catégorie"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(25);
        
        productTable.getColumnModel().getColumn(0).setPreferredWidth(60);  
        productTable.getColumnModel().getColumn(1).setPreferredWidth(300); 
        productTable.getColumnModel().getColumn(2).setPreferredWidth(200); 
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);  
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120); 
        
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setPreferredSize(new Dimension(150, 25));
        
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par titre de film");
        
        loadButton = new JButton("Charger tous les produits");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loadButton.setBackground(new Color(70, 130, 180));
        
        searchButton = new JButton("Rechercher");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 11));
        searchButton.setBackground(new Color(255, 140, 0));
        
        clearFilterButton = new JButton("Effacer filtres");
        clearFilterButton.setFont(new Font("Arial", Font.PLAIN, 11));
        
        statusLabel = new JLabel("Sélectionnez une catégorie ou cliquez sur 'Charger tous les produits'");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        totalLabel = new JLabel("");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalLabel.setForeground(new Color(0, 100, 0));
    }
    
   
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        topPanel.add(loadButton, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Catégorie :"), gbc);
        
        gbc.gridx = 1;
        topPanel.add(categoryComboBox, gbc);
        
        gbc.gridx = 2;
        topPanel.add(new JLabel("Recherche :"), gbc);
        
        gbc.gridx = 3;
        topPanel.add(searchField, gbc);
        
        gbc.gridx = 4;
        topPanel.add(searchButton, gbc);
        
        gbc.gridx = 5;
        topPanel.add(clearFilterButton, gbc);
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Catalogue des produits"));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(totalLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    
    private void setupEventListeners() {
        loadButton.addActionListener(e -> loadAllProducts());
        
        categoryComboBox.addActionListener(e -> {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory != null && selectedCategory.getCategoryId() != -1) {
                loadProductsByCategory(selectedCategory.getCategoryId());
            }
        });
        
        searchButton.addActionListener(e -> searchProducts());
        searchField.addActionListener(e -> searchProducts());
        clearFilterButton.addActionListener(e -> clearFilters());
    }
    
  
    private void loadCategories() {
        categoryComboBox.removeAllItems();
        
        categoryComboBox.addItem(new Category(-1, "Toutes les catégories"));
        
        categories = productDAO.getAllCategories();
        for (Category category : categories) {
            categoryComboBox.addItem(category);
        }
        
        System.out.println("ComboBox chargé avec " + categories.size() + " catégories");
    }
    

    private void loadAllProducts() {
        statusLabel.setText("Chargement de tous les produits...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productDAO.getAllProducts();
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateTable(products);
                    statusLabel.setText("Tous les produits chargés");
                    statusLabel.setForeground(new Color(0, 150, 0));
                    totalLabel.setText(products.size() + " produits affichés");
                } catch (Exception e) {
                    handleError("Erreur lors du chargement", e);
                }
            }
        };
        worker.execute();
    }
    
  
    private void loadProductsByCategory(int categoryId) {
        statusLabel.setText("Filtrage par catégorie...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productDAO.getProductsByCategory(categoryId);
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateTable(products);
                    statusLabel.setText("Produits filtrés par catégorie");
                    statusLabel.setForeground(new Color(0, 150, 0));
                    totalLabel.setText(products.size() + " produits trouvés");
                } catch (Exception e) {
                    handleError("Erreur lors du filtrage", e);
                }
            }
        };
        worker.execute();
    }
    
    private void searchProducts() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un terme de recherche", 
                "Recherche", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        statusLabel.setText("Recherche en cours...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productDAO.searchProductsByTitle(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    updateTable(products);
                    statusLabel.setText("Recherche terminée pour '" + searchTerm + "'");
                    statusLabel.setForeground(new Color(0, 150, 0));
                    totalLabel.setText(products.size() + " produits trouvés");
                } catch (Exception e) {
                    handleError("Erreur lors de la recherche", e);
                }
            }
        };
        worker.execute();
    }
    

    private void clearFilters() {
        categoryComboBox.setSelectedIndex(0);
        searchField.setText("");
        statusLabel.setText("Filtres effacés - Cliquez sur 'Charger tous les produits'");
        statusLabel.setForeground(Color.GRAY);
        totalLabel.setText("");
        tableModel.setRowCount(0);
    }
    

    private void updateTable(List<Product> products) {
        tableModel.setRowCount(0);
        
        for (Product product : products) {
            Object[] rowData = {
                product.getProdId(),
                product.getTitle(),
                product.getActor(),
                String.format("%.2f", product.getPrice()),
                product.getCategoryName()
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void handleError(String message, Exception e) {
        statusLabel.setText(message + ": " + e.getMessage());
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, message + ":\n" + e.getMessage(),
            "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
   
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ProductView().setVisible(true));
    }
}