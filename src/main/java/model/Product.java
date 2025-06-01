package main.java.model;

public class Product {
    private int prodId;
    private int category;
    private String title;
    private String actor;
    private double price;
    private String categoryName; 
    
    public Product() {}
    
    public Product(int prodId, int category, String title, String actor, double price) {
        this.prodId = prodId;
        this.category = category;
        this.title = title;
        this.actor = actor;
        this.price = price;
    }
    
    public Product(int prodId, int category, String title, String actor, double price, String categoryName) {
        this.prodId = prodId;
        this.category = category;
        this.title = title;
        this.actor = actor;
        this.price = price;
        this.categoryName = categoryName;
    }
    
    public int getProdId() {
        return prodId;
    }
    
    public void setProdId(int prodId) {
        this.prodId = prodId;
    }
    
    public int getCategory() {
        return category;
    }
    
    public void setCategory(int category) {
        this.category = category;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getActor() {
        return actor;
    }
    
    public void setActor(String actor) {
        this.actor = actor;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, title='%s', actor='%s', price=%.2f, category='%s'}", 
            prodId, title, actor, price, categoryName != null ? categoryName : String.valueOf(category));
    }
}