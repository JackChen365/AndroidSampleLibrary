package com.github.jackchen.android.sample.library.utils

import android.widget.SeekBar

/**
 * @author airsaid
 */
interface SeekBarChangeListenerAdapter : SeekBar.OnSeekBarChangeListener {
  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

  override fun onStartTrackingTouch(seekBar: SeekBar) {}

  override fun onStopTrackingTouch(seekBar: SeekBar) {}
}