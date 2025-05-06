package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringProductTypeGetDto {
    
    @NotNull(message = "Category must not be null")
    private String category;
    
    @Positive(message = "Expiration time must be positive")
    private Integer expirationTimeInDays = 7;
} 