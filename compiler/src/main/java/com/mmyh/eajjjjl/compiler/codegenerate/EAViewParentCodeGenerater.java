package com.mmyh.eajjjjl.compiler.codegenerate;

import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.annotation.EAParent;
import com.mmyh.eajjjjl.compiler.EAConstant;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAViewInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class EAViewParentCodeGenerater extends EABaseCodeGenerater {

    public EAViewParentCodeGenerater(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void generate(EAViewInfo eaViewInfo) {
        String viewName = eaViewInfo.viewName;
        TypeSpec.Builder tsBuilder = generateClass(eaViewInfo);
        boolean superClassIsView = false;
        if (eaViewInfo.superClass != null) {
            superClassIsView = checkSuperClassIsView(eaViewInfo.superClass);
        }
        addForDiffSuper(tsBuilder, eaViewInfo, superClassIsView);
        createGetBindingMethodAndBindingVariable(tsBuilder, eaViewInfo);
        createInitWidgetMethod(tsBuilder, eaViewInfo, superClassIsView);
        createGetViewModelMethod(tsBuilder, eaViewInfo, superClassIsView);
        createRenderWidgetMethod(tsBuilder, eaViewInfo, superClassIsView);
        createRenderImageMethod(tsBuilder);
        createAdapter(tsBuilder, eaViewInfo);
        String packageFullName = eaUtil.elementUtils.getPackageOf(eaUtil.elementUtils.getTypeElement(viewName)).getQualifiedName().toString();
        JavaFile javaFile = JavaFile.builder(packageFullName, tsBuilder.build())
                .build();
        try {
            javaFile.writeTo(eaUtil.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TypeSpec.Builder generateClass(EAViewInfo eaViewInfo) {
        String viewName = eaViewInfo.viewName;
        TypeSpec.Builder tsBuilder = TypeSpec.classBuilder(viewName.substring(viewName.lastIndexOf(".") + 1) + "Parent")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(EAParent.class);
        if (eaViewInfo.superClass != null) {
            tsBuilder.superclass(TypeName.get(eaViewInfo.superClass));
        }
        return tsBuilder;
    }

    private boolean checkSuperClassIsView(TypeMirror typeMirror) {
        if (typeMirror == null) {
            return false;
        }
        Element element = eaUtil.types.asElement(typeMirror);
        if (element == null) {
            return false;
        }
        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            String name = typeElement.getQualifiedName().toString();
            if (eaUtil.equals(name, EAConstant.c_ViewGroup) || eaUtil.equals(name, EAConstant.c_View)) {
                return true;
            } else {
                return checkSuperClassIsView(typeElement.getSuperclass());
            }
        }
        return false;
    }

    private void addForDiffSuper(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo, boolean superClassIsView) {
        if (superClassIsView) {
            tsBuilder.addField(getCN(EAConstant.c_FragmentActivity), EAConstant.str_viewActivity, Modifier.PUBLIC);
            tsBuilder.addField(getCN(EAConstant.c_Fragment), EAConstant.str_viewFragment, Modifier.PUBLIC);
            tsBuilder.addField(getCN(EAConstant.c_LifecycleOwner), EAConstant.str_lifecycleOwner, Modifier.PUBLIC);
            tsBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder(getCN(EAConstant.c_Context), "context").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super(context)")
                    .build());
            tsBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder(getCN(EAConstant.c_Context), "context").build())
                    .addParameter(ParameterSpec.builder(getCN(EAConstant.c_AttributeSet), "attrs").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super(context, attrs)")
                    .build());
            tsBuilder.addMethod(MethodSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder(getCN(EAConstant.c_Context), "context").build())
                    .addParameter(ParameterSpec.builder(getCN(EAConstant.c_AttributeSet), "attrs").build())
                    .addParameter(ParameterSpec.builder(int.class, "defStyleAttr").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super(context, attrs, defStyleAttr)")
                    .build());
        } else {
        }
    }

    private void createGetBindingMethodAndBindingVariable(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo) {
        for (TypeMirror typeMirror : eaViewInfo.bindings) {
            String bindingName = typeMirror.toString();
            String bindingSimpleName = bindingName.substring(bindingName.lastIndexOf(".") + 1);
            tsBuilder.addField(TypeName.get(typeMirror), eaUtil.firstToLowerCase(bindingSimpleName), Modifier.PROTECTED);
        }
    }

    private void createInitWidgetMethod(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo, boolean superClassIsView) {
        String parentName = eaViewInfo.viewName.replace(".", "_") + "Parent";
        String initMethodName = "init_" + parentName;
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder(initMethodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        initMethod.addParameter(ParameterSpec.builder(getCN(EAConstant.c_ViewGroup), "parent").build());
        Map<TypeMirror, String> bindingSimpleNameMap = new HashMap<>();
        for (TypeMirror typeMirror : eaViewInfo.bindings) {
            String bindingName = eaUtil.firstToLowerCase(typeMirror.toString().substring(typeMirror.toString().lastIndexOf(".") + 1));
            initMethod.addCode("if($N==null){\n", bindingName);
            if (superClassIsView) {
                initMethod.addStatement("$N = $T.inflate($T.from(getContext()), parent, false)", bindingName, ClassName.get(typeMirror), getCN(EAConstant.c_LayoutInflater));
            } else {
                initMethod.addStatement("$N = $T.inflate(getLayoutInflater(), parent, false)", bindingName, ClassName.get(typeMirror));
            }
            initMethod.addCode("}\n");
            bindingSimpleNameMap.put(typeMirror, bindingName);
        }
        for (TypeMirror bindingTM : eaViewInfo.bindings) {
            TypeElement binding = eaUtil.elementUtils.getTypeElement(bindingTM.toString());
            for (Element element : binding.getEnclosedElements()) {
                if (element instanceof VariableElement) {
                    String widgetName = element.getSimpleName().toString();
                    tsBuilder.addField(ClassName.get(element.asType()), widgetName, Modifier.PUBLIC);
                    CodeBlock.Builder cb = CodeBlock.builder();
                    cb.addStatement("this.$N = $N.$N", widgetName, bindingSimpleNameMap.get(bindingTM), widgetName);
                    initMethod.addCode(cb.build());
                }
            }
        }
        tsBuilder.addMethod(initMethod.build());
    }

    private void createRenderWidgetMethod(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo, boolean superClassIsView) {
        String viewName = eaViewInfo.viewName;
        MethodSpec.Builder renderMethod = MethodSpec.methodBuilder("renderWidget_" + viewName.replace(".", "_") + "Parent")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID);
        for (TypeMirror typeMirror : eaViewInfo.viewModels) {
            TypeElement viewModel = eaUtil.elementUtils.getTypeElement(typeMirror.toString());
            String viewModelSimpleName = eaUtil.firstToLowerCase(viewModel.getSimpleName().toString());
            renderMethod.addStatement("$T $N = get$N()", getCN(viewModel.getQualifiedName().toString()), viewModelSimpleName, viewModel.getSimpleName());
            List<? extends Element> viewModelVariable = viewModel.getEnclosedElements();
            for (Element child : viewModelVariable) {
                if (child instanceof VariableElement) {
                    VariableElement ve = (VariableElement) child;
                    if (ve.asType().toString().contains("MutableLiveData")) {
                        EAApi annotation = ve.getAnnotation(EAApi.class);
                        String veName = ve.getSimpleName().toString();

                        TypeName modelName = getCN(Object.class.getCanonicalName());
                        if (ClassName.get(ve.asType()) instanceof ParameterizedTypeName) {
                            ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) ClassName.get(ve.asType());
                            modelName = parameterizedTypeName.typeArguments.get(0);
                        }
                        if (annotation != null && !eaUtil.isEmptyStr(annotation.apiMethod())) {
                            String apiDataClassName = eaUtil.firstToUpperCase(annotation.apiMethod()) + "Data";
                            if (eaUtil.equals(Object.class.getCanonicalName(), modelName.toString())
                                    || eaUtil.equals(apiDataClassName, modelName.toString())) {
                                String packageFullName = eaUtil.elementUtils.getPackageOf(viewModel).getQualifiedName().toString();
                                String viewModelParentName = typeMirror.toString().substring(typeMirror.toString().lastIndexOf(".") + 1) + "Parent";
                                modelName = getCN(packageFullName + "." + viewModelParentName + "." + apiDataClassName);
                            }
                        }
                        String observerFieldName = viewModelSimpleName + eaUtil.firstToUpperCase(veName);
                        tsBuilder.addField(
                                FieldSpec.builder(ParameterizedTypeName.get(getCN(EAConstant.OBSERVER), modelName), observerFieldName, Modifier.PROTECTED)
                                        .build());
                        CodeBlock.Builder cb = CodeBlock.builder();
                        renderMethod.addCode("if($N==null){\n", observerFieldName);
                        renderMethod.addCode("$N = new $T<$T>() {\n", observerFieldName, getCN(EAConstant.OBSERVER), modelName);
                        renderMethod.addCode("@Override\n");
                        renderMethod.addCode("public void onChanged(final $T $N) {\n", modelName, EAConstant.VALUE);
                        renderMethod.addCode(cb.build());
                        String customerRenderMethodName = "render" + eaUtil.firstToUpperCase(veName);
                        renderMethod.addStatement("$N($N)", customerRenderMethodName, EAConstant.VALUE);
                        renderMethod.addCode("}\n");
                        renderMethod.addCode("};\n");
                        renderMethod.addCode("}\n");
                        renderMethod.addStatement("$N.$N.observe($N, $N)", viewModelSimpleName, veName, superClassIsView ? EAConstant.str_lifecycleOwner : EAConstant.THIS, observerFieldName);
                        MethodSpec.Builder customerRenderMethod = MethodSpec.methodBuilder(customerRenderMethodName)
                                .returns(TypeName.VOID)
                                .addModifiers(Modifier.PROTECTED)
                                .addParameter(modelName, EAConstant.VALUE);
                        tsBuilder.addMethod(customerRenderMethod.build());
                    }
                }
            }
        }
        tsBuilder.addMethod(renderMethod.build());
    }

    private void createGetViewModelMethod(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo, boolean superClassIsView) {
        for (TypeMirror typeMirror : eaViewInfo.viewModels) {
            String viewModel = typeMirror.toString();
            MethodSpec.Builder getViewModelMethod = MethodSpec.methodBuilder("get" + viewModel.substring(viewModel.lastIndexOf(".") + 1))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(getCN(viewModel));
            if (superClassIsView) {
                getViewModelMethod.addCode("if ($N != null) {\n", EAConstant.str_viewFragment);
                getViewModelMethod.addStatement("return $T.of($N).get($T.class)", getCN(EAConstant.ViewModelProviders), EAConstant.str_viewFragment, getCN(viewModel));
                getViewModelMethod.addCode("} else {\n");
                getViewModelMethod.addStatement("return $T.of($N).get($T.class)", getCN(EAConstant.ViewModelProviders), EAConstant.str_viewActivity, getCN(viewModel));
                getViewModelMethod.addCode("}\n");
            } else {
                getViewModelMethod.addStatement("return $T.of($N).get($T.class)", getCN(EAConstant.ViewModelProviders), EAConstant.THIS, getCN(viewModel));
            }
            tsBuilder.addMethod(getViewModelMethod.build());
        }
    }

    private void createAdapter(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo) {
        if (eaViewInfo.listBindings.size() > 0) {
            MethodSpec.Builder getListItemInfo = MethodSpec.methodBuilder(EAConstant.m_getListItemInfo)
                    .returns(getCN(EAConstant.c_EAListItemInfo))
                    .addParameter(ClassName.get(eaViewInfo.listModel), "data")
                    .addParameter(int.class, "pos");
            if (eaViewInfo.listBindings.size() > 1) {
                getListItemInfo.addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT);
            } else {
                getListItemInfo.addModifiers(Modifier.PRIVATE);
                getListItemInfo.addStatement("return null");
            }
            tsBuilder.addMethod(getListItemInfo.build());
            tsBuilder.addMethod(MethodSpec.methodBuilder("onListItemClick")
                    .addModifiers(Modifier.PROTECTED)
                    .addParameter(getCN(EAConstant.c_View), "view")
                    .addParameter(ClassName.get(eaViewInfo.listModel), "value")
                    .addParameter(int.class, "pos")
                    .build());
            createHeadOrFootViewHolder(tsBuilder, eaViewInfo, eaViewInfo.headViewBinding, eaViewInfo.headViewModel, "HeadView");
            createHeadOrFootViewHolder(tsBuilder, eaViewInfo, eaViewInfo.footViewBinding, eaViewInfo.footViewModel, "FootView");
            createViewHolder(tsBuilder, eaViewInfo);

            String viewName = eaViewInfo.viewName.substring(eaViewInfo.viewName.lastIndexOf(".") + 1);
            String arrayStr = "array";
            ClassName viewHolder = getCN(EAConstant.RecyclerView_ViewHolder);
            tsBuilder.addField(FieldSpec.builder(getCN(viewName + EAConstant.Adapter), EAConstant.str_mAdapter, Modifier.PROTECTED)
                    .initializer("new $T()", getCN(viewName + EAConstant.Adapter))
                    .build());
            TypeSpec.Builder adapterTS = TypeSpec.classBuilder(viewName + EAConstant.Adapter).addModifiers(Modifier.PUBLIC);
            adapterTS.superclass(ParameterizedTypeName.get(getCN(EAConstant.RecyclerView_Adapter), viewHolder));
            adapterTS.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(eaViewInfo.listModel)), EAConstant.v_data)
                    .initializer("new $T<>()", ArrayList.class)
                    .build());
            adapterTS.addField(getCN(EAConstant.c_View), "footView", Modifier.PRIVATE);
            adapterTS.addField(FieldSpec.builder(boolean.class, EAConstant.str_hasHeadView, Modifier.PRIVATE)
                    .initializer(eaViewInfo.headViewBinding == null ? "false" : "true")
                    .build());
            adapterTS.addField(FieldSpec.builder(boolean.class, EAConstant.str_hasFootView, Modifier.PRIVATE)
                    .initializer(eaViewInfo.footViewBinding == null ? "false" : "true")
                    .build());
            adapterTS.addField(FieldSpec.builder(boolean.class, EAConstant.str_hasMultiItemView, Modifier.PRIVATE)
                    .initializer(eaViewInfo.listBindings.size() > 1 ? "true" : "false")
                    .build());
            if (eaViewInfo.headViewModel != null) {
                adapterTS.addField(FieldSpec.builder(TypeName.get(eaViewInfo.headViewModel), EAConstant.str_HeadViewData, Modifier.PUBLIC)
                        .initializer("new $T()", ClassName.get(eaViewInfo.headViewModel))
                        .build());
                adapterTS.addMethod(MethodSpec.methodBuilder(EAConstant.m_SetHeadViewData)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(eaViewInfo.headViewModel), EAConstant.str_HeadViewData)
                        .addStatement("this.$N = $N", EAConstant.str_HeadViewData, EAConstant.str_HeadViewData)
                        .addStatement("notifyItemChanged(0)")
                        .build());
            }
            if (eaViewInfo.footViewModel != null) {
                adapterTS.addField(FieldSpec.builder(TypeName.get(eaViewInfo.footViewModel), EAConstant.str_FootViewData, Modifier.PUBLIC)
                        .initializer("new $T()", ClassName.get(eaViewInfo.footViewModel))
                        .build());
                adapterTS.addMethod(MethodSpec.methodBuilder(EAConstant.m_SetFootViewData)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(eaViewInfo.footViewModel), EAConstant.str_FootViewData)
                        .addStatement("this.$N = $N", EAConstant.str_FootViewData, EAConstant.str_FootViewData)
                        .addStatement("notifyItemChanged(getItemCount()-1)")
                        .build());
            }
            adapterTS.addField(FieldSpec.builder(ParameterizedTypeName.get(getCN(EAConstant.c_SparseArray), ClassName.get(Class.class)), arrayStr)
                    .initializer("new $T<>()", getCN(EAConstant.c_SparseArray))
                    .build());
            adapterTS.addMethod(MethodSpec.methodBuilder("updateData")
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(eaViewInfo.listModel)), "list")
                    .addParameter(Boolean.class, "append")
                    .addParameter(Boolean.class, "showFootView")
                    .addCode("if (!append) {\n")
                    .addStatement("$N.clear()", EAConstant.v_data)
                    .addCode("}\n")
                    .addCode("if (list != null && list.size() > 0) {\n")
                    .addStatement("$N.addAll(list)", EAConstant.v_data)
                    .addCode("}\n")
                    .addCode("if (footView != null) {\n")
                    .addStatement("footView.setVisibility(showFootView ? View.VISIBLE : View.GONE)")
                    .addCode("}\n")
                    .addStatement("notifyDataSetChanged()")
                    .build());
            adapterTS.addMethod(MethodSpec.methodBuilder("updateData")
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(eaViewInfo.listModel)), "list")
                    .addParameter(Boolean.class, "append")
                    .addStatement("updateData(list, append, false)")
                    .build());
            adapterTS.addMethod(MethodSpec.methodBuilder("getItemViewType")
                    .returns(int.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(int.class, "position")
                    .addCode("if(getItemCount()>data.size()){\n")
                    .addCode("if($N && position==0){\n", EAConstant.str_hasHeadView)
                    .addStatement("return $T.ITEM_HEAD_TYPE", getCN(EAConstant.c_EAListItemInfo))
                    .addCode("}\n")
                    .addCode("if($N &&position==getItemCount()-1){\n", EAConstant.str_hasFootView)
                    .addStatement("return $T.ITEM_FOOT_TYPE", getCN(EAConstant.c_EAListItemInfo))
                    .addCode("}\n")
                    .addStatement("int pos = $N?(position-1):position", EAConstant.str_hasHeadView)
                    .addCode("if ($N) {\n", EAConstant.str_hasMultiItemView)
                    .addStatement("$T info = $N(data.get(pos), pos)", getCN(EAConstant.c_EAListItemInfo), EAConstant.m_getListItemInfo)
                    .addCode("if (info == null) {\n")
                    .addStatement("throw new $T(\"override $N() first\")", getCN(EAConstant.c_RuntimeException), EAConstant.m_getListItemInfo)
                    .addCode("}\n")
                    .addStatement("$N.put(info.itemType, info.bindingClass)", arrayStr)
                    .addStatement("return info.itemType")
                    .addCode("} else {\n")
                    .addStatement("return $T.ITEM_NORMAL_TYPE", getCN(EAConstant.c_EAListItemInfo))
                    .addCode("}\n")
                    .addCode("} else {\n")
                    .addCode("if ($N) {", EAConstant.str_hasMultiItemView)
                    .addStatement("$T info = $N(data.get(position), position)", getCN(EAConstant.c_EAListItemInfo), EAConstant.m_getListItemInfo)
                    .addCode("if (info == null) {\n")
                    .addStatement("throw new $T(\"override $N() first\")", getCN(EAConstant.c_RuntimeException), EAConstant.m_getListItemInfo)
                    .addCode("}\n")
                    .addStatement("$N.put(info.itemType, info.bindingClass)", arrayStr)
                    .addStatement("return info.itemType")
                    .addCode("} else {\n")
                    .addStatement("return $T.ITEM_NORMAL_TYPE", getCN(EAConstant.c_EAListItemInfo))
                    .addCode("}\n")
                    .addCode("}\n")
                    .build());
            MethodSpec.Builder onCreateViewHolderMB = MethodSpec.methodBuilder("onCreateViewHolder")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(viewHolder)
                    .addParameter(getCN(EAConstant.c_ViewGroup), "parent")
                    .addParameter(int.class, "viewType");
            if (eaViewInfo.headViewBinding != null) {
                tsBuilder.addMethod(MethodSpec.methodBuilder("onCreateHeadViewHolder")
                        .addParameter(TypeName.get(eaViewInfo.headViewBinding), "headViewBinding")
                        .addModifiers(Modifier.PROTECTED)
                        .returns(void.class)
                        .build());
                onCreateViewHolderMB.addCode("if (viewType == $T.ITEM_HEAD_TYPE) {\n", getCN(EAConstant.c_EAListItemInfo));
                onCreateViewHolderMB.addStatement("headViewBinding = $T.inflate($T.from(parent.getContext()), parent, false)", ClassName.get(eaViewInfo.headViewBinding), getCN(EAConstant.c_LayoutInflater));
                onCreateViewHolderMB.addStatement("onCreateHeadViewHolder(headViewBinding)");
                onCreateViewHolderMB.addStatement("return new $T(headViewBinding.getRoot())", getCN(viewName + "Parent." + "HeadViewHolder"));
                onCreateViewHolderMB.addCode("}\n");
            }
            if (eaViewInfo.footViewBinding != null) {
                onCreateViewHolderMB.addCode("if (viewType == $T.ITEM_FOOT_TYPE) {\n", getCN(EAConstant.c_EAListItemInfo));
                onCreateViewHolderMB.addStatement("footViewBinding = $T.inflate($T.from(parent.getContext()), parent, false)", ClassName.get(eaViewInfo.footViewBinding), getCN(EAConstant.c_LayoutInflater));
                onCreateViewHolderMB.addStatement("return new $T(footViewBinding.getRoot())", getCN(viewName + "Parent." + "FootViewHolder"));
                onCreateViewHolderMB.addCode("}\n");
            }
            if (eaViewInfo.listBindings.size() > 1) {
                onCreateViewHolderMB.addStatement("String binding = array.get(viewType).getCanonicalName()");
                int i = 0;
                for (TypeMirror binding : eaViewInfo.listBindings) {
                    onCreateViewHolderMB.addCode("$N (binding.equals($T.class.getCanonicalName())) {\n", i == 0 ? "if" : "} else if", ClassName.get(binding));
                    onCreateViewHolderMB.addStatement("$T bindingClass = $T.inflate($T.from(parent.getContext()), parent, false)", ClassName.get(binding), ClassName.get(binding), getCN(EAConstant.c_LayoutInflater));
                    onCreateViewHolderMB.addStatement("return new $T(bindingClass.getRoot())", getCN(viewName + "Parent." + binding.toString().substring(binding.toString().lastIndexOf(".") + 1) + "VH"));
                    i++;
                }
                onCreateViewHolderMB.addCode("}\n");
            } else {
                onCreateViewHolderMB.addCode("if (viewType == $T.ITEM_NORMAL_TYPE) {\n", getCN(EAConstant.c_EAListItemInfo));
                TypeMirror binding = eaViewInfo.listBindings.get(0);
                onCreateViewHolderMB.addStatement("$T binding = $T.inflate($T.from(parent.getContext()), parent, false)", ClassName.get(binding), ClassName.get(binding), getCN(EAConstant.c_LayoutInflater));
                onCreateViewHolderMB.addStatement("return new $T(binding.getRoot())", getCN(viewName + "Parent." + binding.toString().substring(binding.toString().lastIndexOf(".") + 1) + "VH"));
                onCreateViewHolderMB.addCode("}\n");
            }
            MethodSpec.Builder onBindViewHolderMB = MethodSpec.methodBuilder("onBindViewHolder")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(viewHolder, "holder")
                    .addParameter(int.class, "position");
            if (eaViewInfo.headViewBinding != null) {
                adapterTS.addField(TypeName.get(eaViewInfo.headViewBinding), "headViewBinding", Modifier.PUBLIC);
                adapterTS.addMethod(MethodSpec.methodBuilder("getHeadView")
                        .returns(getCN(EAConstant.c_View))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return headViewBinding.getRoot()")
                        .build());
                ClassName cnVH = getCN(viewName + "Parent." + "HeadViewHolder");
                onBindViewHolderMB.addCode("if (holder instanceof $T) {\n", cnVH);
                onBindViewHolderMB.addStatement("(($T) holder).onBindViewHolder($N)", cnVH, EAConstant.str_HeadViewData);
                onBindViewHolderMB.addStatement("return");
                onBindViewHolderMB.addCode("}\n");
            }
            if (eaViewInfo.footViewBinding != null) {
                adapterTS.addField(TypeName.get(eaViewInfo.footViewBinding), "footViewBinding", Modifier.PUBLIC);
                adapterTS.addMethod(MethodSpec.methodBuilder("getFootView")
                        .returns(getCN(EAConstant.c_View))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return footViewBinding.getRoot()")
                        .build());
                ClassName cnVH = getCN(viewName + "Parent." + "FootViewHolder");
                onBindViewHolderMB.addCode("if (holder instanceof $T) {\n", cnVH);
                onBindViewHolderMB.addStatement("(($T) holder).onBindViewHolder($N)", cnVH, EAConstant.str_FootViewData);
                onBindViewHolderMB.addStatement("return");
                onBindViewHolderMB.addCode("}\n");
            }
            for (TypeMirror binding : eaViewInfo.listBindings) {
                ClassName cnVH = getCN(viewName + "Parent." + binding.toString().substring(binding.toString().lastIndexOf(".") + 1) + "VH");
                onBindViewHolderMB.addCode("if (holder instanceof $T) {\n", cnVH);
                onBindViewHolderMB.addStatement("int pos = $N?position-1:position", EAConstant.str_hasHeadView);
                onBindViewHolderMB.addStatement("(($T) holder).onBindViewHolder(data.get(pos), pos)", cnVH);
                onBindViewHolderMB.addStatement("return");
                onBindViewHolderMB.addCode("}\n");
            }
            onCreateViewHolderMB.addStatement("return null");
            adapterTS.addMethod(onCreateViewHolderMB.build());
            adapterTS.addMethod(onBindViewHolderMB.build());
            adapterTS.addMethod(MethodSpec.methodBuilder("getItemCount")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(int.class)
                    .addStatement("int size = $N.size()", EAConstant.v_data)
                    .addCode("if($N){\n", EAConstant.str_hasHeadView)
                    .addStatement("size++")
                    .addCode("}\n")
                    .addCode("if($N){\n", EAConstant.str_hasFootView)
                    .addStatement("size++")
                    .addCode("}\n")
                    .addStatement("return size")
                    .build());
            tsBuilder.addType(adapterTS.build());
        }
    }

    private void createHeadOrFootViewHolder(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo, TypeMirror viewBinding, TypeMirror model, String name) {
        if (viewBinding == null) {
            return;
        }
        TypeSpec.Builder viewHolderTS = TypeSpec.classBuilder(name + "Holder");
        initViewHolder(viewHolderTS, viewBinding, eaViewInfo);
        MethodSpec.Builder onBindViewHolderMB = MethodSpec.methodBuilder(EAConstant.m_onBindViewHolder)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC);
        if (model != null) {
            onBindViewHolderMB.addParameter(TypeName.get(model), EAConstant.str_data);
            tsBuilder.addMethod(MethodSpec.methodBuilder("renderList" + name)
                    .addModifiers(Modifier.PROTECTED)
                    .addParameter(TypeName.get(viewBinding), EAConstant.str_binding)
                    .addParameter(TypeName.get(model), EAConstant.VALUE)
                    .build());
            onBindViewHolderMB.addStatement("renderList$N($N, $N)", name, EAConstant.str_binding, EAConstant.str_data);
        }
        viewHolderTS.addMethod(onBindViewHolderMB.build());
        tsBuilder.addType(viewHolderTS.build());
    }

    private void createViewHolder(TypeSpec.Builder tsBuilder, EAViewInfo eaViewInfo) {
        for (TypeMirror typeMirror : eaViewInfo.listBindings) {
            String vhName = typeMirror.toString() + "VH";
            TypeSpec.Builder viewHolderTS = TypeSpec.classBuilder(vhName.substring(vhName.lastIndexOf(".") + 1));
            initViewHolder(viewHolderTS, typeMirror, eaViewInfo);
            MethodSpec.Builder onBindViewHolderMB = MethodSpec.methodBuilder(EAConstant.m_onBindViewHolder)
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC);
            onBindViewHolderMB.addParameter(TypeName.get(eaViewInfo.listModel), EAConstant.str_data, Modifier.FINAL);
            onBindViewHolderMB.addParameter(int.class, "pos", Modifier.FINAL);
            onBindViewHolderMB.addCode("$N.setOnClickListener(new $T.OnClickListener() {\n", EAConstant.str_itemView, getCN(EAConstant.c_View));
            onBindViewHolderMB.addCode("@Override\n");
            onBindViewHolderMB.addCode("public void onClick(View v) {\n");
            onBindViewHolderMB.addStatement("onListItemClick($N, $N, pos)", EAConstant.str_itemView, EAConstant.str_data);
            onBindViewHolderMB.addCode("}\n");
            onBindViewHolderMB.addStatement("})");

            tsBuilder.addMethod(MethodSpec.methodBuilder("renderListItem")
                    .addModifiers(Modifier.PROTECTED)
                    .addParameter(TypeName.get(typeMirror), EAConstant.str_binding)
                    .addParameter(TypeName.get(eaViewInfo.listModel), EAConstant.str_data)
                    .addParameter(int.class, "pos")
                    .build());
            onBindViewHolderMB.addStatement("renderListItem($N, $N, pos)", EAConstant.str_binding, EAConstant.str_data);
            viewHolderTS.addMethod(onBindViewHolderMB.build());
            tsBuilder.addType(viewHolderTS.build());
        }
    }

    private void initViewHolder(TypeSpec.Builder viewHolderTS, TypeMirror binding, EAViewInfo eaViewInfo) {
        viewHolderTS.superclass(getCN(EAConstant.RecyclerView_ViewHolder));
        viewHolderTS.addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder viewHolderConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getCN(EAConstant.c_View), EAConstant.str_itemView)
                .addStatement("super($N)", EAConstant.str_itemView)
                .addStatement("$N = $T.getBinding($N)", EAConstant.str_binding, getCN(EAConstant.DataBindingUtil), EAConstant.str_itemView);
        viewHolderTS.addField(TypeName.get(binding), EAConstant.str_binding, Modifier.PRIVATE);
        viewHolderTS.addMethod(viewHolderConstructor.build());
    }

    private void createRenderImageMethod(TypeSpec.Builder tsBuilder) {
        MethodSpec.Builder renderImageMethod = MethodSpec.methodBuilder(EAConstant.RenderImageView)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(getCN(EAConstant.ImageView), "imageview")
                .addParameter(String.class, "url")
                .returns(TypeName.VOID);
        tsBuilder.addMethod(renderImageMethod.build());
    }

}
