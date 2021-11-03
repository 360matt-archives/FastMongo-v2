/*
 * Copyright 2006-2021 the original author or authors.
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
package fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.sun;

import fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.ObjectInstantiator;
import fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.annotations.Instantiator;
import fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.annotations.Typology;
import sun.misc.Unsafe;
import fr.i360matt.fastmongo.v2.utils.objenesis.ObjenesisException;
import fr.i360matt.fastmongo.v2.utils.objenesis.instantiator.util.UnsafeUtils;

/**
 * Instantiates an object, WITHOUT calling it's constructor, using
 * {@code sun.misc.Unsafe.allocateInstance()}. Unsafe and its methods are implemented by most
 * modern JVMs.
 *
 * @author Henri Tremblay
 * @see ObjectInstantiator
 */
@SuppressWarnings("restriction")
@Instantiator(Typology.STANDARD)
public class UnsafeFactoryInstantiator<T> implements ObjectInstantiator<T> {

   private final Unsafe unsafe;
   private final Class<T> type;

   public UnsafeFactoryInstantiator(Class<T> type) {
      this.unsafe = UnsafeUtils.getUnsafe(); // retrieve it to fail right away at instantiator creation if not there
      this.type = type;
   }

   public T newInstance() {
      try {
         return type.cast(unsafe.allocateInstance(type));
      } catch (InstantiationException e) {
         throw new ObjenesisException(e);
      }
   }
}
