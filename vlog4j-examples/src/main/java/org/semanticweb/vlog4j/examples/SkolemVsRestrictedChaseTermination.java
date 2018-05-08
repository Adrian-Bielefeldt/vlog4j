package org.semanticweb.vlog4j.examples;

/*-
 * #%L
 * VLog4j Examples
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

import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;

/**
 * This example shows non-termination of the Skolem Chase, versus termination of
 * the Restricted Chase on the same set of rules and facts. Note that the
 * Restricted Chase is the default reasoning algorithm, as it terminates in most
 * cases and generates a smaller number of facts.
 * 
 * @author Irina Dragoste
 *
 */
public class SkolemVsRestrictedChaseTermination {

	public static void main(String[] args)
			throws ReasonerStateException, EdbIdbSeparationException, IncompatiblePredicateArityException, IOException {
		// 1. Instantiating entities, rules and facts
		final Predicate bicycleIDB = Expressions.makePredicate("BicycleIDB", 1);
		final Predicate bicycleEDB = Expressions.makePredicate("BicycleEDB", 1);
		final Predicate wheelIDB = Expressions.makePredicate("WheelIDB", 1);
		final Predicate wheelEDB = Expressions.makePredicate("WheelEDB", 1);
		final Predicate hasPartIDB = Expressions.makePredicate("HasPartIDB", 2);
		final Predicate hasPartEDB = Expressions.makePredicate("HasPartEDB", 2);
		final Predicate isPartOfIDB = Expressions.makePredicate("IsPartOfIDB", 2);
		final Predicate isPartOfEDB = Expressions.makePredicate("IsPartOfEDB", 2);
		final Constant bicycle1 = Expressions.makeConstant("bicycle1");
		final Constant bicycle2 = Expressions.makeConstant("bicycle2");
		final Constant wheel1 = Expressions.makeConstant("wheel1");
		final Variable x = Expressions.makeVariable("x");
		final Variable y = Expressions.makeVariable("y");

		// BicycleIDB(?x) :- BicycleEDB(?x) .
		final Atom bicycleIDBX = Expressions.makeAtom(bicycleIDB, x);
		final Atom bicycleEDBX = Expressions.makeAtom(bicycleEDB, x);
		final Rule rule1 = Expressions.makeRule(bicycleIDBX, bicycleEDBX);

		// WheelIDB(?x) :- WheelEDB(?x) .
		final Atom wheelIDBX = Expressions.makeAtom(wheelIDB, x);
		final Atom wheelEDBX = Expressions.makeAtom(wheelEDB, x);
		final Rule rule2 = Expressions.makeRule(wheelIDBX, wheelEDBX);

		// hasPartIDB(?x, ?y) :- hasPartEDB(?x, ?y) .
		final Atom hasPartIDBXY = Expressions.makeAtom(hasPartIDB, x, y);
		final Atom hasPartEDBXY = Expressions.makeAtom(hasPartEDB, x, y);
		final Rule rule3 = Expressions.makeRule(hasPartIDBXY, hasPartEDBXY);

		// isPartOfIDB(?x, ?y) :- isPartOfEDB(?x, ?y) .
		final Atom isPartOfIDBXY = Expressions.makeAtom(isPartOfIDB, x, y);
		final Atom isPartOfEDBXY = Expressions.makeAtom(isPartOfEDB, x, y);
		final Rule rule4 = Expressions.makeRule(isPartOfIDBXY, isPartOfEDBXY);

		// HasPartIDB(?x, !y), WheelIDB(!y) :- BicycleIDB(?x) .
		final Atom wheelIDBY = Expressions.makeAtom(wheelIDB, y);
		final Rule rule5 = Expressions.makeRule(Expressions.makeConjunction(hasPartIDBXY, wheelIDBY),
				Expressions.makeConjunction(bicycleIDBX));

		// IsPartOfIDB(?x, !y), BicycleIDB(!y) :- WheelIDB(?x) .
		final Atom bycicleIDBY = Expressions.makeAtom(bicycleIDB, y);
		final Rule rule6 = Expressions.makeRule(Expressions.makeConjunction(isPartOfIDBXY, bycicleIDBY),
				Expressions.makeConjunction(wheelIDBX));

		// IsPartOfIDB(?x, ?y) :- HasPartIDB(?y, ?x) .
		final Atom hasPartIDBYX = Expressions.makeAtom(hasPartIDB, y, x);
		final Rule rule7 = Expressions.makeRule(isPartOfIDBXY, hasPartIDBYX);

		// HasPartIDB(?x, ?y) :- IsPartOfIDB(?y, ?x) .
		final Atom isPartOfIDBYX = Expressions.makeAtom(isPartOfIDB, y, x);
		final Rule rule8 = Expressions.makeRule(hasPartIDBXY, isPartOfIDBYX);

		// BicycleEDB(bicycle1) .
		final Atom fact1 = Expressions.makeAtom(bicycleEDB, bicycle1);

		// HasPartEDB(bicycle1, wheel1) .
		final Atom fact2 = Expressions.makeAtom(hasPartEDB, bicycle1, wheel1);

		// Wheel(wheel1) .
		final Atom fact3 = Expressions.makeAtom(wheelEDB, wheel1);

		// BicycleEDB(b) .
		final Atom fact4 = Expressions.makeAtom(bicycleEDB, bicycle2);

		// 2. Loading, reasoning, and querying.
		// Use try-with resources, or remember to call close() to free the reasoner
		// resources.
		try (Reasoner reasoner = Reasoner.getInstance()) {

			reasoner.addRules(rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8);
			reasoner.addFacts(fact1, fact2, fact3, fact4);
			reasoner.load();

			System.out.println("Answers to query " + hasPartIDBXY + " before reasoning:");
			printOutQueryAnswers(hasPartIDBXY, reasoner);

			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			reasoner.setReasoningTimeout(1);
			System.out.println("Starting Skolem Chase with 1 second timeout.");
			boolean skolemChaseFinished = reasoner.reason();
			System.out.println("Has Skolem Chase algorithm finished before 1 second timeout? " + skolemChaseFinished);
			System.out.println(
					"Answers to query " + hasPartIDBXY + " after reasoning with the Skolem Chase for 1 second:");
			printOutQueryAnswers(hasPartIDBXY, reasoner);

			System.out.println();
			System.out.println("Reseting reasoner; discarding facts generated during reasoning.");
			reasoner.resetReasoner();
			reasoner.load();

			System.out.println("Answers to query " + hasPartIDBXY + " before reasoning:");
			printOutQueryAnswers(hasPartIDBXY, reasoner);

			reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
			reasoner.setReasoningTimeout(null);
			System.out.println("Starting Restricted Chase with no timeout.");
			boolean restrictedChaseFinished = reasoner.reason();
			System.out.println("Has Restricted Chase algorithm finished? " + restrictedChaseFinished);
			System.out.println("Answers to query " + hasPartIDBXY + " after reasoning with the Restricted Chase:");
			printOutQueryAnswers(hasPartIDBXY, reasoner);
		}
	}

	private static void printOutQueryAnswers(Atom queryAtom, Reasoner reasoner) throws ReasonerStateException {
		try (QueryResultIterator queryResultIterator = reasoner.answerQuery(queryAtom, true)) {
			queryResultIterator.forEachRemaining(queryResult -> System.out.println(" - " + queryResult));
		}
	}
}