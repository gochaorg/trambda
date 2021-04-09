package xyz.cofe.trambda.tcp.demo;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ServControl {
    public default void doGc(){
        System.out.println(
            "doGc()" +
                " free="+Runtime.getRuntime().freeMemory()+
                " max="+Runtime.getRuntime().maxMemory()+
                " total="+Runtime.getRuntime().totalMemory());
        System.gc();
    }

    public default void runFinalization(){
        System.out.println(
            "runFinalization()" +
                " free="+Runtime.getRuntime().freeMemory()+
                " max="+Runtime.getRuntime().maxMemory()+
                " total="+Runtime.getRuntime().totalMemory());
        System.runFinalization();
    }

    public default Map<String,Object> memInfo(){
        var info = new LinkedHashMap<String,Object>();
        info.put("free",Runtime.getRuntime().freeMemory());
        info.put("max",Runtime.getRuntime().maxMemory());
        info.put("total",Runtime.getRuntime().totalMemory());
        return info;
    }

    public default void exit(int code){
        System.exit(code);
    }
}
