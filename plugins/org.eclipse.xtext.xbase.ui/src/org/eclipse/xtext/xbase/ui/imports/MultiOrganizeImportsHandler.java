/*******************************************************************************
 * Copyright (c) 2014 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.ui.imports;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.MultiOrganizeImportAction;
import org.eclipse.jdt.internal.ui.browsing.LogicalPackage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.FileExtensionProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * First runs JDT Organize imports.<br>
 * Than, collects all NonJavaResources in a "files per project" {@link Multimap}.<br>
 * The result is forwarded to the {@link MultiImportOrganizer}.
 * 
 * @since 2.7
 * 
 * @author Dennis Huebner (dhuebner) - Initial contribution and API
 */
@Singleton
public class MultiOrganizeImportsHandler extends AbstractHandler {
	@Inject
	private FileExtensionProvider fileExtensions;
	@Inject
	private Provider<MultiImportOrganizer> importOrganizerProvider;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchSite activeSite = HandlerUtil.getActiveSite(event);
		MultiOrganizeImportAction javaDelegate = new MultiOrganizeImportAction(activeSite);
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) currentSelection;
			if (shouldRunJavaOrganizeImports()) {
				ICompilationUnit[] compilationUnits = javaDelegate.getCompilationUnits(structuredSelection);
				if (compilationUnits.length > 0) {
					javaDelegate.run(structuredSelection);
				}
			}
			Multimap<IProject, IFile> files = collectFiles(structuredSelection);
			final List<Change> organizeImports = importOrganizerProvider.get().organizeImports(files);
			Shell shell = activeSite.getShell();

			IRunnableWithProgress op = new IRunnableWithProgress() {

				public void run(IProgressMonitor mon) throws InvocationTargetException, InterruptedException {
					int totalWork = organizeImports.size();
					mon.beginTask(Messages.OrganizeImports, totalWork);
					for (int i = 0; !mon.isCanceled() && i < organizeImports.size(); i++) {
						Change change = organizeImports.get(i);
						mon.setTaskName(Messages.OrganizeImports + " - Xtend (" + (i + 1)
								+ " of " + totalWork + ")");
						try {
							mon.subTask(change.getName());
							change.perform(new SubProgressMonitor(mon, 1));
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
					}
				}
			};
			try {
				new ProgressMonitorDialog(shell).run(true, true, op);
			} catch (InvocationTargetException e) {
				handleException(e);
			} catch (InterruptedException e) {
				handleException(e);
			}
		}
		return event.getApplicationContext();
	}

	private void handleException(Exception e) throws ExecutionException {
		throw new ExecutionException(Messages.MultiOrganizeImportsHandler_exceptionMessage, e);
	}

	/**
	 * Subclasses may override to collect the relevant resources.
	 */
	protected boolean shouldHandleFile(IFile object) {
		String fileExtension = object.getFileExtension();
		return fileExtension != null && fileExtensions.getFileExtensions().contains(fileExtension);
	}

	/**
	 * Should JDT's MultiOrganizeImportAction be executed before perform.
	 */
	protected boolean shouldRunJavaOrganizeImports() {
		return true;
	}

	private Multimap<IProject, IFile> collectFiles(IStructuredSelection structuredSelection) {
		Multimap<IProject, IFile> result = HashMultimap.create();
		for (Object object : structuredSelection.toList()) {
			collectRelevantFiles(object, result);
		}
		return result;
	}

	private void collectRelevantFiles(Object element, Multimap<IProject, IFile> result) {
		try {
			if (element instanceof IJavaElement) {
				IJavaElement elem = (IJavaElement) element;
				if (elem.exists()) {
					switch (elem.getElementType()) {
						case IJavaElement.PACKAGE_FRAGMENT:
							collectRelevantFiles((IPackageFragment) elem, result);
							break;
						case IJavaElement.PACKAGE_FRAGMENT_ROOT:
							collectRelevantFiles((IPackageFragmentRoot) elem, result);
							break;
						case IJavaElement.JAVA_PROJECT:
							IPackageFragmentRoot[] roots;
							roots = ((IJavaProject) elem).getPackageFragmentRoots();
							for (int k = 0; k < roots.length; k++) {
								collectRelevantFiles(roots[k], result);
							}
							break;
					}
				}
			} else if (element instanceof LogicalPackage) {
				IPackageFragment[] packageFragments = ((LogicalPackage) element).getFragments();
				for (int k = 0; k < packageFragments.length; k++) {
					IPackageFragment pack = packageFragments[k];
					if (pack.exists()) {
						collectRelevantFiles(pack, result);
					}
				}
			} else if (element instanceof IWorkingSet) {
				IWorkingSet workingSet = (IWorkingSet) element;
				IAdaptable[] elements = workingSet.getElements();
				for (int j = 0; j < elements.length; j++) {
					collectRelevantFiles(elements[j], result);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void collectRelevantFiles(IPackageFragment element, Multimap<IProject, IFile> result)
			throws JavaModelException {
		collectIFiles(result, element.getNonJavaResources());
	}

	private void collectRelevantFiles(IPackageFragmentRoot root, Multimap<IProject, IFile> result)
			throws JavaModelException {
		if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
			IJavaElement[] children = root.getChildren();
			for (int i = 0; i < children.length; i++) {
				collectRelevantFiles((IPackageFragment) children[i], result);
			}
		}
	}

	private void collectIFiles(Multimap<IProject, IFile> result, Object[] nonJavaResources) {
		for (Object object : nonJavaResources) {
			if (object instanceof IFile) {
				IFile iFile = (IFile) object;
				if (shouldHandleFile(iFile))
					result.put(iFile.getProject(), iFile);
			}
		}
	}
}