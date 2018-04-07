package org.semanticweb.vlog4j.evaluation;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.VariableImpl;

public class LUBMQueries {

	private static final Variable x = new VariableImpl("x");
	private static final Variable y = new VariableImpl("y");
	private static final Variable z = new VariableImpl("z");
	private static final Variable w = new VariableImpl("w");
	private static final Variable u = new VariableImpl("u");
	private static final Variable v = new VariableImpl("v");
	private static final Variable t = new VariableImpl("t");

	private static final Predicate worksFor = Expressions
			.makePredicate("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor", 2);
	private static final Predicate publicationAuthor = Expressions
			.makePredicate("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor", 2);

	private static final String advisor = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#advisor";
	private static final String faculty = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Faculty";
	private static final String teacherOf = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf";
	private static final String course = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course";
	private static final String memberOf = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#memberOf";
	private static final String department = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Department";
	private static final String takesCourse = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#takesCourse";
	private static final String student = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Student";

	/*
	 * SELECT ?x ?z
	 * 
	 * WHERE
	 * 
	 * { ?x <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor> ?y .
	 * 
	 * ?z <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#worksFor> ?y .
	 * 
	 * ?w <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor>
	 * ?z . }
	 */
	private static final Atom lubm1Head = Expressions.makeAtom("lubm1", x, z);
	private static final Atom lubm1bodyAtom1 = Expressions.makeAtom(worksFor, x, y);
	private static final Atom lubm1bodyAtom2 = Expressions.makeAtom(worksFor, z, y);
	private static final Atom lubm1bodyAtom3 = Expressions.makeAtom(publicationAuthor, w, z);
	private static final Rule lubm1 = Expressions.makeRule(lubm1Head, lubm1bodyAtom1, lubm1bodyAtom2, lubm1bodyAtom3);

	/*
	 * SELECT?x WHERE
	 * 
	 * { ?x <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#advisor> ?y .
	 * 
	 * ?y <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	 * <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Faculty> .
	 * 
	 * ?y <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf> ?z .
	 * 
	 * ?z <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	 * <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course> .
	 * 
	 * ?y <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#memberOf> ?w .
	 * 
	 * ?w <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	 * <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Department> . }
	 */
	private static Atom lubm2Head = Expressions.makeAtom("lubm2", w, z);
	private static Atom bodyAtom4 = Expressions.makeAtom(memberOf, y, w);
	private static Atom bodyAtom3 = Expressions.makeAtom(teacherOf, y, z);

	private static final Rule lubm2 = Expressions.makeRule(lubm2Head, bodyAtom3, bodyAtom4);

	// SELECT?x?y?z*WHERE
	// { ?y <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#teacherOf> ?z .
	// ?z <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	// <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Course> .
	// ?x <http:// www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#advisor> ?y .
	// ?y <http:// www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://
	// www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Faculty> .
	// ?x <http:// www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#takesCourse> ?z .

	// ?x <http:// www.w3.org/1999/02/22-rdf-syntax-ns#type>
	// <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Student> }

	// lubm3-025_abox_commas,1451,56,6315,5,2,0
	private static final Atom lubm3Head = Expressions.makeAtom("lubm3", x, y);
	// private static final Atom lubm3Head = Expressions.makeAtom("lubm3", x, z);
	private static final Atom lubm3BodyAtom1 = Expressions.makeAtom(memberOf, x, z);//
	private static final Atom lubm3BodyAtom2 = Expressions.makeAtom(department, z);
	// private static final Atom lubm3BodyAtom3 = Expressions.makeAtom(advisor, x,
	// y);
	// private static final Atom lubm3BodyAtom4 = Expressions.makeAtom(faculty, y);
	private static final Atom lubm3BodyAtom5 = Expressions.makeAtom(takesCourse, x, y);
	// private static final Atom lubm3BodyAtom6 = Expressions
	// .makeAtom("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor",
	// x);//
	// private static final Rule lubm3 = Expressions.makeRule(lubm3Head,
	// lubm3BodyAtom1, lubm3BodyAtom2, lubm3BodyAtom3,
	// lubm3BodyAtom4, lubm3BodyAtom5, lubm3BodyAtom6);
	// private static final Rule lubm3 = Expressions.makeRule(lubm3Head,
	// lubm3BodyAtom1, lubm3BodyAtom2, lubm3BodyAtom3,
	// lubm3BodyAtom5, lubm3BodyAtom6);
	// private static final Rule lubm3 = Expressions.makeRule(lubm3Head,
	// lubm3BodyAtom3, lubm3BodyAtom5, lubm3BodyAtom6);
	private static final Rule lubm3 = Expressions.makeRule(lubm3Head, lubm3BodyAtom1, lubm3BodyAtom2, lubm3BodyAtom5);

	// SELECT? x WHERE {
	// ?x <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor>
	// ?z .
	// ?x <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor>
	// ?y .
	// ?z <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#advisor> ?y .
	// ?y <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
	// <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor> .
	// ?z <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#memberOf> ?w .
	// ?y <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#memberOf> ?w . }

	private static final Atom lubm4Head = Expressions.makeAtom("lubm4", z, y);
	private static final Atom lubm4BodyAtom1 = Expressions
			.makeAtom("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor", x, z);
	private static final Atom lubm4BodyAtom2 = Expressions
			.makeAtom("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#publicationAuthor", x, y);

	// private static final Atom lubm4BodyAtom3 = Expressions
	// .makeAtom("http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#Professor",
	// x);

	private static final Rule lubm4 = Expressions.makeRule(lubm4Head, lubm4BodyAtom1, lubm4BodyAtom2);

	public static Rule getLUBMQuery(String queryRuleId) {
		if (queryRuleId.endsWith("1"))
			return lubm1;
		else if (queryRuleId.endsWith("2"))
			return lubm2;
		else if (queryRuleId.endsWith("3"))
			return lubm3;
		else if (queryRuleId.endsWith("4"))
			return lubm4;
		return null;
	}
}
