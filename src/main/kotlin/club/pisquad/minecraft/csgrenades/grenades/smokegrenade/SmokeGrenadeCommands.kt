package club.pisquad.minecraft.csgrenades.grenades.smokegrenade

import club.pisquad.minecraft.csgrenades.CounterStrikeGrenades
import club.pisquad.minecraft.csgrenades.GrenadeType
import club.pisquad.minecraft.csgrenades.command.GrenadeCommandBuilder
import club.pisquad.minecraft.csgrenades.grenades.smokegrenade.debug.SmokeGrenadeDebugOptions
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object SmokeGrenadeCommands : GrenadeCommandBuilder {
    override fun build(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal(CounterStrikeGrenades.ID).then(
                Commands.literal(GrenadeType.SMOKE_GRENADE.resourceKey)
                    .then(
                        Commands.literal("debug").then(
                            Commands.literal("showVoxelOutline").then(
                                Commands.argument("state", BoolArgumentType.bool()).executes { context ->
                                    SmokeGrenadeDebugOptions.Outline.showOutline =
                                        BoolArgumentType.getBool(context, "state")
                                    0
                                }
                            )
                        )
                    )
            )
        )
    }
}