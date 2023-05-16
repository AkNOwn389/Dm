package com.aknown389.dm.utils

import java.io.IOException

class ApiExceptions(message:String): IOException(message)

class NoInternetException(message: String): IOException(message)