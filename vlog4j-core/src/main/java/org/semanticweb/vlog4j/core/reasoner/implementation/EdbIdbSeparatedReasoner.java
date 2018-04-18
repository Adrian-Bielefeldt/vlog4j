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
import java.io.IOException;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

public interface EdbIdbSeparatedReasoner {

	void setAlgorithm(Algorithm algorithm);

	Algorithm getAlgorithm();

	void setReasoningTimeout(Integer seconds);

	Integer getReasoningTimeout();

	void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException;

	RuleRewriteStrategy getRuleRewriteStrategy();

	void load(EdbIdbKnowledgeBase knowledgeBase) throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException;

	boolean reason() throws IOException, ReasonerStateException;

	QueryResultIterator answerQuery(Atom queryAtom, boolean includeBlanks) throws ReasonerStateException;

	void exportQueryAnswersToCsv(Atom queryAtom, String csvFilePath, boolean includeBlanks)
			throws ReasonerStateException, IOException;

	void resetReasoner();

	void close();

	void setLogLevel(LogLevel logLevel);

	LogLevel getLogLevel();

	void setLogFile(String filePath);

}
