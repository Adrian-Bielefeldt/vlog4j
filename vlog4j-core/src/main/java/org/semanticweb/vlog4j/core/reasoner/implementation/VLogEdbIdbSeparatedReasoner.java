package org.semanticweb.vlog4j.core.reasoner.implementation;

import java.io.IOException;
import java.util.Formatter;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.ReasonerState;
import org.semanticweb.vlog4j.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import karmaresearch.vlog.AlreadyStartedException;
import karmaresearch.vlog.EDBConfigurationException;
import karmaresearch.vlog.NotStartedException;
import karmaresearch.vlog.TermQueryResultIterator;
import karmaresearch.vlog.VLog;

public class VLogEdbIdbSeparatedReasoner implements EdbIdbSeparatedReasoner {

	private static Logger LOGGER = LoggerFactory.getLogger(VLogReasoner.class);

	private final VLog vLog = new VLog();
	private ReasonerState reasonerState = ReasonerState.BEFORE_LOADING;
	private EdbIdbKnowledgeBase knowledgeBase;

	private LogLevel internalLogLevel = LogLevel.WARNING;
	private Algorithm algorithm = Algorithm.RESTRICTED_CHASE;
	private Integer timeoutAfterSeconds;
	private RuleRewriteStrategy ruleRewriteStrategy = RuleRewriteStrategy.NONE;

	/**
	 * Holds the state of the reasoning result. Has value {@code true} if reasoning
	 * has completed, {@code false} if it has been interrupted.
	 */
	private boolean reasoningCompleted;

	@Override
	public void setAlgorithm(final Algorithm algorithm) {
		Validate.notNull(algorithm, "Algorithm cannot be null!");
		this.algorithm = algorithm;
	}

	@Override
	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {
		if (seconds != null) {
			Validate.isTrue(seconds > 0, "Only strictly positive timeout period alowed!", seconds);
		}
		timeoutAfterSeconds = seconds;
	}

