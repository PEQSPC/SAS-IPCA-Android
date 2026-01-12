package com.example.lojasocial.ui.admin.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Scaffold wrapper for admin screens with bottom navigation
 *
 * @param navController Navigation controller for bottom bar
 * @param showBottomBar Whether to show the bottom bar (default: true)
 * @param content Screen content
 */
@Composable
fun AdminScaffold(
    navController: NavController,
    modifier: Modifier = Modifier,
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AdminBottomBar(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                )
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
