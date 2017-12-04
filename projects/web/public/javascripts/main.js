
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