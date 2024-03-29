package com.jerry.myapp.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.gson.internal.LinkedTreeMap
import com.jerry.myapp.R
import com.jerry.myapp.base.BaseViewModel
import com.jerry.myapp.base.ViewState
import com.jerry.myapp.constants.CREATE_USER_FAIL
import com.jerry.myapp.constants.CREATE_USER_OK
import com.jerry.myapp.constants.DELETE_USER_OK
import com.jerry.myapp.constants.DELETE_USER_FAIL
import com.jerry.myapp.model.*
import com.jerry.myapp.responsitory.NetworkRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToLong


@HiltViewModel
class UserListViewModel
    @Inject constructor(private val mNetworkRepository : NetworkRepository) : BaseViewModel(){

    private val _getUserListViewState = MutableStateFlow<ViewState<UserListResponse>>(ViewState.Normal)
    val getUserListState = _getUserListViewState.asStateFlow()

    private val _createUserViewState = MutableStateFlow<ViewState<CreateUserResponse>>(ViewState.Normal)
    val createUserState = _createUserViewState.asStateFlow()

    private val _deleteUserViewState = MutableStateFlow<ViewState<DeleteUserResponse>>(ViewState.Normal)
    val deleteUserState = _deleteUserViewState.asStateFlow()

    private val _userListViewState = MutableStateFlow<ViewState<List<User>>>(ViewState.Normal)
    val userListState = _userListViewState.asStateFlow()

    private var _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private var meta : Meta? = null
    private var userList : List<User> = emptyList()

    init {
        getUserList(1)
    }

    fun refresh(){
        getUserList(1)
    }

    fun getNextPage(){
        if (meta!=null && meta?.pagination!=null){
            if (meta!!.pagination!!.page < meta!!.pagination!!.pages){
                getUserList(meta!!.pagination!!.page + 1)
            }
        }
        else
            getUserList(1)
    }

    fun getUserList(page : Int){
        mUiScope.launch {
            _getUserListViewState.value = ViewState.Loading
            try {
                val data = mIoScope.async {
                    return@async mNetworkRepository.getUserList(page)
                }.await()
                if (data.code==200) {
                    meta = data.meta
                    if (page==1){
                        userList = emptyList()
                        _userListViewState.value = ViewState.Clean
                        delay(500)
                    }
                    userList = userList.plus(data.data)
                    _userListViewState.value = ViewState.Success(userList)
                    _getUserListViewState.value = ViewState.Success(data)
                }
                else {
                    _getUserListViewState.value = ViewState.Failure(returnErrorStatusFromServer(data.code))
                }
            } catch (e: Exception) {
                _getUserListViewState.value = ViewState.Failure(returnError(e))
            }
        }
    }

    fun createUser(name:String, email: String){
        mUiScope.launch {
            _createUserViewState.value = ViewState.Loading
            try {
                val newUser = User(
                    name=name,
                    email=email
                )
                _user.value = newUser
                val data = mIoScope.async {
                    return@async mNetworkRepository.createUser(newUser)
                }.await()
                if (data?.code != null && (data?.code==CREATE_USER_OK || data?.code==CREATE_USER_FAIL)) {
                    if (data.code == CREATE_USER_OK) {//success
                        val myList = userList.toMutableList()

                        data.data.let {
                            _userListViewState.value = ViewState.Clean

                            val t: LinkedTreeMap<Any, Any> = data.data as LinkedTreeMap<Any, Any>
                            newUser.id = (t.get("id") as Double).roundToLong()
                            myList.add(0, newUser)
                            userList = myList.toList()

                            _userListViewState.value = ViewState.Success(userList)
                        }
                        _createUserViewState.value = ViewState.Success(data)
                        clearUser()
                    } else if (data.code == CREATE_USER_FAIL) {//something wrong of input
                        var list: ArrayList<Any> = data.data as ArrayList<Any>
                        val builder = StringBuilder()
                        var idx = 0
                        list.forEach {
                            val t: LinkedTreeMap<Any, Any> = it as LinkedTreeMap<Any, Any>
                            if (idx > 0) {
                                builder.append("\n")
                            }
                            builder.append(t.get("field") as String)
                                .append(" : ")
                                .append(t.get("message") as String)
                            idx++
                        }
                        _createUserViewState.value = ViewState.Failure(builder.toString())
                        builder.clear()
                    }
                }
                else if (data?.code != null){
                    _createUserViewState.value = ViewState.Failure(returnErrorStatusFromServer(data.code))
                } else
                    _createUserViewState.value = ViewState.Failure(R.string.unknown_error)

            } catch (e: Exception) {
                _createUserViewState.value = ViewState.Failure(returnError(e))
            }
        }
    }

    fun deleteUser(user: User){
        mUiScope.launch {
            _deleteUserViewState.value = ViewState.Loading
            try {
                val data = mIoScope.async {
                    return@async mNetworkRepository.deleteUser(user.id)
                }.await()
                if (data.code ==DELETE_USER_OK || data.code ==DELETE_USER_FAIL) {
                    if (data.data!=null)
                        _deleteUserViewState.value = ViewState.Failure(data.data!!.message)
                    else {
                        _deleteUserViewState.value = ViewState.Success(data)
                        val myList =  userList.toMutableList()
                        myList.let{
                            myList.remove(user)
                            userList = myList.toList()
                        }
                        _userListViewState.value = ViewState.Success(userList)
                    }
                }
                else if (data?.code != null){
                    _deleteUserViewState.value = ViewState.Failure(returnErrorStatusFromServer(data.code))
                } else
                    _deleteUserViewState.value = ViewState.Failure(R.string.unknown_error)
            } catch (e: Exception) {
                _deleteUserViewState.value = ViewState.Failure(returnError(e))
            }
        }
    }

    fun clearUser(){
        _user.postValue(User(name="",email=""))
    }
}

