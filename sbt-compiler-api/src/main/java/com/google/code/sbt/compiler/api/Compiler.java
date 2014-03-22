/*
 * Copyright 2013-2014 Grzegorz Slowikowski (gslowikowski at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.google.code.sbt.compiler.api;

/**
 * SBT compiler interface.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface Compiler
{
    String getDefaultScalaVersion();

    String getDefaultSbtVersion();

    /**
     * Performs the compilation of the project.
     * 
     * @param configuration   the configuration description of the compilation
     *   to perform
     * @throws CompilerException if compilation exception occurs.
     */
    void performCompile( CompilerConfiguration configuration )
        throws CompilerException;

}
