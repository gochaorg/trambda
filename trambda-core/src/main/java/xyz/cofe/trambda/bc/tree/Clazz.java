package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.objectweb.asm.ClassWriter;
import xyz.cofe.trambda.Tuple2;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationDef;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.ann.EmbededAnnotation;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.cls.CSource;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.cls.ClsByteCode;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;

public class Clazz implements GetAnnotations, GetDefinition {
    protected static final Pattern namePattern =
        Pattern.compile("(?is)[$\\w][$\\w\\d_]*(\\.[$\\w][$\\w\\d_]*)*");

    public Clazz(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !namePattern.matcher(name).matches() ){
            throw new IllegalArgumentException("name not valid, serr regex: "+namePattern);
        }

        definition = new CBegin();
        definition.setAccess(
            new AccFlags(0)
                .withOpen(true)
                .withPublic(true)
                .withPublic(true)
                .withSyncronized(true)
                .withTransitive(true)
            .value()
        );
        definition.setName(name.replace(".","/"));
        definition.setSuperName("java/lang/Object");
    }

    public Clazz( List<? extends ByteCode> byteCode ){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );

        definition = byteCode.stream().filter(f -> f instanceof CBegin).map(x -> (CBegin) x).findFirst().orElse(null);
        source = byteCode.stream().filter(f -> f instanceof CSource).map(x -> (CSource) x).findFirst().orElse(null);

        methods = byteCode.stream().map(
            bc -> bc instanceof CMethod ? (CMethod)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Method(cm, byteCode) )
            .collect(Collectors.toList());

        annotations = byteCode.stream().map(
            bc -> bc instanceof ClsByteCode && bc instanceof AnnotationDef ? (AnnotationDef)bc : null
        ).filter( Objects::nonNull )
            .map( a -> new Annotation(a, byteCode).annotationDefVisitorId(a.getAnnotationDefVisitorId()) )
            .collect(Collectors.toList());

        fields = byteCode.stream().map(
            bc -> bc instanceof CField ? (CField)bc : null
        ).filter( Objects::nonNull )
            .map( cm -> new Field(cm, byteCode) )
            .collect(Collectors.toList());
    }

    //region definition : CBegin
    protected CBegin definition;
    public synchronized CBegin getDefinition(){
        return definition;
    }
    public synchronized void setDefinition(CBegin begin){
        this.definition = begin;
    }
    //endregion

    public synchronized String getName(){
        if( definition==null )throw new IllegalStateException("definition==null");
        return definition.getName().replace("/",".");
    }
    public synchronized void setName(String name){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( !namePattern.matcher(name).matches() ){
            throw new IllegalArgumentException("name not valid, serr regex: "+namePattern);
        }
        if( definition==null ){
            definition = new CBegin();
            definition.setAccess(
                new AccFlags(0)
                    .withOpen(true)
                    .withPublic(true)
                    .withPublic(true)
                    .withSyncronized(true)
                    .withTransitive(true)
                    .value()
            );
            definition.setSuperName("java/lang/Object");
        }
        definition.setName(name.replace(".","/"));
    }

