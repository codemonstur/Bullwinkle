package unittests;

import static org.junit.Assert.*;
import static unittests.util.Functions.newTestBnfParser;

import java.io.IOException;
import java.util.Deque;

import app.parsing.Builds;
import app.parsing.ParseTreeObjectBuilder;
import app.parsing.ParseTreeObjectBuilder.BuildException;
import org.junit.Test;

public class ObjectBuilderTest {

	@Test
	public void testBuilder() throws BuildException, IOException {
		final var builder = new DummyBuilder();

		final var node = newTestBnfParser("dummyobject.bnf").build().parse("+ 3 3");
		final var dob = builder.build(node);

		assertNotNull(dob);
		assertTrue(dob instanceof Add);
	}
	
	@Test
	public void testBuilderPop() throws BuildException, IOException {
		final var builder = new DummyBuilderPop();

		final var node = newTestBnfParser("dummyobject.bnf").build().parse("+ 3 3");
		final var dob = builder.build(node);

		assertNotNull(dob);
		assertTrue(dob instanceof Add);
	}
	
	@Test(expected = BuildException.class)
	public void testBuilderInvalid1() throws BuildException, IOException {
		final var db = new InvalidBuilder();

		final var node = newTestBnfParser("dummyobject.bnf").build().parse("+ 3 3");

		db.build(node);
	}
	
	@Test(expected = BuildException.class)
	public void testBuilderInvalid2() throws BuildException, IOException {
		final var db = new InvalidBuilder();

		final var node = newTestBnfParser("dummyobject.bnf").build().parse("- 3 3");

		db.build(node);
	}
	
	public static class DummyBuilder extends ParseTreeObjectBuilder<Object> {
		@Builds(rule="<add>")
		public void handle(final Deque<Object> q) {
			Object o2 = q.pop(); // o2
			Object o1 = q.pop(); // o1
			q.pop(); // symbol
			Add a = new Add();
			a.left = o1;
			a.right = o2;
			q.push(a);
		}
		
		@Builds(rule="<num>")
		public void handleNum(final Deque<Object> q) {
			int n = Integer.parseInt((String) q.pop());
			Num new_n = new Num();
			new_n.n = n;
			q.push(new_n);
		}
	}
	
	public static class DummyBuilderPop extends ParseTreeObjectBuilder<Object> {
		@Builds(rule="<add>", pop=true)
		public Add handle(Object ... parts) {
			assertEquals(3, parts.length);
			Add a = new Add();
			a.right = parts[2];
			a.left = parts[1];
			return a;
		}
		
		@Builds(rule="<num>", pop=true)
		public Num handleNum(Object ... parts) {
			assertEquals(1, parts.length);
			int n = Integer.parseInt((String) parts[0]);
			Num new_n = new Num();
			new_n.n = n;
			return new_n;
		}
	}

	public static class InvalidBuilder extends ParseTreeObjectBuilder<Object> {
		@Builds(rule="<add>")
		public void handle() {
			// Will throw an exception: incorrect arguments
		}
		
		@Builds(rule="<sub>")
		private void handleSub(Deque<Object> q) {
			// Will throw an exception: method is private
			Object o2 = q.pop(); // o2
			Object o1 = q.pop(); // o1
			q.pop(); // symbol
			Add a = new Add();
			a.left = o1;
			a.right = o2;
			q.push(a);
		}
		
		@Builds(rule="<num>")
		public void handleNum(Deque<Object> q) {
			int n = Integer.parseInt((String) q.pop());
			Num new_n = new Num();
			new_n.n = n;
			q.push(new_n);
		}
	}

	public static class Add {
		Object left;
		Object right;
	}

	public static class Num {
		int n;
	}

}
