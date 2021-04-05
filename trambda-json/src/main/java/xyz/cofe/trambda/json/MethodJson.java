package xyz.cofe.trambda.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import xyz.cofe.trambda.bc.*;

public class MethodJson {
    private final static ObjectMapper ob;
    private final static List<Serializer<?>> serializers = new ArrayList<>();

    static {
        ob = new ObjectMapper();
        ob.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule mod = new SimpleModule();


        serializer(Code.class);
        serializer(End.class);
        serializer(Label.class).write((w,v)->w.value("name",v.getName()));
        serializer(VarInsn.class).write((w,v)->
            w.value("opcode",v.getOpcode()).value("variable",v.getVariable()) );
        serializer(JumpInsn.class).write((w, v)->
            w.value("opcode",v.getOpcode()).value("label",v.getLabel()) );
        serializer(TypeArg.class).write((w, v)->
            w.value("type",v.getType()) );
        serializer(FieldInsn.class).write((w, v)->
            w.value("opcode",v.getOpcode())
                .value("owner",v.getOwner())
                .value("name",v.getName())
                .value("descriptor",v.getDescriptor()) );
        serializer(Frame.class).write((w, v)->
            w.value("opcode",v.getType())
                .value("numLocal",v.getNumLocal())
                .value("numStack",v.getNumStack())
                .object("stack",v.getStack())
                .object("local",v.getLocal()) );
        serializer(MethodInsn.class).write((w, v)->
            w.value("opcode",v.getOpcode())
                .value("descriptor",v.getDescriptor())
                .value("owner",v.getOwner())
                .object("name",v.getName()));
        serializer(LineNumber.class).write((w, v)->
            w.value("label",v.getLabel()).value("line",v.getLine()) );
        serializer(InvokeDynamicInsn.class).write((w, v)->
            w.value("name",v.getName())
            .value("descriptor",v.getDescriptor())
            .object("args",v.getBootstrapMethodArguments())
            .object("handle",v.getBootstrapMethodHandle())
        );
        serializer(Handle.class).write((w, v)->
            w.value("owner",v.getOwner())
            .value("name",v.getName())
            .value("desc",v.getDesc())
            .value("tag",v.getTag())
            .value("iface",v.isIface())
        );
        serializer(HandleArg.class).write((w, v)->
            w.object("handle",v.getHandle())
        );
        serializer(IntInsn.class).write((w, v)->
            w.value("opcode",v.getOpcode()).value("operand",v.getOperand())
        );
        serializer(Insn.class).write((w, v)->
            w.value("opcode",v.getOpcode())
        );
        serializer(LdcInsn.class).write((w, v)->
            w.object("value",v.getValue())
                .value("ldcType",v.getLdcType()!=null ? v.getLdcType().name() : null)
        );

        serializers.forEach(s -> s.registry(mod));
        ob.registerModule(mod);
    }

    private static <T> Serializer<T> serializer(Class<T> c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        var ser = new Serializer<>(c);
        serializers.add(ser);
        return ser;
    }

    public static void write(Writer writer, MethodDef methodDef){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );
        try{
            ob.writeValue(writer,methodDef);
        } catch( IOException e ) {
            throw new IOError(e);
        }
    }
    public static String toJson(MethodDef methodDef){
        if( methodDef==null )throw new IllegalArgumentException( "methodDef==null" );
        try{
            return ob.writeValueAsString(methodDef);
        } catch( JsonProcessingException e ) {
            throw new IOError(e);
        }
    }
}
