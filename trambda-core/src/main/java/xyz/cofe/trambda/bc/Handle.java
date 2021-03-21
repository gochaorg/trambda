package xyz.cofe.trambda.bc;

import java.io.Serializable;
import java.util.Objects;

public class Handle implements Serializable {
    private static final long serialVersionUID = 1;

    public Handle(){
    }

    public Handle(org.objectweb.asm.Handle sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        tag = sample.getTag();
        desc = sample.getDesc();
        name = sample.getName();
        owner = sample.getOwner();
        iface = sample.isInterface();
    }

    //region tag
    private int tag;

    public int getTag(){
        return tag;
    }
    public void setTag(int tag){
        this.tag = tag;
    }
    //endregion

    //region desc
    private String desc;

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }
    //endregion

    //region name
    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    //endregion

    //region owner
    private String owner;

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }
    //endregion

    //region iface
    private boolean iface;

    public boolean isIface(){
        return iface;
    }

    public void setIface(boolean iface){
        this.iface = iface;
    }
    //endregion

    @Override
    public String toString(){
        return "Handle{ " +
            "tag=" + tag +
            ", desc='" + desc + '\'' +
            ", name='" + name + '\'' +
            ", owner='" + owner + '\'' +
            ", iface=" + iface +
            '}';
    }

    @Override
    public boolean equals(Object o){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Handle handle = (Handle) o;
        return tag == handle.tag && iface == handle.iface && Objects.equals(desc, handle.desc) && Objects.equals(name, handle.name) && Objects.equals(owner, handle.owner);
    }

    @Override
    public int hashCode(){
        return Objects.hash(tag, desc, name, owner, iface);
    }
}
