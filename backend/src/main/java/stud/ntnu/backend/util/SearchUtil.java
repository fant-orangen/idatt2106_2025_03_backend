package stud.ntnu.backend.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Utility class for performing generic search operations across different entities. This
 * implementation assumes that entity class and column names are provided by developers, not by end
 * users, so the validation is primarily to catch developer errors.
 */
@Component
public class SearchUtil {

  private static final Logger log = LoggerFactory.getLogger(SearchUtil.class);

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Searches for entities where the specified column contains the search term. This method includes
   * security measures to prevent SQL injection by validating that the entity class is a valid JPA
   * entity and the column exists in that entity.
   *
   * @param entityClass The JPA entity class to search in (developer-provided, not user input)
   * @param columnName  The name of the column/field to search in (developer-provided, not user
   *                    input)
   * @param searchTerm  The term to search for (case-insensitive, can be user input)
   * @param <T>         The entity type
   * @return A list of matching entities
   * @throws IllegalArgumentException if the entity class or column name is invalid
   */
  public <T> List<T> searchByDescription(Class<T> entityClass, String columnName,
      String searchTerm) {
    // Validate inputs to prevent SQL injection
    validateEntityAndColumn(entityClass, columnName);

    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      return Collections.emptyList();
    }

    try {
      String jpql =
          "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE LOWER(e." + columnName
              + ") LIKE :searchTerm";
      Query query = entityManager.createQuery(jpql, entityClass);
      query.setParameter("searchTerm", "%" + searchTerm.toLowerCase() + "%");
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error performing search on entity {} with column {}: {}",
          entityClass.getSimpleName(), columnName, e.getMessage());
      return Collections.emptyList();
    }
  }

  /**
   * Searches for entities where the specified column contains the search term, with pagination
   * support.
   *
   * @param entityClass The JPA entity class to search in (developer-provided, not user input)
   * @param columnName  The name of the column/field to search in (developer-provided, not user
   *                    input)
   * @param searchTerm  The term to search for (case-insensitive, can be user input)
   * @param pageable    Pagination information
   * @param <T>         The entity type
   * @return A page of matching entities
   * @throws IllegalArgumentException if the entity class or column name is invalid
   */
  public <T> Page<T> searchByDescription(Class<T> entityClass, String columnName, String searchTerm,
      Pageable pageable) {
    // Validate inputs to prevent SQL injection
    validateEntityAndColumn(entityClass, columnName);

    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    try {
      // Count query for pagination
      String countJpql =
          "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE LOWER(e." + columnName
              + ") LIKE :searchTerm";
      Query countQuery = entityManager.createQuery(countJpql);
      countQuery.setParameter("searchTerm", "%" + searchTerm.toLowerCase() + "%");
      Long totalResults = (Long) countQuery.getSingleResult();

      // Main query with pagination
      String jpql =
          "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE LOWER(e." + columnName
              + ") LIKE :searchTerm";
      Query query = entityManager.createQuery(jpql, entityClass);
      query.setParameter("searchTerm", "%" + searchTerm.toLowerCase() + "%");

      // Apply pagination
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());

      List<T> results = query.getResultList();
      return new PageImpl<>(results, pageable, totalResults);
    } catch (Exception e) {
      log.error("Error performing paginated search on entity {} with column {}: {}",
          entityClass.getSimpleName(), columnName, e.getMessage());
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
  }

  /**
   * Validates that the provided entity class is a valid JPA entity and the column exists in that
   * entity. Since entity class and column names are developer-provided (not user input), this
   * validation primarily serves to catch developer errors rather than prevent SQL injection.
   *
   * @param entityClass The entity class to validate
   * @param columnName  The column name to validate
   * @param <T>         The entity type
   * @throws IllegalArgumentException if the entity class or column name is invalid
   */
  private <T> void validateEntityAndColumn(Class<T> entityClass, String columnName) {
    if (entityClass == null) {
      throw new IllegalArgumentException("Entity class cannot be null");
    }

    if (columnName == null || columnName.trim().isEmpty()) {
      throw new IllegalArgumentException("Column name cannot be null or empty");
    }

    // Validate that the entity class is a JPA entity
    Metamodel metamodel = entityManager.getMetamodel();
    try {
      EntityType<T> entityType = metamodel.entity(entityClass);

      // Validate that the column exists in the entity
      Set<SingularAttribute<? super T, ?>> attributes = entityType.getSingularAttributes();
      boolean columnExists = attributes.stream()
          .anyMatch(attr -> attr.getName().equals(columnName));

      if (!columnExists) {
        throw new IllegalArgumentException(
            "Column '" + columnName + "' does not exist in entity '" +
                entityClass.getSimpleName() + "'");
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid entity class: " + entityClass.getName(), e);
    }
  }
}
