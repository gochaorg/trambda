package xyz.cofe.trambda.clss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypeReference;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.text.out.Output;
import xyz.cofe.trambda.ClassDump;
import xyz.cofe.trambda.bc.AccFlags;
import xyz.cofe.trambda.bc.ByteCode;
import xyz.cofe.trambda.bc.ann.EmbededAnnotation;
import xyz.cofe.trambda.bc.cls.CBegin;
import xyz.cofe.trambda.bc.cls.CField;
import xyz.cofe.trambda.bc.cls.CMethod;

public class ClassSerTest {
    private final Output out = new Output();

    //region test01
    @Test
    public void test01(){
        var classSrc = User2.class;
        var classUrl = classSrc.getResource(
            "/"+
            classSrc.getName().replace(".","/")+".class"
        );
        out.println("url "+classUrl);
        out.println("-".repeat(80));

        try{
            var classBytes = IOFun.readBytes(classUrl);
            ClassReader classReader = new ClassReader(classBytes);
            classReader.accept(classVisitor(),0);
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private ClassVisitor classVisitor(){
        return new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
                out.println(
                    "version="+version+
                        " access="+(new AccFlags(access).flags())+
                        " name="+name+
                        " signature="+signature+
                        " superName="+superName+
                        " interfaces="+(interfaces!=null ? Arrays.asList(interfaces) : null));
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public void visitSource(String source, String debug){
                out.println("source="+source+" debug="+debug);
                super.visitSource(source, debug);
            }

            @Override
            public ModuleVisitor visitModule(String name, int access, String version){
                out.println("module name="+name+" access="+(new AccFlags(access).flags())+" version="+version);
                return super.visitModule(name, access, version);
            }

            @Override
            public void visitNestHost(String nestHost){
                out.println("nestHost="+nestHost);
                super.visitNestHost(nestHost);
            }

            @Override
            public void visitOuterClass(String owner, String name, String descriptor){
                out.println("outerClass owner="+owner+" name="+name+" descriptor="+descriptor);
                super.visitOuterClass(owner, name, descriptor);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
                out.println("annotation descriptor="+descriptor+" visible="+visible);
                String pref = out.getLinePrefix();
                out.setLinePrefix( (pref!=null ? pref.trim() : "")+"ann> " );
                return annotationVisitor();
            }

            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
                out.println("typeAnn typeRef="+typeRef+" typePath="+typePath+" descriptor="+descriptor+" visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            @Override
            public void visitAttribute(Attribute attribute){
                out.println("ann "+attribute);
                super.visitAttribute(attribute);
            }

            @Override
            public void visitNestMember(String nestMember){
                out.println("nestMember "+nestMember);
                super.visitNestMember(nestMember);
            }

            @Override
            public void visitPermittedSubclass(String permittedSubclass){
                out.println("permittedSubclass "+permittedSubclass);
                super.visitPermittedSubclass(permittedSubclass);
            }

            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access){
                out.println("innerClass name="+name+" outerName="+outerName+" innerName="+innerName+
                    " access="+(new AccFlags(access).flags()));
                super.visitInnerClass(name, outerName, innerName, access);
            }

            @Override
            public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature){
                out.println("recordComponent name="+name+" descriptor="+descriptor+" signature="+signature);
                return super.visitRecordComponent(name, descriptor, signature);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value){
                out.println("field" +
                    " access="+(new AccFlags(access).flags())+
                    " name="+name+" descriptor="+descriptor+" signature="+signature+" value="+value
                );
                out.setLinePrefix("field> ");
                return fieldVisitor(access,name,descriptor,signature,value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions){
                out.println(
                    "method"+
                        " access="+(new AccFlags(access).flags())+
                        " name="+name+" descriptor="+descriptor+" signature="+signature+
                        " exceptions="+(exceptions!=null ? Arrays.asList(exceptions) : "null")
                );
                out.setLinePrefix("method> ");
                return methodVisitor();
            }

            @Override
            public void visitEnd(){
                out.setLinePrefix("");
                out.println("end");
                super.visitEnd();
            }
        };
    }
    private FieldVisitor fieldVisitor(int access, String name, String descriptor, String signature, Object value){
        return new FieldVisitor(Opcodes.ASM9) {
            /**
             * Visits an annotation of the field.
             *
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
                out.println("annotation" +
                    " descriptor="+descriptor+
                    " visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an annotation on the type of the field.
             *
             * @param typeRef    a reference to the annotated type. The sort of this type reference must be
             *                   {@link TypeReference#FIELD}. See {@link TypeReference}.
             * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
             *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
             *                   'typeRef' as a whole.
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
                out.println("typeAnnotation" +
                    " typeRef="+typeRef+
                    " typePath="+typePath+
                    " descriptor="+descriptor+
                    " visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            @Override
            public void visitAttribute(Attribute attribute){
                out.print("attribute");
                if( attribute!=null ){
                    out.print(" ");
                    out.print(" "+attribute.type+"/"+attribute.getClass());
                }
                out.println();
            }

            @Override
            public void visitEnd(){
                out.println("end");
                out.setLinePrefix("");
            }
        };
    }
    private AnnotationVisitor annotationVisitor(){
        return new AnnotationVisitor(Opcodes.ASM9) {
            /**
             * Visits a primitive value of the annotation.
             *
             * @param name  the value name.
             * @param value the actual value, whose type must be {@link Byte}, {@link Boolean}, {@link
             *              Character}, {@link Short}, {@link Integer} , {@link Long}, {@link Float}, {@link Double},
             *              {@link String} or {@link Type} of {@link Type#OBJECT} or {@link Type#ARRAY} sort. This
             *              value can also be an array of byte, boolean, short, char, int, long, float or double values
             *              (this is equivalent to using {@link #visitArray} and visiting each array element in turn,
             */
            @Override
            public void visit(String name, Object value){
                if( value instanceof String ){
                    out.println("name="+name+" value=\""+value+"\" : "+value.getClass());
                }else{
                    out.println("name="+name+" value="+value+" : "+(value!=null ? value.getClass().getName() : "null"));
                }
            }

            /**
             * Visits an enumeration value of the annotation.
             *
             * @param name       the value name.
             * @param descriptor the class descriptor of the enumeration class.
             * @param value      the actual enumeration value.
             */
            @Override
            public void visitEnum(String name, String descriptor, String value){
                out.println("enum name="+name+" descriptor="+descriptor+" value="+value);
            }

            /**
             * Visits a nested annotation value of the annotation.
             *
             * @param name       the value name.
             * @param descriptor the class descriptor of the nested annotation class.
             * @return a visitor to visit the actual nested annotation value, or {@literal null} if this
             * visitor is not interested in visiting this nested annotation. <i>The nested annotation
             * value must be fully visited before calling other methods on this annotation visitor</i>.
             */
            @Override
            public AnnotationVisitor visitAnnotation(String name, String descriptor){
                out.println("ann name="+name+" descriptor="+descriptor);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an array value of the annotation. Note that arrays of primitive values (such as byte,
             * boolean, short, char, int, long, float or double) can be passed as value to {@link #visit
             * visit}. This is what {@link ClassReader} does for non empty arrays of primitive values.
             *
             * @param name the value name.
             * @return a visitor to visit the actual array value elements, or {@literal null} if this visitor
             * is not interested in visiting these values. The 'name' parameters passed to the methods of
             * this visitor are ignored. <i>All the array values must be visited before calling other
             * methods on this annotation visitor</i>.
             */
            @Override
            public AnnotationVisitor visitArray(String name){
                out.println("array name="+name);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits the end of the annotation.
             */
            @Override
            public void visitEnd(){
                var pref = out.getLinePrefix().split(">");
                if( pref.length>1 ){
                    pref = Arrays.copyOf(pref,pref.length-1);
                    if( pref.length>1 ){
                        out.setLinePrefix(
                            Arrays.asList(pref).stream().reduce((a,b)->a+">"+b).get()+"> "
                        );
                    }else{
                        out.setLinePrefix(
                            pref[0]+"> "
                        );
                    }
                } else{
                    out.setLinePrefix("");
                }
            }
        };
    }
    private MethodVisitor methodVisitor(){
        return new MethodVisitor(Opcodes.ASM9) {
            /**
             * Visits a parameter of this method.
             *
             * @param name   parameter name or {@literal null} if none is provided.
             * @param access the parameter's access flags, only {@code ACC_FINAL}, {@code ACC_SYNTHETIC}
             *               or/and {@code ACC_MANDATED} are allowed (see {@link Opcodes}).
             */
            @Override
            public void visitParameter(String name, int access){
                out.println("parameter "+name+" access="+(new AccFlags(access).flags()));
            }

            /**
             * Visits the default value of this annotation interface method.
             *
             * @return a visitor to the visit the actual default value of this annotation interface method, or
             * {@literal null} if this visitor is not interested in visiting this default value. The
             * 'name' parameters passed to the methods of this annotation visitor are ignored. Moreover,
             * exacly one visit method must be called on this annotation visitor, followed by visitEnd.
             */
            @Override
            public AnnotationVisitor visitAnnotationDefault(){
                out.println("annotationDefault");
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an annotation of this method.
             *
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible){
                out.println("annotation descriptor="+descriptor+" visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an annotation on a type in the method signature.
             *
             * @param typeRef    a reference to the annotated type. The sort of this type reference must be
             *                   {@link TypeReference#METHOD_TYPE_PARAMETER}, {@link
             *                   TypeReference#METHOD_TYPE_PARAMETER_BOUND}, {@link TypeReference#METHOD_RETURN}, {@link
             *                   TypeReference#METHOD_RECEIVER}, {@link TypeReference#METHOD_FORMAL_PARAMETER} or {@link
             *                   TypeReference#THROWS}. See {@link TypeReference}.
             * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
             *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
             *                   'typeRef' as a whole.
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
                out.println("typeAnnotation" +
                    " typeRef="+typeRef+
                    " typePath="+typePath+
                    " descriptor="+descriptor+
                    " visible="+visible);

                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits the number of method parameters that can have annotations. By default (i.e. when this
             * method is not called), all the method parameters defined by the method descriptor can have
             * annotations.
             *
             * @param parameterCount the number of method parameters than can have annotations. This number
             *                       must be less or equal than the number of parameter types in the method descriptor. It can
             *                       be strictly less when a method has synthetic parameters and when these parameters are
             *                       ignored when computing parameter indices for the purpose of parameter annotations (see
             *                       https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
             * @param visible        {@literal true} to define the number of method parameters that can have
             *                       annotations visible at runtime, {@literal false} to define the number of method parameters
             */
            @Override
            public void visitAnnotableParameterCount(int parameterCount, boolean visible){
                out.println("AnnotableParameterCount parameterCount="+parameterCount+" visible="+visible);
            }

            /**
             * Visits an annotation of a parameter this method.
             *
             * @param parameter  the parameter index. This index must be strictly smaller than the number of
             *                   parameters in the method descriptor, and strictly smaller than the parameter count
             *                   specified in {@link #visitAnnotableParameterCount}. Important note: <i>a parameter index i
             *                   is not required to correspond to the i'th parameter descriptor in the method
             *                   descriptor</i>, in particular in case of synthetic parameters (see
             *                   https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.18).
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible){
                out.println("ParameterAnnotation param="+parameter+" desc="+descriptor+" visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits a non standard attribute of this method.
             *
             * @param attribute an attribute.
             */
            @Override
            public void visitAttribute(Attribute attribute){
                out.println("attribute "+attribute+" : "+(attribute!=null ? attribute.type+"/"+attribute.getClass().getName() : "null"));
            }

            /**
             * Visits an annotation on an instruction. This method must be called just <i>after</i> the
             * annotated instruction. It can be called several times for the same instruction.
             *
             * @param typeRef    a reference to the annotated type. The sort of this type reference must be
             *                   {@link TypeReference#INSTANCEOF}, {@link TypeReference#NEW}, {@link
             *                   TypeReference#CONSTRUCTOR_REFERENCE}, {@link TypeReference#METHOD_REFERENCE}, {@link
             *                   TypeReference#CAST}, {@link TypeReference#CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT}, {@link
             *                   TypeReference#METHOD_INVOCATION_TYPE_ARGUMENT}, {@link
             *                   TypeReference#CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT}, or {@link
             *                   TypeReference#METHOD_REFERENCE_TYPE_ARGUMENT}. See {@link TypeReference}.
             * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
             *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
             *                   'typeRef' as a whole.
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
                out.println("InsnAnnotation typeRef="+typeRef+" typePath="+typePath+" desc="+descriptor+" visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an annotation on an exception handler type. This method must be called <i>after</i> the
             * {@link #visitTryCatchBlock} for the annotated exception handler. It can be called several times
             * for the same exception handler.
             *
             * @param typeRef    a reference to the annotated type. The sort of this type reference must be
             *                   {@link TypeReference#EXCEPTION_PARAMETER}. See {@link TypeReference}.
             * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
             *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
             *                   'typeRef' as a whole.
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible){
                out.println("TryCatchAnnotation typeRef="+typeRef+" typePath="+typePath+" desc="+descriptor+" visible="+visible);
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            /**
             * Visits an annotation on a local variable type.
             *
             * @param typeRef    a reference to the annotated type. The sort of this type reference must be
             *                   {@link TypeReference#LOCAL_VARIABLE} or {@link TypeReference#RESOURCE_VARIABLE}. See {@link
             *                   TypeReference}.
             * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or
             *                   static inner type within 'typeRef'. May be {@literal null} if the annotation targets
             *                   'typeRef' as a whole.
             * @param start      the fist instructions corresponding to the continuous ranges that make the scope
             *                   of this local variable (inclusive).
             * @param end        the last instructions corresponding to the continuous ranges that make the scope of
             *                   this local variable (exclusive). This array must have the same size as the 'start' array.
             * @param index      the local variable's index in each range. This array must have the same size as
             *                   the 'start' array.
             * @param descriptor the class descriptor of the annotation class.
             * @param visible    {@literal true} if the annotation is visible at runtime.
             * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
             * interested in visiting this annotation.
             */
            @Override
            public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible){
                out.println("tLocalVariableAnnotation" +
                    " typeRef="+typeRef+" typePath="+typePath+" desc="+descriptor+" visible="+visible+
                    " start="+(start!=null ? Arrays.asList(start) : "null")+
                    " end="+(end!=null ? Arrays.asList(end) : "null")+
                    " index="+(index!=null ? Arrays.asList(index) : "null")+
                    ""
                );
                out.setLinePrefix( out.getLinePrefix().trim()+"ann> " );
                return annotationVisitor();
            }

            @Override
            public void visitEnd(){
                out.println("end");
                out.setLinePrefix("");
            }
        };
    }
    //endregion

    @Test
    public void test03(){
        var classSrc = User2.class;
        var classUrl = classSrc.getResource(
            "/"+
                classSrc.getName().replace(".","/")+".class"
        );
        out.println("url "+classUrl);
        out.println("-".repeat(80));

        try{
            var classBytes = IOFun.readBytes(classUrl);
            ClassReader classReader = new ClassReader(classBytes);

            List<ByteCode> byteCodes = new ArrayList<>();

            ClassDump dump = new ClassDump();
            dump.byteCode( byteCodes::add );
            classReader.accept(dump,0);

            CBegin begin = byteCodes.stream().filter( b -> b instanceof CBegin ).map( x -> (CBegin)x ).findFirst().get();

            System.out.println("class "+begin);

            begin.walk().tree().forEach( ts -> {
                if( ts.getLevel()>0 ){
                    var pref = ts.nodes().limit(ts.getLevel()).map( b -> {
                        if( b instanceof CMethod ){
                            return CMethod.class.getSimpleName()+"#"+((CMethod) b).getName()+"()";
                        }else if( b instanceof CField ){
                            return CField.class.getSimpleName()+"#"+((CField)b).getName();
                        }
                        return b.getClass().getSimpleName();
                    }).reduce("", (a,b)->a+"/"+b);
                    System.out.print(pref);
                }
                System.out.println("/"+ts.getNode());
            });

            System.out.println("- ".repeat(40));

            byte[] bytes = begin.toByteCode();
            dump.byteCode(System.out::println);
            classReader = new ClassReader(bytes);
            classReader.accept(dump,0);
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
