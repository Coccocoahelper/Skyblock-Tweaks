package mrfast.sbt.config.categories

import mrfast.sbt.config.Config
import mrfast.sbt.config.ConfigProperty
import mrfast.sbt.config.ConfigType
import java.awt.Color

object RiftConfig : Config() {

    @ConfigProperty(
        type = ConfigType.TOGGLE,
        name = "Rift Time Bar",
        description = "Moveable Rift Time Bar that adjusts depending on your time left in the rift / damage taken",
        category = "Rift",
        subcategory = "Stat Displays",
        isParent = true
    )
    var riftTimeBar = false

    @ConfigProperty(
        type = ConfigType.COLOR,
        name = "Rift Fill Color",
        description = "",
        category = "Rift",
        subcategory = "Stat Displays",
        parentName = "Rift Time Bar"
    )
    var riftBarFillColor = Color(0x5A0075FF)

    @ConfigProperty(
        type = ConfigType.COLOR,
        name = "Rift Bar Background",
        description = "",
        category = "Rift",
        subcategory = "Stat Displays",
        parentName = "Rift Time Bar"
    )
    var riftBarBarColor = Color.BLACK

//    @ConfigProperty(
//            type = ConfigType.TOGGLE,
//            name = "Larva Silk Display",
//            description = "Highlights where the line will be drawn when using Larva Silk.",
//            category = "The Rift",
//            subcategory = "General",
//            isParent = true
//    )
//    var larvaSilkDisplay = true
//
//    @ConfigProperty(
//            type = ConfigType.COLOR,
//            name = "Larva Silk Block Color",
//            description = "",
//            category = "The Rift",
//            subcategory = "General",
//            parentName = "Larva Silk Display"
//    )
//    var larvaSilkBlockColor: Color = Color.ORANGE
//
//    @ConfigProperty(
//            type = ConfigType.COLOR,
//            name = "Larva Silk Line Color",
//            description = "",
//            category = "The Rift",
//            subcategory = "General",
//            parentName = "Larva Silk Display"
//    )
//    var larvaSilkLineColor: Color = Color.CYAN
}