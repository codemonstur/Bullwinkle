package app.parsing;

import bullwinkle.error.VisitException;
import bullwinkle.nodes.Node;
import bullwinkle.ParseNodeVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Build an object by traversing a parse tree.
 *
 * @param <T> The type of the object to be built
 */
public abstract class ParseTreeObjectBuilder<T> implements ParseNodeVisitor {
	/**
	 * A stack of arbitrary objects. This stack is manipulated
	 * by the various methods that are called when visiting a parse
	 * tree.
	 */
	protected Deque<Object> stack;

	/**
	 * The object to be built and returned at the end of the visit of
	 * the parse tree.
	 */
	protected T builtObject = null;

	/**
	 * A map that associates non-terminal symbols of the grammar with
	 * specific methods to be called when visiting that symbol in the
	 * traversal of the parse tree.
	 */
	protected final Map<String,MethodAnnotation> methods;

	public ParseTreeObjectBuilder() {
		methods = new HashMap<>();
		fillMethods(methods);
	}

	/**
	 * Build an object from a parse tree
	 * @param tree The parse tree
	 * @return The object
	 * @throws BuildException Generic exception that can be thrown during the
	 *   build process
	 */
	public final synchronized T build(final Node tree) throws BuildException {
		if (tree == null) throw new BuildException("The input tree is null");

		stack = new ArrayDeque<>();
		try {
			preVisit();
			tree.postfixAccept(this);
			builtObject = postVisit(stack);
			return builtObject;
		} catch (VisitException e) {
			throw new BuildException(e);
		}
	}

	/**
	 * Perform some task before starting the traversal of a parse tree
	 */
	protected synchronized void preVisit() {
		// Nothing
	}

	/**
	 * Perform some task after the traversal of a parse tree. By default,
	 * we return the object that is at the top of the stack. Override this
	 * method to perform something else.
	 * @param stack The stack, as it is after traversing the parse tree
	 * @return The object that should be returned to the user
	 */
	@SuppressWarnings("unchecked")
	protected synchronized T postVisit(Deque<Object> stack)
	{
		if (stack.isEmpty())
		{
			return null;
		}
		return (T) stack.peek();
	}

	/**
	 * Retrieves all the methods that have a <tt>@Builds</tt>
	 * annotation in the current class
	 * @param methods A map of methods
	 */
	protected synchronized void fillMethods(final Map<String, MethodAnnotation> methods) {
		List<Class<?>> parents = getParents();
		for (Class<?> cl : parents)
		{
			Method[] ms = cl.getDeclaredMethods();
			for (Method method : ms)
			{
				Builds an = method.getAnnotation(Builds.class);
				if (an != null)
				{
					String non_terminal = an.rule();
					methods.put(non_terminal, new MethodAnnotation(method, an.pop(), an.clean()));
				}
			}
		}
	}

	/**
	 * Gets the list of ancestors of the current class
	 * @return A list of ancestors; the last element of the list is the
	 * current class; each previous element is its ancestor.
	 */
	protected List<Class<?>> getParents() {
		List<Class<?>> parents = new ArrayList<>();
		Class<?> cur_class = getClass();
		while (true) {
			parents.add(0, cur_class);
			Class<?> parent = cur_class.getSuperclass();
			if (parent == Object.class)
				break;
			cur_class = parent;
		}
		return parents;
	}

	@Override
	public synchronized void visit(final Node node) throws VisitException {
		try {
			handleNode(node);
		}
		catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new VisitException(e);
		}
	}

	/**
	 * Performs the actual handling of a parse node
	 * @param node The parse node
	 * @throws IllegalAccessException May be thrown when attempting to invoke a method
	 * @throws InvocationTargetException May be thrown when attempting to invoke a method
	 */
	protected void handleNode(final Node node) throws IllegalAccessException, InvocationTargetException {
		String token_name = node.getToken();
		// Is it a non-terminal symbol?
		if (!token_name.startsWith("<")) {
			stack.push(token_name);
			return;
		}
		// Is there a stack method that handles this non-terminal?
		if (methods.containsKey(token_name)) {
			MethodAnnotation ma = methods.get(token_name);
			if (!ma.pop) {
				ma.m.invoke(this, stack);
				return;
			}
			List<Object> argument_list = new LinkedList<>();
			List<Node> children = node.getChildren();
			for (int i = children.size() - 1; i >= 0; i--) {
				Node child = children.get(i);
				Object o = stack.pop();
				if (!ma.clean || child.getToken().startsWith("<")) {
					argument_list.add(0, o);
				}
			}
			Object[] arguments = argument_list.toArray();
			// We ignore warning S3878 here; the call to invoke
			// *requires* the varargs to be put into an array.
			Object o = ma.m.invoke(this, new Object[]{arguments});
			if (o != null) {
				stack.push(o);
			}	
		}
	}

	@Override
	public synchronized void pop() {
		// Nothing to do. This method is there only to respect the interface
		// of ParseNodeVisitor.
	}

	/**
	 * Exception container to be thrown when attempting to build an object
	 */
	public static class BuildException extends Exception {
		public BuildException(String message)
		{
			super(message);
		}
		public BuildException(Throwable t)
		{
			super(t);
		}
	}

	protected static class MethodAnnotation {
		Method m;
		boolean pop;
		boolean clean;

		public MethodAnnotation(final Method m, final boolean pop, final boolean clean) {
			this.m = m;
			this.pop = pop;
			this.clean = clean;
		}
	}
}