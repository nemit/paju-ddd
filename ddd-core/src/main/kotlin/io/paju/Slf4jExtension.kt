package io.paju

import io.paju.LoggerNameResolver.unwrapCompanionClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier

inline fun <reified T : Any> T.logger(): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(T::class.java).name)
}

object LoggerNameResolver {
    fun <T : Any> unwrapCompanionClass(clazz: Class<T>): Class<*> {
        if (clazz.enclosingClass != null) {
            try {
                val field = clazz.enclosingClass.getField(clazz.simpleName)
                if (Modifier.isStatic(field.modifiers) && field.type == clazz) {
                    return clazz.enclosingClass
                }
            } catch(e: Exception) {
                //ok, it is not a companion object
            }
        }
        return clazz
    }
}


