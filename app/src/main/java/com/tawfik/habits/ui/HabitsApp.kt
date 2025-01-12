package com.tawfik.habits.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tawfik.habits.ui.navigation.HabitsNavigationGraph

@Composable
fun HabitsApp(
    navController: NavHostController = rememberNavController(),
    onDatabaseImport: (Boolean) -> Unit,
    snackbarState: SnackbarHostState
) {
    HabitsNavigationGraph(navController, onDatabaseImport, snackbarState)
}