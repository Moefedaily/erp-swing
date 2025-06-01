package main.java.model;

public class Inventory {
    private int prodId;
    private int quantityInStock;
    private int sales;
    
    private String productTitle;
    private double productPrice;
    private String categoryName;
    
    public Inventory() {}
    
    public Inventory(int prodId, int quantityInStock, int sales) {
        this.prodId = prodId;
        this.quantityInStock = quantityInStock;
        this.sales = sales;
    }
    
    public int getProdId() {
        return prodId;
    }
    
    public void setProdId(int prodId) {
        this.prodId = prodId;
    }
    
    public int getQuantityInStock() {
        return quantityInStock;
    }
    
    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
    
    public int getSales() {
        return sales;
    }
    
    public void setSales(int sales) {
        this.sales = sales;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public boolean isLowStock(int threshold) {
        return quantityInStock <= threshold;
    }
    
    public boolean isOutOfStock() {
        return quantityInStock <= 0;
    }
    
    @Override
    public String toString() {
        return String.format("Inventory{prodId=%d, title='%s', inStock=%d, sales=%d}", 
            prodId, productTitle, quantityInStock, sales);
    }
}