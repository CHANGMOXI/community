/*
    提交问题回复
 */
function post(){
    let questionId = $("#question_id").val();//用jQuery获取input输入框中的question_id
    let content = $("#comment_content").val();

    //调用通用回复方法
    comment2target(questionId,1,content);
}

/*
    提交二级评论
 */
function comment(e) {
    let commentId = e.getAttribute("data-id");
    let content = $("#input-" + commentId).val();

    //调用通用回复方法
    comment2target(commentId,2,content);
}

/*
    通用回复方法
 */
function comment2target(targetId, type, content) {
    if (!content){
        alert("不能回复空内容");
    }

    //使用jQuery.post方法
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",//指定Content-Type
        data: JSON.stringify({
            "parentId": targetId,
            "type": type,
            "content": content
        }), //data为页面要发送给服务器端的评论数据(JSON形式的JS对象)，而页面发送给服务器端的数据一般是字符串
        //可以使用JSON.stringify()将JS对象 转成字符串(序列化) 再发送
        //服务器端接收到字符串之后，在CommentController通过@RequestBody将字符串 反序列化 得到Java对象

        success: function (response){
            if (response.code == 200){//判断状态码
                window.location.reload();//回复成功，刷新页面
            }else {
                if (response.code == 2002){
                    //未登录异常
                    let isAccepted = confirm(response.msg);//提示未登录，确认则跳转登录页面
                    if (isAccepted){
                        //跳转登录页面
                        window.open("https://github.com/login/oauth/authorize?client_id=9706d72cdbecf1898ce0&redirect_uri=http://localhost:8887/callback&scope=user&state=1")
                        //为了不用修改后端API和业务逻辑而做到 判断登录页面是否关闭，应该使用纯页面的方式解决，不应该与服务端有关系
                        //因此使用 HTML5 Web存储的localStorage，把 是否关闭页面isClose 存进浏览器，打开登陆页面登录之后验证是否关闭页面
                        window.localStorage.setItem("isClose",true);
                    }
                }else {
                    //其他异常
                    alert(response.msg);
                }
            }
        },
        dataType: "json"
    });
}

/*
    展开二级评论
 */
function collapseComments(e) {
    let id = e.getAttribute("data-id");
    let comments = $("#comment-" + id);

    //判断是否要展开(没有in，就是要展开)
    if (!comments.hasClass("in")){
        let subCommentContainer = $("#comment-" + id);

        //用jQuery拼接出二级评论列表
        //判断子元素的个数：如果只有1个(评论输入框)，则请求服务端，拿到二级评论数据；如果不止1个，就不用再请求服务端，直接展示
        if (subCommentContainer.children().length == 1){
            $.getJSON( "/comment/" + id, function( data ) {
                //拼接二级评论列表的样式
                $.each( data.data.reverse(), function(index, comment) {
                    let mediaLeftElement = $("<div/>",{
                        "class": "media-left",
                    }).append($("<img/>", {
                        "class": "media-object comment-avatar img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    let mediaBodyElement = $("<div/>",{
                        "class": "media-body",
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')//使用moment.js格式化时间
                    })));

                    let mediaElement = $("<div/>",{
                       "class": "media",
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    let commentElement = $("<div/>",{
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
            });
        }
    }

    //展开或折叠 二级评论
    comments.toggleClass("in");

    //展开或折叠 二级评论，图标变色
    e.classList.toggle("active");
}