	@Override
	public Integer getReasoningTimeout() {
		return timeoutAfterSeconds;
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) throws ReasonerStateException {
		Validate.notNull(ruleRewritingStrategy, "Rewrite strategy cannot be null!");
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState,
					"Rules cannot be re-writen after the reasoner has been loaded! Call reset() to undo loading and reasoning.");
		}
		this.ruleRewriteStrategy = ruleRewritingStrategy;
	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return this.ruleRewriteStrategy;
	}

	@Override
	public void load(EdbIdbKnowledgeBase knowledgeBase)
			throws EdbIdbSeparationException, IOException, IncompatiblePredicateArityException {
		if (this.reasonerState != ReasonerState.BEFORE_LOADING) {
			LOGGER.warn("This method call is ineffective: the Reasoner has already been loaded.");
		} else {
			if (knowledgeBase.getReasoner() != null) {
				throw new IllegalArgumentException("A knowledge base cannot be loaded into multiple reasoners.");
			}
			this.knowledgeBase = knowledgeBase;
			knowledgeBase.setReasoner(this);

			this.knowledgeBase.validateEdbIdbSeparation();

			this.reasonerState = ReasonerState.AFTER_LOADING;

			if (knowledgeBase.getDataSourceForPredicate().isEmpty() && knowledgeBase.getFactsForPredicate().isEmpty()) {
				LOGGER.warn("No facts have been provided.");
			}

			try {
				this.vLog.start(generateDataSourcesConfig(), false);
			} catch (final AlreadyStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			} catch (final EDBConfigurationException e) {
				throw new RuntimeException("Invalid data sources configuration.", e);
			}

			validateDataSourcePredicateArities();

			loadInMemoryFacts();

			if (knowledgeBase.getRules().isEmpty()) {
				LOGGER.warn("No rules have been provided for reasoning.");
			} else {
				loadRules();
			}
		}
	}

	ReasonerState getReasonerState() {
		return reasonerState;
	}

	private void validateDataSourcePredicateArities() throws IncompatiblePredicateArityException {
		for (final Predicate predicate : this.knowledgeBase.getDataSourceForPredicate().keySet()) {
			final int dataSourcePredicateArity;
			try {
				dataSourcePredicateArity = this.vLog.getPredicateArity(ModelToVLogConverter.toVLogPredicate(predicate));
			} catch (final NotStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			}
			if (predicate.getArity() != dataSourcePredicateArity) {
				throw new IncompatiblePredicateArityException(predicate, dataSourcePredicateArity,
						this.knowledgeBase.getDataSourceForPredicate().get(predicate));
			}
		}

	}

	@Override
	public boolean reason() throws IOException, ReasonerStateException {
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Reasoning is not allowed before loading!");
		} else if (this.reasonerState == ReasonerState.AFTER_REASONING) {
			LOGGER.warn(
					"This method call is ineffective: this Reasoner has already reasoned. Successive reason() calls are not supported. Call reset() to undo loading and reasoning and reload to be able to reason again");
		} else {
			this.reasonerState = ReasonerState.AFTER_REASONING;

			final boolean skolemChase = this.algorithm == Algorithm.SKOLEM_CHASE;
			try {
				if (timeoutAfterSeconds == null) {
					this.vLog.materialize(skolemChase);
					reasoningCompleted = true;
				} else {
					reasoningCompleted = this.vLog.materialize(skolemChase, timeoutAfterSeconds);
				}
			} catch (final NotStartedException e) {
				throw new RuntimeException("Inconsistent reasoner state.", e);
			}
		}
		return reasoningCompleted;
	}

	@Override
	public QueryResultIterator answerQuery(Atom queryAtom, boolean includeBlanks) throws ReasonerStateException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		Validate.notNull(queryAtom, "Query atom must not be null!");

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(queryAtom);
		TermQueryResultIterator stringQueryResultIterator;
		try {
			stringQueryResultIterator = this.vLog.query(vLogAtom, true, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
		return new QueryResultIterator(stringQueryResultIterator);
	}

	@Override
	public void exportQueryAnswersToCsv(final Atom queryAtom, final String csvFilePath, final boolean includeBlanks)
			throws ReasonerStateException, IOException {
		final boolean filterBlanks = !includeBlanks;
		if (this.reasonerState == ReasonerState.BEFORE_LOADING) {
			throw new ReasonerStateException(this.reasonerState, "Querying is not alowed before reasoner is loaded!");
		}
		Validate.notNull(queryAtom, "Query atom must not be null!");
		Validate.notNull(csvFilePath, "File to export query answer to must not be null!");
		Validate.isTrue(csvFilePath.endsWith(CsvFileDataSource.CSV_FILE_EXTENSION),
				"Expected .csv extension for file [%s]!", csvFilePath);

		final karmaresearch.vlog.Atom vLogAtom = ModelToVLogConverter.toVLogAtom(queryAtom);
		try {
			this.vLog.writeQueryResultsToCsv(vLogAtom, csvFilePath, filterBlanks);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
	}

	@Override
	public void resetReasoner() {
		this.reasonerState = ReasonerState.BEFORE_LOADING;
		this.vLog.stop();
		LOGGER.warn(
				"Reasoner has been reset. All inferences computed during reasoning have been discarded. More data and rules can be added after resetting. The reasoner needs to be loaded again to perform querying and reasoning.");
	}

	@Override
	public void close() {
		this.vLog.stop();
	}

	String generateDataSourcesConfig() {
		final StringBuilder configStringBuilder = new StringBuilder();
		int dataSourceIndex = 0;
		for (final Predicate predicate : this.knowledgeBase.getDataSourceForPredicate().keySet()) {
			final DataSource dataSource = this.knowledgeBase.getDataSourceForPredicate().get(predicate);
			try (final Formatter formatter = new Formatter(configStringBuilder);) {
				formatter.format(dataSource.toConfigString(), dataSourceIndex,
						ModelToVLogConverter.toVLogPredicate(predicate));
			}
			dataSourceIndex++;
		}
		return configStringBuilder.toString();
	}

	private void loadInMemoryFacts() {
		for (final Predicate predicate : this.knowledgeBase.getFactsForPredicate().keySet()) {
			final Set<Atom> factsForPredicate = this.knowledgeBase.getFactsForPredicate().get(predicate);

			final String vLogPredicate = ModelToVLogConverter.toVLogPredicate(predicate);
			final String[][] tuplesForPredicate = ModelToVLogConverter.toVLogFactTuples(factsForPredicate);
			try {
				this.vLog.addData(vLogPredicate, tuplesForPredicate);
			} catch (final EDBConfigurationException e) {
				throw new RuntimeException("Invalid data sources configuration.", e);
			}
		}
	}

	private void loadRules() {
		final karmaresearch.vlog.Rule[] vLogRuleArray = ModelToVLogConverter
				.toVLogRuleArray(this.knowledgeBase.getRules());
		final karmaresearch.vlog.VLog.RuleRewriteStrategy vLogRuleRewriteStrategy = ModelToVLogConverter
				.toVLogRuleRewriteStrategy(this.ruleRewriteStrategy);
		try {
			this.vLog.setRules(vLogRuleArray, vLogRuleRewriteStrategy);
		} catch (final NotStartedException e) {
			throw new RuntimeException("Inconsistent reasoner state.", e);
		}
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		Validate.notNull(logLevel, "Log level cannot be null!");
		this.internalLogLevel = logLevel;
		this.vLog.setLogLevel(ModelToVLogConverter.toVLogLogLevel(internalLogLevel));
	}

	@Override
	public LogLevel getLogLevel() {
		return internalLogLevel;
	}

	@Override
	public void setLogFile(String filePath) {
		this.vLog.setLogFile(filePath);
	}

}
