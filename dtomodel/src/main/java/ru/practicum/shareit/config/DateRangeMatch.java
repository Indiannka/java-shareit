package ru.practicum.shareit.config;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Documented
public @interface DateRangeMatch {

    String message() default "{DateRangeMatch.invalid. Дата окончания бронирования должна быть после даты начала бронирования}";
    String dateStart();
    String dateEnd();

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    @Target({ElementType.TYPE})
    @Retention(RUNTIME)
    @interface List {
        DateRangeMatch[] value();
    }

}
