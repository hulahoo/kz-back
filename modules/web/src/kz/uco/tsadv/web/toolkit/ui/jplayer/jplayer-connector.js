kz_uco_tsadv_web_toolkit_ui_jplayer_JPlayerServerComponent = function () {
    var source;
    var connector = this;
    var element = connector.getElement();
    $(element).html("<video id='vid1' class='video-js vjs-default-skin' controls preload='auto' width='770' height='550' " +
        " data-setup='{}'> " +
        " <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p> " +
        " </video>");

    var vid = document.getElementById("vid1");
    var player = videojs(vid);
    vid.load();
    vid.addEventListener("loadeddata", function(){
        vid.currentTime = 10000000000;
    });

    connector.onStateChange = function () {
        var state = connector.getState();
        $(element).html("<video id='vid1' class='video-js vjs-default-skin' controls preload='auto' width='770' height='550' " +
            " data-setup='{}'> " +
            "   <source src='" + state.url + "' type='video/mp4'> " +
            " <p class='vjs-no-js'>To view this video please enable JavaScript, and consider upgrading to a web browser that <a href='http://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a></p> " +
            " </video>");
        var vid = document.getElementById("vid1");
        var player = videojs(vid);
        vid.load();
        vid.addEventListener("loadeddata", function(){
            vid.currentTime = 10000000000;
        });
    };
}