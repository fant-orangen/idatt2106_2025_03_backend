package stud.ntnu.backend.dto.map;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMeetingPlaceDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
} 