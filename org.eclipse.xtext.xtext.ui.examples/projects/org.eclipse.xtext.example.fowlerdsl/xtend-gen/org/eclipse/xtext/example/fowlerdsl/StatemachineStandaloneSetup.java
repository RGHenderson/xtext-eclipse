/**
 * generated by Xtext
 */
package org.eclipse.xtext.example.fowlerdsl;

import org.eclipse.xtext.example.fowlerdsl.StatemachineStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages
 * without equinox extension registry
 */
@SuppressWarnings("all")
public class StatemachineStandaloneSetup extends StatemachineStandaloneSetupGenerated {
  public static void doSetup() {
    new StatemachineStandaloneSetup().createInjectorAndDoEMFRegistration();
  }
}