package kcloud.constants


class Privileges {
    val perMath = "MAT"
    val perAi = "ARI"
    val perFunction = "FUC"
    val perFunctionDownload = "FUD"
    val perFunctionActivate = "FAC"
    val perFunctionDeactivate = "FDA"
    val perFunctionUpload = "FUU"
    val perFunctionAll = "FUA"
    val perStorage = "STO"
    val perStorageUpload = "STU"
    val perStorageDownload = "STD"
    val perStorageAll = "STA"
    val perCryptKeyCreate = "CRK"
    val perCryptEncrypt = "CRE"
    val perCryptDecrypt = "CRD"
    val perCryptAll = "CRA"
    val perGenericUpload = "GEU"
    val perGenericDownload = "GED"
    val perTimeGet = "TGC"
    val perTimeReset = "TRE"
    val perTimeIncrement = "TIR"
    val perTimeDecrement = "TDR"
    val perTimeSet = "TSE"
    val perTimeAll = "TAC"
    val perEverything = "ALL"
}


/** Find docs of the privileges at https://github.com/ChippyPlus/kcloud/wiki/privlage */
class PrivilegesInEndpointContext {
    private val permissions = Privileges()

    val mathAny = arrayOf(permissions.perEverything, permissions.perMath)
    val aiGenerate = arrayOf(permissions.perEverything, permissions.perAi)
    val functionUpload = arrayOf(
        permissions.perEverything, permissions.perFunction, permissions.perFunctionAll, permissions.perFunctionUpload,
        permissions.perGenericUpload
    )
    val functionDownload = arrayOf(
        permissions.perEverything, permissions.perFunction, permissions.perFunctionAll, permissions.perFunctionDownload,
        permissions.perGenericDownload
    )
    val functionActivate = arrayOf(
        permissions.perEverything, permissions.perFunction, permissions.perFunctionAll, permissions.perFunctionActivate
    )
    val functionDeactivate = arrayOf(
        permissions.perEverything, permissions.perFunction, permissions.perFunctionAll,
        permissions.perFunctionDeactivate
    )
    val cryptKeygen = arrayOf(permissions.perEverything, permissions.perCryptKeyCreate, permissions.perCryptAll)
    val cryptDecrypt = arrayOf(permissions.perEverything, permissions.perCryptDecrypt, permissions.perCryptAll)
    val cryptEncrypt = arrayOf(permissions.perEverything, permissions.perCryptEncrypt, permissions.perCryptAll)
    val storageUpload = arrayOf(
        permissions.perEverything, permissions.perStorageAll, permissions.perStorageUpload, permissions.perStorage,
        permissions.perGenericUpload
    )
    val storageDownload = arrayOf(
        permissions.perEverything, permissions.perStorageAll, permissions.perStorageDownload, permissions.perStorage,
        permissions.perGenericDownload
    )
    val timeGet = arrayOf(permissions.perEverything, permissions.perTimeGet, permissions.perTimeAll)
    val timeReset = arrayOf(permissions.perEverything, permissions.perTimeReset, permissions.perTimeAll)
    val timeIncrement = arrayOf(permissions.perEverything, permissions.perTimeIncrement, permissions.perTimeAll)
    val timeDecrement = arrayOf(permissions.perEverything, permissions.perTimeDecrement, permissions.perTimeAll)
    val timeSet = arrayOf(permissions.perEverything, permissions.perTimeSet, permissions.perTimeAll)
}

fun PrivilegesInEndpointContext.getPrivilege(endpoints: Endpoints): Array<String> {
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


