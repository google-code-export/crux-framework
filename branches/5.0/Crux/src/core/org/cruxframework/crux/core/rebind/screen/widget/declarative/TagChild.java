/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.rebind.screen.widget.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TagChild
{
	Class<? extends WidgetChildProcessor<?>> value();
	boolean autoProcess() default true;
	Device[] supportedDevices() default {Device.all};
}
