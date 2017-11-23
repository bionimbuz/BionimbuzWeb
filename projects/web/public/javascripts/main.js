
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