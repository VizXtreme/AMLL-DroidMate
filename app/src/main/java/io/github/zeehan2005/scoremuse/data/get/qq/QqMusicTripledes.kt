package io.github.zeehan2005.scoremuse.data.get.qq

/**
 * QQ 音乐专用 3DES 解密器
 *
 * 精确移植自 Python qqmusic_api/algorithms/tripledes.py
 * 参考: https://github.com/L-1124/QQMusicApi, https://github.com/WXRIW/QQMusicDecoder
 *
 * 加密流程（反向即为解密）:
 *   原始 QRC 文本 → Zlib 压缩 → 3DES 加密（非标准实现）→ Hex 编码
 *
 * 3DES-EDE 24字节密钥结构:
 *   KEY = "!@#)(*\$%123ZXC!@!@#)(NHL" (US-ASCII, 24 字节)
 *   解密: K3[16:24] DECRYPT → K2[8:16] ENCRYPT → K1[0:8] DECRYPT
 */
private object Tripledes {
    const val ENCRYPT = 1
    const val DECRYPT = 0
    val KEY_24 = "!@#)(*\$%123ZXC!@!@#)(NHL".toByteArray(Charsets.US_ASCII)

    private const val DES_BLOCK_SIZE = 8

    // 标准 DES S-box (8个)
    private val sbox = arrayOf(
        intArrayOf(14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7, 0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8, 4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0, 15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13),
        intArrayOf(15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10, 3,13,4,7,15,2,8,15,12,0,1,10,6,9,11,5, 0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15, 13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9),
        intArrayOf(10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8, 13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1, 13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7, 1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12),
        intArrayOf(7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15, 13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9, 10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4, 3,15,0,6,10,10,13,8,9,4,5,11,12,7,2,14),
        intArrayOf(2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9, 14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6, 4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14, 11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3),
        intArrayOf(12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11, 10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8, 9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6, 4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13),
        intArrayOf(4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1, 13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6, 1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2, 6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12),
        intArrayOf(13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7, 1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2, 7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8, 2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11)
    )

    private fun sboxBit(a: Int): Int {
        return (a and 32) or ((a and 31) shr 1) or ((a and 1) shl 4)
    }

