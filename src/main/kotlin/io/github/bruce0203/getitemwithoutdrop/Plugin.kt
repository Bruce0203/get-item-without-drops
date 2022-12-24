package io.github.bruce0203.getitemwithoutdrop

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Plugin : JavaPlugin(), Listener {

    private val mats by lazy {
        loadConfig()
        config.getStringList("mats").map {
            try {
                Material.valueOf(it)
            } catch(_: Exception) {
                throw AssertionError("$it is not a Material type")
            }
        }.run {
            val arrayList = ArrayList(this)
            config.getStringList("filter")
                .map { filter -> Material.values().filter { it.name.contains(filter) }.toList() }
                .forEach { arrayList.addAll(it) }
            arrayList
        }
    }

    private fun loadConfig() {
        this.config.options().copyDefaults()
        this.saveDefaultConfig()
    }

    override fun onEnable() {
        mats
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!mats.contains(event.block.type)) return
        event.block.setMetadata(BLOCK_NO_DROP, FixedMetadataValue(this, System.currentTimeMillis()))
    }

    @EventHandler
    fun onDropBlockItem(event: BlockDropItemEvent) {
        if (config.isBoolean("force-drop")) {
            val items = event.items
            items.forEach { item ->
                val block = event.block
                val location = block.location
                val world = block.world
                world.dropItemNaturally(location, item.itemStack);
            }
            return
        }
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