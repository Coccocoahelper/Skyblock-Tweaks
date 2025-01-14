package mrfast.sbt.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

object ItemUtils {

    fun getHeldItem(): ItemStack? {
        return Utils.mc.thePlayer.heldItem
    }

    fun ItemStack.getSkyblockId(): String? {
        val nbt = this.getExtraAttributes() ?: return null

        if (!nbt.hasKey("id")) return null

        val id = nbt.getString("id")
        return when {
            id == "PET" && nbt.hasKey("petInfo") -> {
                val petInfo = DevUtils.convertStringToJson(nbt.getString("petInfo"))?.asJsonObject ?: return null
                val tierInt = petTierToInt(petInfo.get("tier").asString)
                "${petInfo.get("type").asString};$tierInt"
            }
            nbt.hasKey("runes") -> {
                val runeType = nbt.getCompoundTag("runes")?.keySet?.firstOrNull()
                runeType?.let {
                    "${runeType}_RUNE;${nbt.getCompoundTag("runes").getInteger(runeType)}"
                }
            }
            else -> id
        }
    }

    private fun petTierToInt(tier: String): Int {
        if (tier == "MYTHIC") return 5
        if (tier == "LEGENDARY") return 4
        if (tier == "EPIC") return 3
        if (tier == "RARE") return 2
        if (tier == "UNCOMMON") return 1
        if (tier == "COMMON") return 0
        return -1
    }

    fun ItemStack.getExtraAttributes(): NBTTagCompound? {
        if (hasTagCompound()) {
            if (tagCompound != null && tagCompound.hasKey("ExtraAttributes")) {
                return tagCompound.getCompoundTag("ExtraAttributes")
            }
        }
        return null
    }

    fun ItemStack.getSkyblockEnchants(): MutableMap<String, Int> {
        val attributes = this.getExtraAttributes() ?: return mutableMapOf()
        val enchants = attributes.getCompoundTag("enchantments")
        val enchantsOut = mutableMapOf<String, Int>()
        for (enchantment in enchants.keySet) {
            val lvl = enchants.getInteger(enchantment)
            enchantsOut[enchantment] = lvl
        }
        return enchantsOut
    }

    fun ItemStack.getLore(): MutableList<String> {
        val lore = mutableListOf<String>()

        if (hasTagCompound()) {
            val tagCompound = tagCompound

            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                val displayTag = tagCompound.getCompoundTag("display")

                if (displayTag.hasKey("Lore", 9)) {
                    val loreTagList = displayTag.getTagList("Lore", 8)

                    for (i in 0 until loreTagList.tagCount()) {
                        lore.add(loreTagList.getStringTagAt(i))
                    }
                }
            }
        }

        return lore
    }

    fun decodeBase64Inventory(data: String?): List<ItemStack?> {
        val itemStack = mutableListOf<ItemStack?>()

        if (data != null) {
            val decode = Base64.getDecoder().decode(data)

            try {
                val compound = CompressedStreamTools.readCompressed(ByteArrayInputStream(decode))
                val list = compound.getTagList("i", Constants.NBT.TAG_COMPOUND)
                for (i in 0 until list.tagCount()) {
                    val tag = list.getCompoundTagAt(i)
                    val item = ItemStack.loadItemStackFromNBT(tag) ?: null

                    itemStack.add(item)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return itemStack
    }
}