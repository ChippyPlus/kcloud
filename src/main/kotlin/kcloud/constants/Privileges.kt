package kcloud.constants



/** Find docs of the privileges at https://github.com/ChippyPlus/kcloud/wiki/privlage */
class Privileges {
    private val mathAny = arrayOf("ALL","MAT")
    private val aiGenerate = arrayOf("ALL","ART")
    private val functionUpload = arrayOf("ALL","FUC","FUU","GEU")
    private val functionDownload = arrayOf("ALL","FUC","FUD","GED")
    private val functionActivate = arrayOf("ALL","FUC","FAC")
    private val functionDeactivate = arrayOf("ALL","FUC","FDA")
    private val cryptKeygen = arrayOf("ALL","CRK","CRA")
    private val cryptDecrypt = arrayOf("ALL","CRD","CRA")
    private val cryptEncrypt = arrayOf("ALL","CRE","CRA")
    private val storageUpload = arrayOf("ALL","STU","STO")
    private val storageDownload = arrayOf("ALL","STD","STO")

    fun getPrivilege(endpoints: Endpoints):Array<String> {
        return when (endpoints) {
            Endpoints.MathAny -> mathAny
            Endpoints.AiGenerate -> aiGenerate
            Endpoints.CryptKeyGen -> cryptKeygen
            Endpoints.CryptEncrypt -> cryptEncrypt
            Endpoints.CryptDecrypt -> cryptDecrypt
            Endpoints.StorageUpload -> storageUpload
            Endpoints.StorageDownload -> storageDownload
            Endpoints.FunctionUpload -> functionUpload
            Endpoints.FunctionDownload -> functionDownload
            Endpoints.FunctionActivate -> functionActivate
            Endpoints.FunctionDeactivate -> functionDeactivate
        }
    }
}


