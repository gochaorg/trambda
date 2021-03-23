package xyz.cofe.trambda.demo.serv;

import xyz.cofe.trambda.tcp.TcpServerCLI;

public class ServerMain1 {
    public static void main(String[] args){
        TcpServerCLI.main(
            new String[]{
                "service", "xyz.cofe.trambda.demo.api.LinuxEnv",
                "port", "9988", "add",
                "start"
            }
        );
    }
}
