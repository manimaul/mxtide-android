package com.mxmariner.globe.util

import android.content.Context
import com.mousebird.maply.VectorInfo
import com.mousebird.maply.VectorObject
import com.mxmariner.tides.R
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ShapeFileDao @Inject constructor(
    private val ctx: Context
) {

    private fun shapeFile(base_name: String): Single<File> {
        return Single.create<File> { emitter ->
            emitter.onSuccess(arrayOf("shp", "dbf", "prj", "shx").map {
                val f = File(ctx.filesDir, "$base_name.$it")
                if (!f.exists()) {
                    ctx.assets.open("$base_name.$it").use { iStream ->
                        FileOutputStream(f).use { os ->
                            val buffer = ByteArray(1024)
                            var length: Int = iStream.read(buffer)
                            while (length > 0) {
                                os.write(buffer, 0, length)
                                length = iStream.read(buffer)
                            }
                            os.flush()
                        }
                    }
                }
                f
            }.first())
        }.subscribeOn(Schedulers.io())

    }

    fun graticules(): Single<Pair<VectorObject, VectorInfo>> {
        return shapeFile("ne_110m_graticules_10").map {
            it.absolutePath
        }.map { absPath ->
            VectorObject().apply {
                fromShapeFile(absPath)
            } to VectorInfo().apply {
                setColor(ctx.resources.getColor(R.color.colorPrimaryDark, null))
                setLineWidth(1f)
                setFilled(false)
            }
        }
    }

    fun land(): Single<Pair<VectorObject, VectorInfo>> {
        return shapeFile("ne_10m_coastline").map {
            it.absolutePath
        }.map { absPath ->
            VectorObject().apply {
                fromShapeFile(absPath)
            } to VectorInfo().apply {
                setColor(ctx.resources.getColor(R.color.colorPrimaryDark, null))
                setLineWidth(4f)
                setFilled(false)
            }
        }
    }
}