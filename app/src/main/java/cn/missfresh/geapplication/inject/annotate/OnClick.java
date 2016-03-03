package cn.missfresh.geapplication.inject.annotate;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gchen on 16/3/2.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@BaseEvent(listenerType = View.OnClickListener.class, listenerSetter = "setOnClickListener", methodName = "onClick")
public @interface OnClick {
    int[] value();
}
