package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Conjunction;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class NPDQueries {
	private static Variable x = Expressions.makeVariable("X");
	private static Variable y = Expressions.makeVariable("Y");
	private static Variable z = Expressions.makeVariable("Z");
	private static Variable w = Expressions.makeVariable("W");
	private static Variable v = Expressions.makeVariable("V");

	private static Predicate well = Expressions.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#Well", 1);

	private static Predicate blowoutWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#BlowoutWellbore", 1);
	private static Predicate wellboreCorePhoto = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#WellboreCorePhoto", 1);
	private static Predicate continuant = Expressions.makePredicate("http://www.ifomis.org/bfo/1.1/snap#Continuant", 1);
	private static Predicate wellbore = Expressions.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#Wellbore", 1);
	private static Predicate wellboreStratigraphicCoreSet = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#WellboreStratigraphicCoreSet", 1);
	private static Predicate initialWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#InitialWellbore", 1);
	private static Predicate discovery = Expressions.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#Discovery", 1);

	private static Predicate wellOperator = Expressions.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#wellOperator",
			2);
	private static Predicate inLithostratigraphicUnit = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#inLithostratigraphicUnit", 2);
	private static Predicate corePhotoForWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#corePhotoForWellbore", 2);
	private static Predicate stratumForWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#stratumForWellbore", 2);
	private static Predicate belongsToWell = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#belongsToWell", 2);
	private static Predicate drillingOperatorCompany = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#drillingOperatorCompany", 2);
	private static Predicate coreForWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#coreForWellbore", 2);
	private static Predicate discoveryWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#discoveryWellbore", 2);
	private static Predicate corePhotoURL = Expressions.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#corePhotoURL",
			2);
	private static Predicate documentForWellbore = Expressions
			.makePredicate("http://sws.ifi.uio.no/vocab/npd-v2#documentForWellbore", 2);

	private static Conjunction npd1Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("npd1", 2), w, z));
	private static Conjunction npd1Body = Expressions.makeConjunction(Expressions.makeAtom(wellOperator, x, y),
			Expressions.makeAtom(belongsToWell, x, z), Expressions.makeAtom(well, z),
			Expressions.makeAtom(drillingOperatorCompany, x, w));
	private static Rule npd1 = Expressions.makeRule(npd1Head, npd1Body);

	private static Conjunction npd2Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("npd2", 2), w, x));
	private static Conjunction npd2Body = Expressions.makeConjunction(
			Expressions.makeAtom(wellboreStratigraphicCoreSet, x), Expressions.makeAtom(inLithostratigraphicUnit, x, y),
			Expressions.makeAtom(coreForWellbore, x, z), Expressions.makeAtom(wellbore, z),
			Expressions.makeAtom(discovery, w), Expressions.makeAtom(discoveryWellbore, w, z),
			Expressions.makeAtom(initialWellbore, z));
	private static Rule npd2 = Expressions.makeRule(npd2Head, npd2Body);

	private static Conjunction npd3Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("npd3", 2), x, y));
	private static Conjunction npd3Body = Expressions.makeConjunction(Expressions.makeAtom(corePhotoForWellbore, x, y),
			Expressions.makeAtom(wellboreCorePhoto, x), Expressions.makeAtom(corePhotoURL, x, z),
			Expressions.makeAtom(documentForWellbore, w, y), Expressions.makeAtom(continuant, w));
	private static Rule npd3 = Expressions.makeRule(npd3Head, npd3Body);

	private static Conjunction npd4Head = Expressions
			.makeConjunction(Expressions.makeAtom(Expressions.makePredicate("npd4", 2), x, w));
	private static Conjunction npd4Body = Expressions.makeConjunction(Expressions.makeAtom(stratumForWellbore, x, y),
			Expressions.makeAtom(blowoutWellbore, y), Expressions.makeAtom(stratumForWellbore, z, y),
			Expressions.makeAtom(inLithostratigraphicUnit, z, w));
	private static Rule npd4 = Expressions.makeRule(npd4Head, npd4Body);

	public static Rule getNPDQuery(String queryRuleId) {
		if (queryRuleId.endsWith("1"))
			return npd1;
		else if (queryRuleId.endsWith("2"))
			return npd2;
		else if (queryRuleId.endsWith("3"))
			return npd3;
		else if (queryRuleId.endsWith("4"))
			return npd4;
		return null;
	}
}
