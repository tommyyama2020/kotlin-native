/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed -> in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.konan.properties

import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.Configurables
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.util.ArchiveType
import org.jetbrains.kotlin.konan.util.DependencyProcessor
import java.io.File
import java.nio.file.Paths

abstract class KonanPropertiesLoader(override val target: KonanTarget,
                                     val properties: Properties,
                                     private val baseDir: String? = null,
                                     private val host: KonanTarget = HostManager.host) : Configurables {

    private fun llvmDependencies() = when {
        llvmHome?.startsWith("\$predefinedLlvmDistribution") == true -> llvmHome?.resolveValue()
        else -> null
    }.let(::listOfNotNull)

    open val dependencies: List<String>
        get() = hostTargetList("dependencies") + llvmDependencies()

    override fun readProperty(name: String): String =
            properties.getProperty(name)

    override fun downloadDependencies() {
        dependencyProcessor!!.run()
    }

    override fun targetString(key: String): String?
        = properties.targetString(key, target)
    override fun targetList(key: String): List<String>
        = properties.targetList(key, target)
    override fun hostString(key: String): String?
        = properties.hostString(key, host)
    override fun hostList(key: String): List<String>
        = properties.hostList(key, host)
    override fun hostTargetString(key: String): String?
        = properties.hostTargetString(key, target, host)
    override fun hostTargetList(key: String): List<String>
        = properties.hostTargetList(key, target, host)

    override fun absolute(value: String?): String =
            dependencyProcessor!!.resolveFile(value!!).absolutePath
    private val dependencyProcessor by lazy {
        baseDir?.let {
            DependencyProcessor(
                    dependenciesRoot = File(baseDir),
                    properties = this,
                    archiveType = defaultArchiveTypeByHost(host)
            )
        }
    }
}

private fun defaultArchiveTypeByHost(host: KonanTarget): ArchiveType = when (host) {
    KonanTarget.LINUX_X64 -> ArchiveType.TAR_GZ
    KonanTarget.MACOS_X64 -> ArchiveType.TAR_GZ
    KonanTarget.MINGW_X64 -> ArchiveType.ZIP
    else -> error("$host can't be a host platform!")
}