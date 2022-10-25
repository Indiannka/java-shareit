package ru.practicum.shareit.config;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<DateRangeMatch, Object> {

    private String dateStart;
    private String dateEnd;

    @Override
    public void initialize(DateRangeMatch constraintAnnotation) {
        this.dateStart = constraintAnnotation.dateStart();
        this.dateEnd = constraintAnnotation.dateEnd();
    }

    @Override
    public boolean isValid(Object value,  ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime dateStartValue = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(dateStart);
        LocalDateTime dateEndValue = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(dateEnd);

        if (dateStartValue != null && dateEndValue != null) {
            return dateEndValue.isAfter(dateStartValue);
        } else {
            return true;
        }
    }
}