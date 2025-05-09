package stud.ntnu.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation that ensures coordinate pairs (latitude and longitude) are provided
 * together. This annotation can be applied to classes that contain coordinate fields.
 *
 * <p>The validation ensures that:
 * <ul>
 *   <li>If latitude is provided, longitude must also be provided</li>
 *   <li>If longitude is provided, latitude must also be provided</li>
 *   <li>Both coordinates can be absent</li>
 *   <li>Neither coordinate can be present individually</li>
 * </ul>
 *
 * @see CoordinatesPairValidator
 */
@Documented
@Constraint(validatedBy = CoordinatesPairValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoordinatesPair {

  /**
   * The default error message to be displayed when validation fails.
   *
   * @return the error message
   */
  String message() default "Both latitude and longitude must be provided together";

  /**
   * Groups for validation.
   *
   * @return validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Payload for validation.
   *
   * @return validation payload
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * The name of the field containing the latitude value.
   *
   * @return the latitude field name
   */
  String latitudeField();

  /**
   * The name of the field containing the longitude value.
   *
   * @return the longitude field name
   */
  String longitudeField();
}