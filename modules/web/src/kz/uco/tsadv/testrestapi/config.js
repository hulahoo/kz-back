/**
 * @author veronika.buksha
 */

var config = {
    appUrl: "http://localhost:8085/tsadv",
    loginUrl: "http://localhost:8085/tsadv/rest/v2/oauth/token",
    logoutUrl: "http://localhost:8085/tsadv/rest/v2/oauth/revoke",
    serviceUrl: "/rest/v2/services/tsadv_RcApiService",
    userManagementUrl: "/rest/v2/services/base_UserManagementService",
    fileUrl: "/rest/v2/files",
    clientSecret: "client:secret",//[rest.api.client]:[rest.api.secret]
    defaultUser: "admin",
    defaultPassword: "admin"
};

//models example

var dictionaryIntModel = {
    "_entityName":"tsadv$DictionaryInt",
    "id":"c8275b4d-9eb2-6ea7-02c0-a198f6aa0ae3",
    "code":"code",
    "name":"name"
};

var registerModel = {
    user: {
        login: null,
        language: null,
        password: null,
        email: null,
        phoneNumber: null,
        person: {
            firstName: null,
            lastName: null,
            middleName: null,
            birthDate: null,
            nationalIdentifier: null,
            sex: null,
            nationality: null,
            citizenship: null,
            maritalStatus: null,
            cityName: null
        }
    }
};

var jobRequestModel = {
    jobRequestInt: {
        requestDate: null,
        requestStatus: null,
        requisition: null,
        requisitionCode: null,
        requisitionJob: null,
        videoFile: null,
        source: null,
        otherSource: null
    }
}

var personModel = {
    person: {
        firstName: null,
        lastName: null,
        middleName: null,
        birthDate: null,
        nationalIdentifier: null,
        sex: null,
        nationality: null,
        citizenship: null,
        maritalStatus: null
    }
};

var personPhotoModel = {
    person: {
        photo: null
    }
};

var personContactModel = {
    contact: {
        contactType: null,
        contactTypeName: null,
        contactValue: null
    }
};

var personEducationModel = {
    education: {
        school: null,
        startYear: null,
        endYear: null,
        specialization: null,
        level: null,
        degree: null,
        location: null
    }
};

var personExperienceModel = {
    experience: {
        company: null,
        job: null,
        startMonth: null,
        untilNow: null,
        endMonth: null,
        description: null
    }
};

var personCompetenceModel = {
    competence: {
        competence: null,
        scaleLevel: null
    }
};

var personAttachmentModel = {
    attachment: {
        category: null,
        description: null,
        filename: null,
        file: null
    }
};

var personAddressModel = {
    addressInt: {
        id: null,
        city: null
    }
}
var candidateModel = {
    candidateInt: {
        requisitionId: null
    }
}

var updateDeleteModel = {id: null};
var requestInterviewModel = {interview: null};

var requestRequisitionModel = {requisition: null};

var approveInterviewModel = {interview: null};

var cancelInterviewModel = {interview: null, reason: null};

var userModel = {
    user: {
        email: null,
        phoneNumber: null
    }
};

var offerModel = {offer: null};
var rejectOfferModel = {
    offerInt: {
        offerId: null,
        offerComment: null
    }
};

var saveVideoFileModel = {requisition: null, videoFile: null};

/**
 * @param id - preScreeningTest ID
 * */
var interviewQuestionnaireModel = {
    interviewQuestionnaireInt: {
        requisition: null,
        questionnaireId: null,
        interviewStatus: null,
        questions: []
    }
};

/**
 * @param anotherAnswer - filled if type not in MULTI, SINGLE
 *
 * @param type - [TEXT, DATE, NUM, MULTI, SINGLE].
 * DATE format yyyy-MM-dd
 * TEXT max length 4000 char
 * */
var interviewQuestionModel = {
    questionId: null,
    answers: [],
    anotherAnswer: null,
    type: null
};

/**
 * This model used if property "type" of "interviewQuestionModel" equals MULTI or SINGLE
 *
 * @param id - answer ID
 * @param checked - true, if checkbox/radioButton checked else false
 * */
var interviewAnswerModel = {
    answerId: null,
    checked: false
};
