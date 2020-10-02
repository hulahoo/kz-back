/**
 * Created by Yelaman on 05.09.2018.
 */
var config = {
    appUrl: "http://localhost:8085/tsadv",
    loginUrl: "http://localhost:8085/tsadv/rest/v2/oauth/token",
    logoutUrl: "http://localhost:8085/tsadv/rest/v2/oauth/revoke",
    serviceUrl: "/rest/v2/services/tsadv_RecognitionRestService",
    userManagementUrl: "/rest/v2/services/base_UserManagementService",
    fileUrl: "/rest/v2/files",
    clientSecret: "client:secret",//[rest.api.client]:[rest.api.secret]
    defaultUser: "admin",
    defaultPassword: "admin"
};

var commentModel = {
    commentPojo: {
        recognitionId: null,
        text: null,
        parentCommentId: null
    }
};

var getDraftPersonAward = {
    authorEmployeeNumber: null,
    receiverEmployeeNumber: null
}

var createPersonAwardModel = {
    personAwardPojo: {
        authorEmployeeNumber: null,
        receiverEmployeeNumber: null,
        history: null,
        why: null,
        status: null
    },
    language: null
}
var createRecognitionModel = {
    recognitionCreatePojo: {
        authorEmployeeNumber: null,
        qualities: [],
        receiverEmployeeNumber: null,
        recognitionTypeId: null,
        notifyManager: null,
        comment: null
    },
    language: null
}
var updatePersonPreferenceModel = {
    preferenceTypeId: null,
    description: null
}