package com.johnriggsdev.inspiringapps

import com.johnriggsdev.inspiringapps.TestData.Companion.EMPTY_STRING
import com.johnriggsdev.inspiringapps.TestData.Companion.SOME_ERROR_STRING
import com.johnriggsdev.inspiringapps.TestData.Companion.TEST_PROGRESS_10
import com.johnriggsdev.inspiringapps.TestData.Companion.TEST_PROGRESS_100
import com.johnriggsdev.inspiringapps.TestData.Companion.TEST_SEQUENCES_LIST
import com.johnriggsdev.inspiringapps.ui.main.MainActivity
import com.johnriggsdev.inspiringapps.ui.main.MainPresenter
import com.johnriggsdev.inspiringapps.ui.main.MainRepository
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_READ
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.TASK_SORT
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class MainPresenterTest{

    private lateinit var mockRepo : MainRepository
    private lateinit var mockView : MainActivity
    private lateinit var presenter : MainPresenter

    @Before
    fun setup(){
        mockRepo = mock()
        mockView = mock()
        presenter = MainPresenter(mockRepo)

        presenter.attach(mockView)
    }

    @Test
    fun `attaching view sets view`(){
        assert(presenter.getView() != null)
    }

    @Test
    fun `attaching view sets callback in repo`(){
        verify(mockRepo).setNetworkCallback(presenter)
    }

    @Test
    fun `attaching view calls fetch log files in repo`(){
        verify(mockRepo).fetchLogFileFromApi()
    }

    @Test
    fun `attaching view initializes views`(){
        verify(mockView).showProgressBar(true)
    }

    @Test
    fun `onDestroy nullifies view reference`(){
        assert(presenter.getView() != null)
        presenter.onDestroy()
        assert(presenter.getView() == null)
    }

    @Test
    fun `view instructed to show toast on api error`(){
        presenter.onApiError(SOME_ERROR_STRING)
        verify(mockView).showErrorToast(SOME_ERROR_STRING)
    }

    @Test
    fun `onUpdateProgress calls updates view progress test`(){
        presenter.onUpdateProgress(TASK_READ, TEST_PROGRESS_10)
        verify(mockView).updateProgressText("$TASK_READ: $TEST_PROGRESS_10%")

        presenter.onUpdateProgress(TASK_READ, TEST_PROGRESS_100)
        verify(mockView).updateProgressText("$TASK_READ: $TEST_PROGRESS_100%")

        presenter.onUpdateProgress(TASK_SORT, TEST_PROGRESS_100)
        verify(mockView).updateProgressText(EMPTY_STRING)
    }

    @Test
    fun `failures show error toasts`(){
        Mockito.`when`(mockRepo.getString(anyInt())).thenReturn(EMPTY_STRING)

        presenter.onFileDownloadFailure()
        verify(mockView).showErrorToast(EMPTY_STRING)

        presenter.onFileReadFailure()
        verify(mockView, times(2)).showErrorToast(EMPTY_STRING)
    }

    @Test
    fun `onLogsApiSuccess calls appropriate view methods`(){
        presenter.onLogsApiSuccess(TEST_SEQUENCES_LIST)
        verify(mockView).setupRecyclerView(TEST_SEQUENCES_LIST)
        verify(mockView).showProgressBar(false)
    }
}