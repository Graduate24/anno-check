package edu.tsinghua.demo;

import edu.tsinghua.demo.config.ValidatingComponent;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.annotation.Validated;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Validated
public class Tests {

    @Autowired
    ValidatingComponent component;

    @Test
    void givenNull_whenValidate_thenConstraintViolationException() {
        assertThrows(ConstraintViolationException.class, () -> component.validateNotNull(null));
        //jakarta.validation.ConstraintViolationException: validateNotNull.data: 不能为null

    }

    @Test
    public void test() {
        // jakarta.validation.ConstraintViolationException: getNotNullableValue.<return value>: 不能为null
        component.getNotNullableValue(null);
    }


}
