function addComponentsDropDown(e){
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("sheet1");

  const projectColumn = 3;
  const editRange = e.range;

  if(editRange.getColumn() == projectColumn && editRange.getRow() != 1) {
    const projectValue = sheet.getRange(editRange.getRow(), projectColumn).getValue();
    const componentsColumn = 10;
    const dropdown = sheet.getRange(editRange.getRow(), componentsColumn);

    if(projectValue != ""){
      const jiraComponents = getJiraComponents(projectValue);
      Utilities.sleep(500);
      const rule = SpreadsheetApp.newDataValidation().requireValueInList(jiraComponents).build();
      dropdown.setDataValidation(rule)
    }
    else if(projectValue == ""){
      dropdown.clearDataValidations().clearContent();
    }
  }
}

function getJiraComponents(projectKey){
  const jiraService_getJiraComponents =  `URL_HIDDEN_DUE_SECURITY_REASONS${projectKey}`;
  const options = {
    'method': 'GET',
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'headers': {
    'Authorization': 'Basic BASE64_HIDDEN'
    }
  }

  let jiraComponents = JSON.parse(UrlFetchApp.fetch(jiraService_getJiraComponents, options).getContentText());
  jiraComponents = jiraComponents.map(jiraComponent => {
    return jiraComponent.name;
  })
  return jiraComponents;
}