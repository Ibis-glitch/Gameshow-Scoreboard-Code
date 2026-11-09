# Gameshow-Scoreboard-Code
The Java coding section for the Gameshow in CSA

## Fast Money survey responses

The Fast Money screen reads the latest answers from a Google Sheets response
sheet whenever it opens and when **Refresh survey responses** is pressed.

1. Link the Google Form to a response spreadsheet.
2. Enable the Google Sheets API in the Google Cloud project for your API key.
3. Configure the repository-root `.env` file:

   ```text
   apiKey=YOUR_GOOGLE_SHEETS_API_KEY
   spreadsheetId=ID_FROM_THE_SHEET_URL
   sheetRange=B:F
   ```

   `B:F` reads columns B through F from the first sheet tab. If the response tab
   is not the first tab, use its exact tab name, for example
   `'Form Responses 1'!B:F`. The range should include the five question headers
   in its first row and the five answer columns below them.
4. Make sure the spreadsheet is readable by an API key. An API key alone does
   not grant access to a private sheet; private response sheets require Google
   OAuth authorization instead. Do not make a sheet containing sensitive
   responses public just to use an API key.
5. If the app reports HTTP 403, confirm the sheet's link access is **Viewer**,
   the Sheets API is enabled in the API key's Cloud project, and the key's API
   restrictions allow the Sheets API.

Answer matching ignores case and treats spacing and ordinary punctuation
differences as equivalent (for example, `New York` and `new-york`). Numeric
values stay distinct (for example, `2` and `3`). Similar meanings or synonyms
are not guessed automatically.
