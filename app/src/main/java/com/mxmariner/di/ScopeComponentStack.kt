package com.mxmariner.di

import android.util.SparseArray
import java.util.LinkedList

/**
 * Tracks and provides access to injection component(s) for a given Scope.
 *
 * @param <Scope>     Injection scope root
 * @param <Component> The injection component per scope.
 */
class ScopeComponentStack<Scope, Component> {
  private val activityComponents = SparseArray<Component>()
  private val scopeIdStack = LinkedList<Int>()
  /**
   * Get the top most component for the most recently seen scope instance.
   *
   * @return the component.
   */
  val top: Component?
    get() {
      if (!scopeIdStack.isEmpty()) {
        val id = scopeIdStack.first
        return activityComponents[id]
      }
      return null
    }

  /**
   * Get a component for a given scope instance.
   *
   * @param scopeInstance the scope instance.
   * @return the component.
   */
  fun getComponent(scopeInstance: Scope): Component? {
    val id = System.identityHashCode(scopeInstance)
    setScopeTop(scopeInstance)
    return activityComponents[id]
  }

  /**
   * Create a component for a specified scope instance.
   *
   * @param scopeInstance the scope instance.
   * @param factory       the component factory.
   * @return the created component.
   */
  fun createComponentForScope(
    scopeInstance: Scope,
    factory: () -> Component
  ): Component {
    val id = System.identityHashCode(scopeInstance)
    setScopeTop(scopeInstance)
    return getComponent(scopeInstance) ?: factory().apply {
      activityComponents.put(id, this)
    }
  }

  /**
   * Remove the component for a given scope instance.
   *
   * @param scopeInstance the scope instance.
   */
  fun releaseComponent(scopeInstance: Scope) {
    System.identityHashCode(scopeInstance).apply {
      activityComponents.remove(this)
      scopeIdStack.remove(this)
    }

  }

  /**
   * Set the most recently seen scope as the Top.
   *
   * @param scopeInstance the scope instance.
   */
  fun setScopeTop(scopeInstance: Scope) {
    System.identityHashCode(scopeInstance).apply {
      scopeIdStack.remove(this)
      scopeIdStack.addFirst(this)
    }
  }
}
