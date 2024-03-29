package com.mmyh.eajjjjl.compiler.annotationhandler;

import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.compiler.EAConstant;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAApiInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

public class EAViewModelParentAnnotationHandler extends EABaseAnnotationHandler {
    public EAViewModelParentAnnotationHandler(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void handle(Element ele, Map<String, List<EAApiInfo>> apiMap) {
        VariableElement data = (VariableElement) ele;
        TypeElement viewModel = (TypeElement) data.getEnclosingElement();
        List<EAApiInfo> apiList = apiMap.get(viewModel.getQualifiedName().toString());
        if (apiList == null) {
            apiList = new ArrayList<>();
            apiMap.put(viewModel.getQualifiedName().toString(), apiList);
        }
        EAApi eaApi = data.getAnnotation(EAApi.class);
        EAApiInfo apiInfo = new EAApiInfo();
        apiInfo.controlLoadingDialog = eaApi.controlLoadingDialog();
        try {
            eaApi.apiClass();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            if (!eaUtil.equals(typeMirror.toString(), Object.class.getCanonicalName())) {
                apiInfo.apiClass = typeMirror.toString();
            }
        }
        apiInfo.apiMethod = eaApi.apiMethod();
        if (eaUtil.isEmptyStr(apiInfo.apiClass) || eaUtil.isEmptyStr(apiInfo.apiMethod)) {
            String[] apitmp = eaApi.api().split(",");
            apiInfo.apiClass = apitmp[0];
            apiInfo.apiMethod = apitmp[1];
        }
        boolean findApi = false;
        TypeElement apiElement = eaUtil.elementUtils.getTypeElement(apiInfo.apiClass);
        for (Element element : apiElement.getEnclosedElements()) {
            if (element instanceof ExecutableElement && element.getSimpleName().toString().equals(apiInfo.apiMethod)) {
                findApi = true;
                TypeMirror returnType = ((ExecutableElement) element).getReturnType();
                if (returnType.toString().contains("retrofit2.Call")) {
                    ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) ClassName.get(returnType);
                    if (parameterizedTypeName.typeArguments != null && parameterizedTypeName.typeArguments.size() > 0) {
                        apiInfo.returnType = parameterizedTypeName.typeArguments.get(0);
                    }
                }
                break;
            }
        }
        if (!findApi) {
            eaUtil.printError("没有找到(" + apiInfo.apiClass + ":" + apiInfo.apiMethod + ")");
        }
        try {
            eaApi.request();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            apiInfo.requestClassName = typeMirror.toString();
        }
        try {
            eaApi.callBack();
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            if (Object.class.getCanonicalName().equals(typeMirror.toString())) {
                apiInfo.callBack = EAConstant.c_EACallback;
            } else {
                apiInfo.callBack = typeMirror.toString();
            }
        }
        try {
            eaApi.params();
        } catch (MirroredTypesException e) {
            List<? extends TypeMirror> list = e.getTypeMirrors();
            for (TypeMirror typeMirror : list) {
                apiInfo.paramsClassName.add(typeMirror.toString());
            }
        }
        apiList.add(apiInfo);
    }
}
