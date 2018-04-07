package org.semanticweb.vlog4j.evaluation;

/*-
 * #%L
 * vlog4j-evaluation
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

import org.semanticweb.vlog4j.core.model.api.Rule;

public class Queries {

	static Rule getQuery(String queryRuleId) {
		if (queryRuleId.startsWith("chembl"))
			return ChemblQueries.getChemblQuery(queryRuleId);
		else if (queryRuleId.startsWith("npd"))
			return NPDQueries.getNPDQuery(queryRuleId);
		else if (queryRuleId.startsWith("reactome"))
			return ReactomeQueries.getReactomeQueryRule(queryRuleId);
		else if (queryRuleId.startsWith("uniprot"))
			return UniprotQueries.getUniprotQuery(queryRuleId);
		else if (queryRuleId.startsWith("lubm"))
			return LUBMQueries.getLUBMQuery(queryRuleId);
		return null;
	}

	static private Rule getNPDQuery(String queryId) {
		// TODO Auto-generated method stub
		return null;
	}

}
