package com.github.hank9999.mcbot.task

import com.github.hank9999.mcbot.permission.PMCheck
import java.util.*

class CacheTask : TimerTask() {
    override fun run() {
        PMCheck.getCache().forEach {
            if (System.currentTimeMillis() - it.time >= 10 * 60 * 1000) {
                PMCheck.removeCache(it)
            }
        }
    }
}