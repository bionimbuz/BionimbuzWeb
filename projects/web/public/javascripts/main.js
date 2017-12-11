
function flashMessage(message, icon, type) {
    $.notify(
        {
            // options
            icon: icon,
            message: message
        },
        {
            // settings
            type: type,
            delay: 3000,
            timer: 500,
            placement: {
                from: "top",
                align: "center"
            },
            animate: {
                enter: 'animated fadeInDown',
                exit: 'animated fadeOutUp'
        }
    });
}     

function flashSuccess(message) {
    flashMessage(
        message,
        'glyphicon glyphicon glyphicon-ok',
        'success');
}    

function flashError(message) {
    flashMessage(
        message,
        'glyphicon glyphicon-remove',
        'danger');
}     

function flashWarn(message) {
    flashMessage(
        message,
        'glyphicon glyphicon-warning-sign',
        'warning');
}

//Configure the name of file chosen in input
function setFileName(fileInput, fileName) {
	filePath = fileInput.value;
	fileName.value = filePath.substr(filePath.lastIndexOf('\\') + 1);
}   

//Configure the check and uncheck for checkboxes
function configureCheckAllForLabels(checkAllElement, checkOneElement, labelCheckAll, labelUncheckAll) {    
	
    checkAllElement.text(labelCheckAll);    
    var uncheckAll = function() {
        checkOneElement.each(function(){
            it = $(this);
            if(!it[0].disabled) {      
                it.iCheck('uncheck');
            }
        }); 
        checkAllElement.text(labelCheckAll);
        checkAllElement.off().on('click', checkAll);
    };
    var checkAll = function() {             
        checkOneElement.each(function(){
            it = $(this);
            if(!it[0].disabled) {      
                it.iCheck('check');
            }
        });             
        checkAllElement.text(labelUncheckAll);
        checkAllElement.off().on('click', uncheckAll);
    };
    checkAllElement.on('click', checkAll);
    checkAllElement.val(labelCheckAll);
    checkOneElement.on('ifChanged', function() {
        checkAllElement.text(labelCheckAll);
        checkAllElement.off().on('click', checkAll);
    });
}

function block(message) {
    $.blockUI({  
        message: "<h1 style='margin-top: 7px; margin-bottom: 10px;'>"+message+"</h1>",
        css: {
            border: 'none',
            padding: '15px', 
            backgroundColor: '#000', 
            '-webkit-border-radius': '10px', 
            '-moz-border-radius': '10px',
            'border-radius': '10px',
            opacity: .7, 
            color: '#fff' 
        } 
    }); 
}

// Adding event to search buttons
$('.btn-crud-search-form-submit').click(function() {
    $('#searchForm').submit();
});