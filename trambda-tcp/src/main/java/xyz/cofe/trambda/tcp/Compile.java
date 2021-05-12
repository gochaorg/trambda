package xyz.cofe.trambda.tcp;

import xyz.cofe.trambda.LambdaDump;

public class Compile implements Message {
    private LambdaDump dump;

    public LambdaDump getDump(){
        return dump;
    }

    public void setDump(LambdaDump dump){
        this.dump = dump;
    }
}
