package main.java.model;

public class OrderLine {
    private int orderLineId;
    private int orderId;
    private int prodId;
    private int quantity;
    private String orderDate; 
    
    private String productTitle;
    private double productPrice;
    private double lineTotal;
    
    public OrderLine() {}
    
    public OrderLine(int prodId, int quantity, double productPrice) {
        this.prodId = prodId;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.lineTotal = quantity * productPrice;
    }
    
    public OrderLine(int prodId, String productTitle, int quantity, double productPrice) {
        this(prodId, quantity, productPrice);
        this.productTitle = productTitle;
    }
    
    public int getOrderLineId() {
        return orderLineId;
    }
    
    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getProdId() {
        return prodId;
    }
    
    public void setProdId(int prodId) {
        this.prodId = prodId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateLineTotal();
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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
        updateLineTotal();
    }
    
    public double getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }
    

    private void updateLineTotal() {
        this.lineTotal = this.quantity * this.productPrice;
    }
    
   
    public void calculateLineTotal() {
        updateLineTotal();
    }
    
    @Override
    public String toString() {
        return String.format("OrderLine{product='%s', qty=%d, price=%.2f€, total=%.2f€}", 
            productTitle != null ? productTitle : "Product #" + prodId, 
            quantity, productPrice, lineTotal);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderLine orderLine = (OrderLine) obj;
        return orderLineId == orderLine.orderLineId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(orderLineId);
    }
}