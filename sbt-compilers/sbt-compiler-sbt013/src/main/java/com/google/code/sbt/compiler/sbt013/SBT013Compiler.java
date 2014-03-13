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

package com.google.code.sbt.compiler.sbt013;

import java.io.File;

import com.google.code.sbt.compiler.AbstractCompiler;
import com.google.code.sbt.compiler.CompilerConfiguration;
import com.google.code.sbt.compiler.CompilerException;

import scala.Option;

import com.typesafe.zinc.Compiler;
import com.typesafe.zinc.IncOptions;
import com.typesafe.zinc.Inputs;
import com.typesafe.zinc.Setup;

/**
 * SBT 0.13.x compiler (uses Zinc 0.3.0)
 * 
 * @plexus.component role="com.google.code.sbt.compiler.Compiler" role-hint="sbt013"
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT013Compiler
    extends AbstractCompiler
{
    /**
     * SBT compilation order.
     */
    private static final String COMPILE_ORDER = "mixed";

    /**
     * Run compilation in forked JVM.
     */
    private static final boolean FORK_JAVA = false;

    /**
     * {@inheritDoc}
     */
    public String getDefaultScalaVersion()
    {
        return "2.10.3";
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultSbtVersion()
    {
        return "0.13.0";
    }

    /**
     * {@inheritDoc}
     */
    public void performCompile( CompilerConfiguration configuration )
        throws CompilerException
    {
        SBT013Logger sbtLogger = new SBT013Logger( configuration.getLogger() );
        Setup setup =
            Setup.create( configuration.getScalaCompilerFile(), configuration.getScalaLibraryFile(),
                          configuration.getScalaExtraJarFiles(), configuration.getXsbtiFile(),
                          configuration.getCompilerInterfaceSrcFile(), null, SBT013Compiler.FORK_JAVA );
        if ( configuration.getLogger().isDebugEnabled() )
        {
            Setup.debug( setup, sbtLogger );
        }
        Compiler compiler = Compiler.create( setup, sbtLogger );

        Inputs inputs =
            Inputs.create( configuration.getClasspathFiles(), configuration.getSourceFiles(),
                           configuration.getOutputDirectory(), resolveScalacOptions( configuration ),
                           resolveJavacOptions( configuration ), configuration.getAnalysisCacheFile(),
                           configuration.getAnalysisCacheMap(), SBT013Compiler.COMPILE_ORDER, getIncOptions(),
                           configuration.getLogger().isDebugEnabled() /* mirrorAnalysisCache */ );
        if ( configuration.getLogger().isDebugEnabled() )
        {
            Inputs.debug( inputs, sbtLogger );
        }

        try
        {
            compiler.compile( inputs, sbtLogger );
        }
        catch ( xsbti.CompileFailed e )
        {
            throw new CompilerException( "Scala compilation failed", e );
        }
    }

    private IncOptions getIncOptions()
    {
        // comment from SBT (sbt.inc.IncOptions.scala):
        // After which step include whole transitive closure of invalidated source files.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Steps before transitive closure
        int transitiveStep = 3;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // What's the fraction of invalidated source files when we switch to recompiling
        // all files and giving up incremental compilation altogether. That's useful in
        // cases when probability that we end up recompiling most of source files but
        // in multiple steps is high. Multi-step incremental recompilation is slower
        // than recompiling everything in one step.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Limit before recompiling all sources
        double recompileAllFraction = 0.5d;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Print very detailed information about relations, such as dependencies between source files.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable debug logging of analysis relations
        boolean relationsDebug = false;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Enable tools for debugging API changes. At the moment this option is unused but in the
        // future it will enable for example:
        //   - disabling API hashing and API minimization (potentially very memory consuming)
        //   - diffing textual API representation which helps understanding what kind of changes
        //     to APIs are visible to the incremental compiler
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable analysis API debugging
        boolean apiDebug = false;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Controls context size (in lines) displayed when diffs are produced for textual API
        // representation.
        //
        // This option is used only when `apiDebug == true`.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Diff context size (in lines) for API debug
        int apiDiffContextSize = 5;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // The directory where we dump textual representation of APIs. This method might be called
        // only if apiDebug returns true. This is unused option at the moment as the needed functionality
        // is not implemented yet.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Destination for analysis API dump
        Option<File> apiDumpDirectory = Option.empty();

        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Restore previous class files on failure
        boolean transactional = false;

        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Backup location (if transactional)
        Option<File> backup = Option.empty();

        return new IncOptions( transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
                               apiDumpDirectory, transactional, backup );
    }

}
