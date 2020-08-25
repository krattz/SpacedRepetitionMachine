$(document).ready(function(){

    $("#simpleUpload").click(function(){
        $.ajax({
            url:'/create',
                success:function(){
                alert("file upload complete");
                }
        });

    });

    $("#refreshFileButton").click(function(){
        $.ajax({
            url:'/listfiles',
        }).done(function(data){
            console.dir(data);
            var fileHTML= "";
            for(file of data){
            fileHTML += '<li class="list-group-item">' + file.name + '</li>';
            }
            $("#fileListContainer").html(fileHTML);
        });
    });
});