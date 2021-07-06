package xyz.cofe.bc.xml.restore;

import java.util.ArrayList;
import java.util.Arrays;
import org.objectweb.asm.Opcodes;
import xyz.cofe.bc.xml.BCDeserializer;
import xyz.cofe.bc.xml.DeserializeRegistry;
import xyz.cofe.bc.xml.HexCodec;
import xyz.cofe.trambda.bc.bm.BootstrapMethArg;
import xyz.cofe.trambda.bc.bm.DoubleArg;
import xyz.cofe.trambda.bc.bm.FloatArg;
import xyz.cofe.trambda.bc.bm.HandleArg;
import xyz.cofe.trambda.bc.bm.IntArg;
import xyz.cofe.trambda.bc.bm.LdcType;
import xyz.cofe.trambda.bc.bm.LongArg;
import xyz.cofe.trambda.bc.bm.MHandle;
import xyz.cofe.trambda.bc.bm.StringArg;
import xyz.cofe.trambda.bc.bm.TypeArg;
import xyz.cofe.trambda.bc.cls.CMethod;
import xyz.cofe.trambda.bc.mth.*;
import static xyz.cofe.bc.xml.XEventPathBuilder.*;

public class MethodTags implements DeserializeRegistry {
    public static class FrameValue {
        public String tag;
        public boolean isnull;
        public String clazz;
        public String encode;
    }

