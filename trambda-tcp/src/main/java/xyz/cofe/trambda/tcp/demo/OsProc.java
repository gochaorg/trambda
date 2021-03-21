package xyz.cofe.trambda.tcp.demo;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Optional;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.io.fs.File;
import xyz.cofe.text.Text;

public class OsProc implements Serializable {
    private final Integer ppid;
    private final Integer pid;
    private final String name;
    private final String cmdLine;

    public OsProc(int ppid,int pid,String name,String cmdLine){
        this.ppid = ppid;
        this.pid = pid;
        this.name = name;
        this.cmdLine = cmdLine;
    }

    public OsProc(int pid,String name,String cmdLine){
        this.ppid = null;
        this.pid = pid;
        this.name = name;
        this.cmdLine = cmdLine;
    }

    public OsProc(int pid,String name){
        this.ppid = null;
        this.pid = pid;
        this.name = name;
        this.cmdLine = null;
    }

    public Optional<Integer> getPpid(){ return ppid!=null ? Optional.of(ppid) : Optional.empty(); }
    public int getPid(){ return pid; }
    public String getName(){ return name; }
    public Optional<String> getCmdline(){ return cmdLine!=null ? Optional.of(cmdLine) : Optional.empty(); }

    public static OsProc linuxProc(File file){
        if( file.isDir() ){
            try{
                var statusFile = file.resolve("status");
                var status = statusFile.isFile() ? statusFile.readText(Charset.defaultCharset()) : "";
                var cmdLineFile = file.resolve("cmdline");
                var cmdLine = cmdLineFile.isFile() ? cmdLineFile.readText(Charset.defaultCharset()) : "";

                var keyVals = Text.splitNewLinesIterable(status)
                    .map(line -> line.split("\\s*:\\s*",2))
                    .filter(kv -> kv.length==2)
                    .map(kv -> Tuple2.of(kv[0].toLowerCase(), kv[1]));

                String name = keyVals.filter(kv->kv.a().equals("name")).map(Tuple2::b).first().orElse("?");
                String pid = keyVals.filter(kv->kv.a().equals("pid")).map(Tuple2::b).first().orElse("-1");
                String ppid = keyVals.filter(kv->kv.a().equals("ppid")).map(Tuple2::b).first().orElse("-1");

                return new OsProc(Integer.parseInt(ppid), Integer.parseInt(pid),name,cmdLine.replace((char)0,' '));
            } catch( Throwable err ){
                System.err.println("err for "+file+": "+err.getMessage());
            }
        }
        return new OsProc(-1,"?");
    }

    public String toString(){
        return "pid="+pid+" ppid="+ppid+" name="+name+" cmdline="+cmdLine;
    }
}
