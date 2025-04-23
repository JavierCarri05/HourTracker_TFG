package com.example.hourtracker_tfg.Navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hourtracker_tfg.Inicio.HomeScreen
import com.example.hourtracker_tfg.Login.LoginScreen
import com.example.hourtracker_tfg.Register.RegisterScreen

@Composable
fun NavigationScreens(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login)
    {
        composable<Login> {
            LoginScreen (
                //Al pulsar el boton de register, nos envia al formulario de registro
                navigateToRegister = { navController.navigate(Resgistro) },
                //Al pulsar el boton de login, nos envia al home screen
                navigateToHome = { navController.navigate(Home) }
            )
        }

        composable<Resgistro> {
            RegisterScreen(
                //Al pulsar el boton de Login, nos envia al Login
                 navigateToLogin = { navController.navigate(Login) },
                //Al pulsar el boton de Registrar, nos envia al home screen
                navigateToHome = { navController.navigate(Home) }
            )
        }

        composable<Home> {
            HomeScreen()
        }
    }
}