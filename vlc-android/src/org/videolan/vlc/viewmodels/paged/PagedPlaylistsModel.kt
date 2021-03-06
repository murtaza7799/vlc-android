package org.videolan.vlc.viewmodels.paged

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.videolan.medialibrary.Medialibrary
import org.videolan.medialibrary.media.Playlist

class PagedPlaylistsModel(context: Context): MLPagedModel<Playlist>(context), Medialibrary.PlaylistsCb {

    init {
        medialibrary.addPlaylistCb(this)
        if (medialibrary.isStarted) refresh()
    }

    override fun onCleared() {
        medialibrary.removePlaylistCb(this)
        super.onCleared()
    }

    override fun canSortByDuration() = true

    override fun getAll() : Array<Playlist> = medialibrary.getPlaylists(sort, desc)

    override fun getPage(loadSize: Int, startposition: Int)  : Array<Playlist> {
        val list = if (filterQuery == null) medialibrary.getPagedPlaylists(sort, desc, loadSize, startposition)
        else medialibrary.searchPlaylist(filterQuery, sort, desc, loadSize, startposition)
        return list.also { completeHeaders(it, startposition) }
    }

    override fun getTotalCount() = if (filterQuery == null) medialibrary.playlistsCount else medialibrary.getPlaylistsCount(filterQuery)

    override fun onPlaylistsAdded() {
        refresh()
    }

    override fun onPlaylistsModified() {
        refresh()
    }

    override fun onPlaylistsDeleted() {
        refresh()
    }

    class Factory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PagedPlaylistsModel(context.applicationContext) as T
        }
    }
}