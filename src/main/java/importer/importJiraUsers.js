function getJiraUsers(){
  const jiraService_getJiraUsersUrl = "URL_HIDDEN_DUE_SECURITY_REASONS";
  const options = {
    'method': 'GET',
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'headers': {
    'Authorization': 'Basic BASE64_HIDDEN'
    }
  }

  let rowToInsert = 2;
  let jiraUsersDisplayNameByDisplayName = [];
  const jiraUsers = JSON.parse(UrlFetchApp.fetch(jiraService_getJiraUsersUrl, options).getContentText());
  jiraUsers.forEach(jiraUser => {
    jiraUsersDisplayNameByDisplayName.push(jiraUser.displayName);
  })

  jiraUsersDisplayNameByDisplayName = jiraUsersDisplayNameByDisplayName.sort();

  jiraUsersDisplayNameByDisplayName.forEach(jiraUser => {
    insertJiraUser(jiraUser, rowToInsert);
    rowToInsert++;
  })
}

function insertJiraUser(jiraUser, rowToInsert){
  const jiraUser_displayNameColumn = 3;
  const summarySheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("config");
  summarySheet.getRange(rowToInsert, jiraUser_displayNameColumn).setValue(jiraUser);
}