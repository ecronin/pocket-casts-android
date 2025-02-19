package au.com.shiftyjelly.pocketcasts.podcasts.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsEvent
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsSource
import au.com.shiftyjelly.pocketcasts.analytics.AnalyticsTrackerWrapper
import au.com.shiftyjelly.pocketcasts.analytics.EpisodeAnalytics
import au.com.shiftyjelly.pocketcasts.models.entity.Episode
import au.com.shiftyjelly.pocketcasts.models.entity.Playable
import au.com.shiftyjelly.pocketcasts.repositories.playback.PlaybackManager
import au.com.shiftyjelly.pocketcasts.repositories.podcast.EpisodeManager
import au.com.shiftyjelly.pocketcasts.views.helper.EpisodeItemTouchHelper.SwipeAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

@HiltViewModel
class ProfileEpisodeListViewModel @Inject constructor(
    val episodeManager: EpisodeManager,
    val playbackManager: PlaybackManager,
    private val analyticsTracker: AnalyticsTrackerWrapper,
    private val episodeAnalytics: EpisodeAnalytics,
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    lateinit var episodeList: LiveData<List<Episode>>
    private lateinit var mode: ProfileEpisodeListFragment.Mode

    fun setup(mode: ProfileEpisodeListFragment.Mode) {
        this.mode = mode
        val episodeListFlowable = when (mode) {
            is ProfileEpisodeListFragment.Mode.Downloaded -> episodeManager.observeDownloadEpisodes()
            is ProfileEpisodeListFragment.Mode.Starred -> episodeManager.observeStarredEpisodes()
            is ProfileEpisodeListFragment.Mode.History -> episodeManager.observePlaybackHistoryEpisodes()
        }

        episodeList = episodeListFlowable.toLiveData()
    }

    @Suppress("UNUSED_PARAMETER")
    fun episodeSwiped(episode: Playable, index: Int) {
        if (episode !is Episode) return

        launch {
            val source = getAnalyticsSource()
            if (!episode.isArchived) {
                episodeManager.archive(episode, playbackManager)
                trackSwipeAction(SwipeAction.ARCHIVE)
                episodeAnalytics.trackEvent(AnalyticsEvent.EPISODE_ARCHIVED, source, episode.uuid)
            } else {
                episodeManager.unarchive(episode)
                trackSwipeAction(SwipeAction.UNARCHIVE)
                episodeAnalytics.trackEvent(AnalyticsEvent.EPISODE_UNARCHIVED, source, episode.uuid)
            }
        }
    }

    fun episodeSwipeUpNext(episode: Playable) {
        launch {
            if (playbackManager.upNextQueue.contains(episode.uuid)) {
                playbackManager.removeEpisode(episodeToRemove = episode, source = getAnalyticsSource())
                trackSwipeAction(SwipeAction.UP_NEXT_REMOVE)
            } else {
                playbackManager.playNext(episode = episode, source = getAnalyticsSource())
                trackSwipeAction(SwipeAction.UP_NEXT_ADD_TOP)
            }
        }
    }

    fun episodeSwipeUpLast(episode: Playable) {
        launch {
            if (playbackManager.upNextQueue.contains(episode.uuid)) {
                playbackManager.removeEpisode(episodeToRemove = episode, source = getAnalyticsSource())
                trackSwipeAction(SwipeAction.UP_NEXT_REMOVE)
            } else {
                playbackManager.playLast(episode = episode, source = getAnalyticsSource())
                trackSwipeAction(SwipeAction.UP_NEXT_ADD_BOTTOM)
            }
        }
    }

    fun onArchiveFromHereCount(episode: Episode): Int {
        val episodes = episodeList.value ?: return 0
        val index = max(episodes.indexOf(episode), 0) // -1 on not found
        return episodes.count() - index
    }

    fun clearAllEpisodeHistory() {
        launch {
            analyticsTracker.track(AnalyticsEvent.LISTENING_HISTORY_CLEARED)
            episodeManager.clearAllEpisodeHistory()
        }
    }

    private fun trackSwipeAction(swipeAction: SwipeAction) {
        val source = getAnalyticsSource()
        analyticsTracker.track(
            AnalyticsEvent.EPISODE_SWIPE_ACTION_PERFORMED,
            mapOf(
                ACTION_KEY to swipeAction.analyticsValue,
                SOURCE_KEY to source.analyticsValue
            )
        )
    }

    private fun getAnalyticsSource() = when (mode) {
        ProfileEpisodeListFragment.Mode.Downloaded -> AnalyticsSource.DOWNLOADS
        ProfileEpisodeListFragment.Mode.History -> AnalyticsSource.LISTENING_HISTORY
        ProfileEpisodeListFragment.Mode.Starred -> AnalyticsSource.STARRED
    }

    companion object {
        private const val ACTION_KEY = "action"
        private const val SOURCE_KEY = "source"
    }
}