    private fun initialPermutation(input: ByteArray): Pair<Int, Int> {
        val v0 = (input[0].toInt() and 0xFF) or ((input[1].toInt() and 0xFF) shl 8) or
                ((input[2].toInt() and 0xFF) shl 16) or ((input[3].toInt() and 0xFF) shl 24)
        val v1 = (input[4].toInt() and 0xFF) or ((input[5].toInt() and 0xFF) shl 8) or
                ((input[6].toInt() and 0xFF) shl 16) or ((input[7].toInt() and 0xFF) shl 24)

        var s0 = 0
        s0 = s0 or (((v1 ushr 6) and 1) shl 31)
        s0 = s0 or (((v1 ushr 14) and 1) shl 30)
        s0 = s0 or (((v1 ushr 22) and 1) shl 29)
        s0 = s0 or (((v1 ushr 30) and 1) shl 28)
        s0 = s0 or (((v0 ushr 6) and 1) shl 27)
        s0 = s0 or (((v0 ushr 14) and 1) shl 26)
        s0 = s0 or (((v0 ushr 22) and 1) shl 25)
        s0 = s0 or (((v0 ushr 30) and 1) shl 24)
        s0 = s0 or (((v1 ushr 4) and 1) shl 23)
        s0 = s0 or (((v1 ushr 12) and 1) shl 22)
        s0 = s0 or (((v1 ushr 20) and 1) shl 21)
        s0 = s0 or (((v1 ushr 28) and 1) shl 20)
        s0 = s0 or (((v0 ushr 4) and 1) shl 19)
        s0 = s0 or (((v0 ushr 12) and 1) shl 18)
        s0 = s0 or (((v0 ushr 20) and 1) shl 17)
        s0 = s0 or (((v0 ushr 28) and 1) shl 16)
        s0 = s0 or (((v1 ushr 2) and 1) shl 15)
        s0 = s0 or (((v1 ushr 10) and 1) shl 14)
        s0 = s0 or (((v1 ushr 18) and 1) shl 13)
        s0 = s0 or (((v1 ushr 26) and 1) shl 12)
        s0 = s0 or (((v0 ushr 2) and 1) shl 11)
        s0 = s0 or (((v0 ushr 10) and 1) shl 10)
        s0 = s0 or (((v0 ushr 18) and 1) shl 9)
        s0 = s0 or (((v0 ushr 26) and 1) shl 8)
        s0 = s0 or ((v1 and 1) shl 7)
        s0 = s0 or (((v1 ushr 8) and 1) shl 6)
        s0 = s0 or (((v1 ushr 16) and 1) shl 5)
        s0 = s0 or (((v1 ushr 24) and 1) shl 4)
        s0 = s0 or ((v0 and 1) shl 3)
        s0 = s0 or (((v0 ushr 8) and 1) shl 2)
        s0 = s0 or (((v0 ushr 16) and 1) shl 1)
        s0 = s0 or ((v0 ushr 24) and 1)

        var s1 = 0
        s1 = s1 or (((v1 ushr 7) and 1) shl 31)
        s1 = s1 or (((v1 ushr 15) and 1) shl 30)
        s1 = s1 or (((v1 ushr 23) and 1) shl 29)
        s1 = s1 or (((v1 ushr 31) and 1) shl 28)
        s1 = s1 or (((v0 ushr 7) and 1) shl 27)
        s1 = s1 or (((v0 ushr 15) and 1) shl 26)
        s1 = s1 or (((v0 ushr 23) and 1) shl 25)
        s1 = s1 or (((v0 ushr 31) and 1) shl 24)
        s1 = s1 or (((v1 ushr 5) and 1) shl 23)
        s1 = s1 or (((v1 ushr 13) and 1) shl 22)
        s1 = s1 or (((v1 ushr 21) and 1) shl 21)
        s1 = s1 or (((v1 ushr 29) and 1) shl 20)
        s1 = s1 or (((v0 ushr 5) and 1) shl 19)
        s1 = s1 or (((v0 ushr 13) and 1) shl 18)
        s1 = s1 or (((v0 ushr 21) and 1) shl 17)
        s1 = s1 or (((v0 ushr 29) and 1) shl 16)
        s1 = s1 or (((v1 ushr 3) and 1) shl 15)
        s1 = s1 or (((v1 ushr 11) and 1) shl 14)
        s1 = s1 or (((v1 ushr 19) and 1) shl 13)
        s1 = s1 or (((v1 ushr 27) and 1) shl 12)
        s1 = s1 or (((v0 ushr 3) and 1) shl 11)
        s1 = s1 or (((v0 ushr 11) and 1) shl 10)
        s1 = s1 or (((v0 ushr 19) and 1) shl 9)
        s1 = s1 or (((v0 ushr 27) and 1) shl 8)
        s1 = s1 or (((v1 ushr 1) and 1) shl 7)
        s1 = s1 or (((v1 ushr 9) and 1) shl 6)
        s1 = s1 or (((v1 ushr 17) and 1) shl 5)
        s1 = s1 or (((v1 ushr 25) and 1) shl 4)
        s1 = s1 or (((v0 ushr 1) and 1) shl 3)
        s1 = s1 or (((v0 ushr 9) and 1) shl 2)
        s1 = s1 or (((v0 ushr 17) and 1) shl 1)
        s1 = s1 or ((v0 ushr 25) and 1)

        return Pair(s0, s1)
    }

    private fun inversePermutation(s0: Int, s1: Int): ByteArray {
        val data = ByteArray(8)
        var tmp: Int
        tmp = 0
        tmp = tmp or (((s1 ushr 24) and 1) shl 7)
        tmp = tmp or (((s0 ushr 24) and 1) shl 6)
        tmp = tmp or (((s1 ushr 16) and 1) shl 5)
        tmp = tmp or (((s0 ushr 16) and 1) shl 4)
        tmp = tmp or (((s1 ushr 8) and 1) shl 3)
        tmp = tmp or (((s0 ushr 8) and 1) shl 2)
        tmp = tmp or ((s1 and 1) shl 1)
        tmp = tmp or (s0 and 1)
        data[3] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 25) and 1) shl 7)
        tmp = tmp or (((s0 ushr 25) and 1) shl 6)
        tmp = tmp or (((s1 ushr 17) and 1) shl 5)
        tmp = tmp or (((s0 ushr 17) and 1) shl 4)
        tmp = tmp or (((s1 ushr 9) and 1) shl 3)
        tmp = tmp or (((s0 ushr 9) and 1) shl 2)
        tmp = tmp or (((s1 ushr 1) and 1) shl 1)
        tmp = tmp or ((s0 ushr 1) and 1)
        data[2] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 26) and 1) shl 7)
        tmp = tmp or (((s0 ushr 26) and 1) shl 6)
        tmp = tmp or (((s1 ushr 18) and 1) shl 5)
        tmp = tmp or (((s0 ushr 18) and 1) shl 4)
        tmp = tmp or (((s1 ushr 10) and 1) shl 3)
        tmp = tmp or (((s0 ushr 10) and 1) shl 2)
        tmp = tmp or (((s1 ushr 2) and 1) shl 1)
        tmp = tmp or ((s0 ushr 2) and 1)
        data[1] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 27) and 1) shl 7)
        tmp = tmp or (((s0 ushr 27) and 1) shl 6)
        tmp = tmp or (((s1 ushr 19) and 1) shl 5)
        tmp = tmp or (((s0 ushr 19) and 1) shl 4)
        tmp = tmp or (((s1 ushr 11) and 1) shl 3)
        tmp = tmp or (((s0 ushr 11) and 1) shl 2)
        tmp = tmp or (((s1 ushr 3) and 1) shl 1)
        tmp = tmp or ((s0 ushr 3) and 1)
        data[0] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 28) and 1) shl 7)
        tmp = tmp or (((s0 ushr 28) and 1) shl 6)
        tmp = tmp or (((s1 ushr 20) and 1) shl 5)
        tmp = tmp or (((s0 ushr 20) and 1) shl 4)
        tmp = tmp or (((s1 ushr 12) and 1) shl 3)
        tmp = tmp or (((s0 ushr 12) and 1) shl 2)
        tmp = tmp or (((s1 ushr 4) and 1) shl 1)
        tmp = tmp or ((s0 ushr 4) and 1)
        data[7] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 29) and 1) shl 7)
        tmp = tmp or (((s0 ushr 29) and 1) shl 6)
        tmp = tmp or (((s1 ushr 21) and 1) shl 5)
        tmp = tmp or (((s0 ushr 21) and 1) shl 4)
        tmp = tmp or (((s1 ushr 13) and 1) shl 3)
        tmp = tmp or (((s0 ushr 13) and 1) shl 2)
        tmp = tmp or (((s1 ushr 5) and 1) shl 1)
        tmp = tmp or ((s0 ushr 5) and 1)
        data[6] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 30) and 1) shl 7)
        tmp = tmp or (((s0 ushr 30) and 1) shl 6)
        tmp = tmp or (((s1 ushr 22) and 1) shl 5)
        tmp = tmp or (((s0 ushr 22) and 1) shl 4)
        tmp = tmp or (((s1 ushr 14) and 1) shl 3)
        tmp = tmp or (((s0 ushr 14) and 1) shl 2)
        tmp = tmp or (((s1 ushr 6) and 1) shl 1)
        tmp = tmp or ((s0 ushr 6) and 1)
        data[5] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 31) and 1) shl 7)
        tmp = tmp or (((s0 ushr 31) and 1) shl 6)
        tmp = tmp or (((s1 ushr 23) and 1) shl 5)
        tmp = tmp or (((s0 ushr 23) and 1) shl 4)
        tmp = tmp or (((s1 ushr 15) and 1) shl 3)
        tmp = tmp or (((s0 ushr 15) and 1) shl 2)
        tmp = tmp or (((s1 ushr 7) and 1) shl 1)
        tmp = tmp or ((s0 ushr 7) and 1)
        data[4] = (tmp and 0xFF).toByte()
        return data
    }

    private fun f(state: Int, key: List<Int>): Int {
        // E 盒扩展（与 Python 实现完全一致）
        var t1 = ((state and 1) shl 31)
        t1 = t1 or ((state and 0xF8000000.toInt()) ushr 1)
        t1 = t1 or ((state and 0x1F800000) ushr 3)
        t1 = t1 or ((state and 0x01F80000) ushr 5)
        t1 = t1 or ((state and 0x001F8000) ushr 7)
        var t2 = ((state and 0x0001F800) shl 15)
        t2 = t2 or ((state and 0x00001F80) shl 13)
        t2 = t2 or ((state and 0x000001F8) shl 11)
        t2 = t2 or ((state and 0x0000001F) shl 9)
        t2 = t2 or ((state and 0x80000000.toInt()) ushr 23)

        val k0 = ((t1 ushr 24) and 0xFF) xor key[0]
        val k1 = ((t1 ushr 16) and 0xFF) xor key[1]
        val k2 = ((t1 ushr 8) and 0xFF) xor key[2]
        val k3 = ((t2 ushr 24) and 0xFF) xor key[3]
        val k4 = ((t2 ushr 16) and 0xFF) xor key[4]
        val k5 = ((t2 ushr 8) and 0xFF) xor key[5]

        val sboxOut = (
            (sbox[0][sboxBit(k0 shr 2)] shl 28) or
            (sbox[1][sboxBit(((k0 and 0x03) shl 4) or (k1 shr 4))] shl 24) or
            (sbox[2][sboxBit(((k1 and 0x0F) shl 2) or (k2 shr 6))] shl 20) or
            (sbox[3][sboxBit(k2 and 0x3F)] shl 16) or
            (sbox[4][sboxBit(k3 shr 2)] shl 12) or
            (sbox[5][sboxBit(((k3 and 0x03) shl 4) or (k4 shr 4))] shl 8) or
            (sbox[6][sboxBit(((k4 and 0x0F) shl 2) or (k5 shr 6))] shl 4) or
            sbox[7][sboxBit(k5 and 0x3F)]
        )

        // P 盒置换（逐项累加避免运算符优先级问题）
        var pOut = 0
        pOut = pOut or (((sboxOut ushr 16) and 1) shl 31)
        pOut = pOut or (((sboxOut ushr 25) and 1) shl 30)
        pOut = pOut or (((sboxOut ushr 12) and 1) shl 29)
        pOut = pOut or (((sboxOut ushr 11) and 1) shl 28)
        pOut = pOut or (((sboxOut ushr 3) and 1) shl 27)
        pOut = pOut or (((sboxOut ushr 20) and 1) shl 26)
        pOut = pOut or (((sboxOut ushr 4) and 1) shl 25)
        pOut = pOut or (((sboxOut ushr 15) and 1) shl 24)
        pOut = pOut or (((sboxOut ushr 31) and 1) shl 23)
        pOut = pOut or (((sboxOut ushr 17) and 1) shl 22)
        pOut = pOut or (((sboxOut ushr 9) and 1) shl 21)
        pOut = pOut or (((sboxOut ushr 6) and 1) shl 20)
        pOut = pOut or (((sboxOut ushr 27) and 1) shl 19)
        pOut = pOut or (((sboxOut ushr 14) and 1) shl 18)
        pOut = pOut or (((sboxOut ushr 1) and 1) shl 17)
        pOut = pOut or (((sboxOut ushr 22) and 1) shl 16)
        pOut = pOut or (((sboxOut ushr 30) and 1) shl 15)
        pOut = pOut or (((sboxOut ushr 24) and 1) shl 14)
        pOut = pOut or (((sboxOut ushr 8) and 1) shl 13)
        pOut = pOut or (((sboxOut ushr 18) and 1) shl 12)
        pOut = pOut or ((sboxOut and 1) shl 11)
        pOut = pOut or (((sboxOut ushr 5) and 1) shl 10)
        pOut = pOut or (((sboxOut ushr 29) and 1) shl 9)
        pOut = pOut or (((sboxOut ushr 23) and 1) shl 8)
        pOut = pOut or (((sboxOut ushr 13) and 1) shl 7)
        pOut = pOut or (((sboxOut ushr 19) and 1) shl 6)
        pOut = pOut or (((sboxOut ushr 2) and 1) shl 5)
        pOut = pOut or (((sboxOut ushr 26) and 1) shl 4)
        pOut = pOut or (((sboxOut ushr 10) and 1) shl 3)
        pOut = pOut or (((sboxOut ushr 21) and 1) shl 2)
        pOut = pOut or (((sboxOut ushr 28) and 1) shl 1)
        pOut = pOut or ((sboxOut ushr 7) and 1)
        return pOut
    }

    private fun cryptBlock(input: ByteArray, key: Array<ByteArray>): ByteArray {
        var (s0, s1) = initialPermutation(input)

        for (idx in 0 until 15) {
            val prevS1 = s1
            val keyList = key[idx].map { it.toInt() and 0xFF }
            s1 = f(s1, keyList) xor s0
            s0 = prevS1
        }
        val lastKey = key[15].map { it.toInt() and 0xFF }
        s0 = f(s1, lastKey) xor s0

        return inversePermutation(s0, s1)
    }

    private fun keySchedule(key: ByteArray, mode: Int): Array<ByteArray> {
        val schedule = Array(16) { ByteArray(6) }
        val keyRndShift = intArrayOf(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)

        val keyPermC = intArrayOf(
            56,48,40,32,24,16,8,0,57,49,41,33,25,17,9,1,
            58,50,42,34,26,18,10,2,59,51,43,35
        )
        val keyPermD = intArrayOf(
            62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,
            60,52,44,36,28,20,12,4,27,19,11,3
        )
        val keyCompression = intArrayOf(
            13,16,10,23,0,4,2,27,14,5,20,9,22,18,11,3,
            25,7,15,6,26,19,12,1,40,51,30,36,46,54,29,39,
            50,44,32,47,43,48,38,55,33,52,45,41,49,35,28,31
        )

        val v0 = (key[0].toInt() and 0xFF) or ((key[1].toInt() and 0xFF) shl 8) or
                ((key[2].toInt() and 0xFF) shl 16) or ((key[3].toInt() and 0xFF) shl 24)
        val v1 = (key[4].toInt() and 0xFF) or ((key[5].toInt() and 0xFF) shl 8) or
                ((key[6].toInt() and 0xFF) shl 16) or ((key[7].toInt() and 0xFF) shl 24)

        var c = 0L
        for (i in keyPermC.indices) {
            val b = keyPermC[i]
            val bit = if (b < 32) ((v0 ushr (31 - b)) and 1).toLong()
                      else ((v1 ushr (63 - b)) and 1).toLong()
            c = c or (bit shl (31 - i))
        }

        var d = 0L
        for (i in keyPermD.indices) {
            val b = keyPermD[i]
            val bit = if (b < 32) ((v0 ushr (31 - b)) and 1).toLong()
                      else ((v1 ushr (63 - b)) and 1).toLong()
            d = d or (bit shl (31 - i))
        }

        for (i in 0 until 16) {
            c = ((c shl keyRndShift[i]) or (c ushr (28 - keyRndShift[i]))) and 0xFFFFFFF0L
            d = ((d shl keyRndShift[i]) or (d ushr (28 - keyRndShift[i]))) and 0xFFFFFFF0L

            val togen = if (mode == DECRYPT) 15 - i else i

            for (j in 0 until 24) {
                val bit = ((c ushr (31 - keyCompression[j])) and 1L).toInt()
                schedule[togen][j / 8] = (schedule[togen][j / 8].toInt() or (bit shl (7 - (j % 8)))).toByte()
            }
            for (j in 24 until 48) {
                val bit = ((d ushr (31 - (keyCompression[j] - 27))) and 1L).toInt()
                schedule[togen][j / 8] = (schedule[togen][j / 8].toInt() or (bit shl (7 - (j % 8)))).toByte()
            }
        }

        return schedule
    }

    fun tripledesKeySetup(key: ByteArray, mode: Int): List<Array<ByteArray>> {
        return if (mode == ENCRYPT) {
            listOf(
                keySchedule(key.copyOfRange(0, 8), ENCRYPT),
                keySchedule(key.copyOfRange(8, 16), DECRYPT),
                keySchedule(key.copyOfRange(16, 24), ENCRYPT)
            )
        } else {
            listOf(
                keySchedule(key.copyOfRange(16, 24), DECRYPT),
                keySchedule(key.copyOfRange(8, 16), ENCRYPT),
                keySchedule(key.copyOfRange(0, 8), DECRYPT)
            )
        }
    }

    fun tripledesCrypt(data: ByteArray, key: List<Array<ByteArray>>): ByteArray {
        var result = data.copyOf()
        for (i in 0 until 3) {
            result = cryptBlock(result, key[i])
        }
        return result
    }
}

/**
 * 使用 QQ 音乐 3DES 算法解密数据
 *
 * @param encryptedBytes 加密的字节数组（必须是 8 字节的倍数）
 * @return 解密后的字节数组
 */
fun decrypt3DesEde(encryptedBytes: ByteArray): ByteArray {
    require(encryptedBytes.size % 8 == 0) {
        "Data length ${encryptedBytes.size} is not a multiple of 8"
    }
    val schedule = Tripledes.tripledesKeySetup(Tripledes.KEY_24, Tripledes.DECRYPT)
    val result = ByteArray(encryptedBytes.size)
    val blockCount = encryptedBytes.size / 8

    for (blockIdx in 0 until blockCount) {
        val offset = blockIdx * 8
        val block = encryptedBytes.copyOfRange(offset, offset + 8)
        val decrypted = Tripledes.tripledesCrypt(block, schedule)
        decrypted.copyInto(result, offset)
    }

    return result
}
