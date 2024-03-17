package com.ozanyazici.composeflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MyViewModel: ViewModel() {

    // Flow coroutine içinde asenkron çalışıyor. Flow ınt veri tutuyor. emit metodu veriyi yayıyor.
    val countDownTimerFlow = flow<Int> {
        val countDownFrom = 10
        var counter = countDownFrom
        emit(countDownFrom)
        while (counter > 0) {
            delay(1000)
            counter--
            emit(counter)
        }
    }

    init {
        collectInViewModel()
    }

    // Flow u compose dışında burada veya başka yerlerde de kullanabiliriz o da böyle oluyor.
    // Coroutine içinde yaptık çünkü flow un kendisi coroutine içinde asenkron çalışıyor.
    // Bazı yazılımcılar LiveData ile de flow ile de hemen hemen aynı şeyler yapılabiliyor
    // ama flow ile bir tık daha fazla şey yapılabiliyor o yüzden flow kullanın diyebiliyor.
    // Mesela veriyi filtrelemek gibi.
    private fun collectInViewModel() {

        viewModelScope.launch {

            countDownTimerFlow
                .filter {
                    it %3 == 0
                }
                .map {
                    it + it
                }
                .collect{
                println("counter is: ${it}")
            }

            // 2 saniye delay veridiğimiz için (yukarıdaki delay 1 saniye) bir değeri alıp bekliyor
            // o sırada diğer değer geliyor eskisini silip yenisini alıyor ve böyle devam ediyor.
            // Süreler arasındaki farktan dolayı yazdırılan değer en son değer olan 0 oluyor.
            // Delay vermeseydik hepsini yazdıracaktı.
            /*
            countDownTimerFlow.collectLatest {
                delay(2000)
                println("counter is: ${it}")
            }
             */

        }

        /* yukarıdaki gibide yapılabilir böyle de yapılabilir.
        countDownTimerFlow.onEach {
            println(it)
        }.launchIn(viewModelScope)
         */
    }

    // LiveData comparisons

    private val _liveData = MutableLiveData<String>("KotlinLiveData")
    val liveData: LiveData<String> = _liveData

    fun changeLiveDataValue() {
        _liveData.value = "Live Data"
    }

    // Livedata da initial değer vermek zorunlu değil stateFlow da zorunlu.
    // StateFlow un flow dan farkı StateFlow biraz daha LiveData ya benzetilmiştir.
    private val _stateFlow = MutableStateFlow("KotlinStateflow")
    val stateFlow = _stateFlow.asStateFlow()

    fun changeStateFlowValue() {
        _stateFlow.value = "State Flow"
    }

    // SharedFlow ise birden çok tüketici arasında veri paylaşımı sağlar.
    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun changeSharedFlowValue() {
        viewModelScope.launch {
            _sharedFlow.emit("Shared Flow")
        }
    }

}
























