package examples;/*
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


import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.ParseException;
import bullwinkle.error.VisitException;
import bullwinkle.parsing.BnfParser;
import bullwinkle.model.nodes.Node;
import bullwinkle.output.Graphviz;

public class ShowParseTree {

	public static void main(final String... args) throws InvalidGrammar, ParseException, VisitException {
		String expression = "G (∀ x ∈ /path/to/pingus : (∀ y ∈ /x/position : ((x = \"0\") → (X (∃ z ∈ /y/abcd : ((z=x) ∨ (z=y)))))))";
		BnfParser parser = new BnfParser(SimpleExample.class.getResourceAsStream("/grammars/examples/ltl-fo.bnf"));
		Node node = parser.parse(expression);
		Graphviz visitor = new Graphviz();
		node.prefixAccept(visitor);
		System.out.println(visitor.toOutputString());
	}

}
