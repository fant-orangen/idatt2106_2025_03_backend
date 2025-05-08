package stud.ntnu.backend.dto.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an expiring product type.
 * This class is used to transfer data about product categories and their expiration times.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringProductTypeGetDto {
    
    /**
     * The category of the product.
     * Must not be null.
     */
    @NotNull(message = "Category must not be null")
    private String category;
    
    /**
     * The number of days until the product expires.
     * Must be a positive number.
     * Default value is 7 days.
     */
    @Positive(message = "Expiration time must be positive")
    private Integer expirationTimeInDays = 7;
}