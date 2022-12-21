package io.github.bruce0203.getitemwithoutdrop

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Plugin : JavaPlugin(), Listener {

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) {
        event.block.setMetadata(BLOCK_NO_DROP, FixedMetadataValue(this, System.currentTimeMillis()))
    }

    @EventHandler
    fun onDropBlockItem(event: BlockDropItemEvent) {
        val blockState = event.blockState
        if (!blockState.hasMetadata(BLOCK_NO_DROP)) return
        blockState.removeMetadata(BLOCK_NO_DROP, this)
        event.isCancelled = true
        event.player.inventory.addItem(blockState.data.toItemStack(1))
    }

    companion object {
        const val BLOCK_NO_DROP = "getItemWithoutDrop-BlockNoDrop"
    }

}