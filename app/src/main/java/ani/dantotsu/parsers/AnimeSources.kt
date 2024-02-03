package ani.dantotsu.parsers

import android.content.Context
import ani.dantotsu.Lazier
import ani.dantotsu.lazyList
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefManager.asLiveString
import ani.dantotsu.settings.saving.PrefManager.asLiveStringSet
import ani.dantotsu.settings.saving.PrefName
import eu.kanade.tachiyomi.extension.anime.model.AnimeExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object AnimeSources : WatchSources() {
    override var list: List<Lazier<BaseParser>> = emptyList()
    var pinnedAnimeSources: List<String> = emptyList()

    suspend fun init(fromExtensions: StateFlow<List<AnimeExtension.Installed>>, context: Context) {
        pinnedAnimeSources = PrefManager.getNullableVal<List<String>>(PrefName.AnimeSourcesOrder, null)
            ?: emptyList()

        // Initialize with the first value from StateFlow
        val initialExtensions = fromExtensions.first()
        list = createParsersFromExtensions(initialExtensions) + Lazier(
            { OfflineAnimeParser() },
            "Downloaded"
        )

        // Update as StateFlow emits new values
        fromExtensions.collect { extensions ->
            list = sortPinnedAnimeSources(
                createParsersFromExtensions(extensions),
                pinnedAnimeSources
            ) + Lazier(
                { OfflineAnimeParser() },
                "Downloaded"
            )
        }
    }

    fun performReorderAnimeSources() {
        //remove the downloaded source from the list to avoid duplicates
        list = list.filter { it.name != "Downloaded" }
        list = sortPinnedAnimeSources(list, pinnedAnimeSources) + Lazier(
            { OfflineAnimeParser() },
            "Downloaded"
        )
    }

    private fun createParsersFromExtensions(extensions: List<AnimeExtension.Installed>): List<Lazier<BaseParser>> {
        return extensions.map { extension ->
            val name = extension.name
            Lazier({ DynamicAnimeParser(extension) }, name)
        }
    }

    private fun sortPinnedAnimeSources(
        Sources: List<Lazier<BaseParser>>,
        pinnedAnimeSources: List<String>
    ): List<Lazier<BaseParser>> {
        val pinnedSourcesMap = Sources.filter { pinnedAnimeSources.contains(it.name) }
            .associateBy { it.name }
        val orderedPinnedSources = pinnedAnimeSources.mapNotNull { name ->
            pinnedSourcesMap[name]
        }
        //find the unpinned sources
        val unpinnedSources = Sources.filter { !pinnedAnimeSources.contains(it.name) }
        //put the pinned sources at the top of the list
        return orderedPinnedSources + unpinnedSources
    }
}


object HAnimeSources : WatchSources() {
    private val aList: List<Lazier<BaseParser>> = lazyList(
    )

    override val list = listOf(aList, AnimeSources.list).flatten()
}
