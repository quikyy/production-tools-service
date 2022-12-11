function addVersionsDropDown(e){
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("sheet1");

  const projectColumn = 3;
  const editRange = e.range;

  if(editRange.getColumn() == projectColumn && editRange.getRow() != 1) {
    const projectValue = sheet.getRange(editRange.getRow(), projectColumn).getValue();
    const versionsColumn = 11;
    const dropdown = sheet.getRange(editRange.getRow(), versionsColumn);

    if(projectValue != ""){
      const jiraVersions = getJiraVersions(projectValue);
      Utilities.sleep(500);
      const rule = SpreadsheetApp.newDataValidation().requireValueInList(jiraVersions).build();
      dropdown.setDataValidation(rule)
    }
    else if(projectValue == ""){
      dropdown.clearDataValidations().clearContent();
    }
  }
}

function getJiraVersions(projectKey){
  const jiraService_getJiraVersions =  `URL_HIDDEN_DUE_SECURITY_REASONS${projectKey}`;
  const options = {
    'method': 'GET',
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'headers': {
    'Authorization': 'Basic BASE64_HIDDEN'
    }
  }

  let jiraVersions = JSON.parse(UrlFetchApp.fetch(jiraService_getJiraVersions, options).getContentText());
  jiraVersions = jiraVersions.map(jiraVersion => {
    return jiraVersion.name;
  })
  return jiraVersions;
}