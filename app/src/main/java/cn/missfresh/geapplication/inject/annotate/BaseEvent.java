package cn.missfresh.geapplication.inject.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gchen on 16/3/2.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseEvent {
    Class<?> listenerType();

    String listenerSetter();

    String methodName();
}
