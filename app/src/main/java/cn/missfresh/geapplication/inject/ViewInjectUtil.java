package cn.missfresh.geapplication.inject;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.missfresh.geapplication.inject.annotate.BaseEvent;
import cn.missfresh.geapplication.inject.annotate.ContentView;
import cn.missfresh.geapplication.inject.annotate.ViewInject;

/**
 * Created by gchen on 16/2/29.
 */
public class ViewInjectUtil {
    private static final String METHOD_SET_CONTENT_VIEW = "setContentView";
    private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";

    public static void injectContentView(Activity activity) {
        if (activity != null) {
            Class<? extends Activity> clazz = activity.getClass();
            ContentView annotation = clazz.getAnnotation(ContentView.class);
            if (annotation != null) {
                int contentViewId = annotation.value();
                try {
                    Method method = clazz.getMethod(METHOD_SET_CONTENT_VIEW, int.class);
                    method.setAccessible(true);
                    method.invoke(activity, contentViewId);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void injectViews(Activity activity) {
        if (activity != null) {
            Class<? extends Activity> clazz = activity.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ViewInject annotation = field.getAnnotation(ViewInject.class);
                if (annotation != null) {
                    int viewId = annotation.value();
                    if (viewId != -1) {
                        try {
                            Method method = clazz.getMethod(METHOD_FIND_VIEW_BY_ID, int.class);
                            Object resView = method.invoke(activity, viewId);
                            field.setAccessible(true);
                            field.set(activity, resView);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void injectEvent(Activity activity) {
        if (activity != null) {
            Class<? extends Activity> clazz = activity.getClass();
            Method[] methods = clazz.getMethods();
            //遍历所有的方法
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                //拿到方法上的所有的注解
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation
                            .annotationType();
                    //拿到注解上的注解
                    BaseEvent eventBaseAnnotation = annotationType
                            .getAnnotation(BaseEvent.class);
                    //如果设置为EventBase
                    if (eventBaseAnnotation != null) {
                        //取出设置监听器的名称，监听器的类型，调用的方法名
                        String listenerSetter = eventBaseAnnotation
                                .listenerSetter();
                        Class<?> listenerType = eventBaseAnnotation.listenerType();
                        String methodName = eventBaseAnnotation.methodName();

                        try {
                            //拿到Onclick注解中的value方法
                            Method aMethod = annotationType
                                    .getDeclaredMethod("value");
                            //取出所有的viewId
                            int[] viewIds = (int[]) aMethod
                                    .invoke(annotation, null);
                            //通过InvocationHandler设置代理
                            DynamicHandler handler = new DynamicHandler(activity);
                            handler.addMethod(methodName, method);
                            Object listener = Proxy.newProxyInstance(
                                    listenerType.getClassLoader(),
                                    new Class<?>[]{listenerType}, handler);
                            //遍历所有的View，设置事件
                            for (int viewId : viewIds) {
                                View view = activity.findViewById(viewId);
                                Method setEventListenerMethod = view.getClass()
                                        .getMethod(listenerSetter, listenerType);
                                setEventListenerMethod.invoke(view, listener);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectViews(activity);
        injectEvent(activity);
    }
}
