package com.mmyh.eajjjjl.compiler.codegenerate;

import com.mmyh.eajjjjl.annotation.EAServiceImpl;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAServiceInfo;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;

public class EAServiceCodeGenerater extends EABaseCodeGenerater {
    public EAServiceCodeGenerater(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void generate(EAServiceInfo eaServiceInfo) {
        TypeSpec.Builder tsBuilder = TypeSpec.interfaceBuilder(eaServiceInfo.simpleServiceName.substring(0, eaServiceInfo.simpleServiceName.lastIndexOf("Impl")))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(EAServiceImpl.class)
                        .addMember("name", "\"$N\"", eaServiceInfo.serviceName)
                        .build());
        for (EAServiceInfo.MethodInfo methodInfo : eaServiceInfo.methods) {
            MethodSpec.Builder mb = MethodSpec.methodBuilder(methodInfo.name)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
            Class baseReturn = getBaseType(methodInfo.returnType);
            if (baseReturn != null) {
                mb.returns(baseReturn);
            } else {
                mb.returns(getCN(methodInfo.returnType));
            }
            methodInfo.params.forEach((type, name) -> {
                Class baseParam = getBaseType(type);
                if (baseParam != null) {
                    mb.addParameter(baseParam, name);
                } else {
                    mb.addParameter(getCN(type), name);
                }
            });
            tsBuilder.addMethod(mb.build());
        }
        JavaFile javaFile = JavaFile.builder(eaServiceInfo.packageName, tsBuilder.build())
                .build();
        // 生成class文件
        try {
            javaFile.writeTo(eaUtil.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class getBaseType(String s) {
        if ("byte".equals(s)) {
            return byte.class;
        } else if ("short".equals(s)) {
            return short.class;
        } else if ("int".equals(s)) {
            return int.class;
        } else if ("long".equals(s)) {
            return long.class;
        } else if ("float".equals(s)) {
            return float.class;
        } else if ("double".equals(s)) {
            return double.class;
        } else if ("char".equals(s)) {
            return char.class;
        } else if ("boolean".equals(s)) {
            return boolean.class;
        }
        return null;
    }

}
