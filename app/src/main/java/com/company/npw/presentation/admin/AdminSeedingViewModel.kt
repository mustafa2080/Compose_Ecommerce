package com.company.npw.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.npw.data.seeder.DatabaseSeeder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminSeedingUiState(
    val isSeeding: Boolean = false,
    val seedingResult: String? = null,
    val seedingError: String? = null
)

@HiltViewModel
class AdminSeedingViewModel @Inject constructor(
    private val databaseSeeder: DatabaseSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSeedingUiState())
    val uiState: StateFlow<AdminSeedingUiState> = _uiState.asStateFlow()

    fun seedDatabase() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSeeding = true,
                seedingResult = null,
                seedingError = null
            )
            
            try {
                databaseSeeder.seedDatabase()
                _uiState.value = _uiState.value.copy(
                    isSeeding = false,
                    seedingResult = "Database seeded successfully! ✅"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSeeding = false,
                    seedingError = "Failed to seed database: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            seedingResult = null,
            seedingError = null
        )
    }
}