    @Override
    public void registry(BCDeserializer derser){
        if( derser==null )return;

        derser.registry("MAnnotableParameterCount", ev -> {
            if( ev.isStartElement() ){
                var obj = new MAnnotableParameterCount();
                ev.attribute("parameterCount").tryInteger().ifPresent(obj::setParameterCount);
                ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);
                ev.contribute( obj );
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MAnnotation", ev -> {
            if( ev.isStartElement() ){
                var obj = new MAnnotation();
                ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);
                ev.contribute( obj );
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MAnnotationDefault", ev -> {
            if( ev.isStartElement() ){
                var obj = new MAnnotationDefault();
                ev.contribute( obj );
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });

        derser.registry( tag("MCode"), ev -> {
            if( ev.isStartElement() ){
                var obj = new MCode();
                ev.contribute( obj );
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });

        derser.registry("MEnd", ev -> {
            if( ev.isStartElement() ){
                var obj = new MEnd();
                ev.contribute( obj );
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MFieldInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MFieldInsn();
                ev.contribute( obj );
                ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
                ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));
                ev.attribute("owner").tryString().ifPresent(obj::setOwner);
                ev.attribute("name").tryString().ifPresent(obj::setName);
                ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MFrame", ev -> {
            if( ev.isStartElement() ){
                var obj = new MFrame();
                ev.contribute( obj );
                ev.attribute("type").tryInteger().ifPresent(obj::setType);
                ev.attribute("numLocal").tryInteger().ifPresent(obj::setNumLocal);
                ev.attribute("numStack").tryInteger().ifPresent(obj::setNumStack);
                ev.attribute("typeDecode").tryString().ifPresent( tname -> {
                    switch( tname ){
                        case "F_NEW": obj.setType(Opcodes.F_NEW); break;
                        case "F_APPEND": obj.setType(Opcodes.F_APPEND); break;
                        case "F_CHOP": obj.setType(Opcodes.F_CHOP); break;
                        case "F_FULL": obj.setType(Opcodes.F_FULL); break;
                        case "F_SAME": obj.setType(Opcodes.F_SAME); break;
                        case "F_SAME1": obj.setType(Opcodes.F_SAME1); break;
                    }
                });
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("frameLocal",ev -> {
            if( ev.isStartElement() ){
                var obj = new FrameValue();
                ev.contribute(obj);
                ev.attribute("isnull").tryBoolean().ifPresent(v->obj.isnull=v);
                ev.attribute("class").tryString().ifPresent(v->obj.clazz=v);
                ev.attribute("encode").tryString().ifPresent(v->obj.encode=v);
                obj.tag = "frameLocal";
            }else if( ev.isEndElement() ){
                ev.find(FrameValue.class).ifPresent( frameValue -> {
                    ev.find(MFrame.class).ifPresent( mFrame -> {
                        var lst = mFrame.getLocal();
                        if( lst==null ){
                            lst = new ArrayList<>();
                            mFrame.setLocal(lst);
                        }
                        if( frameValue.isnull ){
                            lst.add(null);
                        }else{
                            lst.add(HexCodec.deserialize(ev.text(false)));
                        }
                    });
                });
            }
        });
        derser.registry("frameStack",ev -> {
            if( ev.isStartElement() ){
                var obj = new FrameValue();
                ev.contribute(obj);
                ev.attribute("isnull").tryBoolean().ifPresent(v->obj.isnull=v);
                ev.attribute("class").tryString().ifPresent(v->obj.clazz=v);
                ev.attribute("encode").tryString().ifPresent(v->obj.encode=v);
                obj.tag = "frameStack";
            }else if( ev.isEndElement() ){
                ev.find(FrameValue.class).ifPresent( frameValue -> {
                    ev.find(MFrame.class).ifPresent( mFrame -> {
                        var lst = mFrame.getStack();
                        if( lst==null ){
                            lst = new ArrayList<>();
                            mFrame.setStack(lst);
                        }
                        if( frameValue.isnull ){
                            lst.add(null);
                        }else{
                            lst.add(HexCodec.deserialize(ev.text(false)));
                        }
                    });
                });
            }
        });
        derser.registry("MIincInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MIincInsn();
                ev.contribute( obj );
                ev.attribute("variable").tryInteger().ifPresent(obj::setVariable);
                ev.attribute("increment").tryInteger().ifPresent(obj::setIncrement);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MInsn();
                ev.contribute( obj );
                ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
                ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MInsnAnnotation", ev -> {
            if( ev.isStartElement() ){
                var obj = new MInsnAnnotation();
                ev.contribute( obj );
                ev.attribute("typeRef").tryInteger().ifPresent(obj::setTypeRef);
                ev.attribute("typePath").tryString().ifPresent(obj::setTypePath);
                ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MIntInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MIntInsn();
                ev.contribute( obj );
                ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
                ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));
                ev.attribute("operand").tryInteger().ifPresent(obj::setOperand);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MInvokeDynamicInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MInvokeDynamicInsn();
                ev.contribute( obj );
                ev.attribute("name").tryString().ifPresent(obj::setName);
                ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("bootstrapHandle", ev -> {
            if( ev.isStartElement() ){
                var obj = new MHandle();
                ev.attribute("name").tryString().ifPresent(obj::setName);
                ev.attribute("owner").tryString().ifPresent(obj::setOwner);
                ev.attribute("desc").tryString().ifPresent(obj::setDesc);
                ev.attribute("tag").tryInteger().ifPresent(obj::setTag);
                ev.attribute("iface").tryBoolean().ifPresent(obj::setIface);
                ev.find(MInvokeDynamicInsn.class).ifPresent(mInvokeDynamicInsn -> mInvokeDynamicInsn.setBootstrapMethodHandle(obj));
            }
        });

        derser.registry(
            tail(
                tag("MInvokeDynamicInsn"),
                and(
                    tag("BootstrapMethArg"),
                    attribute("isnull").eq(true),
                    isStart()
                )
            ),
            ev -> ev.find(MInvokeDynamicInsn.class).ifPresent(inv -> inv.getBootstrapMethodArguments().add(null)));

        derser.registry("TypeArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new TypeArg());
            }else if( ev.isEndElement() ){
                ev.find(TypeArg.class).ifPresent(a -> a.setType(ev.text()));
            }
        });

        derser.registry("StringArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new StringArg());
            }else if( ev.isEndElement() ){
                ev.find(StringArg.class).ifPresent(obj -> obj.setValue(ev.text()));
            }
        });

        derser.registry("LongArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new LongArg());
            }else if( ev.isEndElement() ){
                ev.find(LongArg.class).ifPresent(obj -> obj.setValue(Long.valueOf(ev.text())));
            }
        });

        derser.registry("IntArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new IntArg());
            }else if( ev.isEndElement() ){
                ev.find(IntArg.class).ifPresent(obj -> obj.setValue(Integer.valueOf(ev.text())));
            }
        });

        derser.registry("FloatArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new FloatArg());
            }else if( ev.isEndElement() ){
                ev.find(FloatArg.class).ifPresent(obj -> obj.setValue(Float.valueOf(ev.text())));
            }
        });

        derser.registry("DoubleArg", ev -> {
            if( ev.isStartElement() ){
                ev.contribute(new DoubleArg());
            }else if( ev.isEndElement() ){
                ev.find(DoubleArg.class).ifPresent(obj -> obj.setValue(Double.valueOf(ev.text())));
            }
        });

        derser.registry("MHandle", ev -> {
            if( ev.isStartElement() ){
                var obj = new MHandle();
                ev.contribute(obj);
                ev.attribute("tag").tryInteger().ifPresent(obj::setTag);
                ev.attribute("iface").tryBoolean().ifPresent(obj::setIface);
                ev.attribute("owner").tryString().ifPresent(obj::setOwner);
                ev.attribute("desc").tryString().ifPresent(obj::setDesc);
                ev.attribute("name").tryString().ifPresent(obj::setName);
            }
        });

        derser.registry("HandleArg", ev -> {
            if( ev.isStartElement() ){
                var obj = new HandleArg();
                var mh = new MHandle();
                obj.setHandle(mh);
                ev.contribute(obj);
                ev.attribute("tag").tryInteger().ifPresent(mh::setTag);
                ev.attribute("iface").tryBoolean().ifPresent(mh::setIface);
                ev.attribute("owner").tryString().ifPresent(mh::setOwner);
                ev.attribute("desc").tryString().ifPresent(mh::setDesc);
                ev.attribute("name").tryString().ifPresent(mh::setName);
            }
        });

        derser.registry(
            and(
                tail(
                    tag("MInvokeDynamicInsn"),
                    anyTag().repeat(2,2)
                ),
                isEnd()
            ),
            ev -> {
                ev.find(BootstrapMethArg.class).ifPresent(
                    a -> ev.find(MInvokeDynamicInsn.class).ifPresent(
                        inv -> inv.getBootstrapMethodArguments().add(a)));
            });


        derser.registry("MJumpInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MJumpInsn();
                ev.contribute( obj );
                ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
                ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));
                ev.attribute("label").tryString().ifPresent(obj::setLabel);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MLabel", ev -> {
            if( ev.isStartElement() ){
                var obj = new MLabel();
                ev.contribute( obj );
                ev.attribute("name").tryString().ifPresent(obj::setName);
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });
        derser.registry("MLdcInsn", ev -> {
            if( ev.isStartElement() ){
                var obj = new MLdcInsn();
                ev.contribute( obj );
                ev.attribute("type").tryString().ifPresent(t -> {
                    obj.setLdcType(LdcType.valueOf(t));
                });
                ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
            }
        });

        derser.registry(
            exit(
                tail(
                    tag("MLdcInsn"),
                    tag("value"),
                    tag("BootstrapMethArg"),
                    anyTag()
                )
            )
            , ev -> {
                ev.find(BootstrapMethArg.class).ifPresent(a->{
                    ev.find(MLdcInsn.class).ifPresent(ldc -> {
                        ldc.setValue(a);
                    });
                });
            }
        );

        derser.registry(
            exit(
                tail(
                    tag("MLdcInsn"),
                    tag("value")
                )
            )
            ,ev -> {
                ev.find(MLdcInsn.class).ifPresent( ldc -> {
                    var enc = ev.attribute("encode").tryString();
                    if( enc.isPresent() ){
                        if( enc.get().equals("hex") ){
                            ldc.setValue(HexCodec.deserialize(ev.text(false)));
                        }else{
                            throw new Error("undefined encode "+enc.get()+(ev.element().map(e -> " at "+e.getLocation().toString())).orElse("") );
                        }
                    }else{
                        var cls = ev.attribute("class").tryString();
                        if( cls.isPresent() ){
                            switch( cls.get() ){
                                case "java.lang.String":
                                    ldc.setValue(ev.text());
                                    break;
                                case "java.lang.Boolean":
                                case "boolean":
                                    ldc.setValue(Boolean.parseBoolean(ev.text()));
                                    break;
                                case "java.lang.Byte":
                                case "byte":
                                    ldc.setValue(Byte.parseByte(ev.text()));
                                    break;
                                case "java.lang.Short":
                                case "short":
                                    ldc.setValue(Short.valueOf(ev.text()));
                                    break;
                                case "java.lang.Integer":
                                case "int":
                                    ldc.setValue(Integer.valueOf(ev.text()));
                                    break;
                                case "java.lang.Long":
                                case "long":
                                    ldc.setValue(Long.valueOf(ev.text()));
                                    break;
                                case "java.lang.Float":
                                case "float":
                                    ldc.setValue(Float.valueOf(ev.text()));
                                    break;
                                case "java.lang.Double":
                                case "double":
                                    ldc.setValue(Double.valueOf(ev.text()));
                                    break;
                                case "java.lang.Character":
                                case "char":
                                    var str = ev.text();
                                    if( str.length()>0 ){
                                        ldc.setValue(str.charAt(0));
                                    }else{
                                        System.err.println("expected char"+
                                            (ev.element().map(e -> " at "+e.getLocation().toString())).orElse(""));
                                    }
                                    break;
                                default:
                                    throw new Error("undefined class "+cls.get()+(ev.element().map(e -> " at "+e.getLocation().toString())).orElse("") );
                            }
                        } else {
                            System.err.println("class not set"+
                                (ev.element().map(e -> " at "+e.getLocation().toString())).orElse("")
                            );
                            ldc.setValue(ev.text());
                        }
                    }
                });
            });

        derser.registry(enter(tag("MLineNumber")), ev -> {
            var obj = new MLineNumber();
            ev.contribute( obj );
            ev.attribute("label").tryString().ifPresent(obj::setLabel);
            ev.attribute("line").tryInteger().ifPresent(obj::setLine);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MLocalVariable")), ev -> {
            var obj = new MLocalVariable();
            ev.contribute( obj );
            ev.attribute("name").tryString().ifPresent(obj::setName);
            ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
            ev.attribute("signature").tryString().ifPresent(obj::setSignature);
            ev.attribute("labelStart").tryString().ifPresent(obj::setLabelStart);
            ev.attribute("labelEnd").tryString().ifPresent(obj::setLabelEnd);
            ev.attribute("index").tryInteger().ifPresent(obj::setIndex);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MLocalVariableAnnotation")), ev -> {
            var obj = new MLocalVariableAnnotation();
            ev.contribute(obj);
            ev.attribute("typeRef").tryInteger().ifPresent(obj::setTypeRef);
            ev.attribute("typePath").tryString().ifPresent(obj::setTypePath);
            ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
            ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(exit(tail(
            tag("MLocalVariableAnnotation"),tag("start")
        )), ev -> {
            ev.find(MLocalVariableAnnotation.class).ifPresent( a -> {
                var arr = a.getStartLabels();
                arr = arr!=null ? arr : new String[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = ev.text();
                a.setStartLabels(arr);
            });
        });

        derser.registry(exit(tail(
            tag("MLocalVariableAnnotation"),tag("end")
        )), ev -> {
            ev.find(MLocalVariableAnnotation.class).ifPresent( a -> {
                var arr = a.getEndLabels();
                arr = arr!=null ? arr : new String[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = ev.text();
                a.setEndLabels(arr);
            });
        });

        derser.registry(exit(tail(
            tag("MLocalVariableAnnotation"),tag("index")
        )), ev -> {
            ev.find(MLocalVariableAnnotation.class).ifPresent( a -> {
                var arr = a.getIndex();
                arr = arr!=null ? arr : new int[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = Integer.parseInt(ev.text());
                a.setIndex(arr);
            });
        });

        derser.registry(enter(tag("MLookupSwitchInsn")), ev -> {
            var obj = new MLookupSwitchInsn();
            ev.contribute(obj);
            ev.attribute("defaultHandlerLabel").tryString().ifPresent(obj::setDefaultHandlerLabel);
            ev.attribute("defLabel").tryString().ifPresent(obj::setDefaultHandlerLabel);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });
        derser.registry(exit(tail(
            tag("MLookupSwitchInsn"), tag("key")
        )), ev -> {
            var k = Integer.parseInt(ev.text());
            ev.find(MLookupSwitchInsn.class).ifPresent( lsw -> {
                var arr = lsw.getKeys();
                arr = arr!=null ? arr : new int[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = k;
                lsw.setKeys(arr);
            });
        });
        derser.registry(enter(tail(
            tag("MLookupSwitchInsn"), and(tag("label"),attribute("isnull").eq(true))
        )), ev -> {
            ev.find(MLookupSwitchInsn.class).ifPresent( lsw -> {
                var arr = lsw.getLabels();
                arr = arr!=null ? arr : new String[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = null;
                lsw.setLabels(arr);
            });
        });
        derser.registry(exit(tail(
            tag("MLookupSwitchInsn"), and(tag("label"),attribute("isnull").eq(false))
        )), ev -> {
            var k = ev.text();
            ev.find(MLookupSwitchInsn.class).ifPresent( lsw -> {
                var arr = lsw.getLabels();
                arr = arr!=null ? arr : new String[0];
                arr = Arrays.copyOf(arr,arr.length+1);
                arr[arr.length-1] = k;
                lsw.setLabels(arr);
            });
        });

        derser.registry(enter(tag("MMaxs")), ev -> {
            var obj = new MMaxs();
            ev.contribute(obj);
            ev.attribute("maxLocals").tryInteger().ifPresent(obj::setMaxLocals);
            ev.attribute("maxStack").tryInteger().ifPresent(obj::setMaxStack);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MMethodInsn")), ev -> {
            var obj = new MMethodInsn();
            ev.contribute(obj);

            ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
            ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));

            ev.attribute("owner").tryString().ifPresent(obj::setOwner);
            ev.attribute("name").tryString().ifPresent(obj::setName);
            ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
            ev.attribute("iface").tryBoolean().ifPresent(obj::setIface);

            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MMultiANewArrayInsn")), ev -> {
            var obj = new MMultiANewArrayInsn();
            ev.contribute(obj);

            ev.attribute("numDimensions").tryInteger().ifPresent(obj::setNumDimensions);
            ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);

            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MParameter")), ev -> {
            var obj = new MParameter();
            ev.contribute(obj);

            ev.attribute("name").tryString().ifPresent(obj::setName);
            ev.attribute("access").tryInteger().ifPresent(obj::setAccess);
            ev.attribute("final").tryBoolean().ifPresent(obj::setFinal);
            ev.attribute("mandated").tryBoolean().ifPresent(obj::setMandated);
            ev.attribute("synthetic").tryBoolean().ifPresent(obj::setSynthetic);

            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MParameterAnnotation")), ev -> {
            var obj = new MParameterAnnotation();
            ev.contribute(obj);

            ev.attribute("parameter").tryInteger().ifPresent(obj::setParameter);
            ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
            ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);

            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(enter(tag("MTableSwitchInsn")), ev -> {
            var obj = new MTableSwitchInsn();
            ev.contribute(obj);

            ev.attribute("max").tryInteger().ifPresent(obj::setMax);
            ev.attribute("min").tryInteger().ifPresent(obj::setMin);
            ev.attribute("defaultLabel").tryString().ifPresent(obj::setDefaultLabel);
            ev.attribute("defLabel").tryString().ifPresent(obj::setDefaultLabel);

            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));
        });

        derser.registry(exit(tail(tag("MTableSwitchInsn"),tag("label"))),ev -> {
            ev.find(MTableSwitchInsn.class).ifPresent( ts -> {
                var isNull = ev.attribute("isnull").tryBoolean();
                var value = ( isNull.isEmpty() || !isNull.get() ) ?
                    ev.text() : null;
                var lbls = ts.getLabels();
                lbls = lbls!=null ? lbls : new String[0];
                lbls = Arrays.copyOf(lbls,lbls.length+1);
                lbls[lbls.length-1] = value;
            });
        });

        derser.registry(enter(tag("MTryCatchAnnotation")), ev -> {
            var obj = new MTryCatchAnnotation();
            ev.contribute(obj);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));

            ev.attribute("typeRef").tryInteger().ifPresent( obj::setTypeRef );
            ev.attribute("typePath").tryString().ifPresent( obj::setTypePath );
            ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
            ev.attribute("visible").tryBoolean().ifPresent( obj::setVisible );
        });

        derser.registry(enter(tag("MTryCatchBlock")), ev -> {
            var obj = new MTryCatchBlock();
            ev.contribute(obj);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));

            ev.attr().regex("(?is)(label)?Start").tryString().ifPresent(obj::setLabelStart);
            ev.attr().regex("(?is)(label)?End").tryString().ifPresent(obj::setLabelEnd);
            ev.attr().regex("(?is)(label)?Ha?ndle?r?").tryString().ifPresent(obj::setLabelHandler);
            ev.attribute("type").tryString().ifPresent(obj::setType);
        });

        derser.registry(enter(tag("MTypeAnnotation")), ev -> {
            var obj = new MTypeAnnotation();
            ev.contribute(obj);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));

            ev.attr().regex("(?is)typeRef").tryInteger().ifPresent(obj::setTypeRef);
            ev.attr().regex("(?is)typePath").tryString().ifPresent(obj::setTypePath);
            ev.attr().regex("(?is)descriptor").tryString().ifPresent(obj::setDescriptor);
            ev.attribute("visible").tryBoolean().ifPresent(obj::setVisible);
        });

        derser.registry(enter(tag("MTypeInsn")), ev -> {
            var obj = new MTypeInsn();
            ev.contribute(obj);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));

            ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
            ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));

            ev.attribute("type").tryString().ifPresent(obj::setType);
        });

        derser.registry(enter(tag("MVarInsn")), ev -> {
            var obj = new MVarInsn();
            ev.contribute(obj);
            ev.find(CMethod.class).ifPresent(cMethod -> cMethod.getMethodByteCodes().add(obj));

            ev.attribute("opcode").tryInteger().ifPresent(obj::setOpcode);
            ev.attribute("opcodeName").tryString().ifPresent( opName ->obj.setOpcode(OpCode.valueOf(opName).code));

            ev.attribute("variable").tryInteger().ifPresent(obj::setVariable);
        });
    }
}
