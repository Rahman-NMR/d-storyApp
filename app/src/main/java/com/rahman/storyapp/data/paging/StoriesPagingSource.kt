package com.rahman.storyapp.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rahman.storyapp.data.remote.api.ApiService
import com.rahman.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.delay

class StoriesPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            Log.e("testData paging", "Loading page: $page with size: ${params.loadSize}")
            val responseData = apiService.getStories(page, params.loadSize).listStory
            Log.e("testData paging", "Loaded stories: ${responseData.size}")

            delay(1234)
            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            Log.e("testData error", "Error load: $exception", exception)
            return LoadResult.Error(exception)
        }
    }
}
