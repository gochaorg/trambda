package xyz.cofe.bc.xml.restore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import xyz.cofe.bc.xml.BCDeserializer;
import xyz.cofe.bc.xml.DeserializeRegistry;
import xyz.cofe.bc.xml.HexCodec;
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

public class ClassTags implements DeserializeRegistry {
    @Override
    public void registry(BCDeserializer derser){
        if( derser!=null ){
            derser.registry("CBegin", ev -> {
                if( ev.isStartElement() ){
                    var obj = new CBegin();
                    ev.attribute("version").tryInteger().ifPresent(obj::setVersion);
                    ev.attribute("access").tryInteger().ifPresent(obj::setAccess);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.attribute("signature").tryString().ifPresent(obj::setSignature);
                    ev.attribute("superName").tryString().ifPresent(obj::setSuperName);
                    ev.attribute("interface").tryBoolean().ifPresent(obj::setInterface);
                    ev.contribute( obj );
                }
            });
            derser.registry("interface", ev -> {
                if( ev.isEndElement() ){
                    ev.find(CBegin.class).ifPresent( cb -> {
                        var itfs = cb.getInterfaces();
                        if( itfs==null )itfs = new String[0];
                        itfs = Arrays.copyOf(itfs,itfs.length+1);
                        itfs[itfs.length-1] = ev.text();
                        cb.setInterfaces(itfs);
                    });
                }
            });
            derser.registry("CSource", ev -> {
                if( ev.isStartElement() ){
                    var obj = new CSource();
                    ev.contribute(obj);
                    ev.attribute("source").tryString().ifPresent( obj::setSource );
                    ev.attribute("debug").tryString().ifPresent( obj::setDebug );
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        cBegin.setSource(obj);
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("COuterClass", ev -> {
                if( ev.isStartElement() ){
                    var obj = new COuterClass();
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent( obj::setName );
                    ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
                    ev.attribute("owner").tryString().ifPresent( obj::setOwner );
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        cBegin.setOuterClass(obj);
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CNestHost", ev -> {
                if( ev.isStartElement() ){
                    var obj = new CNestHost();
                    ev.contribute(obj);
                    ev.attribute("nestHost").tryString().ifPresent( obj::setNestHost );
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        cBegin.setNestHost(obj);
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CPermittedSubclass", ev -> {
                if( ev.isStartElement() ){
                    var obj = new CPermittedSubclass();
                    ev.contribute(obj);
                    ev.attribute("permittedSubclass").tryString().ifPresent( obj::setPermittedSubclass );
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        cBegin.setPermittedSubclass(obj);
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CAnnotation", ev -> {
                if( ev.isStartElement() ){
                    CAnnotation obj = new CAnnotation();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var ann = cBegin.getAnnotations();
                        if( ann==null ){
                            ann = new ArrayList<>();
                            cBegin.setAnnotations(ann);
                        }
                        ann.add(obj);
                        ev.attribute("visible").tryBoolean().ifPresent( obj::setVisible );
                        ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CTypeAnnotation", ev -> {
                if( ev.isStartElement() ){
                    CTypeAnnotation obj = new CTypeAnnotation();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var ann = cBegin.getTypeAnnotations();
                        if( ann==null ){
                            ann = new ArrayList<>();
                            cBegin.setTypeAnnotations(ann);
                        }
                        ann.add(obj);
                        ev.attribute("typeRef").tryInteger().ifPresent( obj::setTypeRef );
                        ev.attribute("typePath").tryString().ifPresent( obj::setTypePath );
                        ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
                        ev.attribute("visible").tryBoolean().ifPresent( obj::setVisible );
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CNestMember", ev -> {
                if( ev.isStartElement() ){
                    CNestMember obj = new CNestMember();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var ann = cBegin.getNestMembers();
                        if( ann==null ){
                            ann = new ArrayList<>();
                            cBegin.setNestMembers(ann);
                        }
                        ann.add(obj);
                        ev.attribute("nestMember").tryString().ifPresent( obj::setNestMember );
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CInnerClass", ev -> {
                if( ev.isStartElement() ){
                    CInnerClass obj = new CInnerClass();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var ann = cBegin.getInnerClasses();
                        if( ann==null ){
                            ann = new ArrayList<>();
                            cBegin.setInnerClasses(ann);
                        }
                        ann.add(obj);
                        ev.attribute("name").tryString().ifPresent( obj::setName );
                        ev.attribute("outerName").tryString().ifPresent( obj::setOuterName );
                        ev.attribute("innerName").tryString().ifPresent( obj::setInnerName );
                        ev.attribute("access").tryInteger().ifPresent( obj::setAccess );
                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("CField", ev -> {
                if( ev.isStartElement() ){
                    CField obj = new CField();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var ann = cBegin.getFields();
                        if( ann==null ){
                            ann = new ArrayList<>();
                            cBegin.setFields(ann);
                        }
                        ann.add(obj);
                        ev.attribute("name").tryString().ifPresent( obj::setName );
                        ev.attribute("access").tryInteger().ifPresent( obj::setAccess );
                        ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
                        ev.attribute("signature").tryString().ifPresent( obj::setSignature );

                        ev.attribute("deprecated").tryBoolean().ifPresent( obj::setDeprecated );
                        ev.attribute("final").tryBoolean().ifPresent( obj::setFinal );
                        ev.attribute("enum").tryBoolean().ifPresent( obj::setEnum );
                        ev.attribute("mandated").tryBoolean().ifPresent( obj::setMandated );
                        ev.attribute("private").tryBoolean().ifPresent( obj::setPrivate );
                        ev.attribute("protected").tryBoolean().ifPresent( obj::setProtected );
                        ev.attribute("public").tryBoolean().ifPresent( obj::setPublic );
                        ev.attribute("static").tryBoolean().ifPresent( obj::setStatic );
                        ev.attribute("synthetic").tryBoolean().ifPresent( obj::setSynthetic );
                        ev.attribute("transient").tryBoolean().ifPresent( obj::setTransient );
                        ev.attribute("volatile").tryBoolean().ifPresent( obj::setVolatile );

                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("fieldValue", ev -> {
                if( ev.isStartElement() ){
                    LinkedHashMap<String,String> m = new LinkedHashMap<>();
                    ev.attribute("class").tryString().ifPresent( s -> m.put("class",s));
                    ev.attribute("encode").tryString().ifPresent( s -> m.put("encode",s));
                    ev.contribute(m);
                }else if( ev.isEndElement() ){
                    ev.find(LinkedHashMap.class).ifPresent( m -> {
                        Object _cls = m.get("class");
                        Object _encode = m.get("encode");
                        String cls = _cls!=null ? _cls.toString() : null;
                        String enc = _encode!=null ? _encode.toString() : null;
                        if( enc!=null && enc.equalsIgnoreCase("primitive") && cls!=null ){
                            if( cls.equals(boolean.class.getName()) ){
                                var value = Boolean.parseBoolean(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(byte.class.getName()) ){
                                var value = Byte.parseByte(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(short.class.getName()) ){
                                var value = Short.parseShort(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(int.class.getName()) ){
                                var value = Integer.parseInt(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(Long.class.getName()) ){
                                var value = Long.parseLong(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(Float.class.getName()) ){
                                var value = Float.parseFloat(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(Double.class.getName()) ){
                                var value = Double.parseDouble(ev.text());
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }else if( cls.equals(Character.class.getName()) ){
                                var value1 = (ev.text());
                                var value = value1.length()>0 ? value1.charAt(0) : '?';
                                ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                            }
                        }else if( cls!=null && cls.equals(String.class.getName()) ){
                            var value = ev.text();
                            ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                        }else if( enc!=null && enc.equals("hex") ){
                            var value = HexCodec.deserialize(ev.text());
                            ev.find(CField.class).ifPresent(cField -> cField.setValue(value));
                        }
                    });
                }
            });
            derser.registry("CMethod", ev -> {
                if( ev.isStartElement() ){
                    CMethod obj = new CMethod();
                    ev.contribute(obj);
                    ev.find(CBegin.class).ifPresent( cBegin -> {
                        var cmList = cBegin.getMethods();
                        if( cmList==null ){
                            cmList = new ArrayList<>();
                            cBegin.setMethods(cmList);
                        }
                        cmList.add(obj);
                        ev.attribute("name").tryString().ifPresent( obj::setName );
                        ev.attribute("access").tryInteger().ifPresent( obj::setAccess );
                        ev.attribute("descriptor").tryString().ifPresent( obj::setDescriptor );
                        ev.attribute("signature").tryString().ifPresent( obj::setSignature );

                        ev.attribute("deprecated").tryBoolean().ifPresent( obj::setDeprecated );
                        ev.attribute("public").tryBoolean().ifPresent( obj::setPublic );
                        ev.attribute("protected").tryBoolean().ifPresent( obj::setProtected );
                        ev.attribute("private").tryBoolean().ifPresent( obj::setPrivate );
                        ev.attribute("final").tryBoolean().ifPresent( obj::setFinal );
                        ev.attribute("abstract").tryBoolean().ifPresent( obj::setAbstract );
                        ev.attribute("bridge").tryBoolean().ifPresent( obj::setBridge );
                        ev.attribute("native").tryBoolean().ifPresent( obj::setNative );
                        ev.attribute("strict").tryBoolean().ifPresent( obj::setStrict );
                        ev.attribute("synchronized").tryBoolean().ifPresent( obj::setSynchronized );
                        ev.attribute("mandated").tryBoolean().ifPresent( obj::setMandated );
                        ev.attribute("static").tryBoolean().ifPresent( obj::setStatic );
                        ev.attribute("synthetic").tryBoolean().ifPresent( obj::setSynthetic );
                        ev.attribute("varArgs").tryBoolean().ifPresent( obj::setVarArgs );

                        ev.attribute("order").tryInteger().ifPresent( ord -> cBegin.getOrder().put(obj,ord) );
                    });
                }
            });
            derser.registry("methodException", ev -> {
                if( ev.isEndElement() ){
                    ev.find(CMethod.class).ifPresent( cMethod -> {
                        var txt = ev.text();
                        var expt = cMethod.getExceptions();
                        if( expt==null ){
                            expt = new String[0];
                        }
                        expt = Arrays.copyOf(expt,expt.length+1);
                        expt[expt.length-1] = txt;
                        cMethod.setExceptions(expt);
                    });
                }
            });
        }
    }
}
