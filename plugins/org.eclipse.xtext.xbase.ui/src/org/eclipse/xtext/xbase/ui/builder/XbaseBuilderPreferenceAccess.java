/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.ui.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.compiler.JavaVersion;

import com.google.inject.Inject;

/**
 * @author Miro Spoenemann - Initial contribution and API
 */
public class XbaseBuilderPreferenceAccess {
	
	/**
	 * Preference identifier for generating <code>@SuppressWarnings</code>.
	 */
	public static final String PREF_GENERATE_SUPPRESS_WARNINGS = "generateSuppressWarnings"; //$NON-NLS-1$
	/**
	 * Preference identifier for generating <code>@Generated</code>.
	 */
	public static final String PREF_GENERATE_GENERATED = "generateGeneratedAnnotation"; //$NON-NLS-1$

	/**
	 * Preference identifier for including the current date in <code>@Generated</code> annotations.
	 */
	public static final String PREF_DATE_IN_GENERATED = "includeDateInGenerated"; //$NON-NLS-1$

	/**
	 * Preference identifier for the comment to add to <code>@Generated</code> annotations.
	 */
	public static final String PREF_GENERATED_COMMENT = "generatedAnnotationComment"; //$NON-NLS-1$
	
	/**
	 * Preference identifier for the Java language version of generated code.
	 */
	public static final String PREF_JAVA_VERSION = "targetJavaVersion"; //$NON-NLS-1$
	
	/**
	 * Preference identifier for using the version from the Java compiler compliance level.
	 */
	public static final String PREF_USE_COMPILER_COMPLIANCE = "useJavaCompilerCompliance"; //$NON-NLS-1$
	
	public static class Initializer extends BuilderPreferenceAccess.Initializer {

		@Override
		protected void initializeBuilderPreferences(IPreferenceStore store) {
			super.initializeBuilderPreferences(store);
			store.setDefault(PREF_GENERATE_SUPPRESS_WARNINGS, true);
			store.setDefault(PREF_GENERATE_GENERATED, false);
			store.setDefault(PREF_DATE_IN_GENERATED, false);
			store.setDefault(PREF_JAVA_VERSION, JavaVersion.JAVA5.toString());
			store.setDefault(PREF_USE_COMPILER_COMPLIANCE, true);
		}

	}

	@Inject	
	private IPreferenceStoreAccess preferenceStoreAccess;
	
	public void loadBuilderPreferences(GeneratorConfig generatorConfig, Object context) {
		IPreferenceStore preferenceStore = preferenceStoreAccess.getContextPreferenceStore(context);
		generatorConfig.setGenerateSyntheticSuppressWarnings(preferenceStore.getBoolean(PREF_GENERATE_SUPPRESS_WARNINGS));
		generatorConfig.setGenerateGeneratedAnnotation(preferenceStore.getBoolean(PREF_GENERATE_GENERATED));
		if (generatorConfig.isGenerateGeneratedAnnotation()) {
			generatorConfig.setIncludeDateInGeneratedAnnotation(preferenceStore.getBoolean(PREF_DATE_IN_GENERATED));
			generatorConfig.setGeneratedAnnotationComment(preferenceStore.getString(PREF_GENERATED_COMMENT));
		}
		generatorConfig.setJavaSourceVersion(getJavaVersion(context, preferenceStore));
	}

	public JavaVersion getJavaVersion(Object context, IPreferenceStore preferenceStore) {
		boolean useCompilerCompliance = preferenceStore.getBoolean(PREF_USE_COMPILER_COMPLIANCE);
		if (useCompilerCompliance) {
			IJavaProject javaProject = JavaCore.create(context instanceof IProject ? (IProject) context : null);
			String compilerCompliance;
			if (javaProject != null && javaProject.exists() && javaProject.getProject().isAccessible()) {
				compilerCompliance = javaProject.getOption(JavaCore.COMPILER_SOURCE, true);
			} else {
				compilerCompliance = JavaCore.getOption(JavaCore.COMPILER_SOURCE);
			}
			return fromCompilerCompliance(compilerCompliance);
		} else {
			String stringValue = preferenceStore.getString(PREF_JAVA_VERSION);
			try {
				return JavaVersion.valueOf(stringValue);
			} catch (IllegalArgumentException exception) {
				// Fall back to default value
			}
		}
		return JavaVersion.JAVA5;
	}
	
	public void setJavaVersion(Object context, JavaVersion version) {
		IPreferenceStore preferenceStore = preferenceStoreAccess.getWritablePreferenceStore(context);
		preferenceStore.setValue(PREF_USE_COMPILER_COMPLIANCE, false);
		preferenceStore.setValue(PREF_JAVA_VERSION, version.toString());
	}
	
	private JavaVersion fromCompilerCompliance(String compilerCompliance) {
		if ("1.8".equals(compilerCompliance))
			return JavaVersion.JAVA8;
		else if ("1.7".equals(compilerCompliance))
			return JavaVersion.JAVA7;
		else if ("1.6".equals(compilerCompliance))
			return JavaVersion.JAVA6;
		else
			// Versions lower than 1.5 are not supported
			return JavaVersion.JAVA5;
	}

}
