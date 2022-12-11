function onOpen() {
  const ui = SpreadsheetApp.getUi();

  ui.createMenu('Refresh')
    .addItem('Refresh Projects', 'getJiraProjects')
    .addItem('Refresh Users', 'getJiraUsers')
  .addToUi();
}