//    public synchronized Optional<String> getSuperClassname(){
//        if( definition==null )return Optional.empty();
//
//        String suprClass = definition.getSuperName();
//        if( suprClass==null )return Optional.empty();
//
//        return
//            Optional.of(
//                suprClass.replace("/",".")
//            );
//    }

    //region source : CSource
    protected CSource source;
    public synchronized CSource getSource(){
        return source;
    }
    public synchronized void setSource(CSource source){
        this.source = source;
    }
    //endregion
    //region methods : List<Method>
    protected List<Method> methods;
    public synchronized List<Method> getMethods(){
        if( methods==null )methods = new ArrayList<>();
        return methods;
    }
    public synchronized void setMethods(List<Method> methods){
        this.methods = methods;
    }
    //endregion
    //region annotations : List<Annotation>
    protected List<Annotation> annotations;
    public synchronized List<Annotation> getAnnotations(){
        if( annotations==null )annotations = new ArrayList<>();
        return annotations;
    }
    public synchronized void setAnnotations(List<Annotation> annotations){
        this.annotations = annotations;
    }
    //endregion
    //region fields : List<Field>
    protected List<Field> fields;
    public synchronized List<Field> getFields(){
        if( fields==null )fields = new ArrayList<>();
        return fields;
    }
    public synchronized void setFields(List<Field> fields){
        this.fields = fields;
    }
    //endregion

    public synchronized byte[] toByteCode(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        write(cw);
        return cw.toByteArray();
    }

    public synchronized void write(ClassWriter cw){
        if( cw==null )throw new IllegalArgumentException( "cw==null" );

        /*
        ClassBegin version=55, access=33, name='xyz/cofe/trambda/clss/User2', signature='null', superName='java/lang/Object', interfaces=[]
            ClassSource source='User2.java', debug='null'

            CAnnotation descriptor=Lxyz/cofe/trambda/clss/Desc; visible=true ann.v.id=1
                APairString name=value value="sample User2"
                AEnd

            CField access=2, name='name', descriptor='Ljava/lang/String;', signature='null', value=null
                FAnnotation descriptor=Lxyz/cofe/trambda/clss/Desc; visible=true
                    APairString name=value value="name of user"
                    AEnd
                FieldEnd

            CField access=2, name='emails', descriptor='Ljava/util/List;', signature='Ljava/util/List<Ljava/lang/String;>;', value=null
                FieldEnd

            CMethod access=1, name='<init>', descriptor='()V', signature='null', exceptions=null
                MCode
                MLabel L1448061896
                MLineNumber 7 L1448061896
                MVarInsn ALOAD #25 0
                MMethodInsn INVOKESPECIAL #183 owner=java/lang/Object name=<init> desc=()V iface=false
                MInsn RETURN #177
                MLabel L574434418
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L1448061896 L574434418 0
                MMaxs { stack=1, locals=1 }
                MEnd

            CMethod access=1, name='<init>', descriptor='(Ljava/lang/String;)V', signature='null', exceptions=null
                MCode
                MLabel L1072410641
                MLineNumber 8 L1072410641
                MVarInsn ALOAD #25 0
                MMethodInsn INVOKESPECIAL #183 owner=java/lang/Object name=<init> desc=()V iface=false
                MLabel L283318938
                MLineNumber 9 L283318938
                MVarInsn ALOAD #25 0
                MVarInsn ALOAD #25 1
                MFieldInsn {opcode=PUTFIELD #181, owner='xyz/cofe/trambda/clss/User2', name='name', descriptor='Ljava/lang/String;'}
                MLabel L361571968
                MLineNumber 10 L361571968
                MInsn RETURN #177
                MLabel L2005169944
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L1072410641 L2005169944 0
                MLocalVariable name1 Ljava/lang/String; null L1072410641 L2005169944 1
                MMaxs { stack=2, locals=2 }
                MEnd

            CMethod access=1, name='getName', descriptor='()Ljava/lang/String;', signature='null', exceptions=null
                MAnnotation descriptor=Lxyz/cofe/trambda/clss/Desc; visible=true ann.v.id=3
                    APairString name=value value="name of user"
                    AEnd
                MCode
                MLabel L1470344997
                MLineNumber 16 L1470344997
                MVarInsn ALOAD #25 0
                MFieldInsn {opcode=GETFIELD #180, owner='xyz/cofe/trambda/clss/User2', name='name', descriptor='Ljava/lang/String;'}
                MInsn ARETURN #176
                MLabel L728115831
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L1470344997 L728115831 0
                MMaxs { stack=1, locals=1 }
                MEnd

            CMethod access=1, name='setName', descriptor='(Ljava/lang/String;)V', signature='null', exceptions=null
                MAnnotableParameterCount parameterCount=1 visible=true
                    MParameterAnnotation parameter=0 descriptor=Lxyz/cofe/trambda/clss/Required; visible=true
                        AEnd
                    MParameterAnnotation parameter=0 descriptor=Lxyz/cofe/trambda/clss/MaxLength; visible=true
                        APairInteger name=value value="100"
                        AEnd
                    MParameterAnnotation parameter=0 descriptor=Lxyz/cofe/trambda/clss/MinLength; visible=true
                        APairInteger name=value value="1"
                        AEnd
                MCode
                MLabel L210506412
                MLineNumber 17 L210506412
                MVarInsn ALOAD #25 0
                MVarInsn ALOAD #25 1
                MFieldInsn {opcode=PUTFIELD #181, owner='xyz/cofe/trambda/clss/User2', name='name', descriptor='Ljava/lang/String;'}
                MInsn RETURN #177
                MLabel L112049309
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L210506412 L112049309 0
                MLocalVariable name Ljava/lang/String; null L210506412 L112049309 1
                MMaxs { stack=2, locals=2 }
                MEnd

            CMethod access=1, name='getEmails', descriptor='()Ljava/util/List;', signature='()Ljava/util/List<Ljava/lang/String;>;', exceptions=null
                MAnnotation descriptor=Lxyz/cofe/trambda/clss/Desc; visible=true ann.v.id=7
                    APairString name=value value="emails of user"
                    AEnd
                MCode
                MLabel L1162918744
                MLineNumber 22 L1162918744
                MVarInsn ALOAD #25 0
                MFieldInsn {opcode=GETFIELD #180, owner='xyz/cofe/trambda/clss/User2', name='emails', descriptor='Ljava/util/List;'}
                MInsn ARETURN #176
                MLabel L1321530272
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L1162918744 L1321530272 0
                MMaxs { stack=1, locals=1 }
                MEnd

            CMethod access=1, name='setEmails', descriptor='(Ljava/util/List;)V', signature='(Ljava/util/List<Ljava/lang/String;>;)V', exceptions=null
                MCode
                MLabel L573673894
                MLineNumber 23 L573673894
                MVarInsn ALOAD #25 0
                MVarInsn ALOAD #25 1
                MFieldInsn {opcode=PUTFIELD #181, owner='xyz/cofe/trambda/clss/User2', name='emails', descriptor='Ljava/util/List;'}
                MInsn RETURN #177
                MLabel L1226020905
                MLocalVariable this Lxyz/cofe/trambda/clss/User2; null L573673894 L1226020905 0
                MLocalVariable emails Ljava/util/List; Ljava/util/List<Ljava/lang/String;>; L573673894 L1226020905 1
                MMaxs { stack=2, locals=2 }
                MEnd

            CEnd
        */

        if( definition==null )throw new IllegalStateException("definition==null");

        cw.visit(definition.getVersion(), definition.getAccess(), definition.getName(), definition.getSignature(), definition.getSuperName(), definition.getInterfaces());

        if( source!=null ){
            cw.visitSource(source.getSource(),source.getDebug());
        }

        if( annotations!=null ){
            for( var a : annotations ){
                if( a!=null ){
                    a.write(cw);
                }
            }
        }

        if( fields!=null ){
            for( var f : fields ){
                if( f!=null ){
                    f.write(cw);
                }
            }
        }

        if( methods!=null ){
            for( var m : methods ){
                if( m!=null ){
                    m.write(cw);
                }
            }
        }

        cw.visitEnd();
    }
}
