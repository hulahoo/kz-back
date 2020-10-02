kz_uco_tsadv_web_toolkit_ui_scrumboardcomponent_ScrumBoardComponent = function () {
    var connector = this, element = connector.getElement(), scrum;

    createScrumBoard();

    function createScrumBoard() {
        scrum = $(element).scrumBoardList({
            actions: {
                load: connector.getState().requestUrl
            },
            authorizationToken: connector.getState().authorizationToken,
            afterItemReceive: function () {
                console.log(arguments[1]);
                connector.valueChanged(arguments[1]);
            },
            afterDestroy: function () {
                createScrumBoard();
            }
        });

        $('.dropdown-menu a').on('click', function () {
            var action = $(this).data('name'),
                id = $(this).closest('tr').data('id');
            connector.actionClick(id, action);
        });
    }

    connector.onStateChange = function () {
        if (connector.getState().reload) {
            scrum.data('scrumBoardList').destroy();
        }
    };
};