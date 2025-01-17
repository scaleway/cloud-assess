package org.cloud_assess.config

import ch.kleis.lcaac.core.datasource.CsvSourceOperations
import ch.kleis.lcaac.core.lang.SymbolTable
import ch.kleis.lcaac.core.lang.evaluator.Evaluator
import ch.kleis.lcaac.core.math.basic.BasicNumber
import ch.kleis.lcaac.core.math.basic.BasicOperations
import ch.kleis.lcaac.grammar.Loader
import ch.kleis.lcaac.grammar.LoaderOption
import ch.kleis.lcaac.grammar.parser.LcaLangLexer
import ch.kleis.lcaac.grammar.parser.LcaLangParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isRegularFile

@Configuration
class LcaConfig {
    @Bean
    fun lcaFiles(
        @Value("\${lca.path}") modelDirectory: File,
    ): SymbolTable<BasicNumber> {
        val files = Files.walk(Paths.get(modelDirectory.path))
            .filter { it.isRegularFile() }
            .map {
                val lexer = LcaLangLexer(CharStreams.fromStream(it.toFile().inputStream()))
                val tokens = CommonTokenStream(lexer)
                val parser = LcaLangParser(tokens)
                val lcaFile = parser.lcaFile()
                lcaFile
            }
            .toList()
            .asSequence()
        val loader = Loader(BasicOperations)
        return loader.load(files, listOf(LoaderOption.WITH_PRELUDE))
    }

    @Bean
    fun basicEvaluator(
        @Value("\${lca.path}") modelDirectory: File,
        symbolTable: SymbolTable<BasicNumber>,
    ): Evaluator<BasicNumber> {
        return Evaluator(symbolTable, BasicOperations, CsvSourceOperations(modelDirectory, BasicOperations) )
    }
}
