package stud.ntnu.backend.repository.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stud.ntnu.backend.model.inventory.ProductBatch;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ProductBatch entity operations.
 */
@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Integer> {
  // Basic CRUD operations are provided by JpaRepository

  /**
   * Find all product batches for a given product type.
   *
   * @param productTypeId the ID of the product type
   * @param pageable      pagination information
   * @return a page of product batches
   */
  Page<ProductBatch> findByProductTypeId(Integer productTypeId, Pageable pageable);

  /**
   * Sum the total number of units for a given product type.
   *
   * @param productTypeId the ID of the product type
   * @return the total number of units
   */
  @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.id = :productTypeId")
  Integer sumNumberByProductTypeId(@Param("productTypeId") Integer productTypeId);

  /**
   * Sum the total number of litres for all product batches where the product type category is
   * 'water' and the unit is 'l'.
   *
   * @return the total number of litres
   */
  @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.category = 'water' AND pb.productType.unit = 'l'")
  Integer sumTotalLitresOfWater();

  /**
   * Sum the total number of litres for all product batches where the product type category is
   * 'water', unit is 'l', and household matches.
   *
   * @param householdId the ID of the household
   * @return the total number of litres
   */
  @Query("SELECT COALESCE(SUM(pb.number), 0) FROM ProductBatch pb WHERE pb.productType.category = 'water' AND pb.productType.unit = 'l' AND pb.productType.household.id = :householdId")
  Integer sumTotalLitresOfWaterByHousehold(@Param("householdId") Integer householdId);

  @Query("SELECT COALESCE(SUM(pb.number * pt.caloriesPerUnit), 0) FROM ProductBatch pb " +
      "JOIN pb.productType pt " +
      "WHERE pt.household.id = :householdId " +
      "AND pt.category = 'food'")
  Integer sumTotalCaloriesByHousehold(@Param("householdId") Integer householdId);

  /**
   * Find product batches that are about to expire (between now and future date). Includes all
   * product type and household information for efficient processing.
   *
   * @param fromDate the start date/time (now)
   * @param toDate   the end date/time (future)
   * @return a list of product batches
   */
  @Query("SELECT pb FROM ProductBatch pb JOIN FETCH pb.productType pt JOIN FETCH pt.household " +
      "WHERE pb.expirationTime > :fromDate AND pb.expirationTime < :toDate")
  List<ProductBatch> findExpiringBatches(@Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  /**
   * Find product batches that have already expired (before now). Includes all product type and
   * household information for efficient processing.
   *
   * @param referenceDate the reference date/time (now)
   * @return a list of product batches
   */
  @Query("SELECT pb FROM ProductBatch pb JOIN FETCH pb.productType pt JOIN FETCH pt.household " +
      "WHERE pb.expirationTime < :referenceDate")
  List<ProductBatch> findExpiredBatches(@Param("referenceDate") LocalDateTime referenceDate);

  /**
   * Find expiring product batches for a specific product type.
   *
   * @param productTypeId the ID of the product type
   * @param fromDate      the start date/time
   * @param toDate        the end date/time
   * @param pageable      pagination information
   * @return a page of product batches
   */
  @Query("SELECT pb FROM ProductBatch pb " +
      "WHERE pb.productType.id = :productTypeId " +
      "AND pb.expirationTime > :fromDate " +
      "AND pb.expirationTime < :toDate")
  Page<ProductBatch> findByProductTypeIdAndExpirationTimeBetween(
      @Param("productTypeId") Integer productTypeId,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);
}
