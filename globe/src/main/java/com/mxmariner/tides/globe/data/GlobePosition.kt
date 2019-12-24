package com.mxmariner.tides.globe.data

data class GlobePosition(
        val z: Double,
        val x: Double,
        val y: Double
) {

    companion object {
        fun fromString(string: String?): GlobePosition? {
            return string?.let { str ->
                str.split(",").takeIf {
                    it.size == 3
                }?.let {
                    GlobePosition(it[0].toDoubleOrNull() ?: 0.0,
                            it[1].toDoubleOrNull() ?: 0.0,
                            it[2].toDoubleOrNull() ?: 0.0)
                }
            }
        }
    }

    override fun toString(): String {
        return "$z,$x,$y"
    }
}