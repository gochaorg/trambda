package xyz.cofe.trambda.tcp.demo;

import java.util.List;
import java.util.Map;
import xyz.cofe.trambda.tcp.Publisher;

public interface IEnv extends ServControl {
    public List<OsProc> processes();
    public default void notifyMe( int count, int delay ){
        notifyMe(count,delay,false);
    }
    public void notifyMe( int count, int delay, boolean timed );
}
