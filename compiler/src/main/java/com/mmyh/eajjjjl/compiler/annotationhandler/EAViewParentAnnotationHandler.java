package com.mmyh.eajjjjl.compiler.annotationhandler;


import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAViewInfo;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

public class EAViewParentAnnotationHandler extends EABaseAnnotationHandler {

    public EAViewParentAnnotationHandler(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void handle(Element element, List<EAViewInfo> viewInfoList) {
        TypeElement typeElement = (TypeElement) element;
        EAViewInfo viewInfo = new EAViewInfo();
        viewInfoList.add(viewInfo);
        viewInfo.viewName = typeElement.getQualifiedName().toString();
        EAView eaView = typeElement.getAnnotation(EAView.class);
        try {
            Class<?>[] cls = eaView.viewModels();
        } catch (MirroredTypesException e) {
            List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            viewInfo.viewModels.addAll(typeMirrors);
        }
        try {
            Class<?> cls = eaView.listModel();
        } catch (MirroredTypeException e) {
            viewInfo.listModel = e.getTypeMirror();
        }
        try {
            Class<?> cls = eaView.headViewBinding();
        } catch (MirroredTypeException e) {
            if (!Object.class.getCanonicalName().equals(e.getTypeMirror().toString())) {
                viewInfo.headViewBinding = e.getTypeMirror();
                viewInfo.bindingsString.add(e.getTypeMirror().toString());
            }
        }
        try {
            Class<?> cls = eaView.footViewBinding();
        } catch (MirroredTypeException e) {
            if (!Object.class.getCanonicalName().equals(e.getTypeMirror().toString())) {
                viewInfo.footViewBinding = e.getTypeMirror();
                viewInfo.bindingsString.add(e.getTypeMirror().toString());
            }
        }
        try {
            Class<?>[] cls = eaView.bindings();
        } catch (MirroredTypesException e) {
            List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            if (typeMirrors != null && typeMirrors.size() > 0) {
                viewInfo.bindings.addAll(typeMirrors);
                for (TypeMirror typeMirror : typeMirrors) {
                    viewInfo.bindingsString.add(typeMirror.toString());
                }
            }
        }
        try {
            Class<?>[] cls = eaView.listBindings();
        } catch (MirroredTypesException e) {
            List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            if (typeMirrors != null && typeMirrors.size() > 0) {
                viewInfo.listBindings.addAll(typeMirrors);
                for (TypeMirror typeMirror : typeMirrors) {
                    viewInfo.bindingsString.add(typeMirror.toString());
                }
            }
        }
        try {
            Class<?> cls = eaView.superClass();
        } catch (MirroredTypeException e) {
            if (!Object.class.getCanonicalName().equals(e.getTypeMirror().toString())) {
                viewInfo.superClass = e.getTypeMirror();
            }
        }
        try {
            Class<?> cls = eaView.headViewModel();
        } catch (MirroredTypeException e) {
            if (!Object.class.getCanonicalName().equals(e.getTypeMirror().toString())) {
                viewInfo.headViewModel = e.getTypeMirror();
            }
        }
        try {
            Class<?> cls = eaView.footViewModel();
        } catch (MirroredTypeException e) {
            if (!Object.class.getCanonicalName().equals(e.getTypeMirror().toString())) {
                viewInfo.footViewModel = e.getTypeMirror();
            }
        }
    }

}
