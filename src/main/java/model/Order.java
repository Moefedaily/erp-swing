package main.java.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private LocalDate orderDate;
    private double netAmount;
    private double tax;
    private double totalAmount;
    
    private String customerName;
    private List<OrderLine> orderLines;
    
    public Order() {
        this.orderLines = new ArrayList<>();
    }
    
    public Order(int customerId) {
        this();
        this.customerId = customerId;
        this.orderDate = LocalDate.now();
    }
    
    public void ensureOrderDate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public double getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }
    
    public double getTax() {
        return tax;
    }
    
    public void setTax(double tax) {
        this.tax = tax;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public List<OrderLine> getOrderLines() {
        return orderLines;
    }
    
    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }
    
    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        calculateTotals();
    }
    
    public void removeOrderLine(OrderLine orderLine) {
        this.orderLines.remove(orderLine);
        calculateTotals();
    }
    
    public void clearOrderLines() {
        this.orderLines.clear();
        calculateTotals();
    }
    
    
    public void calculateTotals() {
        this.netAmount = orderLines.stream()
            .mapToDouble(OrderLine::getLineTotal)
            .sum();
        
        this.tax = this.netAmount * 0.0825;
        this.totalAmount = this.netAmount + this.tax;
    }
    
    public int getTotalItems() {
        return orderLines.stream()
            .mapToInt(OrderLine::getQuantity)
            .sum();
    }
    
    @Override
    public String toString() {
        return String.format("Order{id=%d, customer=%s, items=%d, total=%.2f€}", 
            orderId, customerName, getTotalItems(), totalAmount);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return orderId == order.orderId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(orderId);
    }
}