package org.semanticweb.vlog4j.core.reasoner.implementation;

/*-
 * #%L
 * VLog4j Core Components
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.core.reasoner.DataSource;

public class EdbIdbSeparationHandlerTest {

	final PredicateImpl testPredicate = new PredicateImpl("testPredicate", 3);

	@Test
	public void testGenerateEdbPredicate() {
		final Predicate edbPredicate = EdbIdbSeparationHander.generateEdbPredicate(this.testPredicate);
		assertEquals(EdbIdbSeparationHander.EDB_PREFIX + "testPredicate", edbPredicate.getName());
		assertEquals(this.testPredicate.getArity(), edbPredicate.getArity());
	}

	@Test
	public void testGenerateAtomWithDistinctVariables() {
		final Atom atom = EdbIdbSeparationHander.generateAtomWithDistinctVariables(this.testPredicate);
		assertEquals(this.testPredicate, atom.getPredicate());
		final Set<Term> uniqueVariablesName = new HashSet<>(atom.getTerms());
		// the atom terms are distinct
		assertEquals(uniqueVariablesName.size(), atom.getTerms().size());
		// the atom terms match the predicate arity
		assertEquals(this.testPredicate.getArity(), atom.getTerms().size());
		// the atom terms are all variables
		assertEquals(new HashSet<>(atom.getVariables()), new HashSet<>(atom.getTerms()));
	}

	public void testToEdbPredicates() throws IOException {
		final Map<Predicate, DataSource> testDataSourcesForPredicate = new HashMap<>();
		testDataSourcesForPredicate.put(this.testPredicate, new CsvFileDataSource(new File("")));

		final EdbIdbSeparationHander edbIdbSeparationHander = new EdbIdbSeparationHander(
				Sets.newSet(this.testPredicate));
		final Map<Predicate, DataSource> edbPredicates = edbIdbSeparationHander
				.toEdbPredicates(testDataSourcesForPredicate);

		// TODO maybe put this into a separate method
		assertEquals(testDataSourcesForPredicate.values(), edbPredicates.values());
		assertNotEquals(testDataSourcesForPredicate.keySet(), edbPredicates.keySet());
		// TODO test more
	}


}
