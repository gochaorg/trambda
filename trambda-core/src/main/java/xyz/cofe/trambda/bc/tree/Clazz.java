package xyz.cofe.trambda.bc.tree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import xyz.cofe.trambda.Tuple2;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.AnnotationByteCode;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.ann.EmbededAnnotation;
import xyz.cofe.trambda.bc.cls.CAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.cls.CTypeAnnotation;
import xyz.cofe.trambda.bc.cls.ClsByteCode;
import xyz.cofe.trambda.bc.fld.FieldByteCode;
import xyz.cofe.trambda.bc.mth.MethodByteCode;

public class Clazz {
    public static Clazz create(List<? extends ByteCode> byteCode){
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );

        List<ClsByteCode> classByteCode = new ArrayList<>();

        Map<Integer,List<MethodByteCode>> methodByteCode = new LinkedHashMap<>();
        Map<Integer,CMethod> methodDef = new LinkedHashMap<>();

        Map<Integer,List<FieldByteCode>> fieldByteCode = new LinkedHashMap<>();
        Map<Integer,CField> fieldDef = new LinkedHashMap<>();

        Map<Integer,List<AnnotationByteCode>> annByteCode = new LinkedHashMap<>();
        Map<Integer,Annotation> annotationDef = new LinkedHashMap<>();
        Map<Integer,List<Integer>> parentChildAnn = new LinkedHashMap<>();

        Map<Annotation,Integer> rootAnn = new LinkedHashMap<>();

        for( var bc : byteCode ){
            if( bc instanceof ClsByteCode ){
                classByteCode.add((ClsByteCode) bc);
                if( bc instanceof CMethod ){
                    CMethod cm = (CMethod) bc;
                    methodDef.put(cm.getMethodVisitorId(), cm);
                }else if( bc instanceof CField ){
                    CField cf = (CField) bc;
                    fieldDef.put(cf.getFieldVisitorId(), cf);
                }else if( bc instanceof CAnnotation ){
                    CAnnotation ca = (CAnnotation) bc;
                    var a = Annotation.create(ca);
                    annotationDef.put(ca.getAnnotationVisitorId(), a);
                    rootAnn.put(a, ca.getAnnotationVisitorId());
                }else if( bc instanceof CTypeAnnotation ){
                    CTypeAnnotation cta = (CTypeAnnotation)bc;
                    var a = Annotation.create(cta);
                    annotationDef.put(cta.getAnnotationVisitorId(), a);
                    rootAnn.put(a,cta.getAnnotationVisitorId());
                }
            }else if( bc instanceof MethodByteCode ){
                MethodByteCode mbc = (MethodByteCode) bc;
                methodByteCode.computeIfAbsent(
                    mbc.getMethodVisitorId(),
                    x -> new ArrayList<>()
                ).add(mbc);
            }else if( bc instanceof FieldByteCode ){
                FieldByteCode fbc = (FieldByteCode)bc;
                fieldByteCode.computeIfAbsent( fbc.getFieldVisitorId(), x -> new ArrayList<>())
                    .add(fbc);
            }else if( bc instanceof AnnotationByteCode ){
                AnnotationByteCode abc = (AnnotationByteCode) bc;
                annByteCode.computeIfAbsent( abc.getAnnotationVisitorId(), x -> new ArrayList<>())
                    .add(abc);

                if( bc instanceof EmbededAnnotation ){
                    parentChildAnn.computeIfAbsent( abc.getAnnotationVisitorId(), x -> new ArrayList<>() )
                        .add( ((EmbededAnnotation) bc).getEmbededAnnotationVisitorId() );
                }
            }
        }

        Clazz clazz = new Clazz();
        return clazz;
    }
}
