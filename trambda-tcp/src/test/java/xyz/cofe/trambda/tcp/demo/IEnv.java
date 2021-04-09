package xyz.cofe.trambda.tcp.demo;

import java.util.List;
import java.util.Map;

public interface IEnv extends ServControl {
    public List<OsProc> processes();
}
