/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject.config;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.ColumnMappers;
import org.jdbi.v3.core.mapper.ColumnMapperFactory;

/**
 * Used to register a column mapper with either a sql object type or for a specific method.
 */
@ConfiguringAnnotation(RegisterColumnMapperFactory.Impl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RegisterColumnMapperFactory
{
    /**
     * The column mapper factory classes to register
     * @return one or more column mapper factory classes.
     */
    Class<? extends ColumnMapperFactory>[] value();

    class Impl implements Configurer
    {

        @Override
        public void configureForMethod(ConfigRegistry registry, Annotation annotation, Class<?> sqlObjectType, Method method)
        {
            configureForType(registry, annotation, sqlObjectType);
        }

        @Override
        public void configureForType(ConfigRegistry registry, Annotation annotation, Class<?> sqlObjectType)
        {
            RegisterColumnMapperFactory registerColumnMapperFactory = (RegisterColumnMapperFactory) annotation;
            ColumnMappers mappers = registry.get(ColumnMappers.class);
            try {
                Class<? extends ColumnMapperFactory>[] mcs = registerColumnMapperFactory.value();
                for (int i = 0; i < mcs.length; i++) {
                    mappers.register(mcs[i].newInstance());
                }
            }
            catch (Exception e) {
                throw new IllegalStateException("unable to create a specified column mapper factory", e);
            }
        }
    }
}
