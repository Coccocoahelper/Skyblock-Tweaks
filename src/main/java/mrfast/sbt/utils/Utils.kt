package mrfast.sbt.utils

import net.minecraft.client.Minecraft
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object Utils {
    val mc: Minecraft = Minecraft.getMinecraft()

    fun setTimeout(runnable: () -> Unit, delayMillis: Long) {
        Thread {
            Thread.sleep(delayMillis)
            runnable()
        }.start()
    }

    fun cleanColor(text: String): String {
        return text.replace("§[0-9a-fA-F]".toRegex(),"")
    }

    fun formatNumber(number: Int): String {
        return String.format("%,d", number)
    }

    fun clamp(min: Double,value: Double,max:Double): Double {
        return max.coerceAtMost(value.coerceAtLeast(min))
    }

    fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
    }
}