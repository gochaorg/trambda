app.service( "0.0.0.0:9988", new xyz.cofe.trambda.demo.api.LinuxEnv() ) {
    daemon false
    security {
        allow {
            // method("System") {
            //     methodOwner ==~ /java.lang.System/ && methodName in ['gc']
            // }
            // field( "System.out" ) {
            //     fieldOwner ==~ /java.lang.System/ && fieldName in ['out','in','err'] && readAccess
            // }
            method( 'Java compiler' ){
                methodOwner ==~ /java\.lang\.invoke\.(LambdaMetafactory|StringConcatFactory)/
            }
            method( 'Java collections' ){
                methodOwner ==~ /java\.util\.(stream\.(Stream|Collectors)|(List))/
            }
            method( 'Java lang' ){
                methodOwner ==~ /java\.lang\.String/
            }
            method( 'Api '){
                methodOwner ==~ /xyz\.cofe\.trambda\.demo\.api\.(IEnv|OsProc)/
            }
        }
        deny {
            any("ban all")
        }
    }
}

start();