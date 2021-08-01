package bullwinkle.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is attached to a non-terminal symbol in a grammar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Builds 
{
	/**
	 * The name of the non-terminal symbol the method is attached to
	 * @return The name
	 */
	String rule();
	
	/**
	 * Whether to expand the rule
	 * @return {@code true} if the rule is to be expanded, {@code false}
	 * otherwise
	 */
	boolean pop() default false;
	
	/**
	 * Whether to prune the arguments from any terminal symbol
	 * @return {@code true} if the arguments should be cleaned, {@code false}
	 * otherwise
	 */
	boolean clean() default false;
}
