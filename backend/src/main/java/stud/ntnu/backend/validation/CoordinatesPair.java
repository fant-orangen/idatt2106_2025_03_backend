package stud.ntnu.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that if one coordinate (latitude or longitude) is present, the other must also be
 * present. Both can be absent, but they cannot be present individually.
 */
@Documented
@Constraint(validatedBy = CoordinatesPairValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoordinatesPair {

  String message() default "Both latitude and longitude must be provided together";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String latitudeField();

  String longitudeField();
}