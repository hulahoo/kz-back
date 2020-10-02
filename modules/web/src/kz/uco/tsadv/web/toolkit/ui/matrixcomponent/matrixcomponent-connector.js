kz_uco_tsadv_web_toolkit_ui_matrixcomponent_MatrixComponent = function () {
    var connector = this, element = connector.getElement(), matrix;

    createMatrix();

    function createMatrix() {
        matrix = $(element).matrixList({
            actions: {
                load: connector.getState().requestUrl
            },
            authorizationToken: connector.getState().authorizationToken,
            afterItemReceive: function () {
                console.log(arguments[1]);
                connector.valueChanged(arguments[1]);
            },
            afterDestroy: function () {
                createMatrix();
            }
        });
    }

    connector.onStateChange = function () {
        if (connector.getState().reload) {
            matrix.data('matrixList').destroy();
        }
    };
};