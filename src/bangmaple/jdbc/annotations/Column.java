package bangmaple.jdbc.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Column {
    String value() default "";
    boolean nullable() default true;
    long length() default Long.MAX_VALUE;
    boolean unique() default false;
}
