package com.mygdx.game.voxel.reader


object VoxReader {
    /*
    // this is the default palette of voxel colors (the RGBA chunk is only included if the palette is differe) (16 bits, 0RRR RRGG GGGB BBBB)
    private static short[] voxRGBAColors = new short[]{32767, 25599, 19455, 13311, 7167, 1023, 32543, 25375, 19231, 13087, 6943, 799, 32351, 25183,
            19039, 12895, 6751, 607, 32159, 24991, 18847, 12703, 6559, 415, 31967, 24799, 18655, 12511, 6367, 223, 31775, 24607, 18463, 12319, 6175, 31,
            32760, 25592, 19448, 13304, 7160, 1016, 32536, 25368, 19224, 13080, 6936, 792, 32344, 25176, 19032, 12888, 6744, 600, 32152, 24984, 18840,
            12696, 6552, 408, 31960, 24792, 18648, 12504, 6360, 216, 31768, 24600, 18456, 12312, 6168, 24, 32754, 25586, 19442, 13298, 7154, 1010, 32530,
            25362, 19218, 13074, 6930, 786, 32338, 25170, 19026, 12882, 6738, 594, 32146, 24978, 18834, 12690, 6546, 402, 31954, 24786, 18642, 12498, 6354,
            210, 31762, 24594, 18450, 12306, 6162, 18, 32748, 25580, 19436, 13292, 7148, 1004, 32524, 25356, 19212, 13068, 6924, 780, 32332, 25164, 19020,
            12876, 6732, 588, 32140, 24972, 18828, 12684, 6540, 396, 31948, 24780, 18636, 12492, 6348, 204, 31756, 24588, 18444, 12300, 6156, 12, 32742,
            25574, 19430, 13286, 7142, 998, 32518, 25350, 19206, 13062, 6918, 774, 32326, 25158, 19014, 12870, 6726, 582, 32134, 24966, 18822, 12678, 6534,
            390, 31942, 24774, 18630, 12486, 6342, 198, 31750, 24582, 18438, 12294, 6150, 6, 32736, 25568, 19424, 13280, 7136, 992, 32512, 25344, 19200,
            13056, 6912, 768, 32320, 25152, 19008, 12864, 6720, 576, 32128, 24960, 18816, 12672, 6528, 384, 31936, 24768, 18624, 12480, 6336, 192, 31744,
            24576, 18432, 12288, 6144, 28, 26, 22, 20, 16, 14, 10, 8, 4, 2, 896, 832, 704, 640, 512, 448, 320, 256, 128, 64, 28672, 26624, 22528, 20480,
            16384, 14336, 10240, 8192, 4096, 2048, 29596, 27482, 23254, 21140, 16912, 14798, 10570, 8456, 4228, 2114, 1};
    */
    //the default 256 colors palette
    //TODO convert to rgba888 color format?
    private val voxRGBAColors = intArrayOf(0x00000000, 0xffffffff.toInt(), 0xffccffff.toInt(), 0xff99ffff.toInt(), 0xff66ffff.toInt(), 0xff33ffff.toInt(), 0xff00ffff.toInt(), 0xffffccff.toInt(), 0xffccccff.toInt(), 0xff99ccff.toInt(), 0xff66ccff.toInt(), 0xff33ccff.toInt(), 0xff00ccff.toInt(), 0xffff99ff.toInt(), 0xffcc99ff.toInt(), 0xff9999ff.toInt(), 0x6699ffff, 0x3399ffff, 0x0099ffff, 0xff66ffff.toInt(), 0xcc66ffff.toInt(), 0x9966ffff.toInt(), 0x6666ffff, 0x3366ffff, 0x0066ffff, 0xff33ffff.toInt(), 0xcc33ffff.toInt(), 0x9933ffff.toInt(), 0x6633ffff, 0x3333ffff, 0x0033ffff, 0xff00ffff.toInt(), 0xcc00ffff.toInt(), 0x9900ffff.toInt(), 0x6600ffff, 0x3300ffff, 0x0000ffff, 0xffffccff.toInt(), 0xccffccff.toInt(), 0x99ffccff.toInt(), 0x66ffccff, 0x33ffccff, 0x00ffccff, 0xffccccff.toInt(), 0xccccccff.toInt(), 0x99ccccff.toInt(), 0x66ccccff, 0x33ccccff, 0x00ccccff, 0xff99ccff.toInt(), 0xcc99ccff.toInt(), 0x9999ccff.toInt(), 0x6699ccff, 0x3399ccff, 0x0099ccff, 0xff66ccff.toInt(), 0xcc66ccff.toInt(), 0x9966ccff.toInt(), 0x6666ccff, 0x3366ccff, 0x0066ccff, 0xff33ccff.toInt(), 0xcc33ccff.toInt(), 0x9933ccff.toInt(), 0x6633ccff, 0x3333ccff, 0x0033ccff, 0xff00ccff.toInt(), 0xcc00ccff.toInt(), 0x9900ccff.toInt(), 0x6600ccff, 0x3300ccff, 0x0000ccff, 0xffff99ff.toInt(), 0xccff99ff.toInt(), 0x99ff99ff.toInt(), 0x66ff99ff, 0x33ff99ff, 0x00ff99ff, 0xffcc99ff.toInt(), 0xcccc99ff.toInt(), 0x99cc99ff.toInt(), 0x66cc99ff, 0x33cc99ff, 0x00cc99ff, 0xff9999ff.toInt(), 0xcc9999ff.toInt(), 0x999999ff.toInt(), 0x669999ff, 0x339999ff, 0x009999ff, 0xff6699ff.toInt(), 0xcc6699ff.toInt(), 0x996699ff.toInt(), 0x666699ff, 0x336699ff, 0x006699ff, 0xff3399ff.toInt(), 0xcc3399ff.toInt(), 0x993399ff.toInt(), 0x663399ff, 0x333399ff, 0x003399ff, 0xff0099ff.toInt(), 0xcc0099ff.toInt(), 0x990099ff.toInt(), 0x660099ff, 0x330099ff, 0x000099ff, 0xffff66ff.toInt(), 0xccff66ff.toInt(), 0x99ff66ff.toInt(), 0x66ff66ff, 0x33ff66ff, 0x00ff66ff, 0xffcc66ff.toInt(), 0xcccc66ff.toInt(), 0x99cc66ff.toInt(), 0x66cc66ff, 0x33cc66ff, 0x00cc66ff, 0xff9966ff.toInt(), 0xcc9966ff.toInt(), 0x999966ff.toInt(), 0x669966ff, 0x339966ff, 0x009966ff, 0xff6666ff.toInt(), 0xcc6666ff.toInt(), 0x996666ff.toInt(), 0x666666ff, 0x336666ff, 0x006666ff, 0xff3366ff.toInt(), 0xcc3366ff.toInt(), 0x993366ff.toInt(), 0x663366ff, 0x333366ff, 0x003366ff, 0xff0066ff.toInt(), 0xcc0066ff.toInt(), 0x990066ff.toInt(), 0x660066ff, 0x330066ff, 0x000066ff, 0xffff33ff.toInt(), 0xccff33ff.toInt(), 0x99ff33ff.toInt(), 0x66ff33ff, 0x33ff33ff, 0x00ff33ff, 0xffcc33ff.toInt(), 0xcccc33ff.toInt(), 0x99cc33ff.toInt(), 0x66cc33ff, 0x33cc33ff, 0x00cc33ff, 0xff9933ff.toInt(), 0xcc9933ff.toInt(), 0x999933ff.toInt(), 0x669933ff, 0x339933ff, 0x009933ff, 0xff6633ff.toInt(), 0xcc6633ff.toInt(), 0x996633ff.toInt(), 0x666633ff, 0x336633ff, 0x006633ff, 0xff3333ff.toInt(), 0xcc3333ff.toInt(), 0x993333ff.toInt(), 0x663333ff, 0x333333ff, 0x003333ff, 0xff0033ff.toInt(), 0xcc0033ff.toInt(), 0x990033ff.toInt(), 0x660033ff, 0x330033ff, 0x000033ff, 0xffff00ff.toInt(), 0xccff00ff.toInt(), 0x99ff00ff.toInt(), 0x66ff00ff, 0x33ff00ff, 0x00ff00ff, 0xffcc00ff.toInt(), 0xcccc00ff.toInt(), 0x99cc00ff.toInt(), 0x66cc00ff, 0x33cc00ff, 0x00cc00ff, 0xff9900ff.toInt(), 0xcc9900ff.toInt(), 0x999900ff.toInt(), 0x669900ff, 0x339900ff, 0x009900ff, 0xff6600ff.toInt(), 0xcc6600ff.toInt(), 0x996600ff.toInt(), 0x666600ff, 0x336600ff, 0x006600ff, 0xff3300ff.toInt(), 0xcc3300ff.toInt(), 0x993300ff.toInt(), 0x663300ff, 0x333300ff, 0x003300ff, 0xff0000ff.toInt(), 0xcc0000ff.toInt(), 0x990000ff.toInt(), 0x660000ff, 0x330000ff, 0x0000eeff, 0x0000ddff, 0x0000bbff, 0x0000aaff, 0x000088ff, 0x000077ff, 0x000055ff, 0x000044ff, 0x000022ff, 0x000011ff, 0x00ee00ff, 0x00dd00ff, 0x00bb00ff, 0x00aa00ff, 0x008800ff, 0x007700ff, 0x005500ff, 0x004400ff, 0x002200ff, 0x001100ff, 0xee0000ff.toInt(), 0xdd0000ff.toInt(), 0xbb0000ff.toInt(), 0xaa0000ff.toInt(), 0x880000ff.toInt(), 0x770000ff, 0x550000ff, 0x440000ff, 0x220000ff, 0x110000ff, 0xeeeeeeff.toInt(), 0xddddddff.toInt(), 0xbbbbbbff.toInt(), 0xaaaaaaff.toInt(), 0x888888ff.toInt(), 0x777777ff, 0x555555ff, 0x444444ff, 0x222222ff, 0x111111ff)

