package xyz.cofe.jasm.type;

import java.util.List;

public interface ITypeName {
    public boolean isPrimitive();
    public String toString();

    public String getName();
    public String getPackage();
    public String getSimpleName();

    public List<? extends ITypeName> getParams();

    public int getDimension();

    public String toDescription();
    public String toSignature();
}
