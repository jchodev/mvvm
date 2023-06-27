package com.jerry.myapp.base

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.jerry.myapp.R

open class BaseViewModel () : ViewModel() {

    val map: HashMap<Int, Int> = hashMapOf(
        304 to R.string.error_304,
        400 to R.string.error_400,
        401 to R.string.error_401,
        403 to R.string.error_403,
        405 to R.string.error_405,
        415 to R.string.error_415,
        429 to R.string.error_429,
        500 to R.string.error_500
    )

    private val job = SupervisorJob()


    protected val mUiScope = CoroutineScope(Dispatchers.Main + job) //Dispatchers.Main main thread , UI thread
    protected val mIoScope = CoroutineScope(Dispatchers.IO + job)// using for a job / something using long time like http call

    override fun onCleared() {
        super.onCleared()
        this.job.cancel()
    }

    fun returnError(t:Throwable) : Any{
        return if (t is SocketTimeoutException)
            R.string.time_out_error;
        else if (t is UnknownHostException)
            R.string.unknown_host_error;
        else
            t.toString()
    }

    fun returnErrorStatusFromServer(status:Int) : Int{
        return if (map.containsKey(status)) map[status]!! else R.string.unknown_error
    }
}