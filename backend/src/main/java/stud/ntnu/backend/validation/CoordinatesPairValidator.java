package stud.ntnu.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Validator for the CoordinatesPair annotation. Ensures that if one coordinate (latitude or
 * longitude) is present, the other must also be present.
 */
public class CoordinatesPairValidator implements ConstraintValidator<CoordinatesPair, Object> {

  private String latitudeField;
  private String longitudeField;

  @Override
  public void initialize(CoordinatesPair constraintAnnotation) {
    this.latitudeField = constraintAnnotation.latitudeField();
    this.longitudeField = constraintAnnotation.longitudeField();
  }

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