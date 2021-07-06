package xyz.cofe.bc.xml;

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;
import xyz.cofe.simpletypes.SimpleTypes;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ann.AEnd;
import xyz.cofe.trambda.bc.ann.AEnum;
import xyz.cofe.trambda.bc.ann.APair;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.LongArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CInnerClass;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.cls.CNestHost;
import xyz.cofe.trambda.bc.cls.CNestMember;
import xyz.cofe.trambda.bc.cls.COuterClass;
import xyz.cofe.trambda.bc.cls.CPermittedSubclass;
import xyz.cofe.trambda.bc.cls.CSource;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.mth.*;

import static xyz.cofe.bc.xml.StringCodec.*;

import xyz.cofe.xml.FormatXMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

import static org.objectweb.asm.Opcodes.*;

public class BCSeriliazer implements Closeable {
    protected final XmlStream xout;

    public BCSeriliazer(){
        xout = new XmlStream(new OutputStreamWriter(System.out));
    }
    public BCSeriliazer(Writer writer){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        xout = new XmlStream(writer);
    }
    public BCSeriliazer(OutputStream stream, String enconding){
        if( stream==null )throw new IllegalArgumentException( "stream==null" );
        if( enconding==null )throw new IllegalArgumentException( "enconding==null" );
        try{
            xout = new XmlStream(new OutputStreamWriter(stream,enconding));
        } catch( UnsupportedEncodingException e ) {
            throw new IOError(e);
        }
    }
    public BCSeriliazer(OutputStream stream, Charset enconding){
        if( stream==null )throw new IllegalArgumentException( "stream==null" );
        if( enconding==null )throw new IllegalArgumentException( "enconding==null" );
        xout = new XmlStream(new OutputStreamWriter(stream,enconding));
    }

    @Override
    public void close() throws IOException{
        xout.close();
    }

    protected void startElement( String name ){
        xout.start(name);
    }
    protected void endElement(){
        xout.end();
    }
    protected boolean validXmlChar( char c ){
        if( c == 0x09 )return true;
        if( c == 0x0a )return true;
        if( c == 0x0d )return true;
        if( c >= 0x20 && c <= 0xD7FF )return true;
        if( c >= 0xE000 && c <= 0xFFFD )return true;
        if( c == 0xFFFE )return false;
        if( c == 0xFFFF )return false;
        // if( c >= 0x10000 && c <= 0x10FFFF )return true;
        return false;
    }
    protected boolean requriedHexEncode( String content ){
        for( int i=0; i<content.length(); i++ ){
            if( !validXmlChar(content.charAt(i)) )return true;
        }
        return false;
    }

    protected void text( String content ){
        text(content, true, null, null );
    }
    protected void text( String content, boolean checkHexEncode, String encodeAttribute, String hexEncodeValue ){
        if( requriedHexEncode(content) ){
            if( xout.isOpenTag() ){
                attribute(
                    encodeAttribute !=null ? encodeAttribute : "encode",
                    hexEncodeValue != null ? hexEncodeValue : "hex"
                );
            }else{
                throw new IOError(new Error("tag is closed"));
            }
            xout.text(HexCodec.serialize(content));

            StringBuilder sb = new StringBuilder();
            for( int i=0;i<content.length();i++ ){
                char c = content.charAt(i);
                if( validXmlChar(c) ){
                    sb.append(c);
                }else{
                    sb.append("{{").append(StringCodec.hexOf(c)).append("}}");
                }
            }
            xout.comment(sb.toString());
        }else {
            xout.text(content);
        }
    }
    protected void attribute( String name, String value ){
        xout.attribute(name,value);
    }
    protected void attribute( String name, int value ){
        xout.attribute(name,Integer.toString(value));
    }
    protected void attribute( String name, char value ){
        xout.attribute(name,Character.toString(value));
    }
    protected void attribute( String name, boolean value ){
        xout.attribute(name,Boolean.toString(value));
    }
    protected void attribute( String name, long value ){
        xout.attribute(name,Long.toString(value));
    }
    protected void attribute( String name, double value ){
        xout.attribute(name,Double.toString(value));
    }

    public class ElementWriter {
        public void attribute( String name, String value ){
            BCSeriliazer.this.attribute(name, value);
        }
        public void attribute( String name, int value ){
            BCSeriliazer.this.attribute(name, Integer.toString(value));
        }
        public void attribute( String name, long value ){
            BCSeriliazer.this.attribute(name, Long.toString(value));
        }
        public void attribute( String name, double value ){
            BCSeriliazer.this.attribute(name, Double.toString(value));
        }
        public void attribute( String name, boolean value ){
            BCSeriliazer.this.attribute(name, Boolean.toString(value));
        }
        public void text( String txt ){
            BCSeriliazer.this.text(txt);
        }
        public void element( String name, Consumer<ElementWriter> wr ){
            BCSeriliazer.this.element(name, wr);
        }
    }

    private final ElementWriter elWr = new ElementWriter();
    protected void element(String name, Consumer<ElementWriter> wr){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( wr==null )throw new IllegalArgumentException( "wr==null" );
        startElement(name);
        wr.accept(elWr);
        endElement();
        xout.flush();
    }

    protected CBegin clazz;

    public void writeAcessFlagHelp(){
        StringBuilder sb = new StringBuilder();
        sb.append("Access bit flags:\n");
        var maxNameLen = AccFlags.flagName().keySet().stream().map(String::length).max(Integer::compareTo).get();
        AccFlags.flagName().forEach( (flag,num) -> {
            sb.append(Text.align(flag, Align.Begin, " ",maxNameLen+1)).append("=").append(num).append("\n");
        });
        xout.comment(sb.toString());
    }

    public synchronized void write(CBegin clazz){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );
        element("CBegin", w -> {
            BCSeriliazer.this.clazz = clazz;

            w.attribute("version", clazz.getVersion());
            w.attribute("access", clazz.getAccess());

            w.attribute("accessDecode",
                new AccFlags(clazz.getAccess()).flags().toString()
            );

            w.attribute("name", clazz.getName());
            if( clazz.getSignature()!=null ) w.attribute("signature", clazz.getSignature());
            if( clazz.getSuperName()!=null ) w.attribute("superName", clazz.getSuperName());
            w.attribute("interface", clazz.isInterface() );

            var itfs = clazz.getInterfaces();
            if( itfs!=null ){
                for( var itf : itfs ){
                    if( itf!=null ){
                        element("interface", wi -> wi.text(itf));
                    }
                }
            }

            var src = clazz.getSource();
            if( src!=null )write(src);

            var coc = clazz.getOuterClass();
            if( coc!=null )write(coc);

            var cnh = clazz.getNestHost();
            if( cnh!=null )write(cnh);

            var ps = clazz.getPermittedSubclass();
            if( ps!=null )write(ps);

            var ca = clazz.getAnnotations();
            if( ca!=null ) writeAnnotations(ca);

            var ta = clazz.getTypeAnnotations();
            if( ta!=null ) writeTypeAnnotations(ta);

            var nm = clazz.getNestMembers();
            if( nm!=null ) writeNestMembers(nm);

            var ic = clazz.getInnerClasses();
            if( ic!=null ) writeInnerClasses(ic);

            var fs = clazz.getFields();
            if( fs!=null ) writeFields(fs);

            var ms = clazz.getMethods();
            if( ms!=null ) writeMethods(ms);

            BCSeriliazer.this.clazz = null;
        });
        xout.flush();
    }
    public synchronized void write(CSource src){
        if( src==null )throw new IllegalArgumentException( "src==null" );

        startElement("CSource");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(src);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( src.getSource()!=null )attribute("source",src.getSource());
        if( src.getDebug()!=null )attribute("debug",src.getDebug());

        endElement();
    }
    public synchronized void write(COuterClass ocls){
        if( ocls==null )throw new IllegalArgumentException( "ocls==null" );

        startElement("COuterClass");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(ocls);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( ocls.getName()!=null )attribute("name", ocls.getName());
        if( ocls.getDescriptor()!=null )attribute("descriptor", ocls.getDescriptor());
        if( ocls.getOwner()!=null )attribute("owner", ocls.getOwner());

        endElement();
    }
    public synchronized void write(CNestHost nhost){
        if( nhost==null )throw new IllegalArgumentException( "nhost==null" );

        startElement("CNestHost");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(nhost);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( nhost.getNestHost()!=null )attribute("nestHost", nhost.getNestHost());

        endElement();
    }
    public synchronized void write(CPermittedSubclass cpscls){
        if( cpscls==null )throw new IllegalArgumentException( "cpscls==null" );

        startElement("CPermittedSubclass");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cpscls);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( cpscls.getPermittedSubclass()!=null )attribute("permittedSubclass", cpscls.getPermittedSubclass());

        endElement();
    }
    public synchronized void write(CAnnotation ca){
        if( ca==null )throw new IllegalArgumentException( "ca==null" );

        startElement("CAnnotation");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(ca);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        attribute("visible", ca.isVisible());
        if( ca.getDescriptor()!=null )attribute("descriptor", ca.getDescriptor() );

        var annBc = ca.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void writeAnnotations(List<CAnnotation> ca){
        if( ca==null )throw new IllegalArgumentException( "ca==null" );
        for( var f : ca ){
            if( f!=null ){
                write(f);
            }
        }
    }
    public synchronized void write(CTypeAnnotation cta){
        if( cta==null )throw new IllegalArgumentException( "cta==null" );

        startElement("CTypeAnnotation");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cta);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        attribute("typeRef", cta.getTypeRef());
        if( cta.getTypePath()!=null )attribute("typePath", cta.getTypePath());
        if( cta.getDescriptor()!=null )attribute("descriptor", cta.getDescriptor());
        attribute("visible", cta.isVisible());

        var annBc = cta.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void writeTypeAnnotations(List<CTypeAnnotation> cta){
        if( cta==null )throw new IllegalArgumentException( "cta==null" );
        for( var f : cta ){
            if( f!=null ){
                write(f);
            }
        }
    }
    public synchronized void write(CNestMember cnm){
        if( cnm==null )throw new IllegalArgumentException( "cnm==null" );

        startElement("CNestMember");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cnm);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( cnm.getNestMember()!=null )attribute("nestMember", cnm.getNestMember());

        endElement();
    }
    public synchronized void writeNestMembers(List<CNestMember> cnm){
        if( cnm==null )throw new IllegalArgumentException( "cnm==null" );
        for( var f : cnm ){
            if( f!=null ){
                write(f);
            }
        }
    }
    public synchronized void write(CInnerClass cic){
        if( cic==null )throw new IllegalArgumentException( "cic==null" );

        startElement("CInnerClass");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cic);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( cic.getName()!=null )attribute("name", cic.getName());
        if( cic.getOuterName()!=null )attribute("outerName", cic.getOuterName());
        if( cic.getInnerName()!=null )attribute("innerName", cic.getInnerName());

        attribute("access", cic.getAccess());
        attribute("accessDecode",
            new AccFlags(cic.getAccess()).flags().toString()
        );

        endElement();
    }
    public synchronized void writeInnerClasses(List<CInnerClass> cic){
        if( cic==null )throw new IllegalArgumentException( "cic==null" );
        for( var f : cic ){
            if( f!=null ){
                write(f);
            }
        }
    }
    public synchronized void write(CField cf){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );

        startElement("CField");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cf);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( cf.getName()!=null )attribute("name", cf.getName());
        attribute("access", cf.getAccess());
        attribute("accessDecode", new AccFlags(cf.getAccess()).flags().toString() );
        if( cf.getDescriptor()!=null )attribute("descriptor", cf.getDescriptor() );
        if( cf.getSignature()!=null )attribute("signature", cf.getSignature() );

        attribute("deprecated",cf.isDeprecated());
        attribute("final",cf.isFinal());
        attribute("enum",cf.isEnum());
        attribute("mandated",cf.isMandated());
        attribute("private",cf.isPrivate());
        attribute("protected",cf.isProtected());
        attribute("public",cf.isPublic());
        attribute("static",cf.isStatic());
        attribute("synthetic",cf.isSynthetic());
        attribute("transient",cf.isTransient());
        attribute("volatile",cf.isVolatile());

        if( cf.getValue()!=null ){
            var fvalue = cf.getValue();
            //noinspection ConstantConditions
            if( fvalue.getClass().isPrimitive() &&
                (
                    (fvalue.getClass()!=char.class) ||
                    ((fvalue.getClass()==char.class) && validXmlChar((char)fvalue))
                )
            ){
                startElement("fieldValue");
                attribute("class", fvalue.getClass().getName());
                attribute("encode", "primitive");
                text(fvalue.toString());
                endElement();
            }else if( fvalue.getClass() == String.class ){
                startElement("fieldValue");
                attribute("class", fvalue.getClass().getName());
                //attribute("encode", "string");
                text(fvalue.toString());
                endElement();
            }else if( fvalue instanceof Serializable ){
                startElement("fieldValue");
                attribute("class", fvalue.getClass().getName());
                attribute("encode", "hex");
                text(HexCodec.serialize((Serializable) fvalue));
                endElement();
            }else {
                startElement("fieldValue");
                attribute("class", fvalue.getClass().getName());
                attribute("encode", "none");
                endElement();
            }
        }

        endElement();
    }
    public synchronized void writeFields(List<CField> cf){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );
        for( var f : cf ){
            if( f!=null ){
                write(f);
            }
        }
    }
    public synchronized void write(CMethod cm){
        if( cm==null )throw new IllegalArgumentException( "cm==null" );

        startElement("CMethod");

        if( clazz!=null ){
            var ord = clazz.getOrder().get(cm);
            if( ord!=null ){
                attribute("order", ord);
            }
        }

        if( cm.getName()!=null )attribute("name", cm.getName() );

        attribute("access", cm.getAccess());
        attribute("accessDecode", new AccFlags(cm.getAccess()).flags().toString() );

        attribute("deprecated",cm.isDeprecated());
        attribute("public",cm.isPublic());
        attribute("protected",cm.isProtected());
        attribute("private",cm.isPrivate());
        attribute("final",cm.isFinal());
        attribute("abstract",cm.isAbstract());
        attribute("bridge",cm.isBridge());
        attribute("native",cm.isNative());
        attribute("strict",cm.isStrict());
        attribute("synchronized",cm.isSynchronized());
        attribute("mandated",cm.isMandated());
        attribute("static",cm.isStatic());
        attribute("synthetic",cm.isSynthetic());
        attribute("varArgs",cm.isVarArgs());

        if( cm.getDescriptor()!=null )attribute("descriptor", cm.getDescriptor() );
        if( cm.getSignature()!=null )attribute("signature", cm.getSignature() );

        if( cm.getExceptions()!=null ){
            for( var ex : cm.getExceptions() ){
                if( ex!=null ){
                    startElement("methodException");
                    text(ex);
                    endElement();
                }
            }
        }

        var bc = cm.getMethodByteCodes();
        if( bc!=null ){
            for( var mc : bc )write(mc);
        }

        endElement();
    }
    public synchronized void writeMethods(List<CMethod> cm){
        if( cm==null )throw new IllegalArgumentException( "cm==null" );
        for( var m : cm ){
            if( m!=null ){
                write(m);
            }
        }
    }
    public synchronized void write(MethodByteCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        else if( bc instanceof MAnnotableParameterCount )write((MAnnotableParameterCount) bc);
        else if( bc instanceof MAnnotation )write((MAnnotation) bc);
        else if( bc instanceof MAnnotationDefault )write((MAnnotationDefault) bc);
        else if( bc instanceof MCode )write((MCode) bc);
        else if( bc instanceof MEnd )write((MEnd) bc);
        else if( bc instanceof MFieldInsn )write((MFieldInsn) bc);
        else if( bc instanceof MFrame )write((MFrame) bc);
        else if( bc instanceof MIincInsn )write((MIincInsn) bc);
        else if( bc instanceof MInsn )write((MInsn) bc);
        else if( bc instanceof MInsnAnnotation )write((MInsnAnnotation) bc);
        else if( bc instanceof MIntInsn )write((MIntInsn) bc);
        else if( bc instanceof MInvokeDynamicInsn )write((MInvokeDynamicInsn) bc);
        else if( bc instanceof MJumpInsn )write((MJumpInsn) bc);
        else if( bc instanceof MLabel )write((MLabel) bc);
        else if( bc instanceof MLdcInsn )write((MLdcInsn) bc);
        else if( bc instanceof MLineNumber )write((MLineNumber) bc);
        else if( bc instanceof MLocalVariable )write((MLocalVariable) bc);
        else if( bc instanceof MLocalVariableAnnotation )write((MLocalVariableAnnotation) bc);
        else if( bc instanceof MLookupSwitchInsn )write((MLookupSwitchInsn) bc);
        else if( bc instanceof MMaxs )write((MMaxs) bc);
        else if( bc instanceof MMethodInsn )write((MMethodInsn) bc);
        else if( bc instanceof MMultiANewArrayInsn )write((MMultiANewArrayInsn) bc);
        else if( bc instanceof MParameter )write((MParameter) bc);
        else if( bc instanceof MParameterAnnotation )write((MParameterAnnotation) bc);
        else if( bc instanceof MTableSwitchInsn )write((MTableSwitchInsn) bc);
        else if( bc instanceof MTryCatchAnnotation )write((MTryCatchAnnotation) bc);
        else if( bc instanceof MTryCatchBlock )write((MTryCatchBlock) bc);
        else if( bc instanceof MTypeAnnotation )write((MTypeAnnotation) bc);
        else if( bc instanceof MTypeInsn )write((MTypeInsn) bc);
        else if( bc instanceof MVarInsn )write((MVarInsn) bc);
        else {
            xout.comment("undefined MethodByteCode "+bc);
        }
    }
    public synchronized void write(MAnnotableParameterCount bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MAnnotableParameterCount");
        attribute("parameterCount", bc.getParameterCount());
        attribute("visible",bc.isVisible());
        endElement();
    }
    public synchronized void write(MAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MAnnotation");
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        attribute("visible",bc.isVisible());

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MAnnotationDefault bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MAnnotationDefault");

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MCode bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MCode");
        endElement();
    }
    public synchronized void write(MEnd bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MEnd");
        endElement();
    }
    public synchronized void write(MFieldInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MFieldInsn");
        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );
        if( bc.getOwner()!=null )attribute("owner", bc.getOwner());
        if( bc.getName()!=null )attribute("name", bc.getName());
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        endElement();
    }
    private void writeFrameValue( List<Object> list, String tag ){
        if( list!=null ){
            for( var value : list ){
                if( value == null ){
                    startElement(tag);
                    attribute("isnull", true);
                    endElement();
                }else {
                    if( value instanceof Serializable ){
                        startElement(tag);
                        attribute("isnull", false);
                        attribute("class", value.getClass().getName());
                        attribute("toString", value.toString());
                        attribute("encode","hex");
                        text(HexCodec.serialize((Serializable) value));
                        endElement();
                    }else{
                        xout.comment("can't serialize "+tag+": "+value);
                    }
                }
            }
        }
    }
    public synchronized void write(MFrame bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MFrame");

        String typeDecode = null;

        attribute("type",bc.getType());
        switch( bc.getType() ){
            case F_NEW: typeDecode = "F_NEW"; break;
            case F_APPEND: typeDecode = "F_APPEND"; break;
            case F_CHOP: typeDecode = "F_CHOP"; break;
            case F_FULL: typeDecode = "F_FULL"; break;
            case F_SAME: typeDecode = "F_SAME"; break;
            case F_SAME1: typeDecode = "F_SAME1"; break;

            default: typeDecode = null; break;
        }

        attribute("numLocal",bc.getNumLocal());
        attribute("numStack",bc.getNumStack());

        if( typeDecode!=null )attribute("typeDecode", typeDecode);

        writeFrameValue(bc.getLocal(), "frameLocal");
        writeFrameValue(bc.getStack(), "frameStack");

        endElement();
    }
    public synchronized void write(MIincInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MIincInsn");

        attribute("variable", bc.getVariable());
        attribute("increment", bc.getIncrement());

        endElement();
    }
    public synchronized void write(MInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MInsn");

        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );

        endElement();
    }
    public synchronized void write(MInsnAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MInsnAnnotation");

        attribute("typeRef",bc.getTypeRef());
        if( bc.getTypePath()!=null )attribute("typePath",bc.getTypePath());
        if( bc.getDescriptor()!=null )attribute("descriptor",bc.getDescriptor());
        attribute("visible",bc.isVisible());

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }
        endElement();
    }
    public synchronized void write(MIntInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MIntInsn");

        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );

        attribute("operand",bc.getOperand());

        endElement();
    }
    public synchronized void write(MInvokeDynamicInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MInvokeDynamicInsn");

        if( bc.getName()!=null )attribute("name", bc.getName());
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        if( bc.getBootstrapMethodHandle()!=null ){
            var h = bc.getBootstrapMethodHandle();
            element("bootstrapHandle", w -> {
                if( h.getName()!=null )w.attribute("name", h.getName());
                if( h.getOwner()!=null )w.attribute("owner", h.getOwner());
                if( h.getDesc()!=null )w.attribute("desc", h.getDesc());
                w.attribute("tag", h.getTag());
                w.attribute("iface", h.isIface());
            });
        }
        if( bc.getBootstrapMethodArguments()!=null ){
            var args = bc.getBootstrapMethodArguments();
            args.forEach( arg -> {
                if( arg!=null ){
                    write(arg);
                }else{
                    element("BootstrapMethArg", w -> {
                        w.attribute("isnull", true);
                    });
                }
            });
        }

        endElement();
    }
    public synchronized void write(BootstrapMethArg arg){
        if( arg==null )throw new IllegalArgumentException( "arg==null" );
        element("BootstrapMethArg", w -> {
            w.attribute("isnull", false);
            if( arg instanceof TypeArg ){
                var ar = (TypeArg)arg;
                element("TypeArg", aw -> {
                    if( ar.getType()!=null ){
                        aw.text(ar.getType());
                    }
                });
            }else if( arg instanceof StringArg ) {
                var ar = (StringArg)arg;
                element("StringArg", aw -> {
                    if( ar.getValue()!=null ){
                        aw.text(ar.getValue());
                    }
                });
            }else if( arg instanceof MHandle ) {
                var ar = (MHandle)arg;
                element("MHandle", aw -> {
                    aw.attribute("tag", ar.getTag());
                    aw.attribute("iface", ar.isIface());
                    if( ar.getOwner()!=null )aw.attribute("owner", ar.getOwner());
                    if( ar.getDesc()!=null )aw.attribute("desc", ar.getDesc());
                    if( ar.getName()!=null )aw.attribute("name", ar.getName());
                });
            }else if( arg instanceof LongArg ) {
                var ar = (LongArg)arg;
                element("LongArg", aw -> {
                    if( ar.getValue()!=null )aw.text(Long.toString(ar.getValue()));
                });
            }else if( arg instanceof IntArg ) {
                var ar = (IntArg)arg;
                element("IntArg", aw -> {
                    if( ar.getValue()!=null )aw.text(ar.getValue().toString());
                });
            }else if( arg instanceof HandleArg ) {
                var ar = (HandleArg)arg;
                element("HandleArg", aw -> {
                    var h = ar.getHandle();
                    if( h!=null ){
                        aw.attribute("tag", h.getTag());
                        aw.attribute("iface", h.isIface());
                        if( h.getOwner()!=null )aw.attribute("owner", h.getOwner());
                        if( h.getDesc()!=null )aw.attribute("desc", h.getDesc());
                        if( h.getName()!=null )aw.attribute("name", h.getName());
                    }
                });
            }else if( arg instanceof FloatArg ) {
                var ar = (FloatArg)arg;
                element("FloatArg", aw -> {
                    if( ar.getValue()!=null )aw.text(ar.getValue().toString());
                });
            }else if( arg instanceof DoubleArg ) {
                var ar = (DoubleArg)arg;
                element("DoubleArg", aw -> {
                    if( ar.getValue()!=null )aw.text(ar.getValue().toString());
                });
            }else {
                xout.comment("can't write argument: "+arg);
            }
        });
    }
    public synchronized void write(MJumpInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MJumpInsn");

        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );

        if( bc.getLabel()!=null )attribute("label", bc.getLabel());

        endElement();
    }
    public synchronized void write(MLabel bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLabel");
        if( bc.getName()!=null )attribute("name",bc.getName());
        endElement();
    }
    public synchronized void write(MLdcInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLdcInsn");

        var t = bc.getLdcType();
        if( t!=null )attribute("type", t.name());

        if( bc.getValue()!=null ){
            startElement("value");
            var value = bc.getValue();
            attribute("class", value.getClass().getName());
            if( !(value instanceof String || SimpleTypes.isSimple(value.getClass())) )attribute("toString", value.toString());
            if( value instanceof BootstrapMethArg ){
                attribute("encode", "BootstrapMethArg");
                var bm = (BootstrapMethArg)value;
                write(bm);
            }else{
                if( value.getClass()==String.class || SimpleTypes.isSimple(value.getClass()) ){
                    text(value.toString());
                } else if( value instanceof Serializable ){
                    attribute("encode", "hex");
                    text(HexCodec.serialize((Serializable) value));
                }else{
                    xout.comment("can't encode "+value);
                }
            }
            endElement();
        }

        endElement();
    }
    public synchronized void write(MLineNumber bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLineNumber");

        if( bc.getLabel()!=null )attribute("label", bc.getLabel() );
        attribute("line", bc.getLine());

        endElement();
    }
    public synchronized void write(MLocalVariable bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLocalVariable");
        if( bc.getName()!=null )attribute("name", bc.getName());
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        if( bc.getSignature()!=null )attribute("signature", bc.getSignature());
        if( bc.getLabelStart()!=null )attribute("labelStart", bc.getLabelStart());
        if( bc.getLabelEnd()!=null )attribute("labelEnd", bc.getLabelEnd());
        attribute("index", bc.getIndex());
        endElement();
    }
    public synchronized void write(MLocalVariableAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLocalVariableAnnotation");

        attribute("typeRef", bc.getTypeRef());
        if( bc.getTypePath()!=null )attribute("typePath", bc.getTypePath());
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        attribute("visible", bc.isVisible());

        var sls = bc.getStartLabels();
        if( sls!=null ){
            for( var sl : sls ){
                startElement("start");
                if( sl != null ) text(sl);
                endElement();
            }
        }

        var els = bc.getEndLabels();
        if( els!=null ){
            for( var el : els ){
                startElement("end");
                if( el != null ) text(el);
                endElement();
            }
        }

        var idxs = bc.getIndex();
        if( idxs!=null ){
            for( var idx : idxs ){
                startElement("index");
                text(Integer.toString(idx));
                endElement();
            }
        }

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MLookupSwitchInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MLookupSwitchInsn");
        if( bc.getDefaultHandlerLabel()!=null )attribute("defaultHandlerLabel", bc.getDefaultHandlerLabel());

        var keys = bc.getKeys();
        if( keys!=null ){
            for( var key : keys ){
                startElement("key");
                text(Integer.toString(key));
                endElement();
            }
        }

        var lbls = bc.getLabels();
        if( lbls!=null ){
            for( var lbl : lbls ){
                startElement("label");
                if( lbl!=null ){
                    attribute("isnull", false);
                    text(lbl);
                }else{
                    attribute("isnull", true);
                }
                endElement();
            }
        }

        endElement();
    }
    public synchronized void write(MMaxs bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MMaxs");
        attribute("maxLocals",bc.getMaxLocals());
        attribute("maxStack",bc.getMaxStack());
        endElement();
    }
    public synchronized void write(MMethodInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MMethodInsn");
        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );
        if( bc.getOwner()!=null )attribute("owner", bc.getOwner());
        if( bc.getName()!=null )attribute("name", bc.getName());
        if( bc.getDescriptor()!=null )attribute("descriptor", bc.getDescriptor());
        attribute("iface",bc.isIface());
        endElement();
    }
    public synchronized void write(MMultiANewArrayInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MMultiANewArrayInsn");
        if( bc.getDescriptor()!=null )attribute("descriptor",bc.getDescriptor());
        attribute("numDimensions",bc.getNumDimensions());
        endElement();
    }
    public synchronized void write(MParameter bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MParameter");

        if( bc.getName()!=null )attribute("name",bc.getName());

        attribute("access", bc.getAccess());
        attribute("accessDecode", new AccFlags(bc.getAccess()).flags().toString() );
        attribute("final",bc.isFinal());
        attribute("mandated",bc.isMandated());
        attribute("synthetic",bc.isSynthetic());

        endElement();
    }
    public synchronized void write(MParameterAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MParameterAnnotation");

        attribute("parameter",bc.getParameter());
        if( bc.getDescriptor()!=null )attribute("descriptor",bc.getDescriptor());
        attribute("visible",bc.isVisible());

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MTableSwitchInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MTableSwitchInsn");

        attribute("max", bc.getMax());
        attribute("min", bc.getMin());
        if( bc.getDefaultLabel()!=null )attribute("defaultLabel", bc.getDefaultLabel());

        var lbls = bc.getLabels();
        if( lbls!=null ){
            for( var lbl : lbls ){
                startElement("label");
                attribute("isnull", lbl==null);
                if( lbl!=null ){
                    text(lbl);
                }
                endElement();
            }
        }

        endElement();
    }
    public synchronized void write(MTryCatchAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MTryCatchAnnotation");

        attribute("typeRef",bc.getTypeRef());
        if( bc.getTypePath()!=null )attribute("typePath",bc.getTypePath());
        if( bc.getDescriptor()!=null )attribute("descriptor",bc.getDescriptor());
        attribute("visible",bc.isVisible());

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MTryCatchBlock bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MTryCatchBlock");
        if( bc.getLabelStart()!=null )attribute("labelStart",bc.getLabelStart());
        if( bc.getLabelEnd()!=null )attribute("labelEnd",bc.getLabelEnd());
        if( bc.getLabelHandler()!=null )attribute("labelHandler",bc.getLabelHandler());
        if( bc.getType()!=null )attribute("type",bc.getType());
        endElement();
    }
    public synchronized void write(MTypeAnnotation bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MTypeAnnotation");

        attribute("typeRef",bc.getTypeRef());
        if( bc.getTypePath()!=null )attribute("typePath",bc.getTypePath());
        if( bc.getDescriptor()!=null )attribute("descriptor",bc.getDescriptor());
        attribute("visible",bc.isVisible());

        var annBc = bc.getAnnotationByteCodes();
        if( annBc!=null ){
            writeAnnotationByteCode(annBc);
        }

        endElement();
    }
    public synchronized void write(MTypeInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MTypeInsn");

        if( bc.getType()!=null )attribute("type", bc.getType());

        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );

        endElement();
    }
    public synchronized void write(MVarInsn bc){
        if( bc==null )throw new IllegalArgumentException( "bc==null" );
        startElement("MVarInsn");
        attribute( "variable", bc.getVariable() );
        attribute("opcode", bc.getOpcode());
        OpCode.code(bc.getOpcode()).ifPresent( c -> attribute("opcodeName", c.name()) );
        endElement();
    }
    public synchronized void writeAnnotationByteCode(List<AnnotationByteCode> anns){
        if( anns==null )throw new IllegalArgumentException( "anns==null" );
        for( var ann : anns )write( ann );
    }
    public synchronized void write(AnnotationByteCode an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        else if( an instanceof AEnd )write((AEnd) an);
        else if( an instanceof AEnum )write((AEnum) an);
        else if( an instanceof EmAArray )write((EmAArray) an);
        else if( an instanceof EmANameDesc )write((EmANameDesc) an);
        else if( an instanceof APair )write((APair<?>) an);
        else {
            xout.comment("undefined AnnotationByteCode "+an);
        }
    }
    public synchronized void write(AEnd an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("AEnd");
        endElement();
    }
    public synchronized void write(AEnum an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("AEnum");
        if( an.getName()!=null )attribute("name",an.getName());
        if( an.getDescriptor()!=null )attribute("descriptor",an.getDescriptor());
        if( an.getValue()!=null ){
            startElement("AEnumValue");
            text(an.getValue());
            endElement();
        }
        endElement();
    }
    public synchronized void write(EmAArray an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("EmAArray");
        if( an.getName()!=null )attribute("",an.getName());
        endElement();
    }
    public synchronized void write(EmANameDesc an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("EmANameDesc");
        if( an.getDescriptor()!=null )attribute("descriptor",an.getDescriptor());
        if( an.getName()!=null )attribute("name",an.getName());
        endElement();
    }
    public synchronized void write(APair<?> an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        if( an instanceof APair.APairString )write( (APair.APairString)an );
        else if( an instanceof APair.APairByte )write( (APair.APairByte)an );
        else if( an instanceof APair.APairBoolean )write( (APair.APairBoolean)an );
        else if( an instanceof APair.APairCharacter )write( (APair.APairCharacter)an );
        else if( an instanceof APair.APairShort )write( (APair.APairShort)an );
        else if( an instanceof APair.APairInteger )write( (APair.APairInteger)an );
        else if( an instanceof APair.APairLong )write( (APair.APairLong)an );
        else if( an instanceof APair.APairDouble )write( (APair.APairDouble)an );
        else if( an instanceof APair.APairBooleanArr1D )write( (APair.APairBooleanArr1D)an );
        else if( an instanceof APair.APairCharArr1D )write( (APair.APairCharArr1D)an );
        else if( an instanceof APair.APairByteArr1D )write( (APair.APairByteArr1D)an );
        else if( an instanceof APair.APairShortArr1D )write( (APair.APairShortArr1D)an );
        else if( an instanceof APair.APairIntArr1D )write( (APair.APairIntArr1D)an );
        else if( an instanceof APair.APairFloatArr1D )write( (APair.APairFloatArr1D)an );
        else if( an instanceof APair.APairLongArr1D )write( (APair.APairLongArr1D)an );
        else if( an instanceof APair.APairDoubleArr1D )write( (APair.APairDoubleArr1D)an );
        else if( an instanceof APair.APairSerializable )write( (APair.APairSerializable)an );
        else {
            xout.comment("undefined APair "+an);
        }
    }
    public synchronized void write(APair.APairString an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairString");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairStringValue");
            text(an.getValue());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairByte an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairByte");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairByteValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairBoolean an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairBoolean");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairBooleanValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairCharacter an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairCharacter");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairCharacterValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairShort an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairShort");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairShortValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairInteger an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairInteger");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairIntegerValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairLong an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairLong");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairLongValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairFloat an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairFloat");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairFloatValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairDouble an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairDouble");
        if( an.getName()!=null )attribute("name", an.getName());
        if( an.getValue()!=null ){
            startElement("APairDoubleValue");
            text(an.getValue().toString());
            endElement();
        }
        endElement();
    }
    public synchronized void write(APair.APairBooleanArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairBooleanArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairBooleanArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairCharArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );
        startElement("APairCharArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        attribute("encode", "charArray");
        if( an.getValue()!=null ){
            startElement("APairCharArr1DValue");
            text(CharArrayCodec.encode(an.getValue()));
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairByteArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairByteArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairByteArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairShortArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairShortArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairShortArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairIntArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairIntArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairIntArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairFloatArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairFloatArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairFloatArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairLongArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairLongArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairLongArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairDoubleArr1D an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairDoubleArr1D");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairDoubleArr1DValue");
            StringBuilder sb = new StringBuilder();
            for( var v : an.getValue() ){
                if( sb.length() > 0 ) sb.append(",");
                sb.append(v);
            }
            text(sb.toString());
            endElement();
        }

        endElement();
    }
    public synchronized void write(APair.APairSerializable an){
        if( an==null )throw new IllegalArgumentException( "an==null" );

        startElement("APairSerializable");

        if( an.getName()!=null )attribute("name", an.getName());

        if( an.getValue()!=null ){
            startElement("APairSerializableValue");
            attribute("encode", "hex");
            text(HexCodec.serialize(an.getValue()));
            endElement();
        }

        endElement();
    }
}
