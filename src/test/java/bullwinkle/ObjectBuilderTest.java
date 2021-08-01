/*
  Copyright 2014-2017 Sylvain Hallé
  Laboratoire d'informatique formelle
  Université du Québec à Chicoutimi, Canada

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package bullwinkle;

import static org.junit.Assert.*;

import java.util.Deque;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.model.Builds;
import bullwinkle.model.nodes.Node;
import bullwinkle.parsing.BnfParser;
import bullwinkle.parsing.ParseTreeObjectBuilder;
import org.junit.Test;

import bullwinkle.parsing.ParseTreeObjectBuilder.BuildException;

public class ObjectBuilderTest {

	@Test
	public void testBuilder() throws InvalidGrammar, ParseException, BuildException {
		BnfParser parser = new BnfParser(ObjectBuilderTest.class.getResourceAsStream("/grammars/tests/dummyobject.bnf"));
		Node tree = parser.parse("+ 3 3");
		DummyBuilder db = new DummyBuilder();
		Object dob = db.build(tree);
		assertNotNull(dob);
		assertTrue(dob instanceof Add);
	}
	
	@Test
	public void testBuilderPop() throws InvalidGrammar, ParseException, BuildException {
		BnfParser parser = new BnfParser(ObjectBuilderTest.class.getResourceAsStream("/grammars/tests/dummyobject.bnf"));
		Node tree = parser.parse("+ 3 3");
		DummyBuilderPop db = new DummyBuilderPop();
		Object dob = db.build(tree);
		assertNotNull(dob);
		assertTrue(dob instanceof Add);
	}
	
	@Test(expected=BuildException.class)
	public void testBuilderInvalid1() throws InvalidGrammar, ParseException, BuildException {
		// Will cause an IllegalArgumentException
		BnfParser parser = new BnfParser(ObjectBuilderTest.class.getResourceAsStream("/grammars/tests/dummyobject.bnf"));
		Node tree = parser.parse("+ 3 3");
		InvalidBuilder db = new InvalidBuilder();
		db.build(tree);
	}
	
	@Test(expected=BuildException.class)
	public void testBuilderInvalid2() throws InvalidGrammar, ParseException, BuildException {
		// Will cause an IllegalAccessException
		BnfParser parser = new BnfParser(ObjectBuilderTest.class.getResourceAsStream("/grammars/tests/dummyobject.bnf"));
		Node tree = parser.parse("- 3 3");
		InvalidBuilder db = new InvalidBuilder();
		db.build(tree);
	}
	
	public static class DummyBuilder extends ParseTreeObjectBuilder<Object> {
		@Builds(rule="<add>")
		public void handle(Deque<Object> q)
		{
			Object o2 = q.pop(); // o2
			Object o1 = q.pop(); // o1
			q.pop(); // symbol
			Add a = new Add();
			a.left = o1;
			a.right = o2;
			q.push(a);
		}
		
		@Builds(rule="<num>")
		public void handleNum(Deque<Object> q)
		{
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
	
	public static class Add
	{
		Object left;
		Object right;
	}
	
	public static class Num
	{
		int n;
	}

}
