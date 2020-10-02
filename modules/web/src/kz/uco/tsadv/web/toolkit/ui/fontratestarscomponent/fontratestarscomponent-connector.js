kz_uco_tsadv_web_toolkit_ui_fontratestarscomponent_FontRateStarsComponent = function () {
    var connector = this;
    var element = connector.getElement(),
        state = connector.getState();

    $(element).html("<input type='number'/>");

    var rating = $(element).rating({
        showLabel: true,
        starCount: 3,
        inline: true,
        messages: [
            state.messages[0],
            state.messages[1],
            state.messages[2],
            state.messages[3]
        ]
    }).on('change', function () {
        connector.valueChanged(Number($(this).rating('val')));
    });

    rating.rating('val', connector.getState().value);

    connector.onStateChange = function () {
        var state = connector.getState();
    };

};