package com.rahman.storyapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rahman.storyapp.data.database.RemoteKeysEntity
import com.rahman.storyapp.data.database.StoryDatabase
import com.rahman.storyapp.data.database.StoryEntity
import com.rahman.storyapp.data.remote.api.ApiService

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val apiService: ApiService, private val database: StoryDatabase) : RemoteMediator<Int, StoryEntity>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, StoryEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = apiService.getStories(page, state.config.pageSize)
            val endOfPaginationReached = response.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().clearStories()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = response.listStory.map { story ->
                    RemoteKeysEntity(id = story.id ?: "", prevKey = prevKey, nextKey = nextKey)
                }
                val stories = response.listStory.map { story ->
                    StoryEntity(
                        id = story.id ?: "",
                        createdAt = story.createdAt,
                        photoUrl = story.photoUrl,
                        name = story.name,
                        description = story.description,
                        lon = story.lon,
                        lat = story.lat
                    )
                }

                database.remoteKeysDao().insertRemoteKeys(keys)
                database.storyDao().insertStories(stories)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { story ->
                database.remoteKeysDao().getRemoteKeysId(story.id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
            database.remoteKeysDao().getRemoteKeysId(story.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
            database.remoteKeysDao().getRemoteKeysId(story.id)
        }
    }
}