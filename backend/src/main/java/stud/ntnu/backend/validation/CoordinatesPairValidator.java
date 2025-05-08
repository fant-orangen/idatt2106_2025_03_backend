package stud.ntnu.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Validator implementation for the {@link CoordinatesPair} annotation.
 * <p>
 * This validator ensures that coordinate pairs (latitude and longitude) are provided together.
 * The validation rules are:
 * <ul>
 *   <li>If latitude is provided, longitude must also be provided</li>
 *   <li>If longitude is provided, latitude must also be provided</li>
 *   <li>Both coordinates can be absent</li>
 *   <li>Neither coordinate can be present individually</li>
 * </ul>
 *
 * @param <CoordinatesPair> the annotation type
 * @param <Object> the type of object being validated
 */
public class CoordinatesPairValidator implements ConstraintValidator<CoordinatesPair, Object> {

  private String latitudeField;
  private String longitudeField;

  /**
   * Initializes the validator with the field names from the annotation.
   *
   * @param constraintAnnotation the annotation instance containing the field names
   */
  @Override
  public void initialize(CoordinatesPair constraintAnnotation) {
    this.latitudeField = constraintAnnotation.latitudeField();
    this.longitudeField = constraintAnnotation.longitudeField();
  }

  /**
   * Validates that the coordinate fields are either both present or both absent.
   *
   * @param value the object to validate
   * @param context the validation context
   * @return true if the validation passes, false otherwise
   */
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    try {
      Field latField = value.getClass().getDeclaredField(latitudeField);
      Field longField = value.getClass().getDeclaredField(longitudeField);

      latField.setAccessible(true);
      longField.setAccessible(true);

      BigDecimal latitude = (BigDecimal) latField.get(value);
      BigDecimal longitude = (BigDecimal) longField.get(value);

      // Both null is valid
      if (latitude == null && longitude == null) {
        return true;
      }

      // Both not null is valid
      if (latitude != null && longitude != null) {
        return true;
      }

      // One null and one not null is invalid
      return false;

    } catch (NoSuchFieldException | IllegalAccessException e) {
      // If we can't access the fields, we assume the validation fails
      return false;
    }
  }
}