
function setFileName(fileInput, fileName) {
	filePath = fileInput.value;
	fileName.value = filePath.substr(filePath.lastIndexOf('\\') + 1);
}   