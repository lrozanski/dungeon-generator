package com.lrozanski.generator.dungeon.visualization

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.map.AbstractGrid
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.Position
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.stream.ImageOutputStream


data class DebugPoint(val position: Position, val color: Color)

private val debug: MutableSet<DebugPoint> = mutableSetOf()

fun debug(position: Position, color: Color) {
    debug.add(DebugPoint(position, color))
}

object MapVisualizer {

    private val images: MutableList<BufferedImage> = mutableListOf()

    fun clearGifFrames() = images.clear()

    fun createImage(grid: Grid, scale: Int, append: Boolean = true): BufferedImage {
        val (image, graphics) = createGraphics(grid, scale)
        val bgColor = Color(66, 72, 116)
        graphics.color = bgColor
        graphics.fillRect(0, 0, image.width, image.height)

        grid.forEachCell { cell ->
            var color = when (cell.type) {
                CellType.EMPTY -> return@forEachCell
                CellType.WALL -> Color(166, 177, 225)
                CellType.FLOOR -> Color(244, 238, 255)
                CellType.DOOR -> Color(100, 109, 168)
                CellType.STAIRS_UP -> Color(255, 192, 64)
                CellType.STAIRS_DOWN -> Color(255, 100, 102)
                else -> Color.BLACK
            }
            if (cell.secret) {
                if (cell.type == CellType.DOOR) {
                    color = Color(244, 238, 255)
                }
                color = Color(color.red, color.green, color.blue, 128)
            }
            graphics.color = color
            graphics.fillRect(cell.x * scale, cell.y * scale, scale, scale)
        }
        debug.forEach {
            graphics.color = it.color
            graphics.fillRect(it.position.x * scale, it.position.y * scale, scale, scale)
        }
        drawGrid(graphics, grid, scale, bgColor)

        if (append) {
            images += image
        }
        return image
    }

    private fun createGraphics(grid: AbstractGrid<*>, scale: Int): Pair<BufferedImage, Graphics2D> {
        val image = BufferedImage(grid.size.w * scale, grid.size.h * scale, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))

        return Pair(image, graphics)
    }

    private fun drawGrid(graphics: Graphics2D, grid: AbstractGrid<*>, scale: Int, color: Color) {
        graphics.color = Color(color.red, color.green, color.blue, 64)
        graphics.stroke = BasicStroke(1f)

        for (x in 0..grid.size.w) {
            graphics.drawLine(x * scale, 0, x * scale, grid.size.h * scale)
        }
        for (y in 0..grid.size.h) {
            graphics.drawLine(0, y * scale, grid.size.w * scale, y * scale)
        }
    }

    fun createImage(mask: Mask, scale: Int) {
        val (image, graphics) = createGraphics(mask, scale)

        mask.forEachCell { x, y, cell ->
            val color = when (cell) {
                true -> Color(255, 205, 178, 255)
                false -> Color(109, 104, 117, 255)
            }
            graphics.color = color
            graphics.fillRect(x * scale, y * scale, scale, scale)
        }
        drawGrid(graphics, mask, scale, Color(109, 104, 117, 255))

        ImageIO.write(image, "png", File("src/main/resources/corridor_mask.png"))
    }

    fun createGif(outputFile: File, delay: Int = 250) {
        val imageType = images[0].type
        val output: ImageOutputStream = FileImageOutputStream(outputFile)
        val writer = GifSequenceWriter(output, imageType, delay, true)

        try {
            images.forEach { writer.writeToSequence(it) }
        } finally {
            writer.close()
            output.close()
        }
    }
}
