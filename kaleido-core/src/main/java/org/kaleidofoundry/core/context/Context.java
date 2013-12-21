/*  
 * Copyright 2008-2014 the original author or authors 
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
package org.kaleidofoundry.core.context;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.kaleidofoundry.core.config.ConfigurationFactory;
import org.kaleidofoundry.core.config.ConfigurationRegistry;

/**
 * {@link Context} annotation can be used on a class field in order to provide / inject a {@link RuntimeContext} configuration instance to
 * this field<br/>
 * <ul>
 * <ol>
 * <li>the annotated field is a {@link RuntimeContext} class,</li>
 * <li>the annotated field class have an <b>accessible</b> {@link RuntimeContext} field (aggregation),</li>
 * </ol>
 * </ul>
 * In both case, the given field will be automatically created and feeded, using annotation meta parameters.
 * </p> <br/>
 * <hr/>
 * <p>
 * A {@link Context} annotation provides some static meta datas, like :
 * <ul>
 * <li>the context name ({@link #value()}) which is the configuration context identifier,</li>
 * <li>an optionally set of configuration ids ({@link #configurations()}), used to filter where the current runtime context can get its
 * properties</li>
 * <li>a array of {@link #parameters()}, which is a static context configuration stored in the class code (no need of external
 * configuration)</li>
 * </ul>
 * <br/>
 * <b>Be careful : {@link #parameters()} have priority to {@link #configurations()} (the external configuration ids)</b>
 * <p>
 * <hr/>
 * <p>
 * Several aop implementations will be provided :
 * <ul>
 * <li>aspectj (default) - {@link AnnotationContexInjectorAspect}</li>
 * <li>java EE CDI using @Inject</li>
 * <li>spring - incoming</li>
 * <li>guice - incoming</li>
 * </ul>
 * </p>
 * 
 * @author jraduget
 */
@Documented
@Target({ CONSTRUCTOR, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
public @interface Context {

   /**
    * @return context name identifier (used in external configuration). If not set, the context name would be the field or the parameter
    *         name
    */
   String value() default "";

   /**
    * @return configuration identifiers, where to find the context name and properties <br/>
    *         it is optional. if empty : then context name will be search in all registered configuration<br/>
    * @see ConfigurationFactory
    * @see ConfigurationRegistry
    */
   String[] configurations() default {};

   /**
    * does you allow dynamics {@link RuntimeContext} changes from configuration changes ? <br/>
    * <br/>
    * default value is <code>true</code>
    */
   boolean dynamics() default true;

   /**
    * @return defines here your static configuration parameters, that should no be changed at runtime<br/>
    *         if some parameter are set here, they will be used prior to the configurations items (having same name)
    */
   Parameter[] parameters() default {};


}
