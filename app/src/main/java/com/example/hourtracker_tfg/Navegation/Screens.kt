package com.example.hourtracker_tfg.Navegation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Resgistro

@Serializable
data class HourTrackerScreen(val idUsuario: Int) //Le paso el id del usuario