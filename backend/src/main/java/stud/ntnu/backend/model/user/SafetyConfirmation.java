package stud.ntnu.backend.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "safety_confirmations")
@Getter
@Setter
@NoArgsConstructor
public class SafetyConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_safe", nullable = false)
    private Boolean isSafe;

    @Column(name = "safe_at", nullable = false)
    private LocalDateTime safeAt;

    public SafetyConfirmation(User user, Boolean isSafe, LocalDateTime safeAt) {
        this.user = user;
        this.isSafe = isSafe;
        this.safeAt = safeAt;
    }
} 