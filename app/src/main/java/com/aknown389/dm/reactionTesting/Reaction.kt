package com.aknown389.dm.reactionTesting
import java.util.Arrays

/*
* MIT License
*
* Copyright (c) 2018 AmrDeveloper (Amr Hesham)
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
/**
 * Reaction model class used to store information for each reaction
 */
class Reaction {
    val reactText: String?
    val reactType: String?
    val reactIconId: Int

    /**
     * This Constructor for default state because React type not equal react Text
     * for example in library default state text is 'like' but type is 'default'
     */
    constructor(reactText: String?, reactType: String?, reactTextColor: String?, reactIconId: Int) {
        this.reactText = reactText
        this.reactType = reactType
        this.reactIconId = reactIconId
    }

    /**
     * Constructor for all Reaction that text is equal type
     * for example in like state text is 'like' and type is 'like' also
     */
    constructor(reactText: String?, reactTextColor: String?, reactIconId: Int) {
        this.reactText = reactText
        reactType = reactText
        this.reactIconId = reactIconId
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(
            intArrayOf(
                reactIconId,
                reactText.hashCode(),
                reactType.hashCode()
            )
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reaction

        if (reactText != other.reactText) return false
        if (reactType != other.reactType) return false
        if (reactIconId != other.reactIconId) return false

        return true
    }
}