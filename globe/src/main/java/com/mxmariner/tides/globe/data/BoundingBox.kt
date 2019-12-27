package com.mxmariner.tides.globe.data


interface GeoBox {
    val north: Double
    val south: Double
    val east: Double
    val west: Double
}

val globeBox = object : GeoBox {
    override val north: Double
        get() = 90.0
    override val south: Double
        get() = -90.0
    override val east: Double
        get() = 180.0
    override val west: Double
        get() = -180.0

}

//fun geoBoxFromCorners(corners: Array<out Point3d?>) : GeoBox? {
//    return BoundingBox(corners).takeIf { it.isValid }
//}
//
//private class BoundingBox(
//        private val corners: Array<out Point3d?>
//) : GeoBox {
//    private val box: Array<Double> by lazy {
//        val retVal = arrayOf(-90.0, 90.0, -180.0, 180.0) //n,s,e,w
//        corners.map {
//            it?.toPoint2d()?.toDegrees()
//        }.forEach { point ->
//            point?.takeIf {
//                (it.y <= 90.0 || it.y >= -90) &&
//                (it.x <= 180.0 || it.x >= -180.0)
//            }?.let {
//                retVal[0] = Math.max(it.y, retVal[0]) // north
//                retVal[1] = Math.min(it.y, retVal[1]) // south
//                retVal[2] = Math.max(it.x, retVal[2]) // east
//                retVal[3] = Math.min(it.x, retVal[3]) // west
//            }
//        }
//        retVal
//    }
//
//    val isValid: Boolean by lazy {
//        box[0] > box[1] && box[2] > box[3]
//    }
//
//    override val north: Double
//        get() = box[0]
//
//    override val south: Double
//        get() = box[1]
//
//    override val east: Double
//        get() = box[2]
//
//    override val west: Double
//        get() = box[3]
//}