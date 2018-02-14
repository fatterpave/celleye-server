/**
 * Created by obsjoa on 31.03.2017.
 */
$(function(){
    $("#status").html("");
    $('#upload_btn').click(upload);
});

function upload(){
    var file = $('input[name="upload_file"]').get(0).files[0];
    var username = $('input[name="username"]').val();
    var version = $('input[name="version"]').val();
    $("#loader").css("display","");
    var formData = new FormData();
    formData.append('file', file);
    formData.append('username',username);
    formData.append('version',version);

    $.ajax({
        url: '/rest/app/upload',
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(){
            $("#loader").css("display","none");
            $("#status").html("Fil lastet opp");
        },
        error: function(response){
            $("#loader").css("display","none");
            var error = "error";
            if (response.status===500)
            {
                $("#status").html("Feil under opplasting");
            }
            else if(response.status===409)
            {
                $("#status").html("For lavt eller likt versjonsnummer");
            }
        },
        xhr: function() {
            var myXhr = $.ajaxSettings.xhr();
            if (myXhr.upload) {
                myXhr.upload.addEventListener('progress', progress, false);
            } else {
                console.log('Upload progress is not supported.');
            }
            return myXhr;
        }
    });
}

function progress(e) {
    if (e.lengthComputable) {
        $('#progress_percent').text(Math.floor((e.loaded * 100) / e.total));
        $('progress').attr({value:e.loaded,max:e.total});
    }
}