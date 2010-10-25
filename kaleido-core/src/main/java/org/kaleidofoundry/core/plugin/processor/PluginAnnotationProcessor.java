/*  
 * Copyright 2008-2010 the original author or authors 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaleidofoundry.core.plugin.processor;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static org.kaleidofoundry.core.i18n.InternalBundleHelper.PluginMessageBundle;
import static org.kaleidofoundry.core.plugin.PluginConstants.META_PLUGIN_FILE;
import static org.kaleidofoundry.core.plugin.PluginConstants.META_PLUGIN_IMPLEMENTATION_FILE;
import static org.kaleidofoundry.core.plugin.PluginConstants.META_PLUGIN_PATH;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.AbstractElementVisitor6;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;

import org.kaleidofoundry.core.plugin.Declare;
import org.kaleidofoundry.core.plugin.PluginInspector;
import org.kaleidofoundry.core.plugin.PluginRegistryException;

/**
 * Java 6 annotation processor, it is use to introspect (at compile time), specific kaleido plugin annotations :<br/>
 * Use META-INF/services/javax.annotation.processing.Processor text file to enumerate your processor factory <br/>
 * Handle annotation : <br/>
 * <ul>
 * <li>{@link Declare}</li>
 * </ul>
 * <br/>
 * Processor will generate two resources files, use to registry plugin and plugin implementation at runtime. <br/>
 * This two files are generated at compile time in :
 * <ul>
 * <li>META-INF/org.kaleidofoundry.core.plugin</li>
 * <li>META-INF/org.kaleidofoundry.core.plugin.implementation</li>
 * </ul>
 * This files have to be include in your jar class resources.
 * 
 * @see Declare
 * @author Jerome RADUGET
 */
