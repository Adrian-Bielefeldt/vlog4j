package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class ReactomeQueries {

	private static Variable x = Expressions.makeVariable("X");
	private static Variable y = Expressions.makeVariable("Y");
	private static Variable z = Expressions.makeVariable("Z");
	private static Variable w = Expressions.makeVariable("W");
	private static Variable v = Expressions.makeVariable("V");

	// Reactome predicates

	private static Predicate biochemicalReaction = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#BiochemicalReaction", 1);
	private static Predicate complex = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#Complex", 1);
	private static Predicate pathway = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#Pathway", 1);
	private static Predicate sequenceSite = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#SequenceSite", 1);
	private static Predicate protein = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#Protein", 1);
	private static Predicate physicalEntityConcept = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#physicalEntity", 2);
	private static Predicate memberPhysicalEntity = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#memberPhysicalEntity", 2);
	private static Predicate participant = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#participant", 2);
	private static Predicate physicalEntityObjProp = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#physicalEntity", 2);
	private static Predicate pathwayComponent = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#pathwayComponent", 2);
	private static Predicate featureLocation = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#featureLocation", 2);
	private static Predicate sequenceIntervalBegin = Expressions
			.makePredicate("http://www.biopax.org/release/biopax-level3.owl#sequenceIntervalBegin", 2);

	// SELECT ?y ?w WHERE
	// { ?w physicalEntity ?z . ?y physicalEntity ?z . }
	private static Conjunction reactome1Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("reactome1", 2), y, w));
	private static Conjunction reactome1Body = Expressions.makeConjunction(
			Expressions.makeAtom(physicalEntityObjProp, w, z), Expressions.makeAtom(physicalEntityObjProp, y, z));
	private static Rule reactomeQueryRule1 = Expressions.makeRule(reactome1Head, reactome1Body);

	// SELECT ?x ?z WHERE {
	// ?y a BiochemicalReaction . ?z a Complex .
	// ?x a Pathway . ?z memberPhysicalEntity ?w.
	// ?y participant ?z . ?x pathwayComponent ?y . }
	private static Conjunction reactome2Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("reactome2", 2), x, z));
	private static Conjunction reactome2Body = Expressions.makeConjunction(Expressions.makeAtom(biochemicalReaction, y),
			Expressions.makeAtom(complex, z), Expressions.makeAtom(pathway, x),
			Expressions.makeAtom(memberPhysicalEntity, z, w), Expressions.makeAtom(participant, y, z),
			Expressions.makeAtom(pathwayComponent, x, y));
	private static Rule reactomeQueryRule2 = Expressions.makeRule(reactome2Head, reactome2Body);

	// SELECT ?x ?z WHERE {
	// ?z a SequenceSite . ?x featureLocation ?w .
	// ?x featureLocation ?y . ?w sequenceIntervalBegin ?z .
	// ?y sequenceIntervalBegin ?z. ?z a SequenceSite . }
	private static Conjunction reactome3Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("reactome3", 2), x, z));;
	private static Conjunction reactome3Body = Expressions.makeConjunction(Expressions.makeAtom(sequenceSite, z),
			Expressions.makeAtom(featureLocation, x, w), Expressions.makeAtom(featureLocation, x, y),
			Expressions.makeAtom(sequenceIntervalBegin, w, z), Expressions.makeAtom(sequenceIntervalBegin, y, z),
			Expressions.makeAtom(sequenceSite, z));
	private static Rule reactomeQueryRule3 = Expressions.makeRule(reactome3Head, reactome3Body);

	// SELECT ?x ?z WHERE {
	// ?x a Pathway . ?z a Protein .
	// ?w participant ?z . ?y participant ?z .
	// ?x pathwayComponent ?w . ?x pathwayComponent ?y . }
	private static Conjunction reactome4Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("reactome4", 2), x, z));
	private static Conjunction reactome4Body = Expressions.makeConjunction(Expressions.makeAtom(pathway, x),
			Expressions.makeAtom(protein, z), Expressions.makeAtom(participant, w, z),
			Expressions.makeAtom(participant, y, z), Expressions.makeAtom(pathwayComponent, x, w),
			Expressions.makeAtom(pathwayComponent, x, y));
	private static Rule reactomeQueryRule4 = Expressions.makeRule(reactome4Head, reactome4Body);

	static Rule getReactomeQueryRule(String queryRuleId) {
		if (queryRuleId.endsWith("1"))
			return reactomeQueryRule1;
		else if (queryRuleId.endsWith("2"))
			return reactomeQueryRule2;
		else if (queryRuleId.endsWith("3"))
			return reactomeQueryRule3;
		else if (queryRuleId.endsWith("4"))
			return reactomeQueryRule4;
		return null;
	}
}
