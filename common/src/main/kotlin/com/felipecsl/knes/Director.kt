package com.felipecsl.knes

import com.felipecsl.knes.CPU.Companion.FREQUENCY

class Director(
      cartridgeData: ByteArray,
      mapperCallback: MapperStepCallback? = null,
      cpuCallback: CPUStepCallback? = null,
      ppuCallback: PPUStepCallback? = null,
      apuCallback: APUStepCallback? = null
  ) {
  private var isRunning = false
  private val cartridge = INESFileParser.parseCartridge(ByteArrayInputStream(cartridgeData))
  internal val console = Console.newConsole(
      cartridge, mapperCallback, cpuCallback, ppuCallback, apuCallback)

  init {
    console.reset()
  }

  fun run() {
    var startTime = currentTimeMs()
    var totalCycles = 0L
    isRunning = true
    while (isRunning) {
      totalCycles += console.step()
      if (totalCycles >= FREQUENCY) {
        val currentTime = trackConsoleSpeed(startTime, totalCycles)
        totalCycles = 0
        startTime = currentTime
      }
    }
  }

  private fun trackConsoleSpeed(startTime: Long, totalCycles: Long): Long {
    val currentTime = currentTimeMs()
    val msSpent = currentTime - startTime
    val clock = (totalCycles * 1000) / msSpent
    val speed = clock / FREQUENCY.toFloat()
    println("Clock=${clock}Hz (${speed}x)")
    return currentTime
  }

  fun setButtons1(buttons: BooleanArray) {
    console.setButtons(buttons)
  }

  fun reset() {
    isRunning = false
    console.reset()
  }

  fun buffer(): IntArray {
    return console.buffer()
  }

  fun pause() {
    isRunning = false
  }

  fun dumpState(): Map<String, String> {
    return console.state()
  }

  fun restoreState(state: Map<String, *>) {
    console.restoreState(state)
  }
}