#!/usr/bin/env python3

import gspread
from oauth2client.service_account import ServiceAccountCredentials

# Выпустить креды для гугл иснтрукция https://habr.com/ru/post/483302/
# GSPREAD_CREDS_JSON = ""
# GSPREAD_KEY = ""
#
# GSPREAD_SCOPE = ['https://spreadsheets.google.com/feeds', 'https://www.googleapis.com/auth/drive']
# GSPREAD_CREDS = ServiceAccountCredentials.from_json_keyfile_dict(GSPREAD_CREDS_JSON, GSPREAD_SCOPE)

class TableLoader:
    def get_table_content(self):
        gs_connection = gspread.authorize(GSPREAD_CREDS)
        gs = gs_connection.open_by_key(GSPREAD_KEY)
        # ссылка на таблицу. Лучше скопировать к себе https://docs.google.com/spreadsheets/d/1rx0r1eLIdkU_vhM_ZW0a44_3eQCPaD0JUHkfpsHTdbw/edit?usp=sharing
        ws = gs.worksheet('SharedAnalyticsEvents')
        return ws.get_all_values()