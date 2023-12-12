package me.rail.drawing_clock

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.pow
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
                color = MaterialTheme.colorScheme.background,
            ),
    ) {
        val calendar = Calendar.getInstance()
        val second = calendar.get(Calendar.SECOND).toFloat()
        val minute = calendar.get(Calendar.MINUTE).toFloat() + second / 60
        val hour = calendar.get(Calendar.HOUR).toFloat() + minute / 60
        ClockCircle(
            boxWithConstrainsScope = this,
            hour = hour,
            minute = minute,
            second = second,
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ClockCircle(
    boxWithConstrainsScope: BoxWithConstraintsScope,
    hour: Float,
    minute: Float,
    second: Float,
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
        val innerRadius = (boxWithConstrainsScope.maxWidth.toPx() / 2) * 2 / 3
        val outerRadius = (boxWithConstrainsScope.maxWidth.toPx() / 2) * 2 / 3 + 45.71.dp.toPx()

        drawCircle(
            color = Color.Black,
            radius = outerRadius - 26.67.dp.toPx(),
            center = center,
            style = Stroke(
                width = 26.67.dp.toPx(),
            ),
        )

        drawMinuteLines(
            drawScope = this,
            center = center,
            radius = innerRadius,
        )

        drawHourNumbers(
            drawScope = this,
            center = center,
            radius = innerRadius,
            textMeasurer = textMeasurer,
        )

        drawHand(
            drawScope = this,
            center = center,
            step = 30,
            length = 64.76.dp.toPx(),
            width = 7.dp.toPx(),
            color = Color.Black,
            currentValue = currentHour,
        )

        drawHand(
            drawScope = this,
            center = center,
            step = 6,
            length = 87.62.dp.toPx(),
            width = 7.dp.toPx(),
            color = Color.Black,
            currentValue = currentMinute,
        )

        drawCircle(
            color = Color.Red,
            radius = 4.57.dp.toPx(),
            center = center,
        )

        drawCircle(
            color = Color.Black,
            radius = 6.48.dp.toPx(),
            center = center,
            style = Stroke(
                width = 3.81.dp.toPx(),
            ),
        )

        drawHand(
            drawScope = this,
            center = center,
            step = 6,
            length = 91.43.dp.toPx(),
            width = 3.dp.toPx(),
            color = Color.Red,
            currentValue = currentSecond,
        )

        drawStrapMounts(
            drawScope = this,
            maxWidth = boxWithConstrainsScope.maxWidth.toPx(),
            center = center,
            radius = outerRadius,
        )
    }

    LaunchedEffect(currentSecond) {
        while (true) {
            delay(1_000)
            val previousSecond = currentSecond
            val previousMinute = currentMinute
            currentSecond += 1
            currentMinute = (currentMinute + (currentSecond - previousSecond) / 60)
            currentSecond %= 60
            currentHour = (currentHour + (currentMinute - previousMinute) / 60) % 12
            currentMinute %= 60
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
            lineHeight = 22.86.dp.toPx()
            strokeWidth = 1.dp.toPx()
        } else {
            lineHeight = 15.24.dp.toPx()
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
            strokeWidth = strokeWidth,
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
        val angle = (i - 90f) * (PI / 180)
        val endX = center.x + (radius - 34.29.dp.toPx()) * cos(angle)
        val endY = center.y + (radius - 34.29.dp.toPx()) * sin(angle)
        val number = (i / 30).toString()
        val textSize = textMeasurer.measure(
            text = AnnotatedString(number),
        ).size.toSize()
        drawText(
            textMeasurer = textMeasurer,
            text = number,
            size = textSize,
            style = TextStyle(
                color = Color.Black,
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
    length: Float,
    width: Float,
    color: Color,
    currentValue: Float,
) = with(drawScope) {
    val angle= (((currentValue * step) - 90f) * (PI / 180))
    val startX = center.x
    val startY = center.y
    val endX = center.x + length * cos(angle).toFloat()
    val endY = center.y + length * sin(angle).toFloat()
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = width,
    )
}

fun drawStrapMounts(
    drawScope: DrawScope,
    maxWidth: Float,
    center: Offset,
    radius: Float,
) = with(drawScope) {
    for (i in 1..4) {
        val startXRadiusCoefficient = (-1f).pow(i)
        val yRadiusCoefficient = if (i > 2) {
            1f
        } else {
            -1f
        }
        val xCoefficient = if (i % 2 == 0) {
            3f / 4
        } else {
            1f / 4
        }
        val endXCoefficient = (-1f).pow(i - 1)

        drawPath(
            path = Path().apply {
                val x1 = center.x + startXRadiusCoefficient * (radius - 19.05.dp.toPx()) * cos(45 * PI / 180).toFloat()
                val y1 = center.y + yRadiusCoefficient * (radius - 19.05.dp.toPx()) * sin(45 * PI / 180).toFloat()

                val x2 = xCoefficient * maxWidth
                val y2 = center.y + yRadiusCoefficient * radius

                val x3 = xCoefficient * maxWidth
                val y3 = center.y + yRadiusCoefficient * (radius + 11.43.dp.toPx())

                val x4 = xCoefficient * maxWidth + endXCoefficient * 15.24.dp.toPx()
                val y4 = center.y + yRadiusCoefficient * (radius + 15.24.dp.toPx())

                val x5 = xCoefficient * maxWidth + endXCoefficient * 20.95.dp.toPx()
                val y5 = center.y + yRadiusCoefficient * (radius - 34.29.dp.toPx())

                moveTo(
                    x = x1,
                    y = y1,
                )

                quadraticBezierTo(
                    x1 = x2,
                    y1 = y2,
                    x2 = x3,
                    y2 = y3,
                )

                lineTo(
                    x = x4,
                    y = y4,
                )

                lineTo(
                    x = x5,
                    y = y5,
                )
            },
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    DrawingClockTheme {
        Main()
    }
}