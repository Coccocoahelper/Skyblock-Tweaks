package mrfast.sbt

import mrfast.sbt.apis.ItemAbilities
import mrfast.sbt.apis.ItemApi
import mrfast.sbt.apis.PlayerStats
import mrfast.sbt.apis.SkyblockMobDetector
import mrfast.sbt.commands.ConfigCommand
import mrfast.sbt.commands.DebugCommand
import mrfast.sbt.commands.GPTCommand
import mrfast.sbt.commands.PathCommand
import mrfast.sbt.config.Config
import mrfast.sbt.config.ConfigGui
import mrfast.sbt.config.GuiManager
import mrfast.sbt.config.categories.DeveloperConfig
import mrfast.sbt.customevents.PacketEvent
import mrfast.sbt.customevents.WorldLoadEvent
import mrfast.sbt.features.general.*
import mrfast.sbt.features.generalProfitTracker.GeneralProfitTracker
import mrfast.sbt.features.mining.PathTracer
import mrfast.sbt.features.statDisplays.RiftTimeBarDisplay
import mrfast.sbt.features.partyfinder.PartyFinderJoinInfo
import mrfast.sbt.features.statDisplays.*
import mrfast.sbt.managers.*
import mrfast.sbt.utils.DevUtils
import mrfast.sbt.utils.LocationUtils
import mrfast.sbt.utils.SocketUtils
import mrfast.sbt.utils.Utils
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

@Mod(modid = SkyblockTweaks.MOD_ID, name = SkyblockTweaks.MOD_NAME)
class SkyblockTweaks {
    companion object {
        const val MOD_ID = "skyblocktweaks"
        const val MOD_NAME = "Skyblock Tweaks"
        var MOD_VERSION = "1.0.0"
        val config = Config()
    }


    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        config.loadConfig()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)

        MinecraftForge.EVENT_BUS.register(PathTracer)

        // Apis
        MinecraftForge.EVENT_BUS.register(ItemApi)
        MinecraftForge.EVENT_BUS.register(SkyblockMobDetector)

        // Utils
        MinecraftForge.EVENT_BUS.register(LocationUtils)
        MinecraftForge.EVENT_BUS.register(SocketUtils)
        MinecraftForge.EVENT_BUS.register(DevUtils)

        // Managers
        MinecraftForge.EVENT_BUS.register(VersionManager)
        MinecraftForge.EVENT_BUS.register(DataManager)
        MinecraftForge.EVENT_BUS.register(PartyManager)
        MinecraftForge.EVENT_BUS.register(PurseManager)
        MinecraftForge.EVENT_BUS.register(InventoryItemManager)
        MinecraftForge.EVENT_BUS.register(SackManager)

        // Stat Displays
        MinecraftForge.EVENT_BUS.register(HealthDisplay)
        MinecraftForge.EVENT_BUS.register(HealthBarDisplay)
        MinecraftForge.EVENT_BUS.register(DefenseDisplay)
        MinecraftForge.EVENT_BUS.register(EffectiveHealthDisplay)
        MinecraftForge.EVENT_BUS.register(ManaDisplay)
        MinecraftForge.EVENT_BUS.register(ManaBarDisplay)
        MinecraftForge.EVENT_BUS.register(SpeedDisplay)
        MinecraftForge.EVENT_BUS.register(OverflowManaDisplay)
        MinecraftForge.EVENT_BUS.register(PartyDisplay)

        MinecraftForge.EVENT_BUS.register(PartyFinderJoinInfo) // Party Finder
        MinecraftForge.EVENT_BUS.register(NewYearsCakeHelper) // Cake bag sorting helper
        MinecraftForge.EVENT_BUS.register(TrashHighlighter) // Trash Highlighter
        MinecraftForge.EVENT_BUS.register(RiftTimeBarDisplay) // Rift Bar

        // Overlays
        MinecraftForge.EVENT_BUS.register(LowHealthTint)
        MinecraftForge.EVENT_BUS.register(ItemPickupLog)
        MinecraftForge.EVENT_BUS.register(QuiverOverlay) // Quiver Overlay
        MinecraftForge.EVENT_BUS.register(GeneralProfitTracker)

        // Stop above hotbar elements from rendering
        MinecraftForge.EVENT_BUS.register(HideHotbarElements)

        // Gui
        MinecraftForge.EVENT_BUS.register(GuiManager)

        // Api's
        MinecraftForge.EVENT_BUS.register(PlayerStats)
        MinecraftForge.EVENT_BUS.register(ItemAbilities)

        // Commands
        ClientCommandHandler.instance.registerCommand(ConfigCommand())
        ClientCommandHandler.instance.registerCommand(DebugCommand())
        ClientCommandHandler.instance.registerCommand(GPTCommand())
        ClientCommandHandler.instance.registerCommand(PathCommand())

        // Checks mod folder for version of Skyblock Features your using
        val modList = Loader.instance().modList
        for (mod in modList) {
            if (mod.modId == MOD_ID) {
                MOD_VERSION = mod.displayVersion
                break
            }
        }
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent?) {
        config.saveConfig()
        GuiManager.loadGuiElements()
        GuiManager.saveGuiElements()
    }

    // Stop the ESC key from closing config when keybind listening
    @SubscribeEvent
    fun onGuiKeyEvent(event: KeyboardInputEvent.Pre) {
        if (ConfigGui.listeningForKeybind && Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
            event.setCanceled(true)
        }
    }

    // Sends custom event for the world loading, only once, as WorldEvent.Load is called twice
    private var alreadySent = false
    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        if(alreadySent) return
        alreadySent = true

        MinecraftForge.EVENT_BUS.post(WorldLoadEvent())

        Utils.setTimeout({
            alreadySent = false
        },2000)
    }
}
