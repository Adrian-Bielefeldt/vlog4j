package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.core.reasoner.DataSource;

public class EdbIdbSeparationUtil {

	public static String EDB_PREFIX = "EDB-";

	public static Predicate constructEdbPredicate(Predicate predicate) {
		final Predicate edbPredicate = new PredicateImpl("EDB-" + predicate.getName(), predicate.getArity());
		return edbPredicate;
	}

	private static Map<Predicate, DataSource> toEdbPredicates(Map<Predicate, DataSource> dataSourcesForPredicate) {
		final Map<Predicate, DataSource> dataSourcesForEdbPredicate = new HashMap<>();
		// dataSourcesForPredicate.forEach(action);
		// TODO

		return dataSourcesForEdbPredicate;

	}

}
