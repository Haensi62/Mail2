package com.example.mail2.data

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

/** Price for a single cupcake */
const val PRICE_PER_CUPCAKE = 2.00

/** Additional cost for same day pickup of an order */
const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

/**
 * [OrderViewModel] holds information about a cupcake order in terms of quantity, flavor, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */


class MailerViewModel : ViewModel() {
        /**
         * Cupcake state for this order
         */
        private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
        val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()


    // Username
        private val _name : MutableLiveData<String> = MutableLiveData("")
        val name: LiveData<String> = _name

        fun onNameChange(newName: String){
            _name.value = newName
        }

    // Password
        private val _passWd : MutableLiveData<String> = MutableLiveData("")
        val passWd: LiveData<String> = _passWd

        fun onPassWdChange(newName: String){
            _passWd.value = newName
        }

        // Username
        private val _name1 : MutableLiveData<String> = MutableLiveData("")
        val name1: LiveData<String> = _name1

        fun onName1Change(newName: String){
            _name1.value = newName
        }

        // Password
        private val _passWd1 : MutableLiveData<String> = MutableLiveData("")
        val passWd1: LiveData<String> = _passWd1

        fun onPassWd1Change(newName: String){
            _passWd1.value = newName
        }


        /**
         * Set the quantity [numberCupcakes] of cupcakes for this order's state and update the price
         */
        fun setUserName(UserName: String) {
            _uiState.update { currentState ->
                currentState.copy(
                    userName = UserName
                )
            }
         }

        fun setPassWord(PassWord: String) {
            _uiState.update { currentState ->
                currentState.copy(
                    passWord = PassWord
                )
            }
        }

        /**
         * Set the quantity [numberCupcakes] of cupcakes for this order's state and update the price
         */
        fun setQuantity(numberCupcakes: Int) {
            _uiState.update { currentState ->
                currentState.copy(
                    quantity = numberCupcakes,
                    price = calculatePrice(quantity = numberCupcakes)
                )
            }
        }

        /**
         * Set the [desiredFlavor] of cupcakes for this order's state.
         * Only 1 flavor can be selected for the whole order.
         */
        fun setFlavor(desiredFlavor: String) {
            _uiState.update { currentState ->
                currentState.copy(flavor = desiredFlavor)
            }
        }

        /**
         * Set the [pickupDate] for this order's state and update the price
         */
        fun setDate(pickupDate: String) {
            _uiState.update { currentState ->
                currentState.copy(
                    date = pickupDate,
                    price = calculatePrice(pickupDate = pickupDate)
                )
            }
        }

        /**
         * Reset the order state
         */
        fun resetOrder() {
            _uiState.value = OrderUiState(pickupOptions = pickupOptions())
        }

        /**
         * Returns the calculated price based on the order details.
         */
        private fun calculatePrice(
            quantity: Int = _uiState.value.quantity,
            pickupDate: String = _uiState.value.date
        ): String {
            var calculatedPrice = quantity * PRICE_PER_CUPCAKE
            // If the user selected the first option (today) for pickup, add the surcharge
            if (pickupOptions()[0] == pickupDate) {
                calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
            }
            val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
            return formattedPrice
        }

        /**
         * Returns a list of date options starting with the current date and the following 3 dates.
         */
        private fun pickupOptions(): List<String> {
            val dateOptions = mutableListOf<String>()
            val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
            val calendar = Calendar.getInstance()
            // add current date and the following 3 dates.
            repeat(4) {
                dateOptions.add(formatter.format(calendar.time))
                calendar.add(Calendar.DATE, 1)
            }
            return dateOptions
        }
    }