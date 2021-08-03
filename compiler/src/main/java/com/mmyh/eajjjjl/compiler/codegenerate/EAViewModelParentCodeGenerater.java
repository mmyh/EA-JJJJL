package com.mmyh.eajjjjl.compiler.codegenerate;

import com.mmyh.eajjjjl.annotation.EAViewModel;
import com.mmyh.eajjjjl.compiler.EAConstant;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAApiInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

public class EAViewModelParentCodeGenerater extends EABaseCodeGenerater {
    public EAViewModelParentCodeGenerater(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void generate(String viewModelName, Map<String, List<EAApiInfo>> apiMap) {
        TypeElement typeElement = eaUtil.elementUtils.getTypeElement(viewModelName);
        if (typeElement == null) {
            return;
        }
        String packageFullName = eaUtil.elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        String viewModelParentName = viewModelName.substring(viewModelName.lastIndexOf(".") + 1) + "Parent";
        TypeSpec.Builder tsBuilder = TypeSpec.classBuilder(viewModelParentName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        if (typeElement.getAnnotation(EAViewModel.class) != null) {
            try {
                typeElement.getAnnotation(EAViewModel.class).superClass();
            } catch (MirroredTypeException e) {
                if (!e.getTypeMirror().toString().equals(Object.class.getCanonicalName())) {
                    tsBuilder.superclass(TypeName.get(e.getTypeMirror()));
                }
            }
        }
        List<EAApiInfo> apiList = apiMap.get(viewModelName);
        for (EAApiInfo apiInfo : apiList) {
            String apiDataClassName = createApiDataClass(tsBuilder, apiInfo);
            ClassName apiDataClass = getCN(packageFullName + "." + viewModelParentName + "." + apiDataClassName);
            MethodSpec.Builder apiMethod = MethodSpec.methodBuilder(apiInfo.apiMethod)
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC);
            String apiFinishMethodName = apiInfo.apiMethod + "Finish";
            apiMethod.addParameter(getCN(EAConstant.c_LifecycleOwner), EAConstant.str_owner);
            apiMethod.addParameter(getCN(apiInfo.requestClassName), EAConstant.str_request);
            StringBuilder param = new StringBuilder();
            int m = 1;
            for (String paramClassName : apiInfo.paramsClassName) {
                apiMethod.addParameter(getCN(paramClassName), "p" + m, Modifier.FINAL);
                param.append(",").append("p").append(m);
                m++;
            }
            if (apiInfo.controlLoadingDialog) {
                apiMethod.addParameter(boolean.class, "showLoadingDialog");
            }
            apiMethod.addStatement("$T<$T> $N = $T.bind($N, $T.getApi($T.class).$N($N.toJson(), $N.getHeaderMap()))", getCN(EAConstant.c_EACall), apiInfo.returnType, EAConstant.str_eaCall, getCN(EAConstant.c_EACall), EAConstant.str_owner, getCN(EAConstant.c_EARetrofitService), getCN(apiInfo.apiClass), apiInfo.apiMethod, EAConstant.str_request, EAConstant.str_request);
            if (apiInfo.controlLoadingDialog) {
                apiMethod.addCode("if(!showLoadingDialog){\n");
                apiMethod.addStatement("$N.noDialog()", EAConstant.str_eaCall);
                apiMethod.addCode("}\n");
            }
            apiMethod.addCode("$N.enqueue(new $T<$T>() {\n", EAConstant.str_eaCall, getCN(apiInfo.callBack), apiInfo.returnType);
            apiMethod.addCode("@Override\n");
            apiMethod.addCode("protected void onFinish($T $N, $T $N) {\n", apiInfo.returnType, EAConstant.str_response, getCN(EAConstant.c_Throwable), EAConstant.str_err);
            apiMethod.addStatement("$T $N = new $T()", apiDataClass, EAConstant.str_data, apiDataClass);
            apiMethod.addCode("if($N==null){\n", EAConstant.str_response);
            apiMethod.addStatement("$N = new $T()", EAConstant.str_response, apiInfo.returnType);
            apiMethod.addCode("}\n");
            apiMethod.addStatement("$N.$N = $N", EAConstant.str_data, EAConstant.str_response, EAConstant.str_response);
            apiMethod.addStatement("$N.$N = $N", EAConstant.str_data, EAConstant.str_err, EAConstant.str_err);
            if (apiInfo.paramsClassName.size() > 0) {
                for (int i = 1; i <= apiInfo.paramsClassName.size(); i++) {
                    apiMethod.addStatement("$N.$N = $N", EAConstant.str_data, "p" + i, "p" + i);
                }
            }
            apiMethod.addStatement("$N($N)", apiFinishMethodName, EAConstant.str_data);
            apiMethod.addCode("}\n");
            apiMethod.addStatement("})");
            tsBuilder.addMethod(apiMethod.build());
            MethodSpec.Builder apiFinishMethod = MethodSpec.methodBuilder(apiFinishMethodName)
                    .returns(void.class)
                    .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT);
            apiFinishMethod.addParameter(apiDataClass, EAConstant.str_data);
            tsBuilder.addMethod(apiFinishMethod.build());
//            ParameterizedTypeName responseTN = ParameterizedTypeName.get(getCN(class_MutableLiveData), apiInfo.returnType);
//            FieldSpec.Builder res = FieldSpec.builder(responseTN, apiInfo.apiMethod + str_Response, Modifier.PUBLIC)
//                    .initializer("new $T<>()", getCN(class_MutableLiveData));
//            tsBuilder.addField(res.build());
//            ParameterizedTypeName errorTypeTN = ParameterizedTypeName.get(getCN(class_MutableLiveData), getCN(class_RetrofitError_ErrorType));
//            FieldSpec.Builder errorType = FieldSpec.builder(errorTypeTN, apiInfo.apiMethod + str_ErrorType, Modifier.PUBLIC)
//                    .initializer("new $T<>()", getCN(class_MutableLiveData));
//            tsBuilder.addField(errorType.build());
        }

        JavaFile javaFile = JavaFile.builder(packageFullName, tsBuilder.build())
                .build();
        // 生成class文件
        try {
            javaFile.writeTo(eaUtil.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createApiDataClass(TypeSpec.Builder tsBuilder, EAApiInfo apiInfo) {
        String name = eaUtil.firstToUpperCase(apiInfo.apiMethod) + "Data";
        TypeSpec.Builder tb = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        tb.addField(Throwable.class, EAConstant.str_err, Modifier.PUBLIC);
        tb.addField(apiInfo.returnType, EAConstant.str_response, Modifier.PUBLIC);
        int i = 1;
        for (String str : apiInfo.paramsClassName) {
            tb.addField(getCN(str), "p" + i, Modifier.PUBLIC);
            i++;
        }
        tsBuilder.addType(tb.build());
        return name;
    }
}
