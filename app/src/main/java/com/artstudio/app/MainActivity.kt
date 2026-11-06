
package com.artstudio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset

data class InkStroke(
    val color: Color,
    val width: Float,
    val points: SnapshotStateList<Offset>
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArtStudioApp()
        }
    }
}

@Composable
fun ArtStudioApp() {

    val strokes = remember {
        mutableStateListOf<InkStroke>()
    }

    var brushColor by remember {
        mutableStateOf(Color.Black)
    }

    var brushSize by remember {
        mutableFloatStateOf(8f)
    }

    val colors = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color(0xFF00A86B),
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color.White
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF17191F)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // APP HEADER

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text = "ARTSTUDIO",
                    color = Color.White,
                    fontSize = 21.sp
                )

                Row {
                    TextButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                strokes.removeAt(
                                    strokes.lastIndex
                                )
                            }
                        }
                    ) {
                        Text("UNDO", color = Color.White)
                    }

                    TextButton(
                        onClick = {
                            strokes.clear()
                        }
                    ) {
                        Text("CLEAR", color = Color.White)
                    }
                }
            }

            // DRAWING CANVAS

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .pointerInput(brushColor, brushSize) {

                        detectDragGestures(
                            onDragStart = { position ->

                                val stroke = InkStroke(
                                    color = brushColor,
                                    width = brushSize,
                                    points = mutableStateListOf(
                                        position
                                    )
                                )

                                strokes.add(stroke)
                            },

                            onDrag = { change, _ ->

                                change.consume()

                                strokes.lastOrNull()
                                    ?.points
                                    ?.add(change.position)
                            }
                        )
                    }
            ) {

                strokes.forEach { stroke ->

                    val path = Path()

                    stroke.points.forEachIndexed {
                        index, point ->

                        if (index == 0) {
                            path.moveTo(
                                point.x,
                                point.y
                            )
                        } else {
                            path.lineTo(
                                point.x,
                                point.y
                            )
                        }
                    }

                    if (stroke.points.size == 1) {

                        val point = stroke.points.first()

                        drawCircle(
                            color = stroke.color,
                            radius = stroke.width / 2,
                            center = point
                        )

                    } else {

                        drawPath(
                            path = path,
                            color = stroke.color,
                            style = Stroke(
                                width = stroke.width,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // BRUSH SIZE

            Text(
                text = "BRUSH SIZE: ${brushSize.toInt()}",
                color = Color.White
            )

            Slider(
                value = brushSize,
                onValueChange = {
                    brushSize = it
                },
                valueRange = 1f..50f
            )

            // COLOR PALETTE

            Text(
                text = "COLOR PALETTE",
                color = Color.White,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                colors.forEach { color ->

                    Button(
                        onClick = {
                            brushColor = color
                        },
                        modifier = Modifier.size(35.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color
                        )
                    ) {}
                }
            }

            Text(
                text = "Professional Digital Art Studio",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}
