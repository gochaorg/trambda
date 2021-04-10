package xyz.cofe.trambda.tcp.serv.cli

import org.junit.jupiter.api.Test

class ConfTest {
    public void service( String addr, Object inst, @DelegatesTo(ServiceRegistry) Closure conf ){
    }

    @Test
    void test01(){
        println "hello"
        service( "localhost:9900", new Object() ) {
            daemon false
            security {
                allow {
                    invoke("System") {
                        methodOwner ==~ /java.lang.System/ && methodName in ['gc']
                    }
                    field( "System.out" ) {
                        fieldOwner ==~ /java.lang.System/ && fieldName in ['out','in','err'] && readAccess
                    }
                }
                deny {
                    any("ban all")
                }
            }
        }
    }
}
