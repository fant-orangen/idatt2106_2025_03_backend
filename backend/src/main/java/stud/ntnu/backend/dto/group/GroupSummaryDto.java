package stud.ntnu.backend.dto.group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupSummaryDto {
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
} 