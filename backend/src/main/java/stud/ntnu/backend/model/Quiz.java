package stud.ntnu.backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

// TODO: Add JPA annotations (@Entity, @Table, @Id, etc.) when dependencies are available
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    private Long id;
    private String name;
    private String description;
    private String status = "active";
    private Long createdByUserId;
    private LocalDateTime createdAt = LocalDateTime.now();
} 