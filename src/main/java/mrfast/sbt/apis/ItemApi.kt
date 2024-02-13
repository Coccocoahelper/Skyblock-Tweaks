package mrfast.sbt.apis

import com.google.gson.JsonObject
import mrfast.sbt.utils.ItemUtils.getSkyblockId
import mrfast.sbt.utils.NetworkUtils
import net.minecraft.item.ItemStack

object ItemApi {
    private var skyblockItems = JsonObject()
    private var skyblockItemPrices = JsonObject()

    var skyblockItemsLoaded = false

    init {
        println("Loading Skyblock Items from HySky API")
        loadSkyblockItems()
    }

    private fun loadSkyblockItems() {
        Thread {
            val items = NetworkUtils.apiRequestAndParse("https://hysky.de/api/items")
            skyblockItems = items
            if (items.entrySet().size > 0) {
                skyblockItemsLoaded = true
                println("Loaded Skyblock Items from HySky API!!")

                println("Loading Lowest Bin Prices from Moulberry NEU API")
                val lowestBins = NetworkUtils.apiRequestAndParse("https://moulberry.codes/lowestbin.json")
                if (lowestBins.entrySet().size > 0) {
                    lowestBins.entrySet().forEach {
                        if (!skyblockItemPrices.has(it.key)) {
                            skyblockItemPrices.add(it.key, JsonObject())
                        }
                        skyblockItemPrices.get(it.key).asJsonObject.addProperty("lowestBin", it.value.asLong)
                    }
                    println("Loaded Lowest Bin Prices from Moulberry NEU API!!")

                    println("Loading Average Bin Prices from Moulberry NEU API")
                    val averageBins =
                        NetworkUtils.apiRequestAndParse("https://moulberry.codes/auction_averages/3day.json")
                    if (averageBins.entrySet().size > 0) {
                        averageBins.entrySet().forEach {
                            if (!skyblockItemPrices.has(it.key)) {
                                skyblockItemPrices.add(it.key, JsonObject())
                            }
                            val item = skyblockItemPrices.get(it.key).asJsonObject
                            it.value.asJsonObject.entrySet().forEach { priceStat ->
                                item.add(priceStat.key, priceStat.value)
                            }
                        }

                        println("Loaded Average Bin Prices from Moulberry NEU API!!")
                    }
                }
            }
        }.start()
    }

    fun getItemPriceInfo(itemId: String): JsonObject? {
        return skyblockItemPrices.get(itemId)?.asJsonObject
    }

    fun getItemInfo(itemId: String): JsonObject? {
        return skyblockItems.get(itemId)?.asJsonObject
    }

    fun getItemInfo(stack: ItemStack): JsonObject? {
        val id = stack.getSkyblockId() ?: return null
        return skyblockItems.get(id).asJsonObject
    }
}