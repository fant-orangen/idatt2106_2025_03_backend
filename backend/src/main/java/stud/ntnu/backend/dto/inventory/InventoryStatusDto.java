package stud.ntnu.backend.dto.inventory;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning the preparedness status of a household.
 */
@Setter
@Getter
public class InventoryStatusDto {

  // Getters and setters
  private Integer householdId;
  private String householdName;
  private Integer populationCount;
  private Integer survivalDays;
  private Map<String, Integer> productTypeCounts;
  private List<ProductTypeStatusDto> productTypeStatus;

  // Default constructor
  public InventoryStatusDto() {
  }

  // Constructor with all fields
  public InventoryStatusDto(Integer householdId, String householdName, Integer populationCount,
      Integer survivalDays, Map<String, Integer> productTypeCounts,
      List<ProductTypeStatusDto> productTypeStatus) {
    this.householdId = householdId;
    this.householdName = householdName;
    this.populationCount = populationCount;
    this.survivalDays = survivalDays;
    this.productTypeCounts = productTypeCounts;
    this.productTypeStatus = productTypeStatus;
  }

  /**
   * DTO for the status of a product type.
   */
  public static class ProductTypeStatusDto {

    private String productTypeName;
    private Integer count;
    private Integer recommendedCount;
    private Boolean sufficient;

    // Default constructor
    public ProductTypeStatusDto() {
    }

    // Constructor with all fields
    public ProductTypeStatusDto(String productTypeName, Integer count, Integer recommendedCount,
        Boolean sufficient) {
      this.productTypeName = productTypeName;
      this.count = count;
      this.recommendedCount = recommendedCount;
      this.sufficient = sufficient;
    }

    // Getters and setters
    public String getProductTypeName() {
      return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
      this.productTypeName = productTypeName;
    }

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }

    public Integer getRecommendedCount() {
      return recommendedCount;
    }

    public void setRecommendedCount(Integer recommendedCount) {
      this.recommendedCount = recommendedCount;
    }

    public Boolean getSufficient() {
      return sufficient;
    }

    public void setSufficient(Boolean sufficient) {
      this.sufficient = sufficient;
    }
  }
}