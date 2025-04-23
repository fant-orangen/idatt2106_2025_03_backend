package stud.ntnu.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_places")
public class MeetingPlace {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Set createdAt before persist
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public MeetingPlace() {
    }
    
    public MeetingPlace(Household household, String name, BigDecimal latitude, BigDecimal longitude, User createdByUser) {
        this.household = household;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdByUser = createdByUser;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public User getCreatedByUser() {
        return createdByUser;
    }
    
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}