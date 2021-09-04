package bangmaple.jdbc.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Table {
    String name() default "";
    String catalog() default "";
    String schema() default "";
}
