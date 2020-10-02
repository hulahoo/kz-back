kz_uco_tsadv_web_toolkit_ui_orgchartnew_OrgChartNew = function () {

    var connector = this;
    var element = connector.getElement();

    $(element).html("<div id='people'/>");
    $(element).css("padding", "5px 10px");


    var peopleElement = document.getElementById("people");

    $.ajax({
        'url': "./rest/v2/services/tsadv_EmployeeService/generateOgrChart",
        'dataType': 'json',
        'beforeSend': function (request) {
            request.setRequestHeader("Authorization", "Bearer " + connector.getState().authorizationToken);
        },
        data: {
            personGroupId: connector.getState().personGroupId
        }
    }).done(function (data, textStatus, jqXHR) {
        if (data && data.length > 0) {

            /*getOrgChart.themes.myTheme =
                {
                    box: '<g transform="matrix(1,0,0,1,214,190)" class="btn" data-action="preview"><path d="M0 0 L92 0 Q97 0 97 5 L97 40 Q97 45 92 45 L0 45 Q0 45 0 45 L0 0 Q0 0 0 0 Z"></path><text x="25" y="27" width="60">Profile</text></g>',
                    text: '<text width="[width]" class="get-text get-text-[index]" x="[x]" y="[y]">[text]</text>',
                    image: '<clipPath id="clip"><circle cx="60" cy="120" r="40" /></clipPath><image preserveAspectRatio="xMidYMid slice" clip-path="url(#clip)" xlink:href="[href]" x="20" y="80" height="80" width="80"/>',
                    expandCollapseBtnRadius: 20
                };*/


            var orgChart = new getOrgChart(peopleElement, {
                color: "neutralgrey",
                enableDetailsView: false,
                enableEdit: false,
                clickNodeEvent: function (sender, args) {
                    connector.linkClicked(args.node.data["personGroupId"]);
                },
                idField: "id",
                parentIdField: "parentId",
                primaryFields: ["fullName", "positionFullName"],
                photoFields: ["image"],
                width: 1000,
                scale: 0.5,
                expandToLevel: 2,
                dataSource: data
            });

            function clickHandler(sender, args) {

            }
        }
    }).fail(function (jqXHR, textStatus, error) {

    }).always(function () {
        //TODO
    });

    connector.onStateChange = function () {
        $(element).width(state.width);
    }
};