package com.vishal.facedetection.data.model

import java.io.Serializable

data class FaceDimensions(
    val x: Float,
    val y: Float,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) : Serializable
