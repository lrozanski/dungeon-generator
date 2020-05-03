package com.lrozanski.generator.dungeon

import java.awt.BasicStroke
import java.awt.Color
import java.awt.RenderingHints
import java.awt.Stroke
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class MapVisualizer {

    fun createImage(grid: Grid, scale: Int): BufferedImage {
        val image = BufferedImage(grid.size.w * scale, grid.size.h * scale, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()

        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE))
        graphics.renderingHints.add(RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY))

        grid.forEachCell { cell ->
            val color = when (cell.type) {
                CellType.EMPTY -> Color(109, 104, 117, 255)
                CellType.WALL -> Color(229, 152, 155, 255)
                CellType.FLOOR -> Color(255, 205, 178, 255)
                CellType.DOOR -> Color(109, 104, 117, 255)
            }
            graphics.color = color
            graphics.fillRect(cell.x * scale, cell.y * scale, scale, scale)
        }

        graphics.color = Color(109, 104, 117, 100)
        graphics.stroke = BasicStroke(1f)

        for (x in 0..grid.size.w) {
            graphics.drawLine(x * scale, 0, x * scale, grid.size.h * scale)
        }
        for (y in 0..grid.size.h) {
            graphics.drawLine(0, y * scale, grid.size.w * scale, y * scale)
        }

        ImageIO.write(image, "png", File("src/main/resources/map.png"))
        return image
    }
}
