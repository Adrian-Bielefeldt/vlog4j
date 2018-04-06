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
public class ExportVLogReasoner {
	public static void main(String[] args) throws OWLOntologyCreationException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, ReasonerStateException {
		String tboxFilePath = "/Users/carralma/Desktop/vlog-eval/chembl/chembl-tbox-addfiltered-vlog-noInds.ttl";
		String edbInfoFilePath = "/Users/carralma/Desktop/vlog-eval/chembl/chembl-classAss-objPropAss-csv/chembl-classAss-ObjPropAss-csv-commas-010/edb-config-info.txt";
		String csvFilesDirPath = "/Users/carralma/Desktop/vlog-eval/chembl/chembl-classAss-objPropAss-csv/chembl-classAss-objPropAss-csv-commas-010";
		String queryId = "chembl3";
		// Rule queryRule = Queries.getQuery(queryId);
		// System.out.println(queryRule);

		StringBuilder evaluationMetrics = new StringBuilder(csvFilesDirPath);
		long startTime = System.currentTimeMillis();

		Reasoner reasoner = Reasoner.getInstance();
		reasoner.setLogLevel(LogLevel.ERROR);
		reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);

		// Loading OWL ontology TBox axioms
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology tbox = manager.loadOntologyFromOntologyDocument(new File(tboxFilePath));
		long tboxLoaded = System.currentTimeMillis();
		evaluationMetrics.append(",").append((tboxLoaded - startTime));

		// Converting axioms
		OwlToRulesConverter converter = new OwlToRulesConverter();
		converter.addOntology(tbox);
		long tboxConverted = System.currentTimeMillis();
		evaluationMetrics.append(",").append((tboxConverted - tboxLoaded));

		// Loading ontology into VLog
		reasoner.addRules(new ArrayList<Rule>(converter.getRules()));
		reasoner.addRules(ChemblQueries.getChemblQuery("chembl1"));
		reasoner.addRules(ChemblQueries.getChemblQuery("chembl2"));
		reasoner.addRules(ChemblQueries.getChemblQuery("chembl3"));
		reasoner.addRules(ChemblQueries.getChemblQuery("chembl4"));
		reasoner.addFacts(converter.getFacts());
		loadSourceData(edbInfoFilePath, csvFilesDirPath, reasoner);
		reasoner.load();
		long reasonerLoaded = System.currentTimeMillis();
		evaluationMetrics.append(",").append((reasonerLoaded - tboxConverted));

		// Reasoning with VLog
		reasoner.reason();
		long reasonerReasoned = System.currentTimeMillis();
		evaluationMetrics.append(",").append((reasonerReasoned - reasonerLoaded));

		// Querying
		uh("chembl1", evaluationMetrics, reasoner, reasonerReasoned);
		uh("chembl2", evaluationMetrics, reasoner, reasonerReasoned);
		uh("chembl3", evaluationMetrics, reasoner, reasonerReasoned);
		uh("chembl4", evaluationMetrics, reasoner, reasonerReasoned);

		// @NonNull
		// Atom queryAtom = parseQueryAtom(query);
		// long queryParsed = System.currentTimeMillis();
		// reasoner.answerQuery(queryAtom, true);
		// evaluationInformation.append(",").append((System.currentTimeMillis() -
		// queryParsed));

		reasoner.close();
		System.out.println(evaluationMetrics);
	}

	private static void uh(String queryId, StringBuilder evaluationMetrics, Reasoner reasoner, long reasonerReasoned)
			throws ReasonerStateException {
		QueryResultIterator answers = reasoner.answerQuery(Expressions.makeAtom(Expressions.makePredicate(queryId, 2),
				Expressions.makeVariable("x"), Expressions.makeVariable("y")), true);
		int answerCounter = 0;
		while (answers.hasNext()) {
			answers.next();
			answerCounter++;
		}
		System.out.println(answerCounter);
		evaluationMetrics.append(",").append((System.currentTimeMillis() - reasonerReasoned));
		evaluationMetrics.append(",").append(answerCounter);
	}

	private static void loadSourceData(String edbInfoFilePath, String csvFilesDirPath, Reasoner reasoner)
			throws FileNotFoundException, IOException, ReasonerStateException {
		BufferedReader br = new BufferedReader(new FileReader(edbInfoFilePath));
		String edbConfigLine;
		while ((edbConfigLine = br.readLine()) != null) {
			String edbPredicateName = edbConfigLine.substring(1, edbConfigLine.indexOf(" ") - 1) + "EDB";
			String predicateName = edbConfigLine.substring(1, edbConfigLine.indexOf(" ") - 1);
			int arity = Integer
					.parseInt(edbConfigLine.substring(edbConfigLine.lastIndexOf(" ") + 1, edbConfigLine.length()));
			Predicate edbPredicate = new PredicateImpl(edbPredicateName, arity);
			Predicate predicate = new PredicateImpl(predicateName, arity);

			ArrayList<Term> arguments = new ArrayList<>();
			for (int i = 0; i < arity; i++)
				arguments.add(Expressions.makeVariable("X" + i));
			reasoner.addRules(new RuleImpl(Expressions.makeConjunction(new AtomImpl(predicate, arguments)),
					Expressions.makeConjunction(new AtomImpl(edbPredicate, arguments))));

			String fileName = edbConfigLine.substring(edbConfigLine.indexOf(" ") + 1, edbConfigLine.lastIndexOf(" "));
			String pathname = csvFilesDirPath + File.separator + fileName;
			final DataSource edbDataSource = new CsvFileDataSource(new File(pathname));

			reasoner.addFactsFromDataSource(edbPredicate, edbDataSource);
		}
		br.close();
	}

}
