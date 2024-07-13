package kcloud.constants


/** Find docs of the privileges at https://github.com/ChippyPlus/kcloud/wiki/privlage */
class Privileges {
    val mathAny = arrayOf("ALL", "MAT")
    val aiGenerate = arrayOf("ALL", "ART")
    val functionUpload = arrayOf("ALL", "FUC", "FUU", "GEU")
    val functionDownload = arrayOf("ALL", "FUC", "FUD", "GED")
    val functionActivate = arrayOf("ALL", "FUC", "FAC")
    val functionDeactivate = arrayOf("ALL", "FUC", "FDA")
    val cryptKeygen = arrayOf("ALL", "CRK", "CRA")
    val cryptDecrypt = arrayOf("ALL", "CRD", "CRA")
    val cryptEncrypt = arrayOf("ALL", "CRE", "CRA")
    val storageUpload = arrayOf("ALL", "STU", "STO", "GEU")
    val storageDownload = arrayOf("ALL", "STD", "STO", "GED")
    val timeGet = arrayOf("ALL", "TGC", "TAC")
    val timeReset = arrayOf("ALL", "TRE", "TAC")
    val timeIncrement = arrayOf("ALL", "TIR", "TAC")
    val timeDecrement = arrayOf("ALL", "TDR", "TAC")
    val timeSet = arrayOf("ALL", "TSE", "TAC")

}

fun Privileges.getPrivilege(endpoints: Endpoints): Array<String> {
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
        Endpoints.TimeGet -> timeGet
        Endpoints.TimeReset -> timeReset
        Endpoints.TimeIncrement -> timeIncrement
        Endpoints.TimeDeterment -> timeDecrement
        Endpoints.TimeSet -> timeSet
    }
}


