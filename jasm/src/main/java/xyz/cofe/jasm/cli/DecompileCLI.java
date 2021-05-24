package xyz.cofe.jasm.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import xyz.cofe.io.fs.File;
import xyz.cofe.trambda.bc.cls.CBegin;

public class DecompileCLI {
    public static void main(String[] cmdLineArgs){
        if( cmdLineArgs==null )throw new IllegalArgumentException("cmdLineArgs==null");
        var args = new ArrayList<String>();
        for( var i=0; i<cmdLineArgs.length; i++ ){
            var arg = cmdLineArgs[i];
            if( arg==null )throw new IllegalArgumentException("cmdLineArgs["+i+"]==null");
            args.add(arg);
        }

        new DecompileCLI().start(args);
    }

    public void start(List<String> args){
        if( args==null )throw new IllegalArgumentException( "args==null" );
        for( var i=0; i<args.size(); i++ ){
            if( args.get(i)==null ){
                throw new IllegalArgumentException("args[" + i + "]==null");
            }
        }

        var state = "init";
        var argz = new ArrayList<>(args);

        if( argz.isEmpty() ){
            help();
            return;
        }

        while( !argz.isEmpty() ){
            var arg = argz.remove(0);
            switch( state ){
                case "init":
                    switch( arg ){
                        case "-l":
                            state = "list";
                            break;
                        case "-d":
                            state = "decompile";
                            break;
                        default:
                            System.err.println("undefined arg "+arg);
                            break;
                    }
                    break;
                case "list":
                    state = "init";
                    list(arg);
                    break;
                case "decompile":
                    state = "init";
                    cbeginParse(new File(arg));
                    break;
            }
        }
    }

    protected void help(){
    }

    protected void list(String arg){
        File file = new File(arg);
        if( !file.exists() ){
            System.err.println("expect file or dir");
            return;
        }
        if( file.isFile() ){
            var name = file.getName();
            if( name!=null ){
                if( name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip") ){
                    listJar(file);
                } else {
                    System.err.println("expect *.jar | *.zip");
                }
            }
            return;
        }
        if( file.isDir() ){
            listDir(file);
            return;
        }
        System.err.println("undefined "+file);
    }

    protected void listJar( File file ){
        try( var finIn = file.readStream() ){
            var zipIn = new ZipInputStream(finIn);
            while( true ){
                var ze = zipIn.getNextEntry();
                if( ze==null )break;
                System.out.println(
                    (ze.isDirectory() ? "d" : "-")+" "+
                    ze.getName()+" "+
                    ze.getSize()+" bytes"
                );
                zipIn.closeEntry();
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    protected void listDir( File file ){
        System.err.println("unsupported list dir");
    }

    private void cbeginParse( File classFile ){
        cbeginParse(classFile.readAllBytes());
    }

    private void cbeginParse( byte[] bytecode ){
        dump(CBegin.parseByteCode(bytecode));
    }

    private void dump( CBegin cls ){
        CDump.dump(cls);
    }
}
