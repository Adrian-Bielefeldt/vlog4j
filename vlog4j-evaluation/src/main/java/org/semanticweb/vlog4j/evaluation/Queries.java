package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;

public class Queries {

	static Rule getQuery(String queryId) {
		if (queryId.startsWith("chembl"))
			return getChemblQuery(queryId);
		else if (queryId.startsWith("npd"))
			return getNPDQuery(queryId);
		else if (queryId.startsWith("reactome"))
			return ReactomeQueries.getReactomeQueryRule(queryId);
		else if (queryId.startsWith("uniprot"))
			return getUniprotQuery(queryId);

		return null;
	}

	static private Rule getChemblQuery(String queryId) {

		return null;
	}

	static private Rule getNPDQuery(String queryId) {
		// TODO Auto-generated method stub
		return null;
	}

	static private Rule getUniprotQuery(String queryId) {
		// TODO Auto-generated method stub
		return null;
	}
}
