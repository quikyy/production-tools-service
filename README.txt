The service was used to improve the quality of work in the studio. It consisted of three subservices.

1. JiraService is created to help Jira users in their daily duties in Jira. Many automations are created, which are triggered by appropriately set webhooks.
Few of automation examples:
- Copy approvers from Parent Issue to Child issues
- Set start date as Today when Issue status changed to In Progress
- Assign Issue status to To Do if assinged sprint is Active and Status is To Do
- Assign Issue release version based on sprint
- Assign Issue team based of assignee and their teams and project

2. JiraImporter is a tool that allowed producers to upload tasks from Google Spreadsheet directly to Jira. Spreadsheet is properly prepared for the task format in Jira.

3. BudgetTool was created to make it easier for producers to set a budget for their team for the coming months. Prodcuents, through a specially prepared Google Spreadsheet, using a custom function, downloaded data from the database where exchange rates for individual months were stored. The data could be edited and subsequent months added without the intervention of the programmer. Downloaded data was saved in the user's cache to limit queries.

