package com.wikapo.widgeti.util

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize

fun CornerBasedShape.flatEnd(): CornerBasedShape {
    return copy(topEnd = CornerSize(0), bottomEnd = CornerSize(0))
}

fun CornerBasedShape.flatStart(): CornerBasedShape {
    return copy(topStart = CornerSize(0), bottomStart = CornerSize(0))
}