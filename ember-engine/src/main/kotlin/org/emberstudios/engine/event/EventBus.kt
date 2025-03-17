package org.emberstudios.engine.event

import io.github.classgraph.ClassGraph
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.thread.createThread
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

open class Event

interface EventListener

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler

object EventBus {

    val LOGGER = getLogger<EventBus>()

    private val eventMethods =
        mutableMapOf<Class<out Event>, MutableList<Pair<EventListener, KFunction<*>>>>()

    fun initialize() {
        LOGGER.trace { "Scanning for event listeners..." }

        val scanResult = ClassGraph()
            .enableClassInfo()
            .scan()

        val listenerClasses = scanResult
            .getClassesImplementing(EventListener::class.qualifiedName)
            .map { Class.forName(it.name).kotlin }

        for (listenerClass in listenerClasses) {
            if (listenerClass.objectInstance != null)
                register(listenerClass.objectInstance as EventListener)
            else
                register(listenerClass.createInstance() as EventListener)
        }

        scanResult.close()
        LOGGER.trace { "Found ${listenerClasses.size} event listener(s)!" }
    }

    fun initializeAsync() {
        createThread(
            "EventBusClassLookup",
            start = true,
            action = ::initialize
        )
    }

    private fun register(listener: EventListener) {
        val functions = listener::class.functions.filter {
            it.findAnnotation<EventHandler>() != null
        }

        for (function in functions) {
            val params = function.parameters
            if (params.size != 2) {
                LOGGER.warn { "Skipping ${function.name}. Must have exactly one event parameter!" }
                continue
            }

            val eventType = params[1].type.classifier
            if (eventType !is KClass<*> || !Event::class.java.isAssignableFrom(eventType.java)) {
                LOGGER.warn { "Skipping ${function.name}. Parameter is not a valid Event type!" }
                continue
            }

            val eventClass = eventType as KClass<out Event>
            eventMethods.computeIfAbsent(eventClass.java) { mutableListOf() }
                .add(listener to function)
        }
    }

    fun dispatch(event: Event) = eventMethods[event::class.java]
        ?.forEach { (listener, function) -> function.call(listener, event) }

}