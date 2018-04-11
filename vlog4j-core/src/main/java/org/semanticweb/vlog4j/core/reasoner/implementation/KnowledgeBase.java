package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.util.ArrayList;
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

class KnowledgeBase {

	private final List<Rule> rules = new ArrayList<>();
	private Map<Predicate, Set<Atom>> factsForPredicate = new HashMap<>();
	private Map<Predicate, DataSource> dataSourceForPredicate = new HashMap<>();

	List<Rule> getRules() {
		return this.rules;
	}

	Set<Predicate> getInMemoryFactsPredicates() {
		return this.factsForPredicate.keySet();
	}

	Set<Atom> getInMemoryFactsForPredicate(Predicate predicate) {
		return this.factsForPredicate.get(predicate);
	}

	Set<Predicate> getDataSourceFactsPredicates() {
		return this.dataSourceForPredicate.keySet();
	}

	DataSource getDataSourceForPredicate(Predicate predicate) {
		return this.dataSourceForPredicate.get(predicate);
	}

	void addRules(final List<Rule> rules) {
		Validate.noNullElements(rules, "Null rules are not alowed! The list contains a null at position [%d].");
		this.rules.addAll(new ArrayList<>(rules));
	}

	void addFacts(final Collection<Atom> facts) {
		Validate.noNullElements(facts, "Null facts are not alowed! The list contains a fact at position [%d].");
		for (final Atom fact : facts) {
			validateFactTermsAreConstant(fact);

			final Predicate predicate = fact.getPredicate();
			validateNoDataSourceForPredicate(predicate);

			this.factsForPredicate.putIfAbsent(predicate, new HashSet<>());
			this.factsForPredicate.get(predicate).add(fact);
		}
	}

	void addFactsFromDataSource(final Predicate predicate, final DataSource dataSource) {
		Validate.notNull(predicate, "Null predicates are not allowed!");
		Validate.notNull(dataSource, "Null dataSources are not allowed!");
		validateNoDataSourceForPredicate(predicate);
		Validate.isTrue(!factsForPredicate.containsKey(predicate),
				"Multiple data sources for the same predicate are not allowed! Facts for predicate [%s] alredy added in memory: %s",
				predicate, factsForPredicate.get(predicate));

		this.dataSourceForPredicate.put(predicate, dataSource);
	}

	/**
	 * 
	 * @return true, if the knowledge base was modified. False, otherwise.
	 */
	 boolean handleEdbIdbSeparation() {
		// TODO validate reasoner state
		final Set<Predicate> edbIdbPredicates = getEdbIdbPredicates();
		final boolean hasEdbIdbPredicates = edbIdbPredicates.isEmpty();
		if (hasEdbIdbPredicates) {
			final EdbIdbSeparationHander edbIdbSeparationHander = new EdbIdbSeparationHander(edbIdbPredicates);

			this.dataSourceForPredicate = edbIdbSeparationHander.toEdbPredicates(this.dataSourceForPredicate);
			this.factsForPredicate = edbIdbSeparationHander.toEdbPredicates(this.factsForPredicate);
			this.rules.addAll(edbIdbSeparationHander.generateEdbPredicateToPredicateRules());
		}
		return hasEdbIdbPredicates;
	}

	 boolean hasEdbIdbPredicates() {
		final Set<Predicate> intersection = getEdbIdbPredicates();
		return !intersection.isEmpty();
	}

	Set<Predicate> getEdbIdbPredicates() {
		final Set<Predicate> edbPredicates = collectEdbPredicates();
		final Set<Predicate> idbPredicates = collectIdbPredicates();
		final Set<Predicate> intersection = new HashSet<>(idbPredicates);
		intersection.retainAll(edbPredicates);
		return intersection;
	}

	private Set<Predicate> collectEdbPredicates() {
		final Set<Predicate> edbPredicates = new HashSet<>();
		edbPredicates.addAll(getInMemoryFactsPredicates());
		edbPredicates.addAll(getDataSourceFactsPredicates());
		return edbPredicates;
	}

	// TODO generate this when adding rules
	private Set<Predicate> collectIdbPredicates() {
		final Set<Predicate> idbPredicates = new HashSet<>();
		for (final Rule rule : this.rules) {
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
				predicate, this.dataSourceForPredicate.get(predicate));
	}

}
