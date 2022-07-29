package com.github.jackchen.android.core.processor

import androidx.appcompat.app.AppCompatActivity
import com.github.jackchen.android.core.exception.SampleFailedException
import com.github.jackchen.android.core.extension.ExtensionHandler
import com.github.jackchen.android.core.function.FunctionManager
import com.github.jackchen.android.core.processor.clazz.ActivityClassActionProcessor
import com.github.jackchen.android.sample.api.SampleItem
import java.util.ArrayList

/**
 * @author Created by cz
 * @date 2020-01-27 15:24
 * @email bingo110@126.com
 * This is an action com.cz.android.sample.library.processor manager
 * @see ActionProcessor
 */
object ActionProcessManager : ExtensionHandler<ActionProcessor> {
  private val ACTION_PROCESSOR_CLASS_DESC = ActionProcessor::class.java.name.replace('.', '/')
  private val SAMPLE_INTERFACE_PROCESSOR_CLASS_NAME = SampleInterfaceProcessor::class.java.name

  /**
   * all registered com.cz.android.sample.library.processor list
   */
  private val processorList: MutableList<ActionProcessor> = ArrayList()

  init {
    // register default action processor
    register(ActivityClassActionProcessor())
    register(FragmentClassActionProcessor())
    register(DialogFragmentClassActionProcessor())
  }

  override fun handle(className: String, superClass: String, interfaces: List<String>): Boolean {
    if (interfaces.contains(ACTION_PROCESSOR_CLASS_DESC) ||
      superClass == SAMPLE_INTERFACE_PROCESSOR_CLASS_NAME
    ) {
      try {
        val clazz = Class.forName(className)
        val actionProcessor = clazz.newInstance() as ActionProcessor
        register(actionProcessor)
      } catch (e: Exception) {
        e.printStackTrace()
      }
      return true
    }
    return false
  }

  /**
   * Register an new action com.cz.android.sample.library.processor
   *
   * @param extension
   */
  override fun register(extension: ActionProcessor) {
    processorList.add(extension)
  }

  /**
   * Unregister an action com.cz.android.sample.library.processor from list
   *
   * @param extension
   */
  override fun unregister(extension: ActionProcessor) {
    processorList.remove(extension)
  }

  /**
   * process the action when user execute an action
   */
  @Throws(SampleFailedException::class)
  fun process(
    functionManager: FunctionManager,
    context: AppCompatActivity,
    item: SampleItem
  ) {
    val clazz = item.clazz()
    for (processor in processorList) {
      if (processor.isAvailable(clazz)) {
        processor.execute(context, item)
        // process all the functions
        functionManager.execute(context, item)
      }
    }
  }
}
