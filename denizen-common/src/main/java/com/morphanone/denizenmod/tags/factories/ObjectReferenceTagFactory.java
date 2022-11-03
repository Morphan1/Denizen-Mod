package com.morphanone.denizenmod.tags.factories;

import com.denizenscript.denizencore.objects.ObjectTag;

public abstract class ObjectReferenceTagFactory<T extends ObjectTag, R> extends ObjectTagFactory<T> {
    public Class<R> referenceClass;

    public ObjectReferenceTagMetafactory<?, ?> metafactory;

    public ObjectReferenceTagFactory(Class<T> tagClass, Class<R> referenceClass) {
        super(tagClass);
        this.referenceClass = referenceClass;
    }

    public T tryOf(Object obj) {
        if (referenceClass.isInstance(obj)) {
            return of(referenceClass.cast(obj));
        }
        return null;
    }

    public abstract T of(R obj);

    /*@Override
    public boolean isCustom(Method method) {
        return Annotation.find(method, ValidReference.class) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P extends ObjectTag, RT extends ObjectTag> void registerCustom(String name, Class<RT> returnType, Class<P> extraParam, Method method) {
        try {
            if (extraParam != null) {
                tagProcessor.registerTag(returnType, extraParam, name, (TagRunnable.ObjectWithParamInterface<T, RT, P>) TagInterfaceProxy.objectReferenceWithParam((Class<? extends ObjectReferenceTag<R>>)tagClass, method, returnType, extraParam));
            }
            else {
                tagProcessor.registerTag(returnType, name, (TagRunnable.ObjectInterface<T, RT>) TagInterfaceProxy.objectReference((Class<? extends ObjectReferenceTag<R>>)tagClass, method, returnType));
            }
        }
        catch (Throwable e) {
            Debug.echoError(e);
        }
    }*/
}
