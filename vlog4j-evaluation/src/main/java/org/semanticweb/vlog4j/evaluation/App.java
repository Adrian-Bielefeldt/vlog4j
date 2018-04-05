package org.semanticweb.vlog4j.evaluation;

import java.io.BufferedReader;
import java.io.File;
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
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.CsvFileDataSource;
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
public class App {
	public static void main(String[] args) throws OWLOntologyCreationException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, ReasonerStateException {

		String tboxFilePath = "/Users/carralma/eclipse-workspace/vlog4j-parent/vlog4j-evaluation/src/main/data/uniprot-tbox-filtered-vlog.ttl";
		// String edbInfoFilePath = args[1];
		// String csvFilesDirPah = args[2];

		System.out.println("Args:");
		System.out.println(" - TBox: " + tboxFilePath);
		// System.out.println(" - EDB Config: " + edbInfoFilePath);
		// System.out.println(" - CSV filesFolder: " + csvFilesDirPah);

		Reasoner reasoner = Reasoner.getInstance();

		// Adding TBox axioms
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology tbox = manager.loadOntologyFromOntologyDocument(new File(tboxFilePath));
		OwlToRulesConverter converter = new OwlToRulesConverter();
		converter.addOntology(tbox);
		// reasoner.addRules(new ArrayList<Rule>(converter.getRules()));
		// reasoner.addFact
		//
		// String tboxFilePath = args[0];
		// String edbInfoFilePath = args[1];
		// String csvFilesDirPah = args[2];
		//
		// System.out.println("Args:");
		// System.out.println(" - TBox: " + tboxFilePath);
		// System.out.println(" - EDB Config: " + edbInfoFilePath);
		// System.out.println(" - CSV filesFolder: " + csvFilesDirPah);
		//
		// Reasoner reasoner = Reasoner.getInstance();
		//
		// // Adding TBox axioms
		// OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// OWLOntology tbox = manager.loadOntologyFromOntologyDocument(new
		// File(tboxFilePath));
		// OwlToRulesConverter converter = new OwlToRulesConverter();
		// converter.addOntology(tbox);
		// reasoner.addRules(new ArrayList<Rule>(converter.getRules()));
		// reasoner.addFacts(converter.getFacts());
		//
		// // Adding .csv data
		// BufferedReader br = new BufferedReader(new FileReader(edbInfoFilePath));
		// String edbConfigLine;
		// while ((edbConfigLine = br.readLine()) != null) {
		// String edbPredicateName = edbConfigLine.substring(1, edbConfigLine.indexOf("
		// ") - 1) + "EDB";
		// String predicateName = edbConfigLine.substring(1, edbConfigLine.indexOf(" ")
		// - 1);
		// int arity = Integer
		// .parseInt(edbConfigLine.substring(edbConfigLine.lastIndexOf(" ") + 1,
		// edbConfigLine.length()));
		// Predicate edbPredicate = new PredicateImpl(edbPredicateName, arity);
		// Predicate predicate = new PredicateImpl(predicateName, arity);
		//
		// ArrayList<Term> arguments = new ArrayList<>();
		// for (int i = 0; i < arity; i++)
		// arguments.add(Expressions.makeVariable("X" + i));
		// reasoner.addRules(new RuleImpl(Expressions.makeConjunction(new
		// AtomImpl(predicate, arguments)),
		// Expressions.makeConjunction(new AtomImpl(edbPredicate, arguments))));
		//
		// String fileName = edbConfigLine.substring(edbConfigLine.indexOf(" ") + 1,
		// edbConfigLine.lastIndexOf(" "));
		// final DataSource edbDataSource = new CsvFileDataSource(
		// new File(csvFilesDirPah + File.separator + fileName));
		//
		// reasoner.addFactsFromDataSource(edbPredicate, edbDataSource);
		// }
		// br.close();
		//
		// // Loading and reasoning
		// reasoner.load();
		// reasoner.reason();
		// reasoner.close();
	}
}
