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
package org.kaleidofoundry.core.util;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;

/**
 * Some helper methods for aspectj
 * 
 * @author Jerome RADUGET
 */
public abstract class AspectjHelper {

   /**
    * Helper method for debugging joint point
    * 
    * @param logger
    * @param jp
    */
   public static void debugJoinPoint(final Logger logger, final JoinPoint jp) {

	if (logger.isDebugEnabled()) {
	   logger.debug("\t<joinPoint.description>\t\t{} ", jp.toString());
	   logger.debug("\t<joinpoint.static.signature>\t{}", jp.getStaticPart().getSignature());
	   // logger.debug("\t<joinpoint.target.instance>\t{}", jp.getTarget());

	   if (jp.getSignature() instanceof FieldSignature) {
		FieldSignature fs = (FieldSignature) jp.getSignature();
		logger.debug("\t<joinpoint.field>\t\tname = {} ; class = {}", fs.getField().getName(), fs.getDeclaringType().getName());
	   }

	   String[] parametersName = null;
	   Annotation[][] parametersAnnotations = null;

	   for (int cpt = 0; cpt < jp.getArgs().length; cpt++) {

		if (jp.getSignature() instanceof ConstructorSignature) {
		   parametersName = ((ConstructorSignature) jp.getSignature()).getParameterNames();
		   parametersAnnotations = ((ConstructorSignature) jp.getSignature()).getConstructor().getParameterAnnotations();
		}
		if (jp.getSignature() instanceof MethodSignature) {
		   parametersName = ((MethodSignature) jp.getSignature()).getParameterNames();
		   parametersAnnotations = ((MethodSignature) jp.getSignature()).getMethod().getParameterAnnotations();
		}

		if (parametersName != null) {
		   StringBuilder str = new StringBuilder();
		   str.append("\t\targ[" + cpt + "] : ");
		   str.append("name=").append(parametersName[cpt]);
		   str.append("\tvalue=").append(jp.getArgs()[cpt]);
		   str.append("\tannotations=");
		   if (parametersAnnotations != null) {
			for (Annotation paramAnnot : parametersAnnotations[cpt]) {
			   str.append(paramAnnot.toString()).append(" ");
			}
		   }
		   logger.debug("{}", str);
		}
	   }
	}
   }
}
