package stud.ntnu.backend.dto.inventory;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for returning the preparedness status of a household.
 * <p>
 * This class encapsulates information about a household's inventory status, including:
 * <ul>
 *   <li>The household's identity (ID and name)</li>
 *   <li>Population count</li>
 *   <li>Survival days calculation</li>
 *   <li>Detailed breakdown of product types and their counts</li>
 *   <li>Status information for each product type</li>
 * </ul>
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
   * <p>
   * Key: Product type name Value: Current count of that product type
   * </p>
   */
  private Map<String, Integer> productTypeCounts;

  /**
   * A list of product type status objects, each representing the status of a specific product
   * type.
   * <p>
   * Each status object contains detailed information about the current count, recommended count,
   * and whether the amount is sufficient.
   * </p>
   */
  private List<ProductTypeStatusDto> productTypeStatus;

  /**
   * Default constructor.
   */
  public InventoryStatusDto() {
  }

  /**
   * Constructs a new InventoryStatusDto with all fields.
   *
   * @param householdId       the unique identifier of the household
   * @param householdName     the name of the household
   * @param populationCount   the number of people in the household
   * @param survivalDays      the number of days the household can survive with current inventory
   * @param productTypeCounts a map of product type names to their current counts
   * @param productTypeStatus a list of detailed status information for each product type
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
   * Data Transfer Object (DTO) for the status of a product type.
   * <p>
   * This class provides detailed information about a specific product type in the inventory,
   * including:
   * <ul>
   *   <li>Product type name</li>
   *   <li>Current count in inventory</li>
   *   <li>Recommended count</li>
   *   <li>Sufficiency status</li>
   * </ul>
   * </p>
   */
  @Setter
  @Getter
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
     * Constructs a new ProductTypeStatusDto with all fields.
     *
     * @param productTypeName  the name of the product type
     * @param count            the current count of the product type in inventory
     * @param recommendedCount the recommended count for the product type
     * @param sufficient       whether the current count meets or exceeds the recommended count
     */
    public ProductTypeStatusDto(String productTypeName, Integer count, Integer recommendedCount,
        Boolean sufficient) {
      this.productTypeName = productTypeName;
      this.count = count;
      this.recommendedCount = recommendedCount;
      this.sufficient = sufficient;
    }
  }
}