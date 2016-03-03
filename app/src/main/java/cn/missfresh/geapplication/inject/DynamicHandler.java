package cn.missfresh.geapplication.inject;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by gchen on 16/3/2.
 */
public class DynamicHandler implements InvocationHandler {
    private WeakReference<Object> handlerRef;
    private HashMap<String, Method> methodHashMap = new HashMap<>(1);

    public DynamicHandler(Object handler) {
        this.handlerRef = new WeakReference<Object>(handler);
    }

    public void addMethod(String name, Method method) {
        methodHashMap.put(name, method);
    }

    public Object getHandler() {
        return handlerRef.get();
    }

    public void setHandler(Object handler) {
        this.handlerRef = new WeakReference<Object>(handler);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object handler = handlerRef.get();
        if (handler != null) {
            String name = method.getName();
            method = methodHashMap.get(name);
            if (method != null) {
                return method.invoke(handler, args);
            }
        }
        return null;
    }
}
