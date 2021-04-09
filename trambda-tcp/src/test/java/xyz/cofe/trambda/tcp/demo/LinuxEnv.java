package xyz.cofe.trambda.tcp.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xyz.cofe.io.fs.File;

public class LinuxEnv implements IEnv {
    @Override
    public List<OsProc> processes(){
        ArrayList<OsProc> procs = new ArrayList<>();
        File procDir = new File("/proc");
        procDir.dirList().stream()
            .filter( d -> d.getName().matches("\\d+") && d.isDir() )
            .map(OsProc::linuxProc)
            .forEach(procs::add);
        return procs;
    }
}
