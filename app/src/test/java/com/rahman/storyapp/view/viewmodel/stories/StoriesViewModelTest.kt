package com.rahman.storyapp.view.viewmodel.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.rahman.storyapp.data.database.StoryEntity
import com.rahman.storyapp.data.repository.UserRepository
import com.rahman.storyapp.view.ui.stories.adapterview.AdapterStory
import com.rahman.storyapp.view.viewmodel.DataDummy
import com.rahman.storyapp.view.viewmodel.MainDispatcherRule
import com.rahman.storyapp.view.viewmodel.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: UserRepository

    @Test
    fun `when getStories should not null and return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoriesResponse()
        val data: PagingData<StoryEntity> = StoriesPagingSource.snapshot(dummyStories)
        val liveData = MutableLiveData<PagingData<StoryEntity>>()
        liveData.value = data

        Mockito.`when`(repository.getStories()).thenReturn(liveData)

        val storiesViewModel = StoriesViewModel(repository)
        val actualStories: PagingData<StoryEntity> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = AdapterStory.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when getStories empty should return no data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())

        val liveData = MutableLiveData<PagingData<StoryEntity>>()
        liveData.value = data
        Mockito.`when`(repository.getStories()).thenReturn(liveData)

        val storiesViewModel = StoriesViewModel(repository)
        val actualStories: PagingData<StoryEntity> = storiesViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = AdapterStory.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    class StoriesPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
        companion object {
            fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}