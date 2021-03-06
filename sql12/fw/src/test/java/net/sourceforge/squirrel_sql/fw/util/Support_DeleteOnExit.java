/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.squirrel_sql.fw.util;

/**
 * See long comment in FileTest for the origin of this class and why it is licensed under the ASF license.
 */
public class Support_DeleteOnExit {

	public static void main(java.lang.String[] args) {
        for (int i = 0; i < args.length; i++) {
            FileWrapperImpl f1 = new FileWrapperImpl(args[i]);
            f1.deleteOnExit();
        }
    }
}