@SupportedAnnotationTypes({ "org.kaleidofoundry.core.plugin.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
// @SupportedOptions( { "outputFile" })
public class PluginAnnotationProcessor extends AbstractProcessor {

   protected ProcessingEnvironment environment;

   /*
    * (non-Javadoc)
    * @see javax.annotation.processing.AbstractProcessor#init(javax.annotation.processing.ProcessingEnvironment)
    */
   @Override
   public void init(final ProcessingEnvironment environment) {
	this.environment = environment;
   }

   /*
    * (non-Javadoc)
    * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
    */
   @Override
   public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {

	final RegistryPluginVisitor registryVisitor = new RegistryPluginVisitor(environment);

	boolean processedDeclarePlugin = false;

	// scan and accept DeclarePlugin annotation
	for (final Element element : roundEnvironment.getElementsAnnotatedWith(Declare.class)) {
	   element.accept(registryVisitor, null);
	   processedDeclarePlugin = true;
	}

	if (processedDeclarePlugin) {

	   try {

		final PluginInspector pluginInspector = new PluginInspector();

		// if all is right, create interface & class file
		createOutputFile(registryVisitor.getPluginsSet(), SOURCE_OUTPUT, META_PLUGIN_PATH, META_PLUGIN_FILE,
			PluginMessageBundle.getMessage("plugin.info.processor.load.createOutputFilePlugin", pluginInspector.getPluginMetaInfPath()));

		createOutputFile(registryVisitor.getPluginsImplementationsSet(), SOURCE_OUTPUT, META_PLUGIN_PATH, META_PLUGIN_IMPLEMENTATION_FILE,
			PluginMessageBundle.getMessage("plugin.info.processor.load.createOutputFilePluginImpl", pluginInspector.getPluginImplementationMetaInfPath()));

		// load meta informations
		pluginInspector.loadPluginMetaData();
		pluginInspector.loadPluginImplementationMetaData();

		// print load processing messages
		for (final String message : pluginInspector.getEchoMessages()) {
		   environment.getMessager().printMessage(Kind.NOTE, message);
		}

		// coherence check
		final Set<PluginRegistryException> pluginRegistryExceptions = pluginInspector.checkDeclarations();

		if (!pluginRegistryExceptions.isEmpty()) {
		   // print coherence check errors messages
		   final StringBuilder errorMessage = new StringBuilder();
		   for (final PluginRegistryException pre : pluginRegistryExceptions) {
			errorMessage.append("\t").append(pre.getMessage()).append("\n");
		   }

		   environment.getMessager().printMessage(Kind.ERROR, errorMessage.toString());

		} else {
		   environment.getMessager().printMessage(Kind.NOTE, PluginMessageBundle.getMessage("plugin.info.processor.load.checkOk"));
		}

	   } catch (final PluginRegistryException pre) {
		// print registry error in console
		environment.getMessager().printMessage(Kind.ERROR, pre.getMessage());
	   }
	}

	return processedDeclarePlugin;
   }

   /**
    * Create output resource file, using {@link RegistryPluginVisitor} datas
    * 
    * @param qualifierNames
    * @param stdLocation
    * @param packagename
    * @param relativeOutputFilename
    * @param messageNotif
    */
   protected void createOutputFile(final Set<String> qualifierNames, final StandardLocation stdLocation, final String packagename,
	   final String relativeOutputFilename, final String messageNotif) {

	final Filer filer = environment.getFiler();
	final Messager messager = environment.getMessager();
	Writer resourceWriter = null;

	try {
	   // create resource file with env Filter
	   messager.printMessage(Kind.NOTE, messageNotif);
	   resourceWriter = filer.createResource(stdLocation, packagename, relativeOutputFilename).openWriter();

	   // for each annotation found on interface or class, write qualified name in text resource file
	   for (final String qualifier : qualifierNames) {
		messager.printMessage(Kind.NOTE, "\t" + qualifier);
		resourceWriter.append(qualifier).append("\n");
	   }

	} catch (final IOException ioe) {

	   final String errorMessage = PluginMessageBundle.getMessage("plugin.error.processor.createOutputFilePlugin",
		   packagename + "/" + relativeOutputFilename, ioe.getMessage());

	   environment.getMessager().printMessage(Kind.ERROR, errorMessage);

	   throw new IllegalStateException(errorMessage);
	} finally {
	   try {
		if (resourceWriter != null) {
		   resourceWriter.close();
		}
	   } catch (final IOException ioe) {
	   }
	   ;
	}
   }

   /**
    * Annotation visitor for registering {@link Declare} use
    * 
    * @author Jerome RADUGET
    */
   static class RegistryPluginVisitor extends AbstractElementVisitor6<Void, Void> {

	final Set<String> pluginsSet;
	final Set<String> pluginsImplementationsSet;
	final ProcessingEnvironment environment;

	/**
	 * @param environment
	 */
	public RegistryPluginVisitor(final ProcessingEnvironment environment) {
	   this.environment = environment;
	   pluginsSet = new TreeSet<String>();
	   pluginsImplementationsSet = new TreeSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.element.ElementVisitor#visitType(javax.lang.model.element.TypeElement, java.lang.Object)
	 */
	@Override
	public Void visitType(final TypeElement e, final Void p) {
	   switch (e.getKind()) {
	   case CLASS: {
		final Declare registerPluginAnnot = e.getAnnotation(Declare.class);

		if (registerPluginAnnot != null) {
		   handleDeclarePluginImplementation(e, registerPluginAnnot);
		}
		break;
	   }
	   case INTERFACE: {
		final Declare registerPluginAnnot = e.getAnnotation(Declare.class);

		if (registerPluginAnnot != null) {
		   handleDeclarePlugin(e, registerPluginAnnot);
		}
		break;
	   }
	   default: {
		break;
	   }
	   }

	   return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.element.ElementVisitor#visitExecutable(javax.lang.model.element.ExecutableElement, java.lang.Object)
	 */
	@Override
	public Void visitExecutable(final ExecutableElement e, final Void p) {
	   return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.element.ElementVisitor#visitPackage(javax.lang.model.element.PackageElement, java.lang.Object)
	 */
	@Override
	public Void visitPackage(final PackageElement e, final Void p) {
	   return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.element.ElementVisitor#visitTypeParameter(javax.lang.model.element.TypeParameterElement, java.lang.Object)
	 */
	@Override
	public Void visitTypeParameter(final TypeParameterElement e, final Void p) {
	   return null;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.element.ElementVisitor#visitVariable(javax.lang.model.element.VariableElement, java.lang.Object)
	 */
	@Override
	public Void visitVariable(final VariableElement e, final Void p) {
	   return null;
	}

	/**
	 * process handler for DeclarePlugin annotated interface
	 * 
	 * @param interfaceDeclaration
	 * @param declarePluginAnnot
	 */
	void handleDeclarePlugin(final TypeElement interfaceDeclaration, final Declare declarePluginAnnot) {
	   if (declarePluginAnnot.enable()) {
		pluginsSet.add(interfaceDeclaration.getQualifiedName().toString());
	   }
	}

	/**
	 * process handler for DeclarePluginImplementation annotated implementation class
	 * 
	 * @param classDeclaration
	 * @param declarePluginImplementation
	 */
	void handleDeclarePluginImplementation(final TypeElement classDeclaration, final Declare declarePluginImplementation) {
	   if (declarePluginImplementation.enable()) {
		pluginsImplementationsSet.add(classDeclaration.getQualifiedName().toString());
	   }
	}

	/**
	 * @return the pluginsSet
	 */
	Set<String> getPluginsSet() {
	   return pluginsSet;
	}

	/**
	 * @return the pluginsImplementationsSet
	 */
	Set<String> getPluginsImplementationsSet() {
	   return pluginsImplementationsSet;
	}
   }
}
