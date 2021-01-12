package com.mmyh.eajjjjl.compiler.processor;

import com.google.auto.service.AutoService;
import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.compiler.annotationhandler.EAViewModelParentAnnotationHandler;
import com.mmyh.eajjjjl.compiler.codegenerate.EAViewModelParentCodeGenerater;
import com.mmyh.eajjjjl.compiler.model.EAApiInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class EAViewModelParentProcessor extends EABaseProcessor {

    private EAViewModelParentAnnotationHandler handler;

    private EAViewModelParentCodeGenerater codeGenerater;

    private Map<String, List<EAApiInfo>> apiMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(EAApi.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        handler = new EAViewModelParentAnnotationHandler(eaUtil);
        codeGenerater = new EAViewModelParentCodeGenerater(eaUtil);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        printNote("START");
        if (!roundEnv.processingOver()) {
            Set<? extends Element> datas =
                    roundEnv.getElementsAnnotatedWith(EAApi.class);
            for (Element each : datas) {
                handler.handle(each, apiMap);
            }

            Set<String> viewModelNames = apiMap.keySet();
            Iterator<String> iterator = viewModelNames.iterator();
            while (iterator.hasNext()) {
                String viewModelName = iterator.next();
                codeGenerater.generate(viewModelName, apiMap);
            }
            apiMap.clear();
        } else {
            printNote("STOP");
        }
        return false;
    }

}
