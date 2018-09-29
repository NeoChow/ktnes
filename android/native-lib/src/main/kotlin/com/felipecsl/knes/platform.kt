package com.felipecsl.knes

import kotlinx.cinterop.*
import platform.posix.*
import platform.gles.GL_TEXTURE_ENV
import platform.gles.GL_TEXTURE_ENV_COLOR
import platform.gles.GL_TEXTURE_ENV_MODE
import platform.gles.glTexEnvf
import platform.gles.glTexEnvfv
import platform.gles2.glGetShaderiv
import platform.gles2.glShaderSource
import platform.gles2.glVertexAttribPointer
import platform.gles3.*

actual fun currentTimeMs(): Long {
  memScoped {
    val now = alloc<timeval>()
    gettimeofday(now.ptr, null)
    return (now.tv_sec.toLong() * 1000) + (now.tv_usec.toLong() / 1000)
  }
}

actual class AudioSink {
  private val bufferSize = 4096
  private var buffer = FloatArray(bufferSize)
  private var pos = 0

  actual fun write(value: Float) {
    buffer[pos++ % bufferSize] = value
  }

  actual fun drain(): FloatArray {
    val out = buffer.copyOf(pos)
    pos = 0
    return out
  }
}