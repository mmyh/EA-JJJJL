package com.mmyh.eajjjjl.compiler.processor;

import com.google.auto.service.AutoService;
import com.mmyh.eajjjjl.annotation.EAApi;
import com.mmyh.eajjjjl.annotation.EAServiceConfig;
import com.mmyh.eajjjjl.compiler.annotationhandler.EAServiceAnnotationHandler;
import com.mmyh.eajjjjl.compiler.codegenerate.EAServiceCodeGenerater;
import com.mmyh.eajjjjl.compiler.model.EAServiceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class EAServiceProcessor extends EABaseProcessor {

    EAServiceAnnotationHandler annotationHandler;

    EAServiceCodeGenerater codeGenerater;

    List<EAServiceInfo> serviceInfoList = new ArrayList<>();

    boolean isProcessed = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationHandler = new EAServiceAnnotationHandler(eaUtil);
        codeGenerater = new EAServiceCodeGenerater(eaUtil);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(EAServiceConfig.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        printNote("start");
        if (!roundEnv.processingOver() && !isProcessed) {
            Set<? extends Element> serviceCfgs =
                    roundEnv.getElementsAnnotatedWith(EAServiceConfig.class);
            List<String> myServiceList = new ArrayList<>();
            for (Element element : serviceCfgs) {
                EAServiceConfig serviceConfig = element.getAnnotation(EAServiceConfig.class);
                try {
                    serviceConfig.myServices();
                } catch (MirroredTypesException e) {
                    List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
                    for (TypeMirror tm : typeMirrors) {
                        if (!myServiceList.contains(tm.toString())) {
                            myServiceList.add(tm.toString());
                        }
                    }
                }
            }
            try {
                annotationHandler.handle(serviceInfoList, myServiceList);
                for (EAServiceInfo eaServiceInfo : serviceInfoList) {
                    codeGenerater.generate(eaServiceInfo);
                }
                serviceInfoList.clear();
                isProcessed = true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            printNote("stop");
        }
        return false;
    }


}
