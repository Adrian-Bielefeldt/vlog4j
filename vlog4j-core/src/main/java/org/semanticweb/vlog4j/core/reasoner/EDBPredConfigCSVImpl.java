package org.semanticweb.vlog4j.core.reasoner;

import java.io.File;

/*
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

public class EDBPredConfigCSVImpl implements EDBPredicateConfig {
	private final String predicateName;
	private final File sourceFile;

	public EDBPredConfigCSVImpl(final String predName, final File sourceFile) {
		if (!predName.endsWith(".csv")) {
			// TODO Throw Exception: File must end with .csv
		}
		this.predicateName = predName;
		this.sourceFile = sourceFile;
	}

	@Override
	public String getPredicate() {
		return this.predicateName;
	}

	@Override
	public File getSourceFile() {
		return this.sourceFile;
	}
}
