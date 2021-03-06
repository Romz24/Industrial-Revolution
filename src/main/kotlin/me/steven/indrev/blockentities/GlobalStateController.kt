package me.steven.indrev.blockentities

import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap
import me.steven.indrev.utils.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World

object GlobalStateController {

    val UPDATE_PACKET_ID = identifier("global_state_update")

    @Environment(EnvType.CLIENT)
    val chunksToUpdate = hashMapOf<ChunkPos, MutableSet<BlockPos>>()
    @Environment(EnvType.CLIENT)
    val workingStateTracker = Long2BooleanOpenHashMap()

    fun update(world: World, pos: BlockPos, workingState: Boolean) {
        val (x, y, z) = pos
        val players = world.server!!.playerManager.playerList
        for (i in players.indices) {
            val player = players[i]
            if (player.world.registryKey === world.registryKey) {
                val xOffset = x - player.x
                val yOffset = y - player.y
                val zOffset = z - player.z
                if (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset < 64 * 64) {
                    val buf = PacketByteBufs.create()
                    buf.writeLong(pos.asLong())
                    buf.writeBoolean(workingState)
                    ServerPlayNetworking.send(player, UPDATE_PACKET_ID, buf)
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun initClient() {
        var ticks = 0
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            ticks++
            if (client?.world != null && ticks % 10 == 0) {
                chunksToUpdate.entries.forEach { (_, positions) ->

                    val minX = positions.minByOrNull { it.x }?.x ?: return@forEach
                    val minY = positions.minByOrNull { it.y }?.y ?: return@forEach
                    val minZ = positions.minByOrNull { it.z }?.z ?: return@forEach
                    val maxX = positions.maxByOrNull { it.x }?.x ?: return@forEach
                    val maxY = positions.maxByOrNull { it.y }?.y ?: return@forEach
                    val maxZ = positions.maxByOrNull { it.z }?.z ?: return@forEach
                    client.worldRenderer?.scheduleBlockRenders(minX, minY, minZ, maxX, maxY, maxZ)
                }
            }
        }
    }
}