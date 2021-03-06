package org.semanticweb.vlog4j.graal;

/*-
 * #%L
 * VLog4J Graal Import Components
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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeAtom;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConjunction;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;

import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;

/**
 * A utility class containing a {@link ConjunctiveQuery Graal ConjunctiveQuery}.
 * Answering a Graal ConjunctiveQuery over a certain knowledge base is
 * equivalent to adding a {@link Rule} to the knowledge base, <em> prior to
 * reasoning</em>. The rule consists of the query atoms as the body and a single
 * atom with a new predicate containing all the answer variables of the query as
 * the head. After the reasoning process, in which the rule is materialised, is
 * completed, this rule head can then be used as a query atom to obtain the
 * results of the Graal ConjunctiveQuery.
 * 
 * @author Adrian Bielefeldt
 */
public class GraalConjunctiveQueryToRule {
		
	private final Rule rule;
	
	private final Atom query;
	
	/**
	 * Constructor for a GraalConjunctiveQueryToRule. 
	 * @param ruleHeadPredicateName the query predicate name. Becomes the name of the rule head Predicate.
	 * @param answerVariables the query answer variables. They become the terms of the rule head Atom.
	 * @param conjunction the query body. Becomes the rule body.
	 */
	protected GraalConjunctiveQueryToRule(final String ruleHeadPredicateName, final List<Term> answerVariables,
			final Conjunction conjunction) {
		query = makeAtom(ruleHeadPredicateName, answerVariables);
		rule = makeRule(makeConjunction(query), conjunction);
	}

	/**
	 * A rule that needs to be added to the program to answer the
	 * {@link ConjunctiveQuery Graal ConjunctiveQuery} represented by this object.
	 * It consists of all query atoms from the original Graal ConjunctiveQuery as
	 * the body and a single atom containing all the answer variables of the query as the head.
	 * 
	 * @return The rule equivalent to the Graal ConjunctiveQuery represented by this
	 *         object.
	 */
	public Rule getRule() {
		return rule;
	}
	
	/**
	 * A query atom that returns the results of the {@link ConjunctiveQuery Graal
	 * ConjunctiveQuery} represented by this object, provided the corresponding
	 * rule ({@link #getRule()}) has been added to the program. It is equal to the
	 * head of the rule returned by {@link #getRule()}.
	 * 
	 * @return The query atom to obtain the results of the Graal ConjunctiveQuery
	 *         represented by this object.
	 */
	public Atom getQueryAtom() {
		return query;
	}

	@Override
	public int hashCode() {
		return rule.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GraalConjunctiveQueryToRule other = (GraalConjunctiveQueryToRule) obj;
		
		if (!rule.equals(other.rule)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GraalConjunctiveQueryToRule [rule=" + rule + "]";
	}

}
