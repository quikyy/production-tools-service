function addSharedTeamDropDown(e){
  const sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("sheet1");

  const assigneeColumn = 7;
  const editRange = e.range;

  if(editRange.getColumn() == assigneeColumn && editRange.getRow() != 1) {
    const assigneValue = sheet.getRange(editRange.getRow(), assigneeColumn).getValue();
    const sharedTeamsColumn = 9;
    const dropdown = sheet.getRange(editRange.getRow(), sharedTeamsColumn);

    if(assigneValue != ""){
      const assigneeDisplayName = assigneValue;
      const jiraUserSharedTeams = getJiraUserSharedTeams(assigneeDisplayName);
      Utilities.sleep(500);
      const rule = SpreadsheetApp.newDataValidation().requireValueInList(jiraUserSharedTeams).build();
      dropdown.setDataValidation(rule)
    }
    else if(assigneValue == ""){
      dropdown.clearDataValidations().clearContent();
    }
  }
}


function getJiraUserSharedTeams(assigneeDisplayName){
  const jiraService_getJiraUsersSharedTeamUrl= `URL_HIDDEN_DUE_SECURITY_REASONS${assigneeDisplayName}`;
  const options = {
    'method': 'GET',
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'headers': {
    'Authorization': 'Basic BASE64_HIDDEN'
    }
  }

  let jiraUserSharedTeams = JSON.parse(UrlFetchApp.fetch(jiraService_getJiraUsersSharedTeamUrl, options).getContentText());
  jiraUserSharedTeams = jiraUserSharedTeams.map(jiraSharedTeam => {
    return jiraSharedTeam.name;
  })
  return jiraUserSharedTeams;
}