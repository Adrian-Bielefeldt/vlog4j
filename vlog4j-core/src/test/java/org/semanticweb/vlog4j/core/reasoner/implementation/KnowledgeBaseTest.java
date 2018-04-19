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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;

public class KnowledgeBaseTest {

	@Test
	public void testHandleEdbIdbSeparationFactsFromMemory() {

		final Variable vx = Expressions.makeVariable("x");
		// p(x)->q(x)
		final Rule rule = Expressions.makeRule(Expressions.makeAtom("q", vx), Expressions.makeAtom("p", vx));
		// q(c)
		// q(d,d)
		final Atom factIDBpredQ1 = Expressions.makeAtom("q", Expressions.makeConstant("c"));
		final Atom factEDBpredQ2 = Expressions.makeAtom("q", Expressions.makeConstant("d"),
				Expressions.makeConstant("d"));

		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		knowledgeBase.addRules(rule);
		knowledgeBase.addFacts(factIDBpredQ1, factEDBpredQ2);

		assertTrue(knowledgeBase.hasEdbIdbPredicates());
		assertEquals(Sets.newSet(new PredicateImpl("q", 1)), knowledgeBase.getEdbIdbPredicates());

		final boolean changed = knowledgeBase.updateKnowledgeBaseToEnsureEdbIdbSeparation();
		assertTrue(changed);
		assertFalse(knowledgeBase.hasEdbIdbPredicates());
		assertTrue(knowledgeBase.getEdbIdbPredicates().isEmpty());

	}

	// TODO test tHandleEdbIdbSeparationFacts from datasource
	// TODO test tHandleEdbIdbSeparationFacts from memory and datasource

}
