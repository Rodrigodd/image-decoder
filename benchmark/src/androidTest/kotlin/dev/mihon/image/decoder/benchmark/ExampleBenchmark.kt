package dev.mihon.image.decoder.benchmark

import android.content.Context
import android.graphics.Rect
import tachiyomi.decoder.ImageDecoder
import android.util.Log
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        // Initialize context for asset loading
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun decodePng() {
        benchmarkDecoder("image.png")
    }

    @Test
    fun decodeJpg() {
        benchmarkDecoder("image.jpg")
    }

    @Test
    fun decodeHeif() {
        benchmarkDecoder("image.heif")
    }

    @Test
    fun decodeAvif() {
        benchmarkDecoder("image.avif")
    }

    @Test
    fun decodeJxl() {
        benchmarkDecoder("image.jxl")
    }

    @Test
    fun decodeWebp() {
        benchmarkDecoder("image.webp")
    }

    private fun benchmarkDecoder(imageName: String) {

        Log.i("Benchmark", "benchmarking $imageName")

        // fix seed to make tests deterministic
        val rng = Random(99)
        val decoder = getDecoder(imageName)
        benchmarkRule.measureRepeated {
            randomDecode(decoder, rng)
        }
    }

    private fun getDecoder(imageName: String): ImageDecoder {
        return context.assets.open(imageName).use { stream ->
            return@use ImageDecoder.newInstance(stream)!!
        }
    }

    private fun randomDecode(decoder: ImageDecoder, rng: Random) {
        var randomWidth = (0 until decoder.width).random(rng)
        var randomHeight = (0 until decoder.height).random(rng)

        // increase the sample size until the image have a reasonable size
        var sampleSize = 1
        while (randomWidth > 1000 || randomHeight > 1000) {
            randomWidth /= 2
            randomHeight /= 2
            sampleSize *= 2
        }

        if (randomWidth == 0) randomWidth = 1
        if (randomHeight == 0) randomHeight = 1

        val randomX = (0 until decoder.width - randomWidth).random(rng)
        val randomY = (0 until decoder.height - randomHeight).random(rng)

        val region = Rect(randomX, randomY, randomX + randomWidth, randomY + randomHeight)

        decoder.decode(region, sampleSize)!!
    }
}
