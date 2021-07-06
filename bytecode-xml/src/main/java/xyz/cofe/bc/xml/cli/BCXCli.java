package xyz.cofe.bc.xml.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.bc.xml.BCDeserializer;
import xyz.cofe.bc.xml.BCSeriliazer;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.trambda.bc.cls.CBegin;

public class BCXCli {
    public static void main(String[] args){
        var cli = new BCXCli();
        var cs = Charset.defaultCharset();

//        Supplier<Tuple2<OutputStream,Boolean>> output = ()->Tuple2.of(System.out,false);

        try {
            ArrayList<String> cmdLine = new ArrayList<>(Arrays.asList(args));
            while( !cmdLine.isEmpty() ){
                var arg = cmdLine.remove(0);
                switch( arg ){
                    case "xml":
                        if( cmdLine.isEmpty() ){
                            cli.code2xml(System.in,false);
                        }else{
                            var fin = new FileInputStream(cmdLine.remove(0));
                            cli.code2xml(fin,true);
                        }
                        break;
                    case "code":
                        if( cmdLine.isEmpty() ){
                            cli.xml2code(
                                new InputStreamReader(System.in,cs),
                                false,
                                System.out,
                                false
                            );
                        }else {
                            cli.xml2code(
                                new StringReader(new xyz.cofe.io.fs.File(cmdLine.remove(0)).readText(cs)),
                                true,
                                System.out,
                                false
                            );
                        }
                        break;
                    case "-cs":
                        if( cmdLine.isEmpty() )throw new Error("required charset name");
                        cs = Charset.forName(cmdLine.remove(0));
                        break;
//                    case "-o":
//                        if( cmdLine.isEmpty() )throw new Error("required output file name");
//                        String filename = cmdLine.remove(0);
//                        output = ()->{
//                            try{
//                                return Tuple2.of(new FileOutputStream(filename), true);
//                            } catch( FileNotFoundException e ) {
//                                throw new IOError(e);
//                            }
//                        };
//                        break;
                }
            }
        } catch( Throwable err ){
            System.err.println(err.getMessage());
            err.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void code2xml(InputStream stream, boolean close){
        try{
            var bytes = IOFun.readBytes(stream);
            var code = CBegin.parseByteCode(bytes);
            if( close )stream.close();
            code2xml(code);
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }

    private void code2xml(CBegin code){
        new BCSeriliazer().write(code);
    }

    private void xml2code(Reader xml, boolean closeInput, OutputStream out, boolean closeOutput){
        var d = new BCDeserializer();
        var o = d.restore(xml);
        if( o instanceof CBegin ){
            var cbegin = (CBegin)o;
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
            cbegin.write(cw);
            var bytes = cw.toByteArray();
            try{
                out.write(bytes);
                out.flush();
                if( closeInput )xml.close();
                if( closeOutput )out.close();
            } catch( IOException e ) {
                throw new IOError(e);
            }
        }
    }
}
