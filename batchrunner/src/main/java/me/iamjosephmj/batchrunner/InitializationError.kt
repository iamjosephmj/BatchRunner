package me.iamjosephmj.batchrunner

class InitializationError(private val fErrors: List<Throwable?>? = null) : Exception() {
    val serialVersionUID = 1L

    constructor(vararg errors: Throwable?) : this(listOf(*errors))

    constructor(string: String?) : this(Exception(string))

    fun getCauses(): List<Throwable?>? {
        return fErrors
    }
}
