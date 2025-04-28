package stud.ntnu.backend.model.householdAdmin;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class HouseholdAdminId implements Serializable {
    private Integer userId;
    private Integer householdId;
} 