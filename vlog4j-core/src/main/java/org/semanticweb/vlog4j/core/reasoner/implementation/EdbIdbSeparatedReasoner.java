package org.semanticweb.vlog4j.core.reasoner.implementation;

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
