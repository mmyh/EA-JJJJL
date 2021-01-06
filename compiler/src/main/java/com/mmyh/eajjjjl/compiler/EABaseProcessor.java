package com.mmyh.eajjjjl.compiler;

import com.squareup.javapoet.ClassName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public abstract class EABaseProcessor extends AbstractProcessor {

    protected static final String class_LifecycleOwner = "androidx.lifecycle.LifecycleOwner";

    protected static final String class_RECall = "com.mmyh.util.retrofitextension.RECall";

    protected static final String class_RetrofitService = "com.mmyh.util.retrofitextension.RetrofitService";

    protected static final String class_RetrofitError = "com.mmyh.util.retrofitextension.RetrofitError";

    protected static final String class_RetrofitError_ErrorType = "com.mmyh.util.retrofitextension.RetrofitError.ErrorType";

    protected static final String class_MutableLiveData = "androidx.lifecycle.MutableLiveData";

    protected static final String str_owner = "owner";

    protected static final String str_request = "request";

    protected static final String str_reCall = "reCall";

    protected static final String str_response = "response";

    protected static final String str_err = "err";

    protected static final String str_ErrorType = "ErrorType";

    protected static final String str_Response = "Response";

    protected static final String str_data = "data";

    protected Filer filer;

    protected Elements elementUtils;

    protected Types types;

    protected EAUtil util;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        util = new EAUtil(this, processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    protected ClassName getCN(String s) {
        return ClassName.bestGuess(s);
    }

    public void printNote(String string) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, getClass().getSimpleName() + ":" + string);
    }
}
