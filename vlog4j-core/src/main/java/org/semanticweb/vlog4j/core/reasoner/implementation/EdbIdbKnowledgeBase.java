package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.ReasonerState;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public class EdbIdbKnowledgeBase {

	private VLogEdbIdbSeparatedReasoner reasoner;

	private final List<Rule> rules = new ArrayList<>();
	private final Map<Predicate, Set<Atom>> factsForPredicate = new HashMap<>();
	private final Map<Predicate, DataSource> dataSourceForPredicate = new HashMap<>();

	void setReasoner(VLogEdbIdbSeparatedReasoner reasoner) {
		this.reasoner = reasoner;
	}

	VLogEdbIdbSeparatedReasoner getReasoner() {
		return reasoner;
	}

	List<Rule> getRules() {
		return rules;
	}

	Map<Predicate, Set<Atom>> getFactsForPredicate() {
		return factsForPredicate;
	}

	Map<Predicate, DataSource> getDataSourceForPredicate() {
		return dataSourceForPredicate;
	}

	public void addRules(final Rule... rules) throws ReasonerStateException {
		addRules(Arrays.asList(rules));
	}

	public void addRules(final List<Rule> rules) throws ReasonerStateException {
		if (this.reasoner.getReasonerState() != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasoner.getReasonerState(),
					"Rules cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));
	}

	public void addFacts(final Atom... facts) throws ReasonerStateException {
		addFacts(Arrays.asList(facts));
	}

	public void addFacts(final Collection<Atom> facts) throws ReasonerStateException {
		if (this.reasoner.getReasonerState() != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasoner.getReasonerState(),
					"Facts cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.noNullElements(facts, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final Atom fact : facts) {
			validateFactTermsAreConstant(fact);

			final Predicate predicate = fact.getPredicate();
			validateNoDataSourceForPredicate(predicate);

			this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
			this.factsForPredicate.get(predicate).add(fact);
		}
	}

	public void addFactsFromDataSource(final Predicate predicate, final DataSource dataSource) throws ReasonerStateException {
		if (this.reasoner.getReasonerState() != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasoner.getReasonerState(),
					"Data sources cannot be added after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		Validate.notNull(predicate, "Null predicates are not allowed!");
		Validate.notNull(dataSource, "Null dataSources are not allowed!");
		validateNoDataSourceForPredicate(predicate);
		Validate.isTrue(!factsForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added in memory: %s",
				predicate, factsForPredicate.get(predicate));

		dataSourceForPredicate.put(predicate, dataSource);
	}

	public void validateEdbIdbSeparation() throws EdbIdbSeparationException {
		final Set<Predicate> edbPredicates = collectEdbPredicates();
		final Set<Predicate> idbPredicates = collectIdbPredicates();
		final Set<Predicate> intersection = new HashSet<>(edbPredicates);
		intersection.retainAll(idbPredicates);

		if (!intersection.isEmpty()) {
			throw new EdbIdbSeparationException(intersection);
		}
	}

	private Set<Predicate> collectEdbPredicates() {
		final Set<Predicate> edbPredicates = new HashSet<>();
		edbPredicates.addAll(dataSourceForPredicate.keySet());
		edbPredicates.addAll(factsForPredicate.keySet());
		return edbPredicates;
	}

	private Set<Predicate> collectIdbPredicates() {
		final Set<Predicate> idbPredicates = new HashSet<>();
		for (final Rule rule : rules) {
			for (final Atom headAtom : rule.getHead()) {
				idbPredicates.add(headAtom.getPredicate());
			}
		}
		return idbPredicates;
	}

	private void validateFactTermsAreConstant(Atom fact) {
		final Set<Term> nonConstantTerms = new HashSet<>(fact.getTerms());
		nonConstantTerms.removeAll(fact.getConstants());
		Validate.isTrue(nonConstantTerms.isEmpty(),
				"Only Constant terms alowed in Fact atoms! The following non-constant terms [%s] appear for fact [%s]!",
				nonConstantTerms, fact);

	}

	private void validateNoDataSourceForPredicate(final Predicate predicate) {
		Validate.isTrue(!dataSourceForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added from data source: %s",
				predicate, dataSourceForPredicate.get(predicate));
	}

}
