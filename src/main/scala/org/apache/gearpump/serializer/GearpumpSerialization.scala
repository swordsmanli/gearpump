/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gearpump.serializer

import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.apache.gearpump.util.Configs

class GearpumpSerialization {
  val config = Configs.SYSTEM_DEFAULT_CONFIG

  def customize(kryo: Kryo): Unit  = {

    val serializationMap: Map[String, String] = configToMap("gearpump.serializers")

    serializationMap.foreach { kv =>
      val (key, value) = kv
      val keyClass = Class.forName(key)
      val valueClass = Class.forName(value)
      kryo.register(keyClass, valueClass.newInstance().asInstanceOf[Serializer[_]])
    }
    kryo.setReferences(false)
  }

  private final def configToMap(path: String): Map[String, String] = {
    import scala.collection.JavaConverters._
    config.getConfig(path).root.unwrapped.asScala.toMap map { case (k, v) ⇒ (k -> v.toString) }
  }
}
