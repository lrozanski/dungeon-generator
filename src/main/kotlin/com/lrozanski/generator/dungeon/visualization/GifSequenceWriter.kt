package com.lrozanski.generator.dungeon.visualization

import java.awt.image.RenderedImage
import java.io.IOException
import javax.imageio.*
import javax.imageio.metadata.IIOInvalidTreeException
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.ImageOutputStream

/**
 * @see <a href="https://memorynotfound.com/generate-gif-image-java-delay-infinite-loop-example/">GifSequenceWriter</a>
 */
class GifSequenceWriter(out: ImageOutputStream?, imageType: Int, delay: Int, loop: Boolean) {

    private var writer: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    private var params: ImageWriteParam
    private var metadata: IIOMetadata

    @Throws(IIOInvalidTreeException::class)
    private fun configureRootMetadata(delay: Int, loop: Boolean) {
        val metaFormatName = metadata.nativeMetadataFormatName
        val root = metadata.getAsTree(metaFormatName) as IIOMetadataNode

        val graphicsControlExtensionNode = getNode(root, "GraphicControlExtension")
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none")
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE")
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE")
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10))
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0")

        val commentsNode = getNode(root, "CommentExtensions")
        commentsNode.setAttribute("CommentExtension", "Created by: https://memorynotfound.com")

        val appExtensionsNode = getNode(root, "ApplicationExtensions")
        val child = IIOMetadataNode("ApplicationExtension")
        child.setAttribute("applicationID", "NETSCAPE")
        child.setAttribute("authenticationCode", "2.0")

        val loopContinuously = if (loop) 0 else 1
        child.userObject = byteArrayOf(0x1, (loopContinuously and 0xFF).toByte(), (loopContinuously shr 8 and 0xFF).toByte())
        appExtensionsNode.appendChild(child)

        metadata.setFromTree(metaFormatName, root)
    }

    @Throws(IOException::class)
    fun <T : RenderedImage> writeToSequence(img: T?) {
        writer.writeToSequence(IIOImage(img, null, metadata), params)
    }

    @Throws(IOException::class)
    fun close() {
        writer.endWriteSequence()
    }

    companion object {
        private fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
            val nNodes = rootNode.length
            for (i in 0 until nNodes) {
                if (rootNode.item(i).nodeName.equals(nodeName, ignoreCase = true)) {
                    return rootNode.item(i) as IIOMetadataNode
                }
            }
            val node = IIOMetadataNode(nodeName)
            rootNode.appendChild(node)
            return node
        }
    }

    init {
        params = writer.defaultWriteParam
        val imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType)
        metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params)
        configureRootMetadata(delay, loop)
        writer.output = out
        writer.prepareWriteSequence(null)
    }
}
