package club.pisquad.minecraft.csgrenades

import club.pisquad.minecraft.csgrenades.core.entity.CounterStrikeGrenadeEntity
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.time.Duration

object ModLogger {
    val logger: Logger = LogManager.getLogger(CounterStrikeGrenades.ID)

    // Basic passthrough (optional, just for clarity)
    fun info(msg: String, vararg args: Any?) = logger.info(msg, *args)
    fun debug(msg: String, vararg args: Any?) = logger.debug(msg, *args)
    fun warn(msg: String, vararg args: Any?) = logger.warn(msg, *args)
    fun error(msg: String, vararg args: Any?) = logger.error(msg, *args)
    fun trace(msg: String, vararg args: Any?) = logger.trace(msg, *args)


    // Entity related log
    private fun constructEntityLogString(entity: CounterStrikeGrenadeEntity, msg: () -> String): String {
        return "[${entity.grenadeType}(id:${entity.id} tick:${entity.tickCount})]${msg.invoke()}"
    }

    fun trace(entity: CounterStrikeGrenadeEntity, msg: () -> String) {
        if (logger.isTraceEnabled) {
            logger.trace(constructEntityLogString(entity, msg))
        }
    }

    fun debug(entity: CounterStrikeGrenadeEntity, msg: () -> String) {
        if (logger.isDebugEnabled) {
            logger.debug(constructEntityLogString(entity, msg))
        }
    }

    fun info(entity: CounterStrikeGrenadeEntity, msg: () -> String) {
        if (logger.isInfoEnabled) {
            logger.info(constructEntityLogString(entity, msg))
        }
    }

    fun error(entity: CounterStrikeGrenadeEntity, msg: () -> String) {
        if (logger.isErrorEnabled) {
            logger.error(constructEntityLogString(entity, msg))
        }
    }

    // Action time logger
    fun debug(duration: Duration, name: String) {
        if (logger.isDebugEnabled) {
            logger.log(Level.DEBUG, "$name took  ${duration.inWholeMilliseconds.div(1000.0)} seconds")
        }
    }
}
