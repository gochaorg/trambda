package xyz.cofe.trambda.demo.client

import xyz.cofe.trambda.Fn
import xyz.cofe.trambda.demo.api.IEnv
import xyz.cofe.trambda.demo.api.OsProc
import xyz.cofe.trambda.tcp.TcpQuery
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

fun main() {
    val query: TcpQuery<IEnv> = TcpQuery
        .create(IEnv::class.java).host("localhost").port(9988)
        .build()

    query.apply { env: IEnv ->
        env.processes().stream().filter { p: OsProc -> p.name.contains("java") }
        .collect(Collectors.toList())
    }.forEach(Consumer { x: OsProc? -> println(x) })
}