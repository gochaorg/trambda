package xyz.cofe.jasm;

import org.junit.jupiter.api.Test;
import xyz.cofe.jasm.cli.DecompileCLI;

public class DecompileTest {
    @Test
    public void listJar(){
        DecompileCLI.main(
            new String[]{ "-l", "/home/user/code/trambda/jasm/target/jasm-2.0-SNAPSHOT.jar" });
    }
}
