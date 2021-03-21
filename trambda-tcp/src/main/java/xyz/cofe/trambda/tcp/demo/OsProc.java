package xyz.cofe.trambda.tcp.demo;

import java.io.Serializable;
import java.util.Optional;

public class OsProc implements Serializable {
    private final Long ppid;
    private final Long pid;
    private final String name;
    private final String cmdLine;

    public OsProc(long ppid,long pid,String name,String cmdLine){
        this.ppid = ppid;
        this.pid = pid;
        this.name = name;
        this.cmdLine = cmdLine;
    }

    public OsProc(long pid,String name,String cmdLine){
        this.ppid = null;
        this.pid = pid;
        this.name = name;
        this.cmdLine = cmdLine;
    }

    public OsProc(long pid,String name){
        this.ppid = null;
        this.pid = pid;
        this.name = name;
        this.cmdLine = null;
    }

    public Optional<Long> getPpid(){ return ppid!=null ? Optional.of(ppid) : Optional.empty(); }
    public long getPid(){ return pid; }
    public String getName(){ return name; }
    public Optional<String> getCmdline(){ return cmdLine!=null ? Optional.of(cmdLine) : Optional.empty(); }
}
