/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.xtext.ui.editor.preferences.fields.LabelFieldEditor;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public class LanguageRootPreferencePage extends AbstractPreferencePage {

	@Override
	protected void createFieldEditors() {
		addField(new LabelFieldEditor("General settings for " + getLanguageName()
				+ " language.\nThe best language in the whole world!", getFieldEditorParent()));
		addField(new StringFieldEditor("testFeld", "Languagewide setting", getFieldEditorParent()));
	}

	@Override
	protected String qualifiedName() {
		return PreferenceConstants.languageTag(getServiceScope());
	}

}
