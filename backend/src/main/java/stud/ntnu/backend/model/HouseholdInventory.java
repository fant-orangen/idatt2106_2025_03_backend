package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "household_inventory")
public class HouseholdInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(name = "custom_name")
    private String customName;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Set createdAt and updatedAt before persist
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    // Set updatedAt before update
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public HouseholdInventory() {
    }
    
    public HouseholdInventory(Household household, Product product, BigDecimal quantity) {
        this.household = household;
        this.product = product;
        this.quantity = quantity;
    }
    
    public HouseholdInventory(Household household, String customName, BigDecimal quantity) {
        this.household = household;
        this.customName = customName;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Household getHousehold() {
        return household;
    }
    
    public void setHousehold(Household household) {
        this.household = household;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public String getCustomName() {
        return customName;
    }
    
    public void setCustomName(String customName) {
        this.customName = customName;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}