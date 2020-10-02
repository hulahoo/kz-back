kz_uco_tsadv_web_toolkit_ui_ratestarscomponent_RateStarsComponent = function () {
    var connector = this;
    var element = connector.getElement();

    $(element).html("<div/>");

    var rateStars = $("div", element).rateYo({
        starWidth: connector.getState().starWidth,
        ratedFill: "#166ed5",
        normalFill: "#DFDFDF",
        rating: connector.getState().value,
        readOnly: connector.getState().onlyRead,
        fullStar: connector.getState().fullStar
    }).on("rateyo.set", function (e, data) {
        connector.valueChanged(data.rating);
    });

    connector.onStateChange = function () {
        var state = connector.getState();
        if (rateStars.rateYo("rating") != state.value) {
            rateStars.rateYo("rating", state.value);
        }
    };
};