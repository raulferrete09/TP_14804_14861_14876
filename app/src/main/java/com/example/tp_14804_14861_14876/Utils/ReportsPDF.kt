package com.example.tp_14804_14861_14876.Utils

class ReportsPDF {
    internal var name: String? = null
    private var url: String? = null

    constructor() {}
    constructor(name: String?, url: String?, report:String?) {
        this.name = name
        this.url = url
    }

    fun getName(): String? {
        return name
    }

    fun getUrl(): String? {
        return url
    }
}