/*
 * Copyright 2009 Torsten Werner, Ludovic Claude.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.debian.maven.plugin;

import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;

/**
 * Remove installed files and symlinks from the /usr/share hierarchy
 *
 * @goal sysuninstall
 */
public class SysUninstallMojo extends SysInstallMojo {
    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    @Override
    protected void runMojo() throws IOException {
        FileUtils.forceDelete(fullRepoPath());
        FileUtils.fileDelete(fullCompatPath());
    }
}
