package app.parsing;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a method is attached to a non-terminal symbol in a grammar
 */
@Retention(RUNTIME)
@Target({METHOD})
@Documented
public @interface Builds {
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
