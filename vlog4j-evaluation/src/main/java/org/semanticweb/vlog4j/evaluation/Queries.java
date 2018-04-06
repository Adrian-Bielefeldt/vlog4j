package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Rule;

public class Queries {

	static Rule getQuery(String queryRuleId) {
		if (queryRuleId.startsWith("chembl"))
			return getChemblQuery(queryRuleId);
		else if (queryRuleId.startsWith("npd"))
			return getNPDQuery(queryRuleId);
		else if (queryRuleId.startsWith("reactome"))
			return ReactomeQueries.getReactomeQueryRule(queryRuleId);
		else if (queryRuleId.startsWith("uniprot"))
			return UniprotQueries.getUniprotQuery(queryRuleId);

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
