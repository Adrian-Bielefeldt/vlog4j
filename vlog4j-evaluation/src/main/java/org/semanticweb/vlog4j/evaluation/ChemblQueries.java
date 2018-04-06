package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class ChemblQueries {
	private static Variable x = Expressions.makeVariable("X");
	private static Variable y = Expressions.makeVariable("Y");
	private static Variable z = Expressions.makeVariable("Z");
	private static Variable w = Expressions.makeVariable("W");
	private static Variable v = Expressions.makeVariable("V");
	private static Variable u = Expressions.makeVariable("U");

	private static Predicate activity = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#Activity", 1);
	private static Predicate document = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#Document", 1);
	private static Predicate journal = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#Journal", 1);
	private static Predicate smallMolecule = Expressions
			.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#SmallMolecule", 1);
	private static Predicate targetComponent = Expressions
			.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#TargetComponent", 1);
	private static Predicate target = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#Target", 1);
	private static Predicate assay = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#Assay", 1);

	private static Predicate hasMolecule = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasMolecule",
			2);
	private static Predicate hasTarget = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasTarget", 2);
	private static Predicate hasTargetComponent = Expressions
			.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasTargetComponent", 2);
	private static Predicate hasDocument = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasDocument",
			2);
	private static Predicate hasActivity = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasActivity",
			2);
	private static Predicate hasJournal = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasJournal", 2);
	private static Predicate hasSource = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasSource", 2);
	private static Predicate hasUnitOnto = Expressions.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasUnitOnto",
			2);
	private static Predicate hasTargetComponentDescendant = Expressions
			.makePredicate("http://rdf.ebi.ac.uk/terms/chembl#hasTargetComponentDescendant", 2);

	// SELECT ?x ?z WHERE {
	// ?x hasTarget ?y . ?y hasTargetComponent ?z .
	// ?w hasTargetComponentDescendant ?z . ?z a TargetComponent}
	private static Conjunction chembl1Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("chembl1", 2), x, z));
	private static Conjunction chembl1Body = Expressions.makeConjunction(Expressions.makeAtom(hasTarget, x, y),
			Expressions.makeAtom(hasTargetComponent, y, z), Expressions.makeAtom(hasTargetComponentDescendant, w, z),
			Expressions.makeAtom(targetComponent, z), Expressions.makeAtom(target, y));
	private static Rule chembl1 = Expressions.makeRule(chembl1Head, chembl1Body);

	// SELECT ?w ?z WHERE {
	// ?x a Molecule .?x a Activity . ?x hasMolecule ?y . 
	// ?z a Activity . ?z hasMolecule ?y . ?y a SmallMolecule .}
	private static Conjunction chembl2Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("chembl2", 2), w, z));
	private static Conjunction chembl2Body = Expressions.makeConjunction(Expressions.makeAtom(activity, x),
			Expressions.makeAtom(hasMolecule, x, y), Expressions.makeAtom(activity, z),
			Expressions.makeAtom(hasMolecule, z, y), Expressions.makeAtom(smallMolecule, y),
			Expressions.makeAtom(hasActivity, w, x), Expressions.makeAtom(hasActivity, v, z));
	private static Rule chembl2 = Expressions.makeRule(chembl2Head, chembl2Body);

	// SELECT ?w ?z WHERE {
	// ?x a Activity . ?x hasUniteOnto ?w .
	// ?x hasDocument ?y . ?y a Document .
	// ?y hasJournal ? z . ?z a Journal . }
	private static Conjunction chembl3Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("chembl3", 2), w, z));
	private static Conjunction chembl3Body = Expressions.makeConjunction(Expressions.makeAtom(activity, x),
			Expressions.makeAtom(hasUnitOnto, x, w), Expressions.makeAtom(hasDocument, x, y),
			Expressions.makeAtom(document, y), Expressions.makeAtom(hasJournal, y, z),
			Expressions.makeAtom(journal, z));
	private static Rule chembl3 = Expressions.makeRule(chembl3Head, chembl3Body);

	// SELECT ?x ?y WHERE {
	// ?x a Assay . ?x hasSource ?y .
	// ?x hasActivity ?z . ?z a Activity .
	// ?z hasDocument ?w . ?w a Document . }
	private static Conjunction chembl4Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("chembl4", 2), x, y));
	private static Conjunction chembl4Body = Expressions.makeConjunction(Expressions.makeAtom(assay, x),
			Expressions.makeAtom(hasSource, x, y), Expressions.makeAtom(hasActivity, x, z),
			Expressions.makeAtom(activity, z), Expressions.makeAtom(hasDocument, z, w),
			Expressions.makeAtom(document, w));
	private static Rule chembl4 = Expressions.makeRule(chembl4Head, chembl4Body);

	public static Rule getChemblQuery(String queryRuleId) {
		if (queryRuleId.endsWith("1"))
			return chembl1;
		else if (queryRuleId.endsWith("2"))
			return chembl2;
		else if (queryRuleId.endsWith("3"))
			return chembl3;
		else if (queryRuleId.endsWith("4"))
			return chembl4;

		return null;
	}

}
