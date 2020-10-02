kz_uco_tsadv_web_toolkit_ui_chart_ChartServerComponent = function () {
    var connector = this;
    var element = connector.getElement();

    connector.onStateChange = function () {
        var state = connector.getState();
        console.log('personGroupId: ' + state.personGroupId);
    };

    var url = connector.getState().url; //'/tal/rest/v2/services/tsadv_EmployeeService/generate';

    var ajaxURLs = {
        'children': url,
        'parent': url,
        'siblings': url,
        'families': url
    };

    var orgchart = $(element).orgchart({
        "data": url + '?personGroupId=' + connector.getState().personGroupId + '&lang=' + connector.getState().lang + '&systemDate=' + connector.getState().systemDate,
        "depth": 2,
        'pan': true,
        'zoom': true,
        //'ajaxURL': ajaxURLs,
        "nodeContent": "title",
        "toggleSiblingsResp": true,
        "authorizationToken": connector.getState().authorizationToken //+ for rest api security
    }).on('click', 'a.redirect-link', function (event) {
        var id = $(this).closest('.node').attr('id'),
            url = $(this).data('url');

        console.log(id + ' url: ' + url);
        connector.linkClicked([url, id]);
    });

    $(function () {
        console.log('i am ready 2!');
    });
};