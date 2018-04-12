package org.semanticweb.vlog4j.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.QueryResult;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.AtomImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.core.model.implementation.RuleImpl;
import org.semanticweb.vlog4j.core.reasoner.Algorithm;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.LogLevel;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.owlapi.OwlToRulesConverter;

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

/**
 * Hello world!
 *
 */
public class VLogReasonerLauncher {

	public static void main(String[] args) throws OWLOntologyCreationException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, ReasonerStateException {
		final String tboxFilePath = args[0];
		final String edbInfoFilePath = args[1];
		final String csvFilesDirPath = args[2];
		final String queryId = args[3];

		final StringBuilder evaluationMetrics = new StringBuilder(queryId);
		final long startTime = System.currentTimeMillis();

		final Reasoner reasoner = Reasoner.getInstance();
		reasoner.setLogLevel(LogLevel.ERROR);
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);

		// Loading OWL ontology TBox axioms
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology tbox = manager.loadOntologyFromOntologyDocument(new File(tboxFilePath));
		final long tboxLoaded = System.currentTimeMillis();
		evaluationMetrics.append(",").append((tboxLoaded - startTime));

		// Converting axioms
		final OwlToRulesConverter converter = new OwlToRulesConverter();
		converter.addOntology(tbox);
		final long tboxConverted = System.currentTimeMillis();
		evaluationMetrics.append(",").append((tboxConverted - tboxLoaded));

		// Loading ontology into VLog
		reasoner.addRules(new ArrayList<Rule>(converter.getRules()));
		System.out.println(Queries.getQuery(queryId));
		reasoner.addRules(Queries.getQuery(queryId));
		reasoner.addFacts(converter.getFacts());
		loadSourceData(edbInfoFilePath, csvFilesDirPath, reasoner);
		reasoner.load();
		final long reasonerLoaded = System.currentTimeMillis();
		evaluationMetrics.append(",").append((reasonerLoaded - tboxConverted));

		// Reasoning with VLog
		reasoner.reason();
		final long reasonerReasoned = System.currentTimeMillis();
		evaluationMetrics.append(",").append((reasonerReasoned - reasonerLoaded));

		// Querying
		final QueryResultIterator answers = reasoner
				.answerQuery(Expressions.makeAtom(Expressions.makePredicate(queryId, 2), Expressions.makeVariable("x"),
						Expressions.makeVariable("y")), true);
		int answerCounter = 0;
		int termsSizes = 0;
		while (answers.hasNext()) {
			final QueryResult answer = answers.next();
			termsSizes += answer.getTerms().size();
			answerCounter++;
		}

		evaluationMetrics.append(",").append((System.currentTimeMillis() - reasonerReasoned));
		evaluationMetrics.append(",").append(answerCounter);

		answers.close();
		reasoner.close();
		System.out.println(evaluationMetrics);
		System.out.println();
	}

	private static void loadSourceData(String edbInfoFilePath, String csvFilesDirPath, Reasoner reasoner)
			throws FileNotFoundException, IOException, ReasonerStateException {
		final BufferedReader br = new BufferedReader(new FileReader(edbInfoFilePath));
		String edbConfigLine;
		while ((edbConfigLine = br.readLine()) != null) {
			final String edbPredicateName = edbConfigLine.substring(1, edbConfigLine.indexOf(" ") - 1) + "EDB";
			final String predicateName = edbConfigLine.substring(1, edbConfigLine.indexOf(" ") - 1);
			final int arity = Integer
					.parseInt(edbConfigLine.substring(edbConfigLine.lastIndexOf(" ") + 1, edbConfigLine.length()));
			final Predicate edbPredicate = new PredicateImpl(edbPredicateName, arity);
			final Predicate predicate = new PredicateImpl(predicateName, arity);

			final ArrayList<Term> arguments = new ArrayList<>();
			for (int i = 0; i < arity; i++)
				arguments.add(Expressions.makeVariable("X" + i));
			reasoner.addRules(new RuleImpl(Expressions.makeConjunction(new AtomImpl(predicate, arguments)),
					Expressions.makeConjunction(new AtomImpl(edbPredicate, arguments))));

			final String fileName = edbConfigLine.substring(edbConfigLine.indexOf(" ") + 1,
					edbConfigLine.lastIndexOf(" "));
			final String pathname = csvFilesDirPath + File.separator + fileName;
			final DataSource edbDataSource = new CsvFileDataSource(new File(pathname));

			reasoner.addFactsFromDataSource(edbPredicate, edbDataSource);
		}
		br.close();
	}

}