    /// <summary>
    /// Load a MagicaVoxel .vox format file into the custom ushort[] structure that we use for voxel chunks.
    /// </summary>
    /// <param name="stream">An open BinaryReader stream that is the .vox file.</param>
    /// <param name="overrideColors">Optional color lookup table for converting RGB values into my internal engine color format.</param>
    /// <returns>The voxel chunk data for the MagicaVoxel .vox file. in a buffer of isze(32(x)*128(z)*32(y))
    // </returns>
    fun fromMagica(stream: BinaryReader, defaultRGBAVoxPalette: IntArray?): VoxelData? {
        // check out http://voxel.codeplex.com/wikipage?title=VOX%20Format&referringTitle=Home for the file format used below
        // we're going to return a voxel chunk worth of data
        var rgbacolors: IntArray? = null
        var voxelBuffer: MagicaVoxelDataBuffer? = null

        val magic = stream.readChars(4)
        val version = stream.readInt32()

        // a MagicaVoxel .vox file starts with a 'magic' 4 character 'VOX ' identifier
        if (magic == "VOX ") {
            var sizex = 0
            var sizey = 0
            var sizez = 0
            var subsample: Boolean = false

            while (stream.position < stream.streamLen) {
                // each chunk has an ID, size and child chunks
                val chunkName = stream.readChars(4)
                val chunkSize = stream.readInt32()
                val childChunks = stream.readInt32()

                if (chunkName == "PACK") {
                    val numModels = stream.readInt32() //num of SIZE and XYZI chunks
                    if (numModels != 1) throw IllegalArgumentException("reading vox file with multiple models not supported")
                    stream.skipBytes(chunkSize - 4)
                } else if (chunkName == "SIZE") {
                    sizex = stream.readInt32()
                    sizey = stream.readInt32()
                    sizez = stream.readInt32()

                    if (sizex > 32 || sizey > 32) subsample = true

                    stream.skipBytes(chunkSize - 4 * 3)
                } else if (chunkName == "XYZI") {
                    // XYZI contains n voxels
                    val numVoxels = stream.readInt32()
                    val div = if (subsample) 2 else 1

                    // each voxel has x, y, z and color index values
                    voxelBuffer = MagicaVoxelDataBuffer(numVoxels)
                    for (i in 0..voxelBuffer.nvoxels - 1)
                        voxelBuffer.readVoxel(stream, i, subsample)
                } else if (chunkName == "RGBA") {
                    rgbacolors = IntArray(256)

                    for (i in 0..255) {
                        val r = stream.readByte().toInt() and 0xff
                        val g = stream.readByte().toInt() and 0xff
                        val b = stream.readByte().toInt() and 0xff
                        val a = stream.readByte().toInt() and 0xff

                        // convert RGBA to our custom voxel format (16 bits, 0RRR RRGG GGGB BBBB)
                        //colors[i] = (short) (((r & 0x1f) << 10) | ((g & 0x1f) << 5) | (b & 0x1f));
                        rgbacolors[i] = r shl 24 or (g shl 16) or (b shl 8) or a //rgba888 color format used in libgdx

                    }
                } else
                    stream.skipBytes(chunkSize)// there are only 2 chunks we only care about, and they are SIZE and XYZI
                // read any excess bytes
            }
            if (rgbacolors == null) {
                rgbacolors = defaultRGBAVoxPalette ?: voxRGBAColors
            }
            //VoxelGrid vd= new VoxelGrid(sizex,sizey,sizez,voxelBuffer,colors);
            val vd = VoxelData(sizex, sizey, sizez, voxelBuffer!!, rgbacolors)
            return vd
        }
        return null
    }
}