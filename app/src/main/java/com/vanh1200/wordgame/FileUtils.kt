package com.vanh1200.wordgame

import java.io.InputStream

object FileUtils {
    fun readFileAsTextUsingInputStream(inputStream: InputStream) =
        inputStream.readBytes().toString(Charsets.UTF_8)
}

