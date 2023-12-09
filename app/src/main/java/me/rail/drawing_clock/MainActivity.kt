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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rail.drawing_clock.ui.theme.DrawingClockTheme
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask
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
            .background(MaterialTheme.colorScheme.background),
    ) {
        ClockCircle(
            boxWithConstrainsScope = this,
        )
    }
}

@Composable
fun ClockCircle(boxWithConstrainsScope: BoxWithConstraintsScope) {
    Canvas(
        modifier = Modifier,
    ) {
        val center = Offset(
            x = boxWithConstrainsScope.maxWidth.toPx() / 2,
            y = boxWithConstrainsScope.maxHeight.toPx() / 2,
        )
        val radius = (boxWithConstrainsScope.maxWidth.toPx() / 2) * 2 / 3
        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
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
                        android.graphics.Color.argb(90, 255, 255, 255),
                    )
                }
            )

            drawMinuteLines(
                drawScope = this@Canvas,
                center = center,
                radius = radius,
            )

            drawHourHand(
                drawScope = this@Canvas,
                center = center,
            )

            drawMinuteHand(
                drawScope = this@Canvas,
                center = center,
            )
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

fun drawHourHand(
    drawScope: DrawScope,
    center: Offset,
) = with(drawScope) {
    val hour = (((22 * 30) - 90F) * (PI / 180))
    val startX = center.x
    val startY = center.y
    val endX = center.x + 100 * cos(hour).toFloat()
    val endY = center.y + 100 * sin(hour).toFloat()
    drawLine(
        color = Color(0xFFF454FF),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 3.dp.toPx()
    )
}

fun drawMinuteHand(
    drawScope: DrawScope,
    center: Offset,
) = with(drawScope) {
    val minutesAngle= (((53 * 6) - 90F) * (PI / 180))
    val startX = center.x
    val startY = center.y
    val endX = center.x + 180 * cos(minutesAngle).toFloat()
    val endY = center.y + 180 * sin(minutesAngle).toFloat()
    drawLine(
        color = Color(0xFFF454FF),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 3.dp.toPx()
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    DrawingClockTheme {
        Main()
    }
}