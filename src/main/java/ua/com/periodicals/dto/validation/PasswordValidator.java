package ua.com.periodicals.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private Pattern pattern;
    private Matcher matcher;
    private final static String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()]).{8,20})";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return (validatePassword(password));
    }

    private boolean validatePassword(String email) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
