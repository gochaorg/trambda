package xyz.cofe.bc.xml.restore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;
import xyz.cofe.bc.xml.BCDeserializer;
import xyz.cofe.bc.xml.CharArrayCodec;
import xyz.cofe.bc.xml.DeserializeRegistry;
import xyz.cofe.bc.xml.HexCodec;
import xyz.cofe.trambda.bc.ann.AEnd;
import xyz.cofe.trambda.bc.ann.AEnum;
import xyz.cofe.trambda.bc.ann.APair;
import xyz.cofe.trambda.bc.ann.EmAArray;
import xyz.cofe.trambda.bc.ann.EmANameDesc;
import xyz.cofe.trambda.bc.ann.GetAnnotationByteCodes;

public class AnnotationTags implements DeserializeRegistry {
    @Override
    public void registry(BCDeserializer derser){
        if( derser != null ){
            derser.registry("AEnd", ev -> {
                if( ev.isStartElement() ){
                    var obj = new AEnd();
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });

            derser.registry("AEnum", ev -> {
                if( ev.isStartElement() ){
                    var obj = new AEnum();
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("AEnumValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(AEnum.class).ifPresent( a -> a.setValue(ev.text()));
                }
            });

            derser.registry("EmAArray", ev -> {
                if( ev.isStartElement() ){
                    var obj = new EmAArray();
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });

            derser.registry("EmANameDesc", ev -> {
                if( ev.isStartElement() ){
                    var obj = new EmANameDesc();
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.attribute("descriptor").tryString().ifPresent(obj::setDescriptor);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });

            derser.registry("APairString", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairString(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairStringValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairString.class).ifPresent( a -> a.setValue(ev.text()));
                }
            });

            derser.registry("APairByte", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairByte(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairByteValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairByte.class).ifPresent( a -> a.setValue(
                        Byte.parseByte(ev.text())
                    ));
                }
            });

            derser.registry("APairBoolean", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairBoolean(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairBooleanValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairBoolean.class).ifPresent( a -> a.setValue(
                        Boolean.parseBoolean(ev.text())
                    ));
                }
            });

            derser.registry("APairCharacter", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairCharacter(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairCharacterValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairCharacter.class).ifPresent( a -> {
                        var txt = ev.text();
                        if( txt.length()>0 )a.setValue(txt.charAt(0));
                    });
                }
            });

            derser.registry("APairShort", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairShort(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairShortValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairShort.class).ifPresent( a -> a.setValue(
                        Short.parseShort(ev.text())
                    ));
                }
            });

            derser.registry("APairInteger", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairInteger(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairIntegerValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairInteger.class).ifPresent( a -> a.setValue(
                        Integer.valueOf(ev.text())
                    ));
                }
            });

            derser.registry("APairLong", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairLong(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairLongValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairLong.class).ifPresent( a -> a.setValue(
                        Long.valueOf(ev.text())
                    ));
                }
            });

            derser.registry("APairFloat", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairFloat(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairFloatValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairFloat.class).ifPresent( a -> a.setValue(
                        Float.valueOf(ev.text())
                    ));
                }
            });

            derser.registry("APairDouble", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairDouble(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairDoubleValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairDouble.class).ifPresent( a -> a.setValue(
                        Double.valueOf(ev.text())
                    ));
                }
            });

            derser.registry("APairBooleanArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairBooleanArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairBooleanArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairBooleanArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Boolean::parseBoolean)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new boolean[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairCharArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairCharArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairCharArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairCharArr1D.class).ifPresent( a -> {
                        var arr = CharArrayCodec.decode(ev.text());
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairByteArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairByteArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairByteArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairByteArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Byte::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new byte[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairShortArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairShortArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairShortArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairShortArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Short::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new short[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairIntArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairIntArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairIntArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairIntArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Integer::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new int[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairFloatArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairFloatArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairFloatArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairFloatArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Float::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new float[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairLongArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairLongArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairLongArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairLongArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Long::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new long[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairDoubleArr1D", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairDoubleArr1D(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairDoubleArr1DValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairDoubleArr1D.class).ifPresent( a -> {
                        var lst = Arrays.stream(ev.text().split(",")).map(Double::valueOf)
                            .collect(Collectors.toUnmodifiableList());
                        var arr = new double[lst.size()];
                        for( int i=0; i<lst.size(); i++ ){
                            arr[i] = lst.get(i);
                        }
                        a.setValue(arr);
                    });
                }
            });

            derser.registry("APairSerializable", ev -> {
                if( ev.isStartElement() ){
                    var obj = new APair.APairSerializable(null,null);
                    ev.contribute(obj);
                    ev.attribute("name").tryString().ifPresent(obj::setName);
                    ev.find(GetAnnotationByteCodes.class).ifPresent( ann -> {
                        ann.getAnnotationByteCodes().add(obj);
                    });
                }
            });
            derser.registry("APairSerializableValue", ev -> {
                if( ev.isEndElement() ){
                    ev.find(APair.APairSerializable.class).ifPresent( a -> {
                        var hex = HexCodec.deserialize(ev.text());
                        if( hex instanceof Serializable ){
                            a.setValue((Serializable)hex);
                        }else{
                            if( hex!=null ){
                                System.err.println("APairSerializableValue value not Serializable:"+hex);
                            }
                        }
                    });
                }
            });
        }
    }
}
