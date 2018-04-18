package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.api.TermType;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.AtomImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.core.model.implementation.VariableImpl;

/**
 * Class with utility methods for handling a knowledge base with
 * {@link Predicate}s that are both <b>EDB</b> (appear in fact atoms) and
 * <b>IDB</b> (appear in rule head atoms). The utility methods are used by the
 * knowledge base to obtain an EDB/IDB separation by renaming the predicates in
 * facts, and adding transition rules for the renamed predicates, as described
 * below.<br>
 * <br>
 * For each {@code predicate} that is both <b>EDB</b> and <b>IDB</b>
 * <ul>
 * <li>a new, strictly <b>EDB</b>, predicate will be generated
 * ({@code EDB-predicate}). This predicate will replace the original
 * {@code predicate} in facts.</li>
 * <li>a new rule will be generated, having the new {@code EDB-predicate} in the
 * body, and the original {@code predicate} in the head, such that, for all
 * facts with the new {@code EDB-predicate}, a fact with the original
 * {@code predicate} will be inferred.</li>
 * </ul>
 * 
 * @author Irina Dragoste
 *
 */
// TODO add example to javadoc
final class EdbIdbSeparationHander {

	/**
	 * Prefix used for renaming facts predicates.
	 */
	final static String EDB_PREFIX = "EDB-";

	final static String VARIABLE_NAME_PREFIX = "x";

	/**
	 * Set of {@link Predicate}s that are both EDB (appear in fact atoms) and IDB
	 * (appear in rule head atoms).
	 */
	private final Set<Predicate> edbIdbPredicates;

	/**
	 * Constructs a handler for the given set of predicates that are both EDB and
	 * IDB.
	 * 
	 * @param edbIdbPredicates
	 *            set of {@link Predicate}s that are both EDB (appear in fact atoms)
	 *            and IDB (appear in rule head atoms).
	 */
	EdbIdbSeparationHander(Set<Predicate> edbIdbPredicates) {
		this.edbIdbPredicates = edbIdbPredicates;
	}

	/**
	 * Constructs a new {@link Predicate} from the given one, with the same
	 * {@code arity} and with the {@code name} the name of given {@code predicate},
	 * prefixed by {@link EDB_PREFIX}: "EDB-".
	 * 
	 * @param predicate
	 *            given predicate
	 * @return a new {@link Predicate} instance with the same {@code arity} as given
	 *         {@code predicate} ({@link Predicate#getArity()}) and with
	 *         {@code name} the name of given {@code predicate}, prefixed by
	 *         {@link EDB_PREFIX}: "EDB-".
	 */
	// TODO add example to javadoc
	static Predicate generateEdbPredicate(Predicate predicate) {
		final Predicate edbPredicate = new PredicateImpl(EDB_PREFIX + predicate.getName(), predicate.getArity());
		return edbPredicate;
	}

	/**
	 * Constructs an atom with given {@code predicate} and terms of type
	 * {@link TermType#VARIABLE}, each with a distinct name.
	 * 
	 * @param predicate
	 *            predicate for the atom to be constructed
	 * @return an atom with given {@code predicate} and terms of type
	 *         {@link TermType#VARIABLE}, each with a distinct name.
	 */
	static Atom generateAtomWithDistinctVariables(Predicate predicate) {
		final List<Term> terms = new ArrayList<>();
		for (int i = 0; i < predicate.getArity(); i++) {
			final Variable variableI = new VariableImpl(VARIABLE_NAME_PREFIX + i);
			terms.add(variableI);
		}
		return new AtomImpl(predicate, terms);
	}

	<T> Map<Predicate, T> toEdbPredicates(Map<Predicate, T> dataForPredicate) {
		final Map<Predicate, T> dataSourcesForEdbPredicate = new HashMap<>();
		dataForPredicate.entrySet().forEach(entry -> {
			final Predicate predicate = entry.getKey();
			final T value = entry.getValue();

			if (this.edbIdbPredicates.contains(predicate)) {
				final Predicate edbPredicate = generateEdbPredicate(predicate);
				dataSourcesForEdbPredicate.put(edbPredicate, value);
			} else {
				dataSourcesForEdbPredicate.put(predicate, value);
			}

		});
		return dataSourcesForEdbPredicate;
	}

	List<Rule> generateEdbPredicateToPredicateRules() {
		final List<Rule> edbPredicateToPredicateRules = new ArrayList<>();

		this.edbIdbPredicates.forEach(predicate -> {
			final Atom headPredicateAtom = generateAtomWithDistinctVariables(predicate);
			final Atom bodyEdbPredicateAtom = generateAtomWithDistinctVariables(generateEdbPredicate(predicate));
			final Rule rule = Expressions.makeRule(headPredicateAtom, bodyEdbPredicateAtom);
			edbPredicateToPredicateRules.add(rule);
		});
		return edbPredicateToPredicateRules;
	}

}
