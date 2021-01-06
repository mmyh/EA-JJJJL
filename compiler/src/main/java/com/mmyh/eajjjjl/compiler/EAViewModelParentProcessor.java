package com.mmyh.eajjjjl.compiler;

import com.google.auto.service.AutoService;
import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.annotation.EASuper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class EAViewModelParentProcessor extends EABaseProcessor {

    private Map<String, List<ApiInfo>> mApiMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(EAApi.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        printNote("START");
        if (!roundEnv.processingOver()) {
            Set<? extends Element> datas =
                    roundEnv.getElementsAnnotatedWith(EAApi.class);
            for (Element each : datas) {
                VariableElement data = (VariableElement) each;
                TypeElement viewModel = (TypeElement) data.getEnclosingElement();
                List<ApiInfo> apiList = mApiMap.get(viewModel.getQualifiedName().toString());
                if (apiList == null) {
                    apiList = new ArrayList<>();
                    mApiMap.put(viewModel.getQualifiedName().toString(), apiList);
                }
                EAApi eaApi = data.getAnnotation(EAApi.class);
                ApiInfo apiInfo = new ApiInfo();
                apiInfo.showErrorToast = eaApi.showErrorToast();
                apiInfo.showSuccessToast = eaApi.showSuccessToast();
                apiInfo.controlLoadingDialog = eaApi.controlLoadingDialog();
                String[] apitmp = eaApi.api().split(",");
                apiInfo.apiClass = apitmp[0];
                apiInfo.apiMethod = apitmp[1];
                TypeElement apiElement = elementUtils.getTypeElement(apitmp[0]);
                for (Element element : apiElement.getEnclosedElements()) {
                    if (element instanceof ExecutableElement && element.getSimpleName().toString().equals(apiInfo.apiMethod)) {
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
                    apiInfo.callBack = typeMirror.toString();
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

            Set<String> viewModelNames = mApiMap.keySet();
            Iterator<String> iterator = viewModelNames.iterator();
            while (iterator.hasNext()) {
                String viewModelName = iterator.next();
                TypeElement typeElement = elementUtils.getTypeElement(viewModelName);
                TypeSpec.Builder tsBuilder = TypeSpec.classBuilder(viewModelName.substring(viewModelName.lastIndexOf(".") + 1) + "Parent1")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                if (typeElement != null && typeElement.getAnnotation(EASuper.class) != null) {
                    try {
                        typeElement.getAnnotation(EASuper.class).superClass();
                    } catch (MirroredTypeException e) {
                        if (!e.getTypeMirror().toString().equals(Object.class.getCanonicalName())) {
                            tsBuilder.superclass(TypeName.get(e.getTypeMirror()));
                        }
                    }
                }
                List<ApiInfo> apiList = mApiMap.get(viewModelName);
                for (ApiInfo apiInfo : apiList) {
                    MethodSpec.Builder apiMethod = MethodSpec.methodBuilder(apiInfo.apiMethod)
                            .returns(void.class)
                            .addModifiers(Modifier.PUBLIC);
                    String apiFinishMethodName = apiInfo.apiMethod + "Finish";
                    apiMethod.addParameter(getCN(class_LifecycleOwner), str_owner);
                    apiMethod.addParameter(getCN(apiInfo.requestClassName), str_request);
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
                    apiMethod.addStatement("$T<$T> $N = $T.bind($N, $T.getApi($T.class).$N($N.toJson(), $N.getHeaderMap()))", getCN(class_RECall), apiInfo.returnType, str_reCall, getCN(class_RECall), str_owner, getCN(class_RetrofitService), getCN(apiInfo.apiClass), apiInfo.apiMethod, str_request, str_request);
                    if (apiInfo.controlLoadingDialog) {
                        apiMethod.addCode("if(!showLoadingDialog){\n");
                        apiMethod.addStatement("reCall.noDialog()");
                        apiMethod.addCode("}\n");
                    }
                    apiMethod.addCode("$N.enqueue(new $T<$T>() {\n", str_reCall, getCN(apiInfo.callBack), apiInfo.returnType);
                    apiMethod.addCode("@Override\n");
                    apiMethod.addCode("protected void onFinish($T $N, $T.ErrorType $N) {\n", apiInfo.returnType, str_response, getCN(class_RetrofitError), str_err);
                    apiMethod.addCode("if(err!=null){\n");
                    apiMethod.addStatement("$N.setValue($N)", apiInfo.apiMethod + str_ErrorType, str_err);
                    apiMethod.addCode("}\n");
                    apiMethod.addCode("else{\n");
                    apiMethod.addStatement("$N.setValue($N)", apiInfo.apiMethod + str_Response, str_response);
                    apiMethod.addCode("}\n");
                    apiMethod.addStatement("$N($N, $N" + param.toString() + ")", apiFinishMethodName, str_response, str_err);
                    apiMethod.addCode("}\n");
                    apiMethod.addStatement("})");
                    tsBuilder.addMethod(apiMethod.build());
                    MethodSpec.Builder apiFinishMethod = MethodSpec.methodBuilder(apiFinishMethodName)
                            .returns(void.class)
                            .addModifiers(Modifier.PUBLIC);
                    apiFinishMethod.addParameter(apiInfo.returnType, str_data);
                    apiFinishMethod.addParameter(getCN(class_RetrofitError_ErrorType), str_err);
                    int j = 1;
                    for (String paramClassName : apiInfo.paramsClassName) {
                        apiFinishMethod.addParameter(getCN(paramClassName), "p" + j);
                        j++;
                    }
                    tsBuilder.addMethod(apiFinishMethod.build());
                    ParameterizedTypeName responseTN = ParameterizedTypeName.get(getCN(class_MutableLiveData), apiInfo.returnType);
                    FieldSpec.Builder res = FieldSpec.builder(responseTN, apiInfo.apiMethod + str_Response, Modifier.PUBLIC)
                            .initializer("new $T<>()", getCN(class_MutableLiveData));
                    tsBuilder.addField(res.build());
                    ParameterizedTypeName errorTypeTN = ParameterizedTypeName.get(getCN(class_MutableLiveData), getCN(class_RetrofitError_ErrorType));
                    FieldSpec.Builder errorType = FieldSpec.builder(errorTypeTN, apiInfo.apiMethod + str_ErrorType, Modifier.PUBLIC)
                            .initializer("new $T<>()", getCN(class_MutableLiveData));
                    tsBuilder.addField(errorType.build());
                }
                String packageFullName = elementUtils.getPackageOf(elementUtils.getTypeElement(viewModelName)).getQualifiedName().toString();
                JavaFile javaFile = JavaFile.builder(packageFullName, tsBuilder.build())
                        .build();
                // 生成class文件
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mApiMap.clear();
        } else {
            printNote("STOP");
        }
        return false;
    }

    class ApiInfo {

        public String apiClass;

        public String apiMethod;

        public boolean showErrorToast;

        public String requestClassName;

        public boolean showSuccessToast;

        public boolean controlLoadingDialog;

        public TypeName returnType;

        public List<String> paramsClassName = new ArrayList<>();

        public String callBack;
    }

}
