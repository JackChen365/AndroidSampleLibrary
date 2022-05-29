package com.github.jackchen.android.core.component

import com.github.jackchen.android.core.extension.ExtensionHandler
import java.util.*

/**
 * @author Created by cz
 * @date 2020-01-27 21:09
 * @email bingo110@126.com
 */
object ComponentManager : ExtensionHandler<ComponentContainer> {
    private val COMPONENT_CONTAINER_CLASS_DESC = ComponentContainer::class.java.name.replace('.', '/')

    /**
     * The extra component list that will decorate every sample
     */
    private val componentContainerSet: MutableSet<ComponentContainer> =
        TreeSet { c1, c2 ->
            val i = c1.getComponentPriority() - c2.getComponentPriority()
            if (0 == i) -1 else i
        }

    override fun handle(
        className: String, superClass: String,
        interfaces: List<String>
    ): Boolean {
        if (interfaces.contains(COMPONENT_CONTAINER_CLASS_DESC)) {
            try {
                val clazz = Class.forName(className)
                val componentContainer = clazz.newInstance() as ComponentContainer
                register(componentContainer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
    }

    override fun register(extension: ComponentContainer) {
        componentContainerSet.add(extension)
    }

    override fun unregister(extension: ComponentContainer) {
        componentContainerSet.remove(extension)
    }

    fun getComponentContainerSet(): Set<ComponentContainer> {
        return componentContainerSet
    }
}