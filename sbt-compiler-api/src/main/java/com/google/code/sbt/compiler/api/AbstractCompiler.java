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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract base class for SBT compilers.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public abstract class AbstractCompiler
    implements Compiler
{
    /**
     * Returns merged <code>scalaOptions</code> and <code>sourceEncoding</code> configuration parameters.
     * 
     * @param configuration compiler configuration object
     * @return merged parameters
     */
    protected List<String> resolveScalacOptions( CompilerConfiguration configuration )
    {
        String scalacOptions = configuration.getScalacOptions();
        String sourceEncoding = configuration.getSourceEncoding();
        List<String> result = new ArrayList<String>( Arrays.asList( scalacOptions.split( " " ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

    /**
     * Returns merged <code>javacOptions</code> and <code>sourceEncoding</code> configuration parameters.
     * 
     * @param configuration compiler configuration object
     * @return merged parameters
     */
    protected List<String> resolveJavacOptions( CompilerConfiguration configuration )
    {
        String javacOptions = configuration.getJavacOptions();
        String sourceEncoding = configuration.getSourceEncoding();
        List<String> result = new ArrayList<String>( Arrays.asList( javacOptions.split( " " ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

}
