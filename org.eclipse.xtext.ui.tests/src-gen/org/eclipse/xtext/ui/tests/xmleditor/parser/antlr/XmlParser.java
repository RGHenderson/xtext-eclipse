/*
 * generated by Xtext
 */
package org.eclipse.xtext.ui.tests.xmleditor.parser.antlr;

import com.google.inject.Inject;
import org.eclipse.xtext.parser.antlr.AbstractAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.ui.tests.xmleditor.parser.antlr.internal.InternalXmlParser;
import org.eclipse.xtext.ui.tests.xmleditor.services.XmlGrammarAccess;

public class XmlParser extends AbstractAntlrParser {

	@Inject
	private XmlGrammarAccess grammarAccess;

	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens();
	}
	

	@Override
	protected InternalXmlParser createParser(XtextTokenStream stream) {
		return new InternalXmlParser(stream, getGrammarAccess());
	}

	@Override 
	protected String getDefaultRuleName() {
		return "XmlDocument";
	}

	public XmlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(XmlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
