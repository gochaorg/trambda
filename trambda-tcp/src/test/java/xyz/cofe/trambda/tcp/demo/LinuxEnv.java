package xyz.cofe.trambda.tcp.demo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import xyz.cofe.io.fs.File;
import xyz.cofe.trambda.tcp.Publisher;

public class LinuxEnv implements IEnv {
    public LinuxEnv(){
    }

    public LinuxEnv(Publisher<ServerDemoEvent> defaultPublisher){
        this.defaultPublisher = defaultPublisher;
    }

    public LinuxEnv(Events publishers){
        if( publishers!=null ){
            this.defaultPublisher = publishers.defaultPublisher();
            this.timedPublisher = publishers.timedEvents();
        }
    }

    private Publisher<ServerDemoEvent> defaultPublisher;
    private Publisher<ServerDemoEvent2> timedPublisher;

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

    public void notifyMe( int count, int delay, boolean timed ){
        if( count<=0 )return;
        if( timed ){
            var pub = timedPublisher;
            if( pub!=null ){
                for( int i=0; i<count; i++ ){
                    if( i>0 && delay>0 ){
                        try{
                            Thread.sleep(delay);
                        } catch( InterruptedException e ) {
                            break;
                        }
                    }
                    pub.publish( new ServerDemoEvent2("message#"+i) );
                }
            }
        }else {
            var pub = defaultPublisher;
            if( pub!=null ){
                for( int i=0; i<count; i++ ){
                    if( i>0 && delay>0 ){
                        try{
                            Thread.sleep(delay);
                        } catch( InterruptedException e ) {
                            break;
                        }
                    }
                    pub.publish( new ServerDemoEvent("message#"+i) );
                }
            }
        }
    }
}
