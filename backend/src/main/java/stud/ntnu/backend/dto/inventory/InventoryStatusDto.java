package stud.ntnu.backend.dto.inventory;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning the preparedness status of a household.
 * <p>
 * This class encapsulates information about a household's inventory status,
 * including the household's identity, population, survival days, and details
 * about the types and counts of products in the inventory.
 * </p>
 */
@Setter
@Getter
public class InventoryStatusDto {

  /**
   * The unique identifier of the household.
   */
  private Integer householdId;

  /**
   * The name of the household.
   */
  private String householdName;

  /**
   * The number of people in the household.
   */
  private Integer populationCount;

  /**
   * The number of days the household can survive with the current inventory.
   */
  private Integer survivalDays;

  /**
   * A map containing the count of each product type in the household's inventory.
   * The key is the product type name, and the value is the count.
   */
  private Map<String, Integer> productTypeCounts;

  /**
   * A list of product type status objects, each representing the status of a specific product type.
   */
  private List<ProductTypeStatusDto> productTypeStatus;

  /**
   * Default constructor.
   */
  public InventoryStatusDto() {
  }

  /**
   * Constructor with all fields.
   *
   * @param householdId        the unique identifier of the household
   * @param householdName      the name of the household
   * @param populationCount    the number of people in the household
   * @param survivalDays       the number of days the household can survive
   * @param productTypeCounts  a map of product type names to their counts
   * @param productTypeStatus  a list of product type status DTOs
   */
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
   * <p>
   * This class provides details about a specific product type in the inventory,
   * including its name, current count, recommended count, and whether the amount is sufficient.
   * </p>
   */
  public static class ProductTypeStatusDto {

    /**
     * The name of the product type.
     */
    private String productTypeName;

    /**
     * The current count of this product type in the inventory.
     */
    private Integer count;

    /**
     * The recommended count for this product type.
     */
    private Integer recommendedCount;

    /**
     * Whether the current count is sufficient compared to the recommended count.
     */
    private Boolean sufficient;

    /**
     * Default constructor.
     */
    public ProductTypeStatusDto() {
    }

    /**
     * Constructor with all fields.
     *
     * @param productTypeName   the name of the product type
     * @param count             the current count of the product type
     * @param recommendedCount  the recommended count for the product type
     * @param sufficient        whether the current count is sufficient
     */
    public ProductTypeStatusDto(String productTypeName, Integer count, Integer recommendedCount,
        Boolean sufficient) {
      this.productTypeName = productTypeName;
      this.count = count;
      this.recommendedCount = recommendedCount;
      this.sufficient = sufficient;
    }

    /**
     * Gets the name of the product type.
     *
     * @return the product type name
     */
    public String getProductTypeName() {
      return productTypeName;
    }

    /**
     * Sets the name of the product type.
     *
     * @param productTypeName the product type name
     */
    public void setProductTypeName(String productTypeName) {
      this.productTypeName = productTypeName;
    }

    /**
     * Gets the current count of the product type.
     *
     * @return the count
     */
    public Integer getCount() {
      return count;
    }

    /**
     * Sets the current count of the product type.
     *
     * @param count the count
     */
    public void setCount(Integer count) {
      this.count = count;
    }

    /**
     * Gets the recommended count for the product type.
     *
     * @return the recommended count
     */
    public Integer getRecommendedCount() {
      return recommendedCount;
    }

    /**
     * Sets the recommended count for the product type.
     *
     * @param recommendedCount the recommended count
     */
    public void setRecommendedCount(Integer recommendedCount) {
      this.recommendedCount = recommendedCount;
    }

    /**
     * Gets whether the current count is sufficient.
     *
     * @return true if sufficient, false otherwise
     */
    public Boolean getSufficient() {
      return sufficient;
    }

    /**
     * Sets whether the current count is sufficient.
     *
     * @param sufficient true if sufficient, false otherwise
     */
    public void setSufficient(Boolean sufficient) {
      this.sufficient = sufficient;
    }
  }
}