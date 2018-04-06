package org.semanticweb.vlog4j.evaluation;

/*-
 * #%L
 * vlog4j-evaluation
 * %%
 * Copyright (C) 2018 VLog4j Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class UniprotQueries {
	private static Variable x = Expressions.makeVariable("X");
	private static Variable y = Expressions.makeVariable("Y");
	private static Variable z = Expressions.makeVariable("Z");
	private static Variable w = Expressions.makeVariable("W");

	private static Predicate databaseClass = Expressions.makePredicate("http://purl.uniprot.org/core/Database", 1);
	private static Predicate cellularComponentClass = Expressions
			.makePredicate("http://purl.uniprot.org/core/Cellular_Component", 1);
	private static Predicate nucleotideResource = Expressions
			.makePredicate("http://purl.uniprot.org/core/Nucleotide_Resource", 1);
	private static Predicate resource = Expressions.makePredicate("http://purl.uniprot.org/core/Resource", 1);
	private static Predicate cellularComponentObjProperty = Expressions
			.makePredicate("http://purl.uniprot.org/core/cellularComponent", 2);
	private static Predicate databaseObjProperty = Expressions.makePredicate("http://purl.uniprot.org/core/database",
			2);
	private static Predicate locatedIn = Expressions.makePredicate("http://purl.uniprot.org/core/locatedIn", 2);
	private static Predicate locatedOn = Expressions.makePredicate("http://purl.uniprot.org/core/locatedOn", 2);
	private static Predicate translatedFrom = Expressions.makePredicate("http://purl.uniprot.org/core/translatedFrom",
			2);

	// SELECT ?x ?y WHERE {
	// ?x cellularComponent ?z . ?y cellularComponent ?z . }
	private static Conjunction uniprot1Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("uniprot1", 2), x, y));
	private static Conjunction uniprot1Body = Expressions.makeConjunction(
			Expressions.makeAtom(cellularComponentObjProperty, x, z),
			Expressions.makeAtom(cellularComponentObjProperty, y, z));
	private static Rule uniprotQueryRule1 = Expressions.makeRule(uniprot1Head, uniprot1Body);

	// SELECT ?x ? z WHERE {
	// ?w translatedFrom ?x . ?x a Nucleotide_Resource .
	// ?x locatedOn ?y . ?x database ?z . ?z a Database . }
	private static Conjunction uniprot2Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("uniprot2", 2), x, z));
	private static Conjunction uniprot2Body = Expressions.makeConjunction(Expressions.makeAtom(translatedFrom, w, x),
			Expressions.makeAtom(nucleotideResource, x), Expressions.makeAtom(locatedOn, x, y),
			Expressions.makeAtom(databaseObjProperty, x, z), Expressions.makeAtom(databaseClass, z));
	private static Rule uniprotQueryRule2 = Expressions.makeRule(uniprot2Head, uniprot2Body);

	// SELECT ?x ?z WHERE {
	// ?w translatedFrom ?y . ?y a Resource .
	// ?w translatedFrom ?x . ?x a Resource .
	// ?y database ?z . ?x database ?z . ?z a Database . }
	private static Conjunction uniprot3Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("uniprot3", 2), x, z));
	private static Conjunction uniprot3Body = Expressions.makeConjunction(Expressions.makeAtom(translatedFrom, w, y),
			Expressions.makeAtom(resource, y), Expressions.makeAtom(translatedFrom, w, x),
			Expressions.makeAtom(resource, x), Expressions.makeAtom(databaseObjProperty, y, z),
			Expressions.makeAtom(databaseObjProperty, x, z), Expressions.makeAtom(databaseClass, z));
	private static Rule uniprotQueryRule3 = Expressions.makeRule(uniprot3Head, uniprot3Body);

	// SELECT ?x ?z WHERE {
	// ?z a Cellular_Component .
	// ?x locatedIn ?w . ?w cellularComponent ?z .
	// ?x locatedIn ?y . ?y cellularComponent ?z . }
	private static Conjunction uniprot4Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("uniprot4", 2), x, z));
	private static Conjunction uniprot4Body = Expressions.makeConjunction(
			Expressions.makeAtom(cellularComponentClass, z), Expressions.makeAtom(locatedIn, x, w),
			Expressions.makeAtom(cellularComponentObjProperty, w, z), Expressions.makeAtom(locatedIn, x, y),
			Expressions.makeAtom(cellularComponentObjProperty, y, z));
	private static Rule uniprotQueryRule4 = Expressions.makeRule(uniprot4Head, uniprot4Body);

	public static Rule getUniprotQuery(String queryRuleId) {
		if (queryRuleId.endsWith("1"))
			return uniprotQueryRule1;
		else if (queryRuleId.endsWith("2"))
			return uniprotQueryRule2;
		else if (queryRuleId.endsWith("3"))
			return uniprotQueryRule3;
		else if (queryRuleId.endsWith("4"))
			return uniprotQueryRule4;
		return null;
	}

}
