package com.application.elevate.model

fun String.matchesRoute(route: String): Boolean {
    return this == route || this.startsWith(route)
}