/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.utils;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.internal.CoreLog;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public class EditorUtils {

	public static Font fontFromFontData(FontData[] fontDataArray) {
		if (fontDataArray != null && fontDataArray.length > 0) {
			String fontData = PreferenceConverter.getStoredRepresentation(fontDataArray);
			if (!JFaceResources.getFontRegistry().hasValueFor(fontData)) {
				FontData[] fData = PreferenceConverter.basicGetFontData(fontData);
				JFaceResources.getFontRegistry().put(fontData, fData);
			}
			Font font = JFaceResources.getFontRegistry().get(fontData);
			return font;
		}
		;
		return null;
	}

	public static Color colorFromString(String rgbString) {
		if (rgbString != null && rgbString.trim().length() > 0) {
			Color col = JFaceResources.getColorRegistry().get(rgbString);
			try {
				if (col == null) {
					RGB rgb = StringConverter.asRGB(rgbString);
					JFaceResources.getColorRegistry().put(rgbString, rgb);
					col = JFaceResources.getColorRegistry().get(rgbString);
				}
			}
			catch (DataFormatException e) {
				CoreLog.logError("Corrupt color value: " + rgbString, e);
			}
			return col;
		}
		return null;
	}

	public static Color colorFromRGB(RGB rgb) {
		if (rgb == null)
			return null;
		return colorFromString(StringConverter.asString(rgb));
	}
}
