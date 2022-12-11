function getJiraProjects(){
  const jiraService_getJiraProjectsUrl = "URL_HIDDEN_DUE_SECURITY_REASONS";
  const options = {
    'method': 'GET',
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'headers': {
    'Authorization': 'Basic BASE64_HIDDEN'
    }
  }

  let rowToInsert = 2;

  const jiraProjects= JSON.parse(UrlFetchApp.fetch(jiraService_getJiraProjectsUrl, options).getContentText());
  jiraProjects.forEach(jiraProject => {
    insertJiraProjects(jiraProject, rowToInsert);
    rowToInsert++;
  })
}

function insertJiraProjects(jiraProject, rowToInsert){
  const jiraProject_keyColumn = 2;
  const summarySheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("config");
  summarySheet.getRange(rowToInsert, jiraProject_keyColumn).setValue(jiraProject.key);
}
