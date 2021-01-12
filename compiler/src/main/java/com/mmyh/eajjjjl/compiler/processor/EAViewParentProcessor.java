package com.mmyh.eajjjjl.compiler.processor;

import com.google.auto.service.AutoService;
import com.mmyh.eajjjjl.annotation.EAView;
import com.mmyh.eajjjjl.compiler.annotationhandler.EAViewParentAnnotationHandler;
import com.mmyh.eajjjjl.compiler.codegenerate.EAViewParentCodeGenerater;
import com.mmyh.eajjjjl.compiler.model.EAViewInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class EAViewParentProcessor extends EABaseProcessor {

    List<EAViewInfo> viewInfoList = new ArrayList<>();

    EAViewParentAnnotationHandler annotationHandler;

    EAViewParentCodeGenerater codeGenerater;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationHandler = new EAViewParentAnnotationHandler(eaUtil);
        codeGenerater = new EAViewParentCodeGenerater(eaUtil);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(EAView.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        printNote("start");
        if (!roundEnv.processingOver()) {
            Set<? extends Element> eaviews =
                    roundEnv.getElementsAnnotatedWith(EAView.class);
            for (Element element : eaviews) {
                annotationHandler.handle(element, viewInfoList);
            }
            for (EAViewInfo eaViewInfo : viewInfoList) {
                codeGenerater.generate(eaViewInfo);
            }
            viewInfoList.clear();
        } else {
            printNote("stop");
        }
        return false;
    }

}
