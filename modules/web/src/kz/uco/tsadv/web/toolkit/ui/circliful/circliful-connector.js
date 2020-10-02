kz_uco_tsadv_web_toolkit_ui_circliful_CirclifulServerComponent = function () {
    var connector = this, element = connector.getElement(), state = connector.getState();

    $(element).circliful({
        animation: 1,
        animationStep: 5,
        foregroundBorderWidth: state.borderWidth,
        backgroundBorderWidth: state.borderWidth,
        percent: state.percentage,
        textSize: 28,
        textStyle: 'font-size: 12px;',
        /*textColor: '#FFFFFF',*/
        foregroundColor: state.foregroundColor,
        backgroundColor: state.backgroundColor,
        /*fillColor:'#FFFFFF',*/
        fontColor: state.fontColor,
        multiPercentage: 1,
        percentages: [10, 20, 30]
    });
};