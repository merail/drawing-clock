package me.rail.drawing_clock

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay
import me.rail.drawing_clock.ui.theme.DrawingClockTheme
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawingClockTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Black,
            ),
    ) {
        val calendar = Calendar.getInstance()
        ClockCircle(
            boxWithConstrainsScope = this,
            hour = calendar.get(Calendar.HOUR),
            minute = calendar.get(Calendar.MINUTE),
            second = calendar.get(Calendar.SECOND),
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ClockCircle(
    boxWithConstrainsScope: BoxWithConstraintsScope,
    hour: Int,
    minute: Int,
    second: Int,
) {
    var currentHour by remember { mutableStateOf(hour) }
    var currentMinute by remember { mutableStateOf(minute) }
    var currentSecond by remember { mutableStateOf(second) }

    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier,
    ) {
        val center = Offset(
            x = boxWithConstrainsScope.maxWidth.toPx() / 2,
            y = boxWithConstrainsScope.maxHeight.toPx() / 2,
        )
        val radius = (boxWithConstrainsScope.maxWidth.toPx() / 2) * 2 / 3
        drawContext.canvas.nativeCanvas.drawCircle(
            center.x,
            center.y,
            radius,
            Paint().apply {
                strokeWidth = 3.dp.toPx()
                color = android.graphics.Color.parseColor("#040E25")
                style = Paint.Style.FILL
                setShadowLayer(
                    150f,
                    0f,
                    0f,
                    android.graphics.Color.argb(
                        90,
                        255,
                        255,
                        255,
                    ),
                )
            }
        )

        drawMinuteLines(
            drawScope = this@Canvas,
            center = center,
            radius = radius,
        )

        drawHourNumbers(
            drawScope = this@Canvas,
            center = center,
            radius = radius,
            textMeasurer = textMeasurer,
        )

        drawHand(
            drawScope = this@Canvas,
            center = center,
            step = 30,
            length = 100,
            width = 3.dp.toPx(),
            currentValue = currentHour,
        )

        drawHand(
            drawScope = this@Canvas,
            center = center,
            step = 6,
            length = 180,
            width = 3.dp.toPx(),
            currentValue = currentMinute,
        )

        drawHand(
            drawScope = this@Canvas,
            center = center,
            step = 6,
            length = 260,
            width = 3.dp.toPx(),
            currentValue = currentSecond,
        )
    }

    LaunchedEffect(currentHour) {
        while (true) {
            delay(3_600_000)
            currentHour = (currentHour + 1) % 12
        }
    }

    LaunchedEffect(currentMinute) {
        while (true) {
            delay(60_000)
            currentMinute = (currentMinute + 1) % 60
        }
    }

    LaunchedEffect(currentSecond) {
        while (true) {
            delay(1_000)
            currentSecond = (currentSecond + 1) % 60
        }
    }
}

fun drawMinuteLines(
    drawScope: DrawScope,
    center: Offset,
    radius: Float,
) = with(drawScope) {
    var lineHeight: Float
    var strokeWidth: Float
    for (i in 0..360 step 6) {
        if (i % 30 == 0) {
            lineHeight = 60f
            strokeWidth = 1.dp.toPx()
        } else {
            lineHeight = 40f
            strokeWidth = Stroke.HairlineWidth
        }
        val angle = i.toFloat() * (PI / 180)
        val startX = center.x + radius * cos(angle)
        val startY = center.y + radius * sin(angle)
        val endX = center.x + (radius - lineHeight) * cos(angle)
        val endY = center.y + (radius - lineHeight) * sin(angle)
        drawLine(
            color = Color.Gray,
            start = Offset(startX.toFloat(), startY.toFloat()),
            end = Offset(endX.toFloat(), endY.toFloat()),
            strokeWidth = strokeWidth
        )
    }
}

@OptIn(ExperimentalTextApi::class)
fun drawHourNumbers(
    drawScope: DrawScope,
    center: Offset,
    radius: Float,
    textMeasurer: TextMeasurer,
) = with(drawScope) {
    for (i in 30..360 step 30) {
        val angle = (i - 90F) * (PI / 180)
        val endX = center.x + (radius - 90) * cos(angle)
        val endY = center.y + (radius - 90) * sin(angle)
        val number = (i / 30).toString()
        val textSize = textMeasurer.measure(
            text = AnnotatedString(number),
        ).size.toSize()
        drawText(
            textMeasurer = textMeasurer,
            text = number,
            size = textSize,
            style = TextStyle(
                color = Color.Red,
            ),
            topLeft = Offset(
                x = endX.toFloat() - textSize.width / 2,
                y = endY.toFloat() - textSize.height / 2,
            ),
        )
    }
}

fun drawHand(
    drawScope: DrawScope,
    center: Offset,
    step: Int,
    length: Int,
    width: Float,
    currentValue: Int,
) = with(drawScope) {
    val secondsAngle= (((currentValue * step) - 90F) * (PI / 180))
    val startX = center.x
    val startY = center.y
    val endX = center.x + length * cos(secondsAngle).toFloat()
    val endY = center.y + length * sin(secondsAngle).toFloat()
    drawLine(
        color = Color(0xFFF454FF),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = width,
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    DrawingClockTheme {
        Main()
    }
}