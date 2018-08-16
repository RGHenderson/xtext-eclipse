/**
 * generated by Xtext
 */
package org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Polygon Based Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.PolygonBasedShape#getShape <em>Shape</em>}</li>
 * </ul>
 *
 * @see org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.NoTerminalExtensionPackage#getPolygonBasedShape()
 * @model
 * @generated
 */
public interface PolygonBasedShape extends EObject
{
  /**
   * Returns the value of the '<em><b>Shape</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.PolygonBasedNodeShape}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Shape</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Shape</em>' attribute.
   * @see org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.PolygonBasedNodeShape
   * @see #setShape(PolygonBasedNodeShape)
   * @see org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.NoTerminalExtensionPackage#getPolygonBasedShape_Shape()
   * @model
   * @generated
   */
  PolygonBasedNodeShape getShape();

  /**
   * Sets the value of the '{@link org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.PolygonBasedShape#getShape <em>Shape</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Shape</em>' attribute.
   * @see org.eclipse.xtext.ui.tests.editor.contentassist.noTerminalExtension.PolygonBasedNodeShape
   * @see #getShape()
   * @generated
   */
  void setShape(PolygonBasedNodeShape value);

} // PolygonBasedShape